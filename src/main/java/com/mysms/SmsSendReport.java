package com.mysms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SmsSendReport {
    private AtomicInteger failCount = new AtomicInteger(0);
    private AtomicInteger SuccessCount = new AtomicInteger(0);
    private ConcurrentHashMap<String, String> reportContent = new ConcurrentHashMap<>(32);
    private String reportMsg = "";

    public SmsSendReport() {
    }

    public SmsSendReport(AtomicInteger failCount, AtomicInteger successCount, ConcurrentHashMap<String, String> reportContent, String reportMsg) {
        this.failCount = failCount;
        SuccessCount = successCount;
        this.reportContent = reportContent;
        this.reportMsg = reportMsg;
    }

    public AtomicInteger getFailCount() {
        return failCount;
    }

    public void setFailCount(AtomicInteger failCount) {
        this.failCount = failCount;
    }

    public AtomicInteger getSuccessCount() {
        return SuccessCount;
    }

    public void setSuccessCount(AtomicInteger successCount) {
        SuccessCount = successCount;
    }

    public ConcurrentHashMap<String, String> getReportContent() {
        return reportContent;
    }

    public void setReportContent(ConcurrentHashMap<String, String> reportContent) {
        this.reportContent = reportContent;
    }

    public String getReportMsg() {
        return reportMsg;
    }

    public void setReportMsg(String reportMsg) {
        this.reportMsg = reportMsg;
    }

    public String obtainFailMsg() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : reportContent.entrySet()) {
            sb.append(entry.getKey()).append(":\n").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
