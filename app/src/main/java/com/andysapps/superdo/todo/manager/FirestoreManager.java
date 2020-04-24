package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.enums.BucketUpdateType;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.CreateOrUpdateUserFailureEvent;
import com.andysapps.superdo.todo.events.CreateOrUpdateUserSuccessEvent;
import com.andysapps.superdo.todo.events.FetchUserFailureEvent;
import com.andysapps.superdo.todo.events.FetchUserSuccessEvent;
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.FetchBucketEvent;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.User;
import com.andysapps.superdo.todo.model.notification_reminders.SimpleNotification;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;

/**
 * Created by Andrews on 29,October,2019
 */

public class FirestoreManager {

    public static FirestoreManager ourInstance;

    public static final String TAG = "FirestoreManager";

    public static final String DB_USER = "users";
    public static final String DB_TASKS = "tasks";
    public static final String DB_HABITS = "habits";
    public static final String DB_BUCKETS = "buckets";
    public static final String DB_NOTIFICATIONS = "notifications";

    HashMap<String, Task> taskHashMap;
    HashMap<String, Bucket> bucketHashMap;

    Query taskQuery;
    Query bucketQuery;

    public User user;

    FirebaseFirestore firestore;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    public static void destroy() {
        ourInstance = null;
    }

    private FirestoreManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        taskHashMap = new HashMap<>();
        bucketHashMap = new HashMap<>();
    }

    public static void initialise(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public static void initialiseAndRegisterAlarms(Context context) {
        ourInstance = new FirestoreManager(context);
        ourInstance.fetchUser(context, true);
    }

    public static void initialiseForNotification(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public void clearUserData() {
        taskHashMap.clear();
        taskHashMap = null;
        bucketHashMap.clear();
        bucketHashMap = null;
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
        bucket.setBucketIcon(0);
        bucket.setTagColor(BucketColors.Red.toString()); // light Red

        return bucket;
    }

    public Bucket getDefaultPersonalbucket() {
        Bucket bucket = new Bucket();
        bucket.setDocumentId(user.getUserId());
        if (user.getFirstName() != null) {
            bucket.setName(user.getFirstName() + " Tasks");
        } else {
            bucket.setName("Personal");
        }
        bucket.setId(user.getUserId());
        bucket.setBucketIcon(BucketType.personal.getValue());
        bucket.setTagColor(BucketColors.Red.toString()); // light Red

        return bucket;
    }

    public void fetchUser(Context context, boolean remindAlarms) {
        Log.e(TAG, "fetchUser() called user id " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        firestore.collection(DB_USER).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {

                    Log.d(TAG, "fetchUser() called");

                    if (!task.isSuccessful()) {
                        Log.d(TAG, "fetchUser() called is not successful");
                        EventBus.getDefault().post(new FetchUserFailureEvent());
                        return;
                    }

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot == null) {
                        EventBus.getDefault().post(new FetchUserFailureEvent());
                        return;
                    }

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "fetchUser() called is successful");
                        user = documentSnapshot.toObject(User.class);

                        fetchUserData(context, remindAlarms);
                        EventBus.getDefault().post(new FetchUserSuccessEvent());
                    } else {
                        EventBus.getDefault().post(new FetchUserFailureEvent());
                    }

                });
    }

    public CollectionReference getUserTaskCollection() {
        return firestore.collection(DB_USER)
                .document(user.getUserId())
                .collection(DB_TASKS);
    }

    public CollectionReference getUserBucketCollection() {
        return firestore.collection(DB_USER)
                .document(user.getUserId())
                .collection(DB_BUCKETS);
    }

    public void fetchUserData(Context context, boolean registerReminders) {

        Source source = Source.SERVER;

        Log.e(TAG, "fetchUserData() called with: user id " + user.getUserId());

        taskQuery = getUserTaskCollection().whereEqualTo("deleted", false);

        bucketQuery = getUserBucketCollection().whereEqualTo("deleted", false)
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

        getUserTaskCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {

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
                        taskHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Task.class));
                        updateTaskList(TaskUpdateType.Added, taskHashMap.get(dc.getDocument().getId()));
                        break;
                }
            }
        });

        //////
        /// bucket query
        //////

        getUserBucketCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {

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

    public void createOrUpdateUser(User user) {
        firestore.collection(DB_USER).document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    this.user = user;
                    fetchUserData(null, false);
                    EventBus.getDefault().post(new CreateOrUpdateUserSuccessEvent());
                })
                .addOnFailureListener(e -> {
                    EventBus.getDefault().post(new CreateOrUpdateUserFailureEvent());
                });
    }

    public void updateUser(User user) {
        firestore.collection(DB_USER).document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    EventBus.getDefault().post(new CreateOrUpdateUserSuccessEvent());
                })
                .addOnFailureListener(e -> {
                    EventBus.getDefault().post(new CreateOrUpdateUserFailureEvent());
                });
    }

    public String uploadTask(Task task) {
        String id = getUserTaskCollection().document().getId();
        task.setDocumentId(id);
        getUserTaskCollection().document(id).set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading task", e));
        return id;
    }

    public void updateTask(Task task) {
        Log.d(TAG, "updateTask() called with: task = [" + task + "]");
        getUserTaskCollection().document(task.getDocumentId())
                .set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating task", e));
    }

    public void uploadBucket(Bucket bucket) {
        getUserBucketCollection().document()
                .set(bucket)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Bucket uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading bucket", e));
    }

    public void updateBucket(Bucket bucket) {
       getUserBucketCollection()
                .document(bucket.getDocumentId())
                .set(bucket)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading task", e));
    }

    public void uploadNotification(SimpleNotification notification) {

        firestore.collection(DB_NOTIFICATIONS).document()
                .set(notification)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot : Bucket uploadedAccount successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading bucket", e));

    }

    ///////////////////////////////
    ////// ACTIONS
    /////////////////

    public void actionMarkCompleted(String taskDocumentId) {

        if (taskDocumentId == null) {
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = firestore.collection(DB_USER).document(user.getUid()).collection(DB_TASKS).document(taskDocumentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Task task1 = document.toObject(Task.class);
                        task1.setTaskAction(true);
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
