package com.andysapps.superdo.todo.model;

import com.andysapps.superdo.todo.Utils;

import java.sql.Timestamp;
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
}
