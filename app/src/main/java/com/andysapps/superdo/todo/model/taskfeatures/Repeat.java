package com.andysapps.superdo.todo.model.taskfeatures;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.RepeatType;
import com.andysapps.superdo.todo.model.SuperDate;

/**
 * Created by Andrews on 27,November,2019
 */

public class Repeat implements Cloneable{

    public boolean isEnabled;

    String repeatType;

    int daysInterval;

    int monthDate = 1;

    boolean onSunday;
    boolean onMonday;
    boolean onTuesday;
    boolean onWednesday;
    boolean onThursday;
    boolean onFriday;
    boolean onSaturday;

    SuperDate startDate;

    SuperDate lastCompletedDate;

    SuperDate lastRepeatedDate;

    public Repeat() {
    }



    @Override
    public Repeat clone() throws CloneNotSupportedException {
        return (Repeat) super.clone();
    }

    public Repeat(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getDaysInterval() {
        return daysInterval;
    }

    public void setDaysInterval(int daysInterval) {
        this.daysInterval = daysInterval;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public SuperDate getStartDate() {
        return startDate;
    }

    public void setStartDate(SuperDate startDate) {
        this.startDate = startDate;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(int monthDate) {
        this.monthDate = monthDate;
    }

    public SuperDate getLastRepeatedDate() {
        return lastRepeatedDate;
    }

    public void setLastRepeatedDate(SuperDate lastRepeatedDate) {
        this.lastRepeatedDate = lastRepeatedDate;
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

    public SuperDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(SuperDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public boolean isAtleastHavingOnceAWeek() {
        if (isOnSunday() || isOnMonday() || isOnTuesday() || isOnWednesday()
                || isOnThursday() || isOnFriday() || isOnSaturday()) {
            return true;
        }

        setOnSunday(true);
        return false;
    }

    public int getWeekDaysCount() {
        int i = 0;
        if (isOnSunday()) i++;
        if (isOnMonday()) i++;
        if (isOnTuesday()) i++;
        if (isOnWednesday()) i++;
        if (isOnThursday()) i++;
        if (isOnFriday()) i++;
        if (isOnSaturday()) i++;
        return i;
    }

    public String getRepeatString() {

        String repeatString = "";

        if (repeatType == null) {
            return repeatString;
        }

        if (repeatType.equals(RepeatType.Day.name())) {

            if (daysInterval != 1 && daysInterval != 0) {
                if (startDate.hasDate) {
                    repeatString = "Every " + daysInterval + " days at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
                } else {
                    repeatString = "Every " + daysInterval + " days at " + startDate.getTimeString();
                }

                return repeatString;
            } else {
                if (startDate.hasDate) {
                    repeatString = "Everyday at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
                } else {
                    repeatString = "Everyday at " + startDate.getTimeString();
                }
            }

            return repeatString;
        }

        if (repeatType.equals(RepeatType.Week.name())) {
            if (startDate.hasDate) {
                repeatString = "Every week at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
            } else {
                repeatString = "Every week at " + startDate.getTimeString();
            }
            return repeatString;
        }

        if (repeatType.equals(RepeatType.Month.name())) {
            if (monthDate == 0) {
                monthDate = 1;
            }
            repeatString = Utils.monthDates[monthDate - 1] + " of every month at " + startDate.getTimeString();
            return repeatString;
        }

        return repeatString;
    }


}