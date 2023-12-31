package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.sidekicks.ContactCard;
import com.andysapps.superdo.todo.model.sidekicks.Deadline;
import com.andysapps.superdo.todo.model.sidekicks.Focus;
import com.andysapps.superdo.todo.model.sidekicks.Location;
import com.andysapps.superdo.todo.model.sidekicks.Remind;
import com.andysapps.superdo.todo.model.sidekicks.Repeat;
import com.andysapps.superdo.todo.model.sidekicks.Subtasks;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DecimalFormat;
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

    boolean isTaskCompleted;

    ////
    // SIDE KICKS
    /////

    SuperDate doDate;

    Repeat repeat;

    Deadline deadline;

    Subtasks subtasks;

    Remind remind;

    Focus focus;

    ContactCard contactCard;

    Location location;

    /////
    // BUCKET
    /////

    String bucketId;

    String bucketName;

    String bucketColor;

    ///////
    // OTHER
    ///////

    @ServerTimestamp
    Date created;

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

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    public void setDeadline(Deadline deadline) {
        this.deadline = deadline;
    }

    public Subtasks getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Subtasks subtasks) {
        this.subtasks = subtasks;
    }

    public Remind getRemind() {
        return remind;
    }

    public void setRemind(Remind remind) {
        this.remind = remind;
    }

    public Focus getFocus() {
        return focus;
    }

    public void setFocus(Focus focus) {
        this.focus = focus;
    }

    public ContactCard getContactCard() {
        return contactCard;
    }

    public void setContactCard(ContactCard contactCard) {
        this.contactCard = contactCard;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
            return "No Do Date";
        }

        String duedate;

        if (Utils.isSuperDateToday(doDate)) {
            return "Do Today by " + getTimeString();
        } else if (Utils.isSuperDateTomorrow(doDate)) {
            return "Do Tomorrow by " + getTimeString();
        }

        if (doDate.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = "Do " + doDate.getMonthString() + " "+ doDate.getDate() + ", " + doDate.getYear() + " by " + getTimeString();
        } else {
            duedate = "Do " + doDate.getMonthString() + " "+ doDate.getDate() + " by " + getTimeString();
        }

        return duedate;
    }

    public  String getTimeString() {

        if (doDate == null) {
            return "No Time";
        }

        int hours = doDate.hours;
        String meridien = " AM";

        if (hours >= 12) {
            if (hours != 12) {
                hours = doDate.hours - 12;
            }
            meridien = " PM";
        }

        // format to two decimal
        String min =  new DecimalFormat("00").format(doDate.minutes);

        return hours + " : " + min + meridien;
    }

}
