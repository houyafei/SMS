package com.mysms.service;

import com.mysms.SmsSendReport;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tencentsms.SmsResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.mysms.service.SomeService.checkMsgPhoneAndContent;
import static com.mysms.service.SomeService.checkMultInputMsg;

public class SendMsgService extends Service<SmsSendReport> {

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

    }

    public SendMsgService(File msgFile, SendType sendType) {
        if (sendType == SendType.FileMsg) {
            this.msgFile = msgFile;
            this.sendType = sendType;
        }

    }

    int count = 0;


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
                try {
                    BufferedReader bf = new BufferedReader(new FileReader(msgFile));
                    bf.lines().forEach(t -> {
                        SmsResponse smsResponse = SomeService.sendSingleMsg(t);
                        updateReport(smsResponse, t);
                        ++count;
                        updateMessage("已经发送：" + count + "条");
                    });

                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                System.out.println(1+"-" + mulInputMsg);
                if (checkMultInputMsg(mulInputMsg)) {
                    System.out.println(2+"-" + mulInputMsg);
                    String[] msgContent = mulInputMsg.split("\n");
                    int count = 0;
                    int max = msgContent.length > 0 ? msgContent.length : 1;

                    for (String sms : msgContent) {

                        SmsResponse smsResponse = SomeService.sendSingleMsg(sms);
                        updateReport(smsResponse, sms);


                        updateProgress(++count, max);
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

    private void updateReport(SmsResponse smsResponse, String sms) {
        if (smsResponse.result() == 0) {
            report.getSuccessCount().getAndAdd(1);
        } else {
            report.getFailCount().getAndAdd(1);
            report.getReportContent().put(sms, smsResponse.errmsg());
        }
    }
}
