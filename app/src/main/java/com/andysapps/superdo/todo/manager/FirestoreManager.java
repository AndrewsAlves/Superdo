package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.andysapps.superdo.todo.events.firestore.UploadTaskFailureEvent;
import com.andysapps.superdo.todo.events.firestore.UploadTaskSuccessEvent;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrews on 29,October,2019
 */

public class FirestoreManager {

    private static FirestoreManager ourInstance;

    public static final String TAG = "FirestoreManager";

    public static final String DB_USER = "user";
    public static final String DB_TASKS = "tasks";
    public static final String DB_BUCKETS = "buckets";

    List<Bucket> bucketList;
    List<Task> taskList;

    public String userId;

    FirebaseFirestore firestore;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    private FirestoreManager(Context context) {
        firestore =  FirebaseFirestore.getInstance();
    }

    public static void intialize(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    public static Bucket getAllTasksBucket(Context context) {
        Bucket bucket = new Bucket();
        bucket.setName("All Tasks");
        bucket.setDescription(SharedPrefsManager.getDescAllTasks(context));
        bucket.setTagColor("#F64F59"); // light red

        return bucket;
    }

    public void fetchUserTasks() {

        Source source = Source.DEFAULT;

        Query query = firestore.collection(DB_USER).whereEqualTo("userId", userId);

        query.get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

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

}
