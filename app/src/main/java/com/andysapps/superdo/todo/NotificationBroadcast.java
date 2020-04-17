package com.andysapps.superdo.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;

import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_notification_id;
import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_task_id;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_afternoon;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_evening;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_morning;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_night;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_remind;

/**
 * Created by Andrews on 17,February,2020
 */

public class NotificationBroadcast extends BroadcastReceiver {
    private static final String TAG = "NotificationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SuperdoAlarmManager.getInstance() == null) {
            SuperdoAlarmManager.initialise(context);
        }

        if (intent.getExtras() == null) {
            return;
        }

        SuperdoNotificationManager.initialise(context);

        if (FirestoreManager.getInstance() == null) {
            FirestoreManager.initialiseForNotification(context);
        }

        String notificationId = intent.getExtras().getString(intent_key_notification_id);

        if (notificationId!= null) {

            if (notificationId.equals(notification_id_morning)
                    || notificationId.equals(notification_id_evening)
                    || notificationId.equals(notification_id_afternoon)
                    || notificationId.equals(notification_id_night)) {

                SuperdoNotificationManager.getInstance().postNotificationDaily(context, intent.getExtras().getString(intent_key_notification_id));

            } else if (intent.getExtras().getString(intent_key_notification_id).equals(notification_id_remind)) {
                SuperdoNotificationManager.getInstance().postNotificationRemind(context, intent.getExtras().getString(intent_key_task_id));
                Log.e(TAG, "onReceive: Notification Remind triggered");
            } else if (intent.getExtras().getString(intent_key_notification_id).equals(notification_id_deadline)) {
                SuperdoNotificationManager.getInstance().postNotificationDeadline(context, intent.getExtras().getString(intent_key_task_id), intent);
                Log.e(TAG, "onReceive: Notification Deadline triggered");
            }

        }

    }
}
