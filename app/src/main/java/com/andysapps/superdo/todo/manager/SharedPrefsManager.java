package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Andrews on 18,September,2019
 */

public class SharedPrefsManager {

    static final String PREF_BACKGROUND_PROCESS_STARTED = "lastSceduledTime";

    static final String PREF_SUPERDO_USER_FIRSTNAME = "user_first_name";
    static final String PREF_SUPERDO_USER_LASTNAME = "user_last_name";

    static final String DESC_ALL_TASKS = "desc_all_tasks";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void saveUserName(Context context, String firstName, String LastName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_SUPERDO_USER_FIRSTNAME, firstName);
        editor.putString(PREF_SUPERDO_USER_LASTNAME, LastName);
        editor.apply();
    }

    public static String getUserFirstName(Context context) {
        return getSharedPreferences(context).getString(PREF_SUPERDO_USER_FIRSTNAME, "Human");
    }

    public static String getUserLastName(Context context) {
        return getSharedPreferences(context).getString(PREF_SUPERDO_USER_LASTNAME, "Human");
    }

    public static void saveBackgroundRegistriesTimeStamp(Context context, long timestamp) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(PREF_BACKGROUND_PROCESS_STARTED, timestamp);
        editor.apply();
    }

    public static long getLastBackgroundRegisteredTimestamp(Context context) {
        return getSharedPreferences(context).getLong(PREF_BACKGROUND_PROCESS_STARTED, 0);
    }

    public static void deleteAllData(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }


}
