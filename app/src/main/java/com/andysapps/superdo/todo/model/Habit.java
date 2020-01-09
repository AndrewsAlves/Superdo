package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.HabitCategory;
import com.andysapps.superdo.todo.enums.HabitGoalDay;
import com.andysapps.superdo.todo.model.sidekicks.Repeat;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrews on 19,October,2019
 */

public class Habit implements Cloneable {

    @DocumentId
    String documentId;

    String userId;

    int habitIndex;

    // step 1

    String name;

    HabitCategory habitCategory;

    boolean isHabitDone;
    boolean destroyHabit;
    boolean createhabit;

    // step 2
    int totalDays;
    HabitGoalDay habitGoalDay;

    boolean remind;

    SuperDate superTime;

    boolean onSunday;
    boolean onMonday;
    boolean onTuesday;
    boolean onWednesday;
    boolean onThursday;
    boolean onFriday;
    boolean onSaturday;

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

    public Habit() {
    }

    @Override
    public Habit clone() throws CloneNotSupportedException {
        return (Habit) super.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isHabitDone() {
        return isHabitDone;
    }

    public void setHabitDone(boolean habitDone) {
        isHabitDone = habitDone;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public HabitCategory getHabitCategory() {
        return habitCategory;
    }

    public void setHabitCategory(HabitCategory habitCategory) {
        this.habitCategory = habitCategory;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public SuperDate getSuperTime() {
        return superTime;
    }

    public void setSuperTime(SuperDate superTime) {
        this.superTime = superTime;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getHabitIndex() {
        return habitIndex;
    }

    public void setHabitIndex(int habitIndex) {
        this.habitIndex = habitIndex;
    }

    public boolean isDestroyHabit() {
        return destroyHabit;
    }

    public void setDestroyHabit(boolean destroyHabit) {
        this.destroyHabit = destroyHabit;
    }

    public boolean isCreatehabit() {
        return createhabit;
    }

    public void setCreatehabit(boolean createhabit) {
        this.createhabit = createhabit;
    }

    public HabitGoalDay getHabitGoalDay() {
        return habitGoalDay;
    }

    public void setHabitGoalDay(HabitGoalDay habitGoalDay) {
        this.habitGoalDay = habitGoalDay;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public boolean isOnSunday() {
        return onSunday;
    }

    public void setOnSunday(boolean onSunday) {
        this.onSunday = onSunday;
    }

    public boolean isOnMonday() {
        return onMonday;
    }

    public void setOnMonday(boolean onMonday) {
        this.onMonday = onMonday;
    }

    public boolean isOnTuesday() {
        return onTuesday;
    }

    public void setOnTuesday(boolean onTuesday) {
        this.onTuesday = onTuesday;
    }

    public boolean isOnWednesday() {
        return onWednesday;
    }

    public void setOnWednesday(boolean onWednesday) {
        this.onWednesday = onWednesday;
    }

    public boolean isOnThursday() {
        return onThursday;
    }

    public void setOnThursday(boolean onThursday) {
        this.onThursday = onThursday;
    }

    public boolean isOnFriday() {
        return onFriday;
    }

    public void setOnFriday(boolean onFriday) {
        this.onFriday = onFriday;
    }

    public boolean isOnSaturday() {
        return onSaturday;
    }

    public void setOnSaturday(boolean onSaturday) {
        this.onSaturday = onSaturday;
    }

    public String getDoDateString() {

        if (superTime == null) {
            return "No Date";
        }

        String duedate;

        if (superTime.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = superTime.getMonthString() + " "+ superTime.getDate() + ", " + superTime.getYear();
        } else {
            duedate = superTime.getMonthString() + " "+ superTime.getDate();
        }

        return duedate;
    }

    public String getDoDateString2() {

        if (superTime == null) {
            return "No Do Date";
        }

        String duedate;

        if (Utils.isSuperDateToday(superTime)) {
            return "Do Today by " + getTimeString();
        } else if (Utils.isSuperDateTomorrow(superTime)) {
            return "Do Tomorrow by " + getTimeString();
        }

        if (superTime.getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = "Do " + superTime.getMonthString() + " "+ superTime.getDate() + ", " + superTime.getYear() + " by " + getTimeString();
        } else {
            duedate = "Do " + superTime.getMonthString() + " "+ superTime.getDate() + " by " + getTimeString();
        }

        return duedate;
    }

    public  String getTimeString() {

        if (superTime == null) {
            return "No Time";
        }

        int hours = superTime.hours;
        String meridien = " AM";

        if (hours >= 12) {
            if (hours != 12) {
                hours = superTime.hours - 12;
            }
            meridien = " PM";
        }

        // format to two decimal
        String min =  new DecimalFormat("00").format(superTime.minutes);

        return hours + " : " + min + meridien;
    }

    public String getHabitString() {

        switch (habitGoalDay) {
            case EVERYDAY:
                return " \" " + name + " everyday" + " \" ";
            case ONCE_IN_TWO_DAYS:
                return " \" " + name + " once in two days" + " \" ";
            case WEEKLY:
                return " \" " + name + getWeeklyString() + " \" ";
        }

        return " \" " + name + " \" ";
    }

    public String getWeeklyString() {

        int count = getWeeklyCount();

        if (count == 7) {
            return " everyday";
        }

        if (count == 6) {
            return " 6 days a week";
        }

        if (count == 5) {
            return " 5 days a week";
        }

        if (count == 4) {
            return " 4 days a week";
        }

        if (count == 3) {
            return " thrice a week";
        }

        if (count == 2) {
            return " twice a week";
        }

        if (count == 1) {
            return " once a week";
        }

        return " weekly";
    }

    public int getWeeklyCount() {
        int count = 0;

        if (isOnSunday()) count++;
        if (isOnMonday()) count++;
        if (isOnTuesday()) count++;
        if (isOnWednesday()) count++;
        if (isOnThursday()) count++;
        if (isOnFriday()) count++;
        if (isOnSaturday()) count++;

        return count;
    }

}
