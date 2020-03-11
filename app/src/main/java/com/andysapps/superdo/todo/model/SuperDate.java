package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.model.sidekicks.Deadline;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrews on 12,November,2019
 */

public class SuperDate implements Cloneable {

    public int hours;
    public int minutes;

    public int date;
    public int month;
    public int year;

    public boolean hasDate;
    public boolean hasTime;

    Date timestamp;

    public SuperDate() { }

    @Override
    public SuperDate clone() throws CloneNotSupportedException {
        return (SuperDate) super.clone();
    }

    public SuperDate(Date timestamp) {
        this.timestamp = timestamp;
    }

    public SuperDate(long timestamp) {
        this.timestamp = new Timestamp(timestamp);
    }

    public SuperDate(int day, int month, int year) {
        this.hasDate = true;
        this.date = day;
        this.month = month;
        this.year = year;
    }

    public SuperDate(int hours, int minutes) {
        hasTime = true;
        this.hours = hours;
        this.minutes = minutes;
    }

    public SuperDate(int day, int month, int year, int hours, int minutes) {
        this.date = day;
        this.month = month;
        this.year = year;
        this.hours = hours;
        this.minutes = minutes;
        hasTime = true;
        hasDate = true;
    }

    public SuperDate(Calendar calendar) {
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        hasDate = true;
        hasTime = true;
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
        hasDate = true;
    }

    public boolean isHasDate() {
        if (date != 0) {
            return true;
        }
        return hasDate;
    }

    public boolean isHasTime() {
        if (hours != 0) {
            return true;
        }
        return hasTime;
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
        hasTime = true;
    }

    public String getMonthStringLong() {
        return Utils.getMonthStringLong(month);
    }

    public String getSuperDateString() {

        String duedate;

        if (!isHasDate()) {
            return "Set Date";
        }

        if (Utils.isSuperDateToday(this)) {
            return "Today";
        }

        if (Utils.isSuperDateTomorrow(this)) {
            return "Tomorrow";
        }

        duedate = getDate() + ", " + getMonthStringLong()  + ", " + getYear();

        return duedate;
    }

    public  String getTimeString() {

        if (!hasTime) {
            return "00 : 00 AM";
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

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMonthString() {
        return Utils.getMonthString(month);
    }
}
