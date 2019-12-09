package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Andrews on 12,November,2019
 */
public class SuperDate {

    public int hours;
    public int minutes;

    public int date;
    public int month;
    public int year;

    public boolean hasDate;
    public boolean hasTime;

    Date timestamp;

    public SuperDate() {

    }

    public SuperDate(Date timestamp) {
        this.timestamp = timestamp;
    }

    public SuperDate(long timestamp) {
        this.timestamp = new Timestamp(timestamp);
    }

    public SuperDate(int day, int month, int year) {
        this.date = day;
        this.month = month;
        this.year = year;
    }

    public SuperDate(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
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

    public String getMonthStringLong() {
        return Utils.getMonthStringLong(month);
    }

    public String getSuperDateString() {

        String duedate;

        if (!hasDate) {
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
