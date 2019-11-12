package com.andysapps.superdo.todo.manager;

import android.util.Log;

import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 06,November,2019
 */
public class TaskOrganiser {

    private static final String TAG = "TaskOrganiser";
    private static TaskOrganiser ourInstance = new TaskOrganiser();

    public List<Task> allTaskList;
    public List<Task> todayTaskList;
    public List<Task> tomorrowTaskList;
    public List<Task> someDayTaskList;

    public static TaskOrganiser getInstance() {
        return ourInstance;
    }

    private TaskOrganiser() {
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        someDayTaskList = new ArrayList<>();
    }

    public void organiseAllTasks() {

        allTaskList = new ArrayList<>();
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        someDayTaskList = new ArrayList<>();

        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();

        allTaskList.addAll(allTasks.values());

        if (allTasks == null) {
            return;
        }

        // Divided the tasks based on listing

        for (Task task : allTasks.values()) {

            if (task.getListedIn() == TaskListing.TODAY) {
                todayTaskList.add(task);
            }

            if (task.getListedIn() == TaskListing.SOMEDAY) {
                someDayTaskList.add(task);
            }

            if (task.getListedIn() == TaskListing.TOMORROW) {

                // add all tomorrow task to today if thier timestamp is less than current timestamp
                if (task.getDoDate().getTimestamp().getTime() < Calendar.getInstance().getTime().getTime()) {
                    task.setListedIn(TaskListing.TODAY);
                    todayTaskList.add(task);
                    FirestoreManager.getInstance().updateTask(task); // update tomorrow task to today
                } else {

                    int taskDate = task.getDoDate().getDate();
                    int todayDate = Calendar.getInstance().get(Calendar.DATE);

                    if (taskDate <= todayDate) {
                        task.setListedIn(TaskListing.TODAY);
                        todayTaskList.add(task);
                        FirestoreManager.getInstance().updateTask(task); // update tomorrow task to today
                    } else {
                        tomorrowTaskList.add(task);
                    }
                }
            }
        }

        // Sort the task by indexing

        Collections.sort(todayTaskList, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        return o1.getTaskIndex() - o2.getTaskIndex();
                    }
                });

        Collections.sort(tomorrowTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getTaskIndex() - o2.getTaskIndex();
            }
        });

        Collections.sort(someDayTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getTaskIndex() - o2.getTaskIndex();
            }
        });

        Log.e(TAG, "organiseAllTasks: tasks size : " + allTasks.size());
    }

    public List<Task> getTodayTaskList() {
        Log.e(TAG, "getTodayList: tasks size : " + todayTaskList.size());
        return todayTaskList;
    }

    public List<Task> getTasks(TaskListing listing) {

        switch (listing) {
            case TODAY:
                return todayTaskList;
            case TOMORROW:
                return tomorrowTaskList;
            case SOMEDAY:
                return someDayTaskList;
        }

        return allTaskList;
    }

    public int getTaskIndex(TaskListing listing) {

        switch (listing) {
            case TODAY:
                return todayTaskList.size();
            case TOMORROW:
                return tomorrowTaskList.size();
            case SOMEDAY:
                return someDayTaskList.size();
        }

        return allTaskList.size();
    }



}
