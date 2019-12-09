package com.andysapps.superdo.todo.model.sidekicks;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.RepeatType;
import com.andysapps.superdo.todo.model.SuperDate;

import java.util.List;

/**
 * Created by Andrews on 27,November,2019
 */

public class Repeat implements Cloneable{

    public boolean isEnabled;

    int times;

    int monthDaysIndex;

    RepeatType repeatType;

    SuperDate startDate;

    boolean onSunday;
    boolean onMonday;
    boolean onTuesday;
    boolean onWednesday;
    boolean onThursday;
    boolean onFriday;
    boolean onSaturday;

    public Repeat() {
    }

    @Override
    public Repeat clone() throws CloneNotSupportedException {
        return (Repeat) super.clone();
    }

    public Repeat(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
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

    public int getMonthDaysIndex() {
        return monthDaysIndex;
    }

    public void setMonthDaysIndex(int monthDaysIndex) {
        this.monthDaysIndex = monthDaysIndex;
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

    public String getRepeatString() {

        String repeatString = "";

        if (repeatType == RepeatType.Day) {

            if (times != 1 && times != 0) {
                if (startDate.hasDate) {
                    repeatString = "Every " + times + " days at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
                } else {
                    repeatString = "Every " + times + " days at " + startDate.getTimeString();
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

        if (repeatType == RepeatType.Week) {
            if (times != 1 && times != 0) {
                if (startDate.hasDate) {
                    repeatString = "Every " + times + " Weeks at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
                } else {
                    repeatString = "Every " + times + " Weeks at " + startDate.getTimeString();
                }

                return repeatString;
            } else {
                if (startDate.hasDate) {
                    repeatString = "Every week at " + startDate.getTimeString() + " from " + startDate.getSuperDateString();
                } else {
                    repeatString = "Every week at " + startDate.getTimeString();
                }
            }

            return repeatString;
        }

        if (repeatType == RepeatType.Month) {
            repeatString = Utils.monthDates[monthDaysIndex] + " of every month at " + startDate.getTimeString();
            return repeatString;
        }

        return repeatString;
    }
}
