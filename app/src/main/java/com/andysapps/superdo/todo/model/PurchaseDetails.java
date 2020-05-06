package com.andysapps.superdo.todo.model;

import java.util.Date;

public class PurchaseDetails {

    String skyId;
    String purchaseToken;
    String orderId;


    // PRUCHASED
    int status;
    Date purchaseTime;

    public PurchaseDetails() {
    }

    public String getSkyId() {
        return skyId;
    }

    public void setSkyId(String skyId) {
        this.skyId = skyId;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
