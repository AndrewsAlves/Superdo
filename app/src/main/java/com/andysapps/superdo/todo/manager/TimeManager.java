package com.andysapps.superdo.todo.manager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrews on 17,October,2019
 */

public class TimeManager {

    public static Date getTime() {
        return Calendar.getInstance().getTime();
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

}
