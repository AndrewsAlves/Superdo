package com.andysapps.superdo.todo.model.notification_reminders;

import com.andysapps.superdo.todo.model.SuperDate;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Andrews on 26,February,2020
 */

public class SimpleNotification {

    @DocumentId
    String documentId;

    String notificationId;

    String contentTitle;
    String contentText;
    String contextBigText;

    SuperDate notificationTime;

    boolean isDeleted = false;

    @ServerTimestamp
    Date created;

    public SimpleNotification() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContextBigText() {
        return contextBigText;
    }

    public void setContextBigText(String contextBigText) {
        this.contextBigText = contextBigText;
    }

    public SuperDate getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(SuperDate notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
