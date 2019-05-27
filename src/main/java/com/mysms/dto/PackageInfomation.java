package com.mysms.dto;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PackageInfomation {
    private SimpleIntegerProperty packageId;
    private SimpleIntegerProperty amount;
    private SimpleIntegerProperty used;
    private SimpleStringProperty packageType;
    private SimpleStringProperty beginTime;
    private SimpleStringProperty endTime;
    private SimpleStringProperty createTime;

    public PackageInfomation(int packageId,
                             int amount,
                             int used,
                             int packageType,
                             String beginTime,
                             String endTime,
                             String createTime) {
        this.packageId = new SimpleIntegerProperty(packageId);
        this.amount = new SimpleIntegerProperty(amount);
        this.used = new SimpleIntegerProperty(used);
        this.packageType = new SimpleStringProperty(packageType == 0 ? "赠送套餐" : "购买套餐");
        this.beginTime = new SimpleStringProperty(beginTime);
        this.endTime = new SimpleStringProperty(endTime);
        this.createTime = new SimpleStringProperty(createTime);
    }

    public int getPackageId() {
        return packageId.get();
    }

    public SimpleIntegerProperty packageIdProperty() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId.set(packageId);
    }

    public int getAmount() {
        return amount.get();
    }

    public SimpleIntegerProperty amountProperty() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public int getUsed() {
        return used.get();
    }

    public SimpleIntegerProperty usedProperty() {
        return used;
    }

    public void setUsed(int used) {
        this.used.set(used);
    }

    public String getPackageType() {
        return packageType.get();
    }

    public SimpleStringProperty packageTypeProperty() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType.set(packageType);
    }

    public String getBeginTime() {
        return beginTime.get();
    }

    public SimpleStringProperty beginTimeProperty() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime.set(beginTime);
    }

    public String getEndTime() {
        return endTime.get();
    }

    public SimpleStringProperty endTimeProperty() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

    public String getCreateTime() {
        return createTime.get();
    }

    public SimpleStringProperty createTimeProperty() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime.set(createTime);
    }

    @Override
    public String toString() {
        return "PackageInfomation{" +
                "packageId=" + packageId +
                ", amount=" + amount +
                ", used=" + used +
                ", packageType=" + packageType +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}
