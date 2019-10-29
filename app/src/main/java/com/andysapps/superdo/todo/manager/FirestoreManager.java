package com.andysapps.superdo.todo.manager;

import android.content.Context;

import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;

import java.util.List;

/**
 * Created by Admin on 29,October,2019
 */
public class FirestoreManager {

    private static FirestoreManager ourInstance;

    List<Bucket> bucketList;
    List<Task> taskList;

    public static FirestoreManager getInstance() {
        return ourInstance;
    }

    private FirestoreManager(Context context) {
    }

    public static void intialize(Context context) {
        ourInstance = new FirestoreManager(context);
    }

    public List getAllTasks() {
        return taskList;
    }

}
