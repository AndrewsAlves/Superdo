package com.andysapps.superdo.todo.model.sidekicks;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.model.Task;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrews on 12,November,2019
 */
public class Deadline implements Cloneable {

    public int hours;
    public int minutes;

    public int date;
    public int month;
    public int year;

    public boolean hasDate;
    public boolean hasTime;

    Date timestamp;

    public boolean isEnabled;

    public Deadline() { }

    public Deadline(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Deadline(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Deadline(long timestamp) {
        this.timestamp = new Timestamp(timestamp);
    }

    public Deadline(int day, int month, int year) {
        this.date = day;
        this.month = month;
        this.year = year;
    }

    public Deadline(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override
    public Deadline clone() throws CloneNotSupportedException {
        return (Deadline) super.clone();
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setDoDate(int date, int month, int year) {
        this.setDate(date);
        this.setMonth(month);
        this.setYear(year);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTime(int hour, int minutes) {
        this.setHours(hour);
        this.setMinutes(minutes);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMonthString() {
        return Utils.getMonthString(month);
    }

    public String getMonthStringLong() {
        return Utils.getMonthStringLong(month);
    }

    public String getDeadlineDateString() {

        String duedate;

        if (!hasDate) {
            return "Set Date";
        }

        duedate = getDate() + ", " + getMonthStringLong()  + ", " + getYear();

        return duedate;
    }

    public String getDoDateStringMain() {

        if (!hasDate) {
            return "No Deadline";
        }

        String duedate;

        if (getDate() == Utils.getTomorrow().get(Calendar.DATE)) {
            return "Today by " + getTimeString();
        } else if (getDate() == Utils.getTomorrow().get(Calendar.DATE)) {
            return "Tomorrow by " + getTimeString();
        }

        if (getYear() != Calendar.getInstance().get(Calendar.YEAR)) {
            duedate = getDate() + ", " + getMonthStringLong() + ", "+ getYear() + " at " + getTimeString();
        } else {
            duedate = getDate() + ", " + getMonthStringLong() + " at " + getTimeString();
        }

        return duedate;
    }

    public  String getTimeString() {

        if (!hasTime) {
            return "00 : 00";
        }

        int hours1 = hours;
        String meridien = " AM";

        if (hours > 12) {
            hours1 = hours - 12;
            meridien = " PM";
        }

        // format to two decimal
        String min =  new DecimalFormat("00").format(minutes);

        return hours1 + " : " + min + meridien;
    }
}
