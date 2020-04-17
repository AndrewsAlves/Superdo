package com.andysapps.superdo.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager;

import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_notification_id;
import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_task_id;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.CHANNEL_DAILY;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.CHANNEL_REMINDER;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_afternoon;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_evening;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_morning;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_night;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_remind;

/**
 * Created by Andrews on 17,February,2020
 */

public class BackgroundBroadcast extends BroadcastReceiver {

    private static final String TAG = "NotificationBroadcast";

    public static final String key_id_background_process = "key_background_process";
    public static final String id_process_register_daily_alarms = "background_process_register_alarms";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SuperdoAlarmManager.getInstance() == null) {
            SuperdoAlarmManager.initialise(context);
        }

        if (intent.getExtras() == null) {
            return;
        }

        String bgProcessId = intent.getExtras().getString(key_id_background_process);

        if (bgProcessId!= null) {
            if (bgProcessId.equals(id_process_register_daily_alarms)) {

                if (TaskOrganiser.getInstance() == null) {
                    TaskOrganiser.initialise();
                }

                SuperdoNotificationManager.initialise(context);
                SuperdoAlarmManager.initialise(context);
                SuperdoAlarmManager.getInstance().registerBackgroundProcesses(context, true);

                if (FirestoreManager.getInstance() == null) {
                    FirestoreManager.initialiseAndRegisterAlarms(context);
                } else {
                    SuperdoAlarmManager.getInstance().registerDailyNotificationsAndReminders(context);
                }

                SuperdoNotificationManager.getInstance().createNotification(context,new Intent(context, MainActivity.class),
                        CHANNEL_REMINDER,
                        R.drawable.ic_notification,
                        "Registering reminders",
                        "Tap to view your tasks!",
                        "",
                        101);
            }
        }
    }
}
