package com.andysapps.superdo.todo.model.sidekicks;

import com.andysapps.superdo.todo.enums.RepeatType;
import com.andysapps.superdo.todo.model.SuperDate;

import java.util.List;

/**
 * Created by Andrews on 27,November,2019
 */
public class Repeat {

    public boolean isEnabled;

    int times;

    RepeatType repeatType;

    SuperDate startDate;

    List<String> weekDays;

    public Repeat() {
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

    public List<String> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<String> weekDays) {
        this.weekDays = weekDays;
    }

    public String getRepeatString() {

        String repeatString = " ";

        if (repeatType == RepeatType.Day) {

            if (times != 1) {
                if (startDate.hasDate) {
                    repeatString = "Every " + times + " days at " + startDate.getTimeString() + " from " + startDate.getMonthStringLong();
                } else {
                    repeatString = "Every " + times + " days at " + startDate.getTimeString();
                }

                return repeatString;
            } else {
                if (startDate.hasDate) {
                    repeatString = "Everyday at " + startDate.getTimeString() + " from " + startDate.getMonthStringLong();
                } else {
                    repeatString = "Everyday at " + startDate.getTimeString();
                }
            }

            return repeatString;
        }

        if (repeatType == RepeatType.Week) {

        }

        return repeatString;
    }
}
