package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.HabitCategory;
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

    String name;

    HabitCategory habitCategory;

    int habitIndex;

    boolean isHabitDone;

    boolean destroyHabit;
    boolean createhabit;

    boolean everyDay;
    boolean onceInTwoDays;
    boolean weekly;
    boolean remind;

    ////
    // SIDE KICKS
    /////

    SuperDate doDate;

    Repeat repeat;

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

    public int getHabitIndex() {
        return habitIndex;
    }

    public void setHabitIndex(int habitIndex) {
        this.habitIndex = habitIndex;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
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

    public boolean isEveryDay() {
        return everyDay;
    }

    public void setEveryDay(boolean everyDay) {
        this.everyDay = everyDay;
    }

    public boolean isOnceInTwoDays() {
        return onceInTwoDays;
    }

    public void setOnceInTwoDays(boolean onceInTwoDays) {
        this.onceInTwoDays = onceInTwoDays;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
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
