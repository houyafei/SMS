package com.mysms.service;

import com.mysms.SmsSendReport;
import com.mysms.ValueConstant;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import tencentsms.SmsResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


import static com.mysms.service.SomeService.checkMultInputMsg;

public class SendMsgService extends Service<SmsSendReport> {


    private ExecutorService executor;

    private LinkedList<Future<SmsReqAndResponse>> resultQueue = new LinkedList<>();

    private SmsSendReport report = new SmsSendReport();

    private String mulInputMsg;

    private File msgFile;

    private SendType sendType = SendType.Nothing;

    public enum SendType {
        Nothing, FileMsg, MultInputMsg,
    }

    public SendMsgService(String mulInputMsg, SendType sendType) {
        if (sendType == SendType.MultInputMsg) {
            this.mulInputMsg = mulInputMsg;
            this.sendType = sendType;
        }

        executor = new ThreadPoolExecutor(ValueConstant.AVAIL_PROCESSORS,
                ValueConstant.AVAIL_PROCESSORS * 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque(512));
    }

    public SendMsgService(File msgFile, SendType sendType) {
        if (sendType == SendType.FileMsg) {
            this.msgFile = msgFile;
            this.sendType = sendType;
        }

        executor = new ThreadPoolExecutor(ValueConstant.AVAIL_PROCESSORS,
                ValueConstant.AVAIL_PROCESSORS * 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque(512));
    }

    @Override
    protected Task<SmsSendReport> createTask() {
        Task<SmsSendReport> task = null;
        switch (sendType) {
            case FileMsg:
                task = senFileMsgTask();
                break;
            case MultInputMsg:
                task = sendMulMsgTask();
                break;
            case Nothing:
        }
        return task;
    }

    private Task<SmsSendReport> senFileMsgTask() {
        return new Task<SmsSendReport>() {
            @Override
            protected SmsSendReport call() throws Exception {
                int count = 0;
                // way 1 2988ms,223条
                BufferedReader bf = new BufferedReader(new FileReader(msgFile));
                AtomicBoolean flag = new AtomicBoolean(true);
                new Thread(() -> {
                    bf.lines().forEach(t -> resultQueue.addLast(executor.submit(() -> new SmsReqAndResponse(t, SomeService.sendSingleMsg(t)))));
                    flag.set(false);
                }).start();
                while (flag.get() || !resultQueue.isEmpty()) {
                    if (!resultQueue.isEmpty()) {
                        updateMessage("已经发送：" + (++count) + "条");
                        updateReport(resultQueue.removeFirst().get());
                    }
                }
                updateMessage("总计发送：" + count + "条，OK");
                bf.close();
                return report;
            }

            @Override
            protected void updateProgress(long workDone, long max) {
                super.updateProgress(workDone, max);
            }

            @Override
            protected void updateMessage(String message) {
                super.updateMessage(message);
            }

            @Override
            protected void cancelled() {
                super.cancelled();

            }
        };
    }

    private Task<SmsSendReport> sendMulMsgTask() {
        return new Task<SmsSendReport>() {
            @Override
            protected SmsSendReport call() throws Exception {

                if (checkMultInputMsg(mulInputMsg)) {
                    String[] msgContent = mulInputMsg.split("\n");
                    int count = 0;
                    int max = msgContent.length > 0 ? msgContent.length : 1;
                    // way 1  (208条：3122ms)
                    new Thread(() -> {
                        for (String sms : msgContent) {
                            resultQueue.addLast(executor.submit(() -> new SmsReqAndResponse(sms, SomeService.sendSingleMsg(sms))));
                        }
                    }).start();
                    while (max > count) {
                        if (!resultQueue.isEmpty()) {
                            updateProgress(++count, max);
                            updateReport(resultQueue.removeFirst().get());
                        }
                    }
                } else {
                    updateMessage("fail");
                    report.setReportMsg("参数格式不正确");
                }

                return report;
            }

            @Override
            protected void updateProgress(long workDone, long max) {
                super.updateProgress(workDone, max);
                updateMessage("已经发送：" + workDone + "条");
            }

            @Override
            protected void updateMessage(String message) {
                super.updateMessage(message);
            }
        };
    }
    private void updateReport(SmsReqAndResponse smsResponse) {
        if (smsResponse.getSmsResponse().result() == 0) {
            report.getSuccessCount().getAndAdd(1);
        } else {
            report.getFailCount().getAndAdd(1);
            report.getReportContent().put(smsResponse.getSmsContent(), smsResponse.getSmsResponse().errmsg());
        }
    }
}


class SmsReqAndResponse {
    private String smsContent;
    private SmsResponse smsResponse;

    SmsReqAndResponse(String smsContent, SmsResponse smsResponse) {
        this.smsContent = smsContent;
        this.smsResponse = smsResponse;
    }

    String getSmsContent() {
        return smsContent;
    }

    SmsResponse getSmsResponse() {
        return smsResponse;
    }


}