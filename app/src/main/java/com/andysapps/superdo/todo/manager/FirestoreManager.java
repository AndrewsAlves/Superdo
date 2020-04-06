package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.enums.BucketUpdateType;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.FetchUserFailureEvent;
import com.andysapps.superdo.todo.events.FetchUserSuccessEvent;
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.FetchBucketEvent;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskFailureEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskSuccessEvent;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.User;
import com.andysapps.superdo.todo.model.notification_reminders.SimpleNotification;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Andrews on 29,October,2019
 */

public class FirestoreManager {

    private static FirestoreManager ourInstance;

    public static final String TAG = "FirestoreManager";

    public static final String DB_USER = "user";
    public static final String DB_TASKS = "tasks";
    public static final String DB_HABITS = "habits";
    public static final String DB_BUCKETS = "buckets";
    public static final String DB_NOTIFICATIONS = "notifications";

    HashMap<String, Task> taskHashMap;
    HashMap<String, Bucket> bucketHashMap;

    Query taskQuery;
    Query bucketQuery;

    public User user;
    public String userId = "test_user";
    public String documentID;

    FirebaseFirestore firestore;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    private FirestoreManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        taskHashMap = new HashMap<>();
        bucketHashMap = new HashMap<>();

    }

    public static void initialise(Context context) {
        ourInstance = new FirestoreManager(context);
        ourInstance.fetchUserData(context, false);
    }

    public static void initialiseAndRegisterAlarms(Context context) {
        ourInstance = new FirestoreManager(context);
        ourInstance.fetchUserData(context, true);
    }

    public static void initialiseForNotification(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public HashMap<String, Task> getHasMapTask() {
        return taskHashMap;
    }

    public HashMap<String, Bucket> getHasMapBucket() {
        return bucketHashMap;
    }

    public static Bucket getAllTasksBucket() {
        Bucket bucket = new Bucket();
        bucket.setDocumentId("all_tasks");
        bucket.setName("All Tasks");
        bucket.setId("id_all_tasks");
        bucket.setBucketType(BucketType.Tasks.toString());
        bucket.setTagColor(BucketColors.Red.toString()); // light Red

        return bucket;
    }

    public void fetchUser() {
        firestore.collection(DB_USER).document(documentID).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot == null) {
                        return;
                    }

                    user = documentSnapshot.toObject(User.class);
                    EventBus.getDefault().post(new FetchUserSuccessEvent());
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    EventBus.getDefault().post(new FetchUserFailureEvent());
                });
    }

    public void fetchUserData(Context context, boolean registerReminders) {

        Source source = Source.DEFAULT;

        taskQuery = firestore.collection(DB_TASKS).whereEqualTo("userId", userId).whereEqualTo("deleted", false);
        bucketQuery = firestore.collection(DB_BUCKETS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("deleted", false)
                .orderBy("created", Query.Direction.DESCENDING);

        if (!registerReminders) initQuerySnapshots();

        taskQuery.get(source).addOnSuccessListener(queryDocumentSnapshots -> {

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                if (documentSnapshot == null) {
                    continue;
                }

                taskHashMap.put(documentSnapshot.getId(), documentSnapshot.toObject(Task.class));
            }

            TaskOrganiser.getInstance().organiseAllTasks();

            if (registerReminders) {
                SuperdoAlarmManager.getInstance().registerDailyNotificationsAndReminders(context);
                return;
            }

            EventBus.getDefault().post(new FetchTasksEvent(true));
        }).addOnFailureListener(e -> EventBus.getDefault().post(new FetchTasksEvent(false)));


        if (registerReminders) {
            return;
        }

        bucketQuery.get(source).addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                if (documentSnapshot == null) {
                    continue;
                }

                bucketHashMap.put(documentSnapshot.getId(), documentSnapshot.toObject(Bucket.class));
            }

            TaskOrganiser.getInstance().organiseAllTasks();
            EventBus.getDefault().post(new FetchBucketEvent(true));
        }).addOnFailureListener(e -> EventBus.getDefault().post(new FetchBucketEvent(false)));
    }

    public void initQuerySnapshots() {

        firestore.collection(DB_TASKS).whereEqualTo("userId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (queryDocumentSnapshots == null) {
                Log.e(TAG, "Query snapshot null : " + taskHashMap);
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                switch (dc.getType()) {
                    case ADDED:
                        taskHashMap.put(dc.getDocument().getId(),dc.getDocument().toObject(Task.class));
                        updateTaskList(TaskUpdateType.Added,taskHashMap.get(dc.getDocument().getId()));
                        break;
                    /*case MODIFIED:
                        if(taskHashMap.containsKey(dc.getDocument().getId())) {
                            taskHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Task.class));
                            updateTaskList(TaskUpdateType.Modified,taskHashMap.get(dc.getDocument().getId()));
                        }
                        break;*/
                  /*  case MODIFIED:
                        if(taskHashMap.containsKey(documentId)) {
                            // only the task completed task Update
                            if (taskHashMap.get(documentId).isHabitDone() != task.isHabitDone()) {
                                taskHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Task.class));
                                TaskOrganiser.getInstance().organiseAllTasks();
                                Log.e(TAG, "Query snapshot only Checking :");
                            } else {
                                taskHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Task.class));
                                updateTaskList(DocumentChange.Type.MODIFIED,taskHashMap.get(dc.getDocument().getId()));
                            }
                        }
                        break;
                    case REMOVED:
                        if(taskHashMap.containsKey(dc.getDocument().getId())) {
                            taskHashMap.remove(dc.getDocument().getId());
                            updateTaskList(DocumentChange.Type.REMOVED,taskHashMap.get(dc.getDocument().getId()));
                        }
                        break;*/
                }
            }
        });

        //////
        /// bucket query
        //////

        firestore.collection(DB_BUCKETS).whereEqualTo("userId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {

            Log.e(TAG, "Query snapshot executed  : " + queryDocumentSnapshots);

            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (queryDocumentSnapshots == null) {
                Log.e(TAG, "Query snapshot null : " + bucketHashMap);
                return;
            }

            Log.e(TAG, "Query snapshot not null : " + queryDocumentSnapshots);

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                Log.e(TAG, "Query snapshot buckets: ");

                switch (dc.getType()) {
                    case ADDED:
                        bucketHashMap.put(dc.getDocument().getId(),dc.getDocument().toObject(Bucket.class));
                        updateBucketList(BucketUpdateType.Added,bucketHashMap.get(dc.getDocument().getId()));
                        break;
                    case MODIFIED:
                        if(bucketHashMap.containsKey(dc.getDocument().getId())) {
                            bucketHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Bucket.class));
                            updateBucketList(BucketUpdateType.Modified,bucketHashMap.get(dc.getDocument().getId()));
                        }
                        break;
                    /*case REMOVED:
                        if(bucketHashMap.containsKey(dc.getDocument().getId())) {
                            bucketHashMap.remove(dc.getDocument().getId());
                            updateBucketList(DocumentChange.Type.REMOVED,bucketHashMap.get(dc.getDocument().getId()));
                        }
                        break;*/

                }
            }
        });
    }

    public void updateBucketList(BucketUpdateType change, Bucket bucket) {
        TaskOrganiser.getInstance().organiseAllTasks();
        EventBus.getDefault().post(new BucketUpdatedEvent(change, bucket));
    }

    public void updateTaskList(TaskUpdateType change, Task task) {
        TaskOrganiser.getInstance().organiseAllTasks();
        EventBus.getDefault().post(new TaskUpdatedEvent(change, task));
    }

    public void uploadTask(Task task) {

        firestore.collection(DB_TASKS).document()
                .set(task)
                .addOnSuccessListener(aVoid -> {
                    EventBus.getDefault().post(new UploadTaskSuccessEvent());
                    Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!");
                })
                .addOnFailureListener(e -> {
                    EventBus.getDefault().post(new UploadTaskFailureEvent());
                    Log.e(TAG, "Error uploading task", e);
                });
    }

    public void updateTask(Task task) {

        firestore.collection(DB_TASKS).document(task.getDocumentId())
                .set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading task", e));
    }

    public void uploadBucket(Bucket bucket) {

        firestore.collection(DB_BUCKETS).document()
                .set(bucket)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Bucket uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading bucket", e));
    }

    public void uploadNotification(SimpleNotification notification) {

        firestore.collection(DB_NOTIFICATIONS).document()
                .set(notification)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Bucket uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading bucket", e));
    }

    public void updateBucket(Bucket bucket) {

        firestore.collection(DB_BUCKETS).document(bucket.getDocumentId())
                .set(bucket)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading task", e));
    }

    ///////////////////////////////
    ////// ACTIONS
    /////////////////

    public void actionMarkCompleted(String taskDocumentId) {

        if (taskDocumentId == null) {
            return;
        }

        DocumentReference docRef = firestore.collection(DB_TASKS).document(taskDocumentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Task task1 = document.toObject(Task.class);
                        task1.setTaskCompleted(true);
                        updateTask(task1);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
