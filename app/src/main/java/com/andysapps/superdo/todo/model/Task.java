package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

/**
 * Created by Andrews on 19,October,2019
 */

public class Task {

    String name;

    String description;

    TaskListing listedIn;

    // DD MM YYYY
    int[] dueDate;

    // HH MM
    int[] time;

    String repeat;

    String priority;

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

    public int[] getDueDate() {
        return dueDate;
    }

    public void setDueDate(int[] dueDate) {
        this.dueDate = dueDate;
    }

    public int[] getTime() {
        return time;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
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

    public String getDueDateString() {

        if (dueDate == null) {
            return null;
        }

        String duedate;

        duedate = Utils.getMonthString(dueDate[1]) + " "+ dueDate[0];

        return duedate;
    }
}
