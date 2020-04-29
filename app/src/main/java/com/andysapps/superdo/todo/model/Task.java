package com.andysapps.superdo.todo.model;

import android.util.Log;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.taskfeatures.ContactCard;
import com.andysapps.superdo.todo.model.taskfeatures.Deadline;
import com.andysapps.superdo.todo.model.taskfeatures.Focus;
import com.andysapps.superdo.todo.model.taskfeatures.Location;
import com.andysapps.superdo.todo.model.taskfeatures.Remind;
import com.andysapps.superdo.todo.model.taskfeatures.Repeat;
import com.andysapps.superdo.todo.model.taskfeatures.Subtask;
import com.andysapps.superdo.todo.model.taskfeatures.Subtasks;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.andysapps.superdo.todo.manager.FirestoreManager.TAG;

/**
 * Created by Andrews on 19,October,2019
 */

public class Task implements Cloneable {

    @DocumentId
    String documentId;

    String userId;

    String title;

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

    int espritPoints;

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

    ////////////////////////////////
    ///////// GETTER AND SETTER
    ///////////////////////////////

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBucketId(String bucketId) {
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
        this.isTaskCompleted = taskCompleted;
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

    public int getEspritPoints() {
        return espritPoints;
    }

    public void calculateEspritPoints(int espritPoints) {
        this.espritPoints = espritPoints;
    }

    public int getRemindRequestCode() {
        return remindRequestCode;
    }

    public void setRemindRequestCode(int remindRequestCode) {
        this.remindRequestCode = remindRequestCode;
    }

    ///////////////////////////////////
    //////////// OTHER FUNCTIONS
    //////////////////////////////////

    public void setTaskCompletedAction(boolean taskCompleted) {
        isTaskCompleted = taskCompleted;

        if (taskCompleted) {
            Log.e(TAG, "setTaskCompleted: Task completed SET DATE");
            taskCompletedDate = Calendar.getInstance().getTime();
            calculateEspritPoints();

            if (getRepeat() != null) {
                getRepeat().setLastCompletedDate(new SuperDate(Calendar.getInstance()));
                Utils.setNextDoDate(this);
            }

        } else {
            taskCompletedDate = null;
            calculateEspritPoints();

            if (getRepeat() != null) {
                getRepeat().setLastCompletedDate(null);
                Utils.setNextDoDate(this);
            }
        }
    }

    public Bucket getBucket() {
        if (FirestoreManager.getInstance().getHasMapBucket().containsKey(bucketId)) {
            return FirestoreManager.getInstance().getHasMapBucket().get(bucketId);
        } else {
            return FirestoreManager.getAllTasksBucket();
        }
    }

    public int generateNewRemindRequestCode() {
        Random random = new Random();
        remindRequestCode = random.nextInt(1000) * random.nextInt(10);
        Log.e("Task", "generateNewRequestCode: " + remindRequestCode);
        return remindRequestCode;
    }

    public void calculateEspritPoints() {
        int taskCompleted = 5;
        int deadlineTask = 20;
        int remindedTask = 20;
        int subTaskPoint = 1;
        int completeMissesTask = 2;

        if (!isTaskCompleted) {
            return;
        }

        if (espritPoints != 0) {
            return;
        }

        if (!Utils.isSuperDateIsPast(doDate)) {
            Log.e("Task ", "setEspritPoints: task completed +5");
            espritPoints += taskCompleted;
        } else {
            Log.e("Task ", "setEspritPoints: missed task +2");
            espritPoints += completeMissesTask;
        }

        if (deadline != null) {
            SuperDate deadlineDate = new SuperDate(deadline.date, deadline.month, deadline.year, deadline.hours, deadline.minutes);
            if (!Utils.isSuperDateIsPast(deadlineDate)) {
                Log.e("Task ", "setEspritPoints: Deadlined task +20");
                espritPoints +=  deadlineTask;
            }
        }

        if (isToRemind()) {
            Log.e("Task ", "setEspritPoints: Reminded task +20");
            espritPoints +=  remindedTask;
        }

        if (subtasks != null) {
            int i = 0;
            for (Subtask subtask : subtasks.getSubtaskList()) {
                if (subtask.isTaskCompleted()) {
                    i++;
                    Log.e("Task ", "setEspritPoints: subtask task " + i + " +1");
                    espritPoints++;
                }
            }
        }

        FirestoreManager.getInstance().addEspritScore(espritPoints);
    }

    public String getDoDateString2() {

        if (doDate == null) {
            return "No Do Date";
        }

        if (repeat != null) {
            if (!Utils.isSuperDateToday(doDate) && !Utils.isSuperDateTomorrow(doDate)) {
                return "Repeats " + doDate.getSuperDateString() + " by " + doDate.getTimeString();
            }
        }

        return "Do " + doDate.getSuperDateString() + " by " + doDate.getTimeString();
    }
}
