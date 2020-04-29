package com.andysapps.superdo.todo;

import android.content.Context;

import com.andysapps.superdo.todo.model.Task;

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

    public static String getSnackBarString(Context context, Task task) {
        if (task.getRepeat() != null) {
            return "Done for today, Repeats " + task.getDoDate().getSuperDateString();
        } else {
            return context.getString(R.string.snackbar_taskcompleted);
        }
    }



}
