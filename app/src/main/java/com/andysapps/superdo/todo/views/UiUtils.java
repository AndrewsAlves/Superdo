package com.andysapps.superdo.todo.views;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Admin on 19,March,2020
 */
public class UiUtils {

    public static int getDips(Context context, int dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dps,
                context.getResources().getDisplayMetrics());
    }
}
