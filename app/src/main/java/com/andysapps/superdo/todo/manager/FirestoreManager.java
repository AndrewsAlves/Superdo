package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.enums.BucketUpdateType;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.FetchBucketEvent;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskFailureEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskSuccessEvent;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.core.OrderBy;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by Andrews on 29,October,2019
 */

public class FirestoreManager {

    private static FirestoreManager ourInstance;

    public static final String TAG = "FirestoreManager";

    public static final String DB_USER = "user";
    public static final String DB_TASKS = "tasks";
    public static final String DB_BUCKETS = "buckets";

    HashMap<String, Task> taskHashMap;
    HashMap<String, Bucket> bucketHashMap;

    Query taskQuery;
    Query bucketQuery;

    public String userId = "test_user";

    FirebaseFirestore firestore;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    private FirestoreManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        fetchUserData();
        taskHashMap = new HashMap<>();
        bucketHashMap = new HashMap<>();
    }

    public static void intialize(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public HashMap<String, Task> getHasMapTask() {
        return taskHashMap;
    }

    public HashMap<String, Bucket> getHasMapBucket() {
        return bucketHashMap;
    }

    public static Bucket getAllTasksBucket(Context context) {
        Bucket bucket = new Bucket();
        bucket.setDocumentId("all_tasks");
        bucket.setName("All Tasks");
        bucket.setBucketType(BucketType.Tasks.toString());
        bucket.setDescription(SharedPrefsManager.getDescAllTasks(context));
        bucket.setTagColor(BucketColors.Red.toString()); // light Red

        return bucket;
    }

    public void fetchUserData() {

        Source source = Source.DEFAULT;

        taskQuery = firestore.collection(DB_TASKS).whereEqualTo("userId", userId).whereEqualTo("isDeleted", false);
        bucketQuery = firestore.collection(DB_BUCKETS).whereEqualTo("userId", userId).whereEqualTo("isDeleted", false).orderBy("created", Query.Direction.DESCENDING);

        initQuerySnapshots();

        taskQuery.get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (documentSnapshot == null) {
                        continue;
                    }

                    taskHashMap.put(documentSnapshot.getId(), documentSnapshot.toObject(Task.class));
                }

                TaskOrganiser.getInstance().organiseAllTasks();
                EventBus.getDefault().post(new FetchTasksEvent(true));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                EventBus.getDefault().post(new FetchTasksEvent(false));
            }
        });

        bucketQuery.get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (documentSnapshot == null) {
                        continue;
                    }

                    bucketHashMap.put(documentSnapshot.getId(), documentSnapshot.toObject(Bucket.class));
                }

                TaskOrganiser.getInstance().organiseAllTasks();
                EventBus.getDefault().post(new FetchBucketEvent(true));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                EventBus.getDefault().post(new FetchBucketEvent(false));
            }
        });
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
                            // only the task completed task update
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new UploadTaskSuccessEvent());
                        Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new UploadTaskFailureEvent());
                        Log.e(TAG, "Error uploading task", e);
                    }
                });
    }

    public void updateTask(Task task) {

        firestore.collection(DB_TASKS).document(task.getDocumentId())
                .set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error uploading task", e);
                    }
                });
    }

    public void uploadBucket(Bucket bucket) {

        firestore.collection(DB_BUCKETS).document()
                .set(bucket)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot : Bucket uploadedAccount successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error uploading bucket", e);
                    }
                });
    }

    public void updateBucket(Bucket bucket) {

        firestore.collection(DB_BUCKETS).document(bucket.getDocumentId())
                .set(bucket)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot : Task uploadedAccount successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error uploading task", e);
                    }
                });
    }
}
