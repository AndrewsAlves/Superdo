package com.andysapps.superdo.todo.model;

import android.util.Log;

import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.manager.FirestoreManager;
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
import java.util.Random;

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

    Date taskCompletedDate;

    ////
    // SIDE KICKS
    /////

    SuperDate doDate;

    Repeat repeat;

    Deadline deadline;

    Subtasks subtasks;

    Remind remind;

    boolean toRemind;

    int remindRequestCode;

    Focus focus;

    ContactCard contactCard;

    Location location;

    /////
    // BUCKET
    /////

    String bucketId;

    ///////
    // OTHER
    ///////

    @ServerTimestamp
    Date created;

    boolean movedToBin;
    boolean deleted;

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

    public Bucket getBucket() {
        if (FirestoreManager.getInstance().getHasMapBucket().containsKey(bucketId)) {
            return FirestoreManager.getInstance().getHasMapBucket().get(bucketId);
        } else {
            return FirestoreManager.getAllTasksBucket();
        }
    }

    public void setBucketId(String bucket) {
        this.bucketId = bucketId;
    }

    public String getBucketId() {
        return bucketId;
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

        if (taskCompleted) {
            taskCompletedDate = Calendar.getInstance().getTime();
        } else {
            taskCompletedDate = null;
        }

        isTaskCompleted = taskCompleted;
    }

    public boolean isMovedToBin() {
        return movedToBin;
    }

    public void setMovedToBin(boolean movedToBin) {
        this.movedToBin = movedToBin;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public Remind getRemind() {
        return remind;
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

    public void setRemind(Remind remind) {
        this.remind = remind;
    }

    public boolean isToRemind() {
        return toRemind;
    }

    public void setToRemind(boolean toRemind) {
        this.toRemind = toRemind;
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


    public Date getTaskCompletedDate() {
        return taskCompletedDate;
    }

    public void setTaskCompletedDate(Date taskCompletedDate) {
        this.taskCompletedDate = taskCompletedDate;
    }

    public int getRemindRequestCode() {
        return remindRequestCode;
    }

    public void setRemindRequestCode(int remindRequestCode) {
        this.remindRequestCode = remindRequestCode;
    }

    public int generateNewRequestCode() {
        Random random = new Random();
        remindRequestCode = random.nextInt(1000) * random.nextInt(10);
        Log.e("Task", "generateNewRequestCode: " + remindRequestCode);
        return remindRequestCode;
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

        return "Do " + doDate.getSuperDateString() + " by " + doDate.getTimeString();

        /*if (Utils.isSuperDateToday(doDate)) {
            return "Do Today by " + getTimeString();
        } else if (Utils.isSuperDateTomorrow(doDate)) {
            return "Do Tomorrow by " + getTimeString();
        }

        if (doDate.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = "Do " + doDate.getMonthString() + " "+ doDate.getDate() + ", " + doDate.getYear() + " by " + getTimeString();
        } else {
            duedate = "Do " + doDate.getMonthString() + " "+ doDate.getDate() + " by " + getTimeString();
        } */

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
