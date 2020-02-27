package com.andysapps.superdo.todo.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.notification_reminders.SimpleNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_NOTIFICATIONS;
import static com.andysapps.superdo.todo.manager.FirestoreManager.DB_TASKS;

/**
 * Created by Andrews on 26,February,2020
 */
public class NotificationManager {

    private static NotificationManager ourInstance;

    public static final String notification_id_morning = "morning";
    public static final String notification_id_afternoon = "afternoon";
    public static final String notification_id_evening = "evening";
    public static final String notification_id_night = "night";

    private final FirebaseFirestore firestore;
    Query taskQuery;

    public static NotificationManager getInstance() {
        return ourInstance;
    }

    private NotificationManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        //uploadDailyNotifications();
    }

    public static void initialise(Context context) {
        ourInstance = new NotificationManager(context);
    }

    public void createNotification(Context context, String channelId, String contentTitle, String contentText, String contentBogText) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_superdo)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBogText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(101, builder.build());
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

                SimpleNotification notification = getSimpleNotification(notificationList);

                createNotification(context, SuperdoAlarmManager.CHANNEL_DAILY,
                        notification.getContentTitle(),
                        notification.getContentText(),
                        notification.getContextBigText());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

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

    public SimpleNotification getSimpleNotification(List<SimpleNotification> notifications) {
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
