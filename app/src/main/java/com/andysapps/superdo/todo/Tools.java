package com.andysapps.superdo.todo;

import java.util.Calendar;

/**
 * Created by Admin on 24,April,2020
 */
public class Tools {

    public static Calendar get30SecondsLessCalender() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -1);

        return c;
    }
}
