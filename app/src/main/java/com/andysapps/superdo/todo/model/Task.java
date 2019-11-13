package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrews on 19,October,2019
 */

public class Task implements Cloneable {

    @DocumentId
    String documentId;

    String userId;

    String name;

    String description;

    TaskListing listedIn;

    int taskIndex;

    SuperDate doDate;

    SuperDate dueDate;

    String repeat;

    int priority;

    String bucketId;

    String bucketName;

    String bucketColor;

    boolean toRemind;

    List<String> subTasks;

    @ServerTimestamp
    Date created;

    boolean isTaskCompleted;

    boolean isDeleted;

    public Task() {
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SuperDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(SuperDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketColor() {
        return bucketColor;
    }

    public void setBucketColor(String bucketColor) {
        this.bucketColor = bucketColor;
    }

    public boolean isToRemind() {
        return toRemind;
    }

    public void setToRemind(boolean toRemind) {
        this.toRemind = toRemind;
    }

    public List<String> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<String> subTasks) {
        this.subTasks = subTasks;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        isTaskCompleted = taskCompleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public TaskListing getListedIn() {
        return listedIn;
    }

    public void setListedIn(TaskListing listedIn) {
        this.listedIn = listedIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SuperDate getDoDate() {
        return doDate;
    }

    public void setDoDate(SuperDate doDate) {
        this.doDate = doDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    public String getDueDateString() {

        if (dueDate == null) {
            return "No deadline";
        }

        String duedate;

        if (dueDate.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = dueDate.getMonthString() + " "+ dueDate.getDate() + ", " + dueDate.getYear();
        } else {
            duedate = dueDate.getMonthString() + " "+ dueDate.getDate();
        }

        return duedate;
    }

    public String getDoDateString() {

        if (doDate == null) {
            return "No Date";
        }

        String duedate;

        if (doDate.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = doDate.getMonthString() + " "+ doDate.getDate() + ", " + doDate.getYear();
        } else {
            duedate = doDate.getMonthString() + " "+ doDate.getDate();
        }

        return duedate;
    }

    public String getDoDateString2() {

        if (doDate == null) {
            return "No Date";
        }

        String duedate;

        if (doDate.getDate() == Calendar.getInstance().get(Calendar.DATE)) {
            return "Do Today";
        } else if (doDate.getDate() == Utils.getTomorrow().get(Calendar.DATE)) {
            return "Do Tomorrow";
        }

        if (doDate.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = doDate.getMonthString() + " "+ doDate.getDate() + ", " + doDate.getYear();
        } else {
            duedate = doDate.getMonthString() + " "+ doDate.getDate();
        }

        return duedate;
    }

    public  String getTimeString() {

        if (doDate == null) {
            return "No Time";
        }

        String timeString;

        if (doDate.hours > 12) {
            timeString = doDate.hours + " : " + doDate.minutes + " pm";
        } else {
            timeString = doDate.hours + " : " + doDate.minutes + " am";
        }

        return timeString;
    }

}
