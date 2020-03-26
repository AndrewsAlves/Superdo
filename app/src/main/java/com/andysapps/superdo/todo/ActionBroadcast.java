package com.andysapps.superdo.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SuperdoAudioManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager;

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

public class ActionBroadcast extends BroadcastReceiver {

    private static final String TAG = "NotificationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() == null) {
            return;
        }

        if (FirestoreManager.getInstance() == null) {
            FirestoreManager.initialiseForNotification(context);
        }

        if (SuperdoAudioManager.getInstance() == null) {
            SuperdoAudioManager.init(context);
        }

        String actionValue = intent.getExtras().getString(Constants.key_action);
        String taskDocumentId = intent.getStringExtra(Constants.key_taskid);

        if (actionValue!= null) {

            Log.e(TAG, "onReceive: task documentID " + taskDocumentId);
            Log.e(TAG, "onReceive: NotificationID " + intent.getExtras().getInt(Constants.key_notification_request_id, -1));

            FirestoreManager.getInstance().actionMarkCompleted(taskDocumentId);
            SuperdoAudioManager.getInstance().playTaskCompleted();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(intent.getExtras().getInt(Constants.key_notification_request_id, -1));
        }

    }
}
