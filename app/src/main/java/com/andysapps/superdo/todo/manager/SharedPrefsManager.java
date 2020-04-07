package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Andrews on 18,September,2019
 */

public class SharedPrefsManager {

    static final String PREF_SUPERDO_USER_ID = "user_id";
    static final String PREF_SUPERDO_USER_DOCUMENT_ID = "user_document_id";

    static final String DESC_ALL_TASKS = "desc_all_tasks";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(PREF_SUPERDO_USER_ID, "null user");
    }

    public static String getUserDocumentId(Context context) {
        return getSharedPreferences(context).getString(PREF_SUPERDO_USER_DOCUMENT_ID, "null user");
    }

    public static void saveUserId(Context context, String userId, String documentId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_SUPERDO_USER_ID, userId);
        editor.putString(PREF_SUPERDO_USER_DOCUMENT_ID, documentId);
        editor.apply();
    }

    public static String getDescAllTasks(Context context) {
        return getSharedPreferences(context).getString(DESC_ALL_TASKS, "Here you will see All of your tasks :)");
    }

    public static void saveDesc(Context context, String desc) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DESC_ALL_TASKS, desc);
        editor.apply();
    }

}
