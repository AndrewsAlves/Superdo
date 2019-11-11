package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andysapps.superdo.todo.events.firestore.FetchUserDataFailureEvent;
import com.andysapps.superdo.todo.events.firestore.FetchUserDataSuccessEvent;
import com.andysapps.superdo.todo.events.firestore.NotifyDataUpdate;
import com.andysapps.superdo.todo.events.firestore.UploadTaskFailureEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskSuccessEvent;
import com.andysapps.superdo.todo.events.ui.TaskAddedEvent;
import com.andysapps.superdo.todo.events.ui.TaskDeletedEvent;
import com.andysapps.superdo.todo.events.ui.TaskModifedEvent;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    List<Bucket> bucketList;
    List<Task> taskList;

    public String userId = "test_user";

    FirebaseFirestore firestore;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    private FirestoreManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
        fetchUserData();
        taskHashMap = new HashMap<>();
    }

    public static void intialize(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public HashMap<String, Task> getHasMapTask() {
        return taskHashMap;
    }

    public static Bucket getAllTasksBucket(Context context) {
        Bucket bucket = new Bucket();
        bucket.setName("All Tasks");
        bucket.setDescription(SharedPrefsManager.getDescAllTasks(context));
        bucket.setTagColor("#F64F59"); // light red

        return bucket;
    }

    public void fetchUserData() {

        Source source = Source.DEFAULT;

        Query queryTask = firestore.collection(DB_TASKS).whereEqualTo("userId", userId);

        queryTask.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots == null) {
                    Log.e(TAG, "Query snapshot null : " + taskList);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()) {
                        case ADDED:
                            taskHashMap.put(dc.getDocument().getId(),dc.getDocument().toObject(Task.class));
                            TaskOrganiser.getInstance().organiseAllTasks();
                            EventBus.getDefault().post(new TaskAddedEvent(taskHashMap.get(dc.getDocument().getId())));
                            Log.d(TAG, "New Request: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            if(taskHashMap.containsKey(dc.getDocument().getId())) {
                                taskHashMap.put(dc.getDocument().getId(), dc.getDocument().toObject(Task.class));
                                TaskOrganiser.getInstance().organiseAllTasks();
                                EventBus.getDefault().post(new TaskModifedEvent(taskHashMap.get(dc.getDocument().getId())));
                            }
                            Log.d(TAG, "Modified Request: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            if(taskHashMap.containsKey(dc.getDocument().getId())) {
                                taskHashMap.remove(dc.getDocument().getId());
                                TaskOrganiser.getInstance().organiseAllTasks();
                                EventBus.getDefault().post(new TaskDeletedEvent(dc.getDocument().toObject(Task.class)));
                            }
                            Log.d(TAG, "Removed Request: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });

        queryTask.get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                taskList = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (documentSnapshot == null) {
                        continue;
                    }

                    taskHashMap.put(documentSnapshot.getId(), documentSnapshot.toObject(Task.class));
                    taskList.add(documentSnapshot.toObject(Task.class));
                }

                TaskOrganiser.getInstance().organiseAllTasks();
                EventBus.getDefault().post(new FetchUserDataSuccessEvent());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                EventBus.getDefault().post(new FetchUserDataFailureEvent());
            }
        });

        Query queryBucket = firestore.collection(DB_BUCKETS).whereEqualTo("userId", userId);

        queryBucket.get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                TaskOrganiser.getInstance().organiseAllTasks();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

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

    public void updateTaskComepleted() {

    }

}
