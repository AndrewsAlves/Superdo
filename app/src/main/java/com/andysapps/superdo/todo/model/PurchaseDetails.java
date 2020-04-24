package com.andysapps.superdo.todo.model;

public class PurchaseDetails {

    String skyId;
    String purchaseToken;

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
}
