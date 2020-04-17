package com.andysapps.superdo.todo.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andysapps.superdo.todo.ActionBroadcast;
import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.notification_reminders.SimpleNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import io.grpc.okhttp.internal.Util;

import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_BUCKETS;
import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_NOTIFICATIONS;
import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_TASKS;
import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_USER;
import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_notification_deadline_type;

/**
 * Created by Andrews on 26,February,2020
 */

public class SuperdoNotificationManager {

    private static final String TAG = "NotificationManager";
    private static SuperdoNotificationManager ourInstance;

    public static final String CHANNEL_DAILY = "sd_notification_recursive";

    public static final String CHANNEL_TASK = "sd_notification_tasks";
    public static final String CHANNEL_DEADLINE = "sd_notification_deadline";
    public static final String CHANNEL_REMINDER = "sd_notification_reminder";
    public static final String CHANNEL_FOCUS = "sd_notification_focus";
    public static final String CHANNEL_SUBSCRIPTIONS= "sd_notification_subscription";
    public static final String CHANNEL_RANDOM = "sd_notification_random";

    public static final String notification_id_morning = "morning";
    public static final String notification_id_afternoon = "afternoon";
    public static final String notification_id_evening = "evening";
    public static final String notification_id_night = "night";

    public static final String notification_id_remind = "remind";
    public static final String notification_id_deadline = "deadline";
    public static final String notification_id_task = "task";

    public static final String notification_id_deadline_daybefore = "deadline_day_before";
    public static final String notification_id_deadline_morning = "deadline_morning";
    public static final String notification_id_deadline_done = "deadline_done";

    private final FirebaseFirestore firestore;
    Query taskQuery;

    public static SuperdoNotificationManager getInstance() {
        return ourInstance;
    }

    private SuperdoNotificationManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        createNotificationChannels(context);
        //uploadDailyNotifications();
    }

    public static void initialise(Context context) {
        ourInstance = new SuperdoNotificationManager(context);
    }

    public static void destroy() {
        ourInstance = null;
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

    public void createNotification(Context context, Intent intent, String channelId, int smallIcon, String contentTitle, String contentText, String contentBogText, int notificationId) {

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setColor(context.getResources().getColor(R.color.lightOrange))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBogText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    public NotificationCompat.Builder getSimpleNotification(Context context, Intent intent, String channelId, int smallIcon, String contentTitle, String contentText, String contentBogText, int notificationId) {

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setColor(context.getResources().getColor(R.color.lightOrange))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBogText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        return builder;
    }

    ////////////////////////////////////////////
    ////////// NOTIFICATION AND REMINDERS
    /////////////////////////////////////////////

    public void postNotificationDaily(Context context, String notificationId) {

        ArrayList<SimpleNotification> notificationList = new ArrayList<>();

        if (!validateNotificationTime(notificationId)) {
            return;
        }

        taskQuery = firestore.collection(DB_NOTIFICATIONS).whereEqualTo("notificationId", notificationId).whereEqualTo("deleted", false);

        taskQuery.get(Source.DEFAULT).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (documentSnapshot == null) {
                        continue;
                    }

                    notificationList.add(documentSnapshot.toObject(SimpleNotification.class));
                }

                SimpleNotification notification = getSimpleNotificationForDaily(notificationList);

                createNotification(context,new Intent(context, MainActivity.class),
                        CHANNEL_DAILY,
                        R.drawable.ic_notification,
                        notification.getContentTitle(),
                        notification.getContentText(),
                        notification.getContextBigText(),
                        101);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /*public void postNotificationRemind(Context context, String taskDocId) {

        DocumentReference snapshot  = firestore.collection(DB_TASKS).document(taskDocId);
        snapshot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {

                Task remindTask;

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            remindTask = document.toObject(Task.class);

                            SimpleNotification notification = new SimpleNotification();

                            switch (RemindType.valueOf(remindTask.getRemind().getRemindType())) {
                                case REMIND_ONCE:

                                    notification = new SimpleNotification();
                                    notification.setContentTitle("Hey human, Reminder for you");
                                    notification.setContentText(remindTask.getName());
                                    notification.setContextBigText(remindTask.getName());

                                    pushRemindNotification(context, notification, remindTask.getRemind().getRemindRequestCode());

                                    break;
                                case REMIND_REPEAT:

                                    switch (RepeatType.valueOf(remindTask.getRemind().getRemindRepeat().getRepeatType())) {

                                        case Day:

                                            notification = new SimpleNotification();
                                            notification.setContentTitle("Hey human, Reminder for you");
                                            notification.setContentText(remindTask.getName());
                                            notification.setContextBigText(remindTask.getName());

                                            pushRemindNotification(context, notification, remindTask.getRemind().getRemindRequestCode());

                                            break;
                                        case Week:

                                            if (Utils.isWeeklyRepeatFallsThisDate(remindTask.getRemind().getRemindRepeat())) {
                                                notification = new SimpleNotification();
                                                notification.setContentTitle("Hey human, Weekly Reminder for you");
                                                notification.setContentText(remindTask.getName());
                                                notification.setContextBigText(remindTask.getName());

                                                pushRemindNotification(context, notification, remindTask.getRemind().getRemindRequestCode());
                                            }

                                            break;
                                        case Month:
                                            notification = new SimpleNotification();
                                            notification.setContentTitle("Hey human, Monthy Reminder for you");
                                            notification.setContentText(remindTask.getName());
                                            notification.setContextBigText(remindTask.getName());

                                            pushRemindNotification(context, notification, remindTask.getRemind().getRemindRequestCode());
                                    }

                                    break;
                            }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }*/

     public void postNotificationRemind(Context context, String taskDocId) {

         if (FirebaseAuth.getInstance().getCurrentUser() == null) {
             return;
         }

         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference snapshot  = firestore.collection(DB_USER).document(user.getUid()).collection(DB_TASKS).document(taskDocId);
        snapshot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {

                Task remindTask;

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            remindTask = document.toObject(Task.class);

                            if (Utils.isSuperDateIsPast(remindTask.getDoDate())) {
                                return;
                            }

                            SimpleNotification notification = new SimpleNotification();

                            if (Utils.isReminderMissed(remindTask.getDoDate())) {
                                notification.setContentTitle("Hey " + SharedPrefsManager.getUserFirstName(context) + ", " + "Missed reminder for you today");
                            } else {
                                notification.setContentTitle("Superdo Reminder," + SharedPrefsManager.getUserFirstName(context) + "," +  " Done with this task!");
                            }

                            notification.setContentText(remindTask.getName());
                            notification.setContextBigText(remindTask.getName());

                            pushRemindNotification(context, notification , remindTask);

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void pushRemindNotification(Context context, SimpleNotification notification, Task remindTask) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.key_taskid, remindTask.getDocumentId());

        Intent actionMarkDoneIntent = new Intent(context, ActionBroadcast.class);
        actionMarkDoneIntent.putExtra(Constants.key_action, Constants.value_action_markdone);
        actionMarkDoneIntent.putExtra(Constants.key_taskid, remindTask.getDocumentId());
        actionMarkDoneIntent.putExtra(Constants.key_notification_request_id, remindTask.getRemindRequestCode());

        PendingIntent pendingActionIntent = PendingIntent.getBroadcast(context, remindTask.getRemindRequestCode(), actionMarkDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getSimpleNotification(context,intent,
                CHANNEL_REMINDER,
                R.drawable.ic_notification_remind,
                notification.getContentTitle(),
                notification.getContentText(),
                notification.getContextBigText(),
                remindTask.getRemindRequestCode());

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notification_remind, "Mark Done", pendingActionIntent).build();
        builder.setAutoCancel(true);
        builder.addAction(action);

        Log.e(TAG, "onReceive: notification id " + remindTask.getRemindRequestCode());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(remindTask.getRemindRequestCode(), builder.build());
    }

    public void postNotificationDeadline(Context context, String taskDocId, Intent intent) {

         if (FirebaseAuth.getInstance().getCurrentUser() == null) {
             return;
         }

         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

         DocumentReference snapshot  = firestore.collection(DB_USER).document(user.getUid()).collection(DB_TASKS).document(taskDocId);
         snapshot.get().addOnCompleteListener(task -> {

            Task deadlineTask;

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        deadlineTask = document.toObject(Task.class);

                        Log.e(TAG, "postNotificationDeadline: deadline executed" );

                        SimpleNotification notification = new SimpleNotification();
                        notification.setContentText(deadlineTask.getName());
                        notification.setContextBigText(deadlineTask.getName());

                        if (intent.getExtras().getString(intent_key_notification_deadline_type) == null) {
                            return;
                        }

                        switch (intent.getExtras().getString(intent_key_notification_deadline_type)) {

                            case notification_id_deadline_morning:
                                notification.setContentTitle("Hi " + SharedPrefsManager.getUserFirstName(context) + "," + " you have a deadline today.");
                                break;
                            case notification_id_deadline_daybefore:
                                notification.setContentTitle("Hey " + SharedPrefsManager.getUserFirstName(context) + "," + " you have a deadline tomorrow");
                                break;
                            case notification_id_deadline_done:
                                notification.setContentTitle("Hey " + SharedPrefsManager.getUserFirstName(context) + "," + " Have you done your deadline task?");
                                break;
                        }

                        pushDeadlineNotification(context, notification, deadlineTask);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void pushDeadlineNotification(Context context, SimpleNotification notification, Task deadlineTask) {

        int requestCode = deadlineTask.getDeadline().getDeadlineRequestCode();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.key_taskid, deadlineTask.getDocumentId());

        Intent actionMarkDoneIntent = new Intent(context, ActionBroadcast.class);
        actionMarkDoneIntent.putExtra(Constants.key_action, Constants.value_action_markdone);
        actionMarkDoneIntent.putExtra(Constants.key_taskid, deadlineTask.getDocumentId());
        actionMarkDoneIntent.putExtra(Constants.key_notification_request_id, requestCode);

        PendingIntent pendingActionIntent = PendingIntent.getBroadcast(context, requestCode, actionMarkDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getSimpleNotification(context,intent,
                CHANNEL_DEADLINE,
                R.drawable.ic_notification_deadline,
                notification.getContentTitle(),
                notification.getContentText(),
                notification.getContextBigText(),
                requestCode);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notification_deadline, "Mark Done", pendingActionIntent).build();
        builder.setAutoCancel(true);
        builder.addAction(action);

        Log.e(TAG, "onReceive: notification id " + requestCode);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, builder.build());
    }

    public boolean validateNotificationTime(String notificationTimeZone) {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        switch (notificationTimeZone) {
            case notification_id_morning:
                if (hours >= 7 && hours <= 10){
                    return true;
                }
                break;
            case notification_id_afternoon:
                if (hours >= 12 && hours <= 16){
                    return true;
                }
                break;
            case notification_id_evening:
                if (hours >= 17 && hours <= 19){
                    return true;
                }
                break;
            case notification_id_night:
                if (hours >= 21){
                    return true;
                }
                break;
        }

        return false;
    }

    public SimpleNotification getSimpleNotificationForDaily(List<SimpleNotification> notifications) {
        Random random = new Random();
        int index = random.nextInt(notifications.size());
        return notifications.get(index);
    }


    public void uploadDailyNotifications() {
        String[] titleMorning ={"Plan", "Savour life", "Productivity"};
        String[] titleAfternoon ={"Procrastination", "Simple quote", "Half of the day"};
        String[] titleNight ={"Tommorow starts the night before ", "Plan", "Gentle suggestion"};

        String[] contentMorning ={"Failing to plan is planning to fail. Simple but its true, A goal without plan is just a wish. Click to add your first task",
                "Dont just breath it in, exhale the moment to intake the next",
                "Completing things with less time and effort. Planning a head will boost your productivity. Start with superdo "};

        String[] contentAfternoon ={"Its a form of not willing to do a minute of Work. So start with just reving your thoughts about getting things done with Superdo",
                "Amatures sit and wait for inspiration, the rest of us just get up and go to work. Start planning with superdo",
                "You are half way through your day. Have you done with half of your tasks. Click to start visualising"};

        String[] contentNight ={"Start planning your todo's, Tasks, Events, goals for tommorrow with Superdo",
                "A goal without plan is just a wish. Plan your days, weeks, months ahead to clear your path with superdo",
                "Have a goal of no screens on bed."};

        for (int i = 0; i < 3 ; i++) {
            SimpleNotification notification = new SimpleNotification();
            notification.setNotificationId(notification_id_morning);
            notification.setContentTitle(titleMorning[i]);
            notification.setContentText(contentMorning[i]);
            FirestoreManager.getInstance().uploadNotification(notification);
        }
        for (int i = 0; i < 3 ; i++) {
            SimpleNotification notification = new SimpleNotification();
            notification.setNotificationId(notification_id_afternoon);
            notification.setContentTitle(titleAfternoon[i]);
            notification.setContentText(contentAfternoon[i]);
            FirestoreManager.getInstance().uploadNotification(notification);
        }
        for (int i = 0; i < 3 ; i++) {
            SimpleNotification notification = new SimpleNotification();
            notification.setNotificationId(notification_id_night);
            notification.setContentTitle(titleNight[i]);
            notification.setContentText(contentNight[i]);
            FirestoreManager.getInstance().uploadNotification(notification);
        }
    }
}
