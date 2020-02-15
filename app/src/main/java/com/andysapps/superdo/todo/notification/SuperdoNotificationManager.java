package com.andysapps.superdo.todo.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.activity.MainActivity;

import java.util.UUID;

public class SuperdoNotificationManager {

    private static SuperdoNotificationManager ourInstance;

    public static final String CHANNEL_TASK = "sd_notification_tasks";
    public static final String CHANNEL_DAILY = "sd_notification_recursive";
    public static final String CHANNEL_DEADLINE = "sd_notification_deadline";
    public static final String CHANNEL_REMINDER = "sd_notification_reminder";
    public static final String CHANNEL_FOCUS = "sd_notification_focus";
    public static final String CHANNEL_SUBSCRIPTIONS= "sd_notification_subscription";
    public static final String CHANNEL_RANDOM = "sd_notification_random";

    public static SuperdoNotificationManager getInstance() {
        return ourInstance;
    }

    private SuperdoNotificationManager(Context context) {
        this.createNotificationChannels(context);
    }

    public static void initialise(Context context) {
        ourInstance = new SuperdoNotificationManager(context);
    }

    public void createNotification(Context context, String channelId) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_superdo)
                .setContentTitle("Superdo notification")
                .setContentText("Starting your day right can increase your productivity")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Start planning with superdo and leverage your morning."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(101, builder.build());
    }

    public void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_DAILY, "Daily", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            for (int i = 1 ; i <= 7 ; i++) {
                switch (i) {
                    case 1:
                        channel = new NotificationChannel(CHANNEL_DAILY, "Daily", importance);
                        break;
                    case 2:
                        channel = new NotificationChannel(CHANNEL_TASK, "Tasks", importance);
                        break;
                    case 3:
                        channel = new NotificationChannel(CHANNEL_DEADLINE, "Deadline", importance);
                        break;
                    case 4:
                        channel = new NotificationChannel(CHANNEL_REMINDER, "Reminders", importance);
                        break;
                    case 5:
                        channel = new NotificationChannel(CHANNEL_FOCUS, "Focus", importance);
                        break;
                    case 6:
                        channel = new NotificationChannel(CHANNEL_SUBSCRIPTIONS, "Subscription", importance);
                        break;
                    case 7:
                        channel = new NotificationChannel(CHANNEL_RANDOM, "Random", importance);
                        break;
                }
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
