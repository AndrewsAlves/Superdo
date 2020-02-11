package com.andysapps.superdo.todo.manager;

import android.util.Log;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;

import java.util.ArrayList;
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
    public List<Task> thisWeekTaskList;
    public List<Task> thisMonthTaskList;
    public List<Task> upcomingTaskList;

    public List<Bucket> bucketList;

    public static TaskOrganiser getInstance() {
        return ourInstance;
    }

    private TaskOrganiser() {
        allTaskList = new ArrayList<>();
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        thisWeekTaskList = new ArrayList<>();
        thisMonthTaskList = new ArrayList<>();
        upcomingTaskList = new ArrayList<>();
    }

    public void organiseAllTasks() {

        allTaskList = new ArrayList<>();
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        thisWeekTaskList = new ArrayList<>();
        thisMonthTaskList = new ArrayList<>();
        upcomingTaskList = new ArrayList<>();

        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();

        if (allTasks == null) {
            return;
        }

        // Divided the tasks based on listing

        for (Task task : allTasks.values()) {

            if (task.isDeleted()) {
                continue;
            }

            allTaskList.add(task);

            if (Utils.isSuperDateToday(task.getDoDate())) {
                task.setListedIn(TaskListing.TODAY);
                todayTaskList.add(task);
            }

            // add all past tasks to today tasks
            if (Utils.isSuperDateIsPast(task.getDoDate())) {
                task.setListedIn(TaskListing.TODAY);
                todayTaskList.add(task);
            }

            if (Utils.isSuperDateTomorrow(task.getDoDate())) {
                task.setListedIn(TaskListing.TOMORROW);
                tomorrowTaskList.add(task);
            }

            if (Utils.isSuperdateThisMonth(task.getDoDate())) {
                task.setListedIn(TaskListing.THIS_MONTH);
                thisMonthTaskList.add(task);
            }

            if (Utils.isSuperdateIsUpcoming(task.getDoDate())) {
                task.setListedIn(TaskListing.UPCOMING);
                upcomingTaskList.add(task);
                //Log.e(TAG, "organiseAllTasks: task name : " + task.getName());
                //Log.e(TAG, "organiseAllTasks: index : " + task.getHabitIndex());
                //Log.e(TAG, "organiseAllTasks: Listing : UPCOMING");
            }



           /* if (task.getHabitCategory() == TaskListing.TODAY_TASKS) {
                todayTaskList.add(task);
            }

            if (task.getHabitCategory() == TaskListing.UPCOMING) {
                upcomingTaskList.add(task);
            }

            if (task.getHabitCategory() == TaskListing.TOMORROW) {

                // add all tomorrow task to today if thier timestamp is less than current timestamp
                if (task.getDoDate().getTimestamp().getTime() < Calendar.getInstance().getTime().getTime()) {
                    task.setHabitCategory(TaskListing.TODAY_TASKS);
                    todayTaskList.add(task);
                    FirestoreManager.getInstance().updateTask(task); // update tomorrow task to today
                } else {

                    int taskDate = task.getDoDate().getDate();
                    int todayDate = Calendar.getInstance().get(Calendar.DATE);

                    if (taskDate <= todayDate) {
                        task.setHabitCategory(TaskListing.TODAY_TASKS);
                        todayTaskList.add(task);
                        FirestoreManager.getInstance().updateTask(task); // update tomorrow task to today
                    } else {
                        tomorrowTaskList.add(task);
                    }
                }
            }*/
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

        Collections.sort(upcomingTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getTaskIndex() - o2.getTaskIndex();
            }
        });

        organiseBuckets();

        Log.e(TAG, "organiseAllTasks: tasks size : " + allTasks.size());
    }

    public void organiseBuckets() {

        bucketList = new ArrayList<>();

        for (Bucket bucket : FirestoreManager.getInstance().getHasMapBucket().values()) {
            if (bucket.isDeleted()) {
                continue;
            }
            bucketList.add(bucket);
        }
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
            case UPCOMING:
                return upcomingTaskList;
        }

        return allTaskList;
    }

    public int getTaskSize(TaskListing listing) {

        switch (listing) {
            case TODAY:
                return todayTaskList.size();
            case TOMORROW:
                return tomorrowTaskList.size();
            case UPCOMING:
                return upcomingTaskList.size();
        }

        return allTaskList.size();
    }

    public List<Task> getThisMonthTaskList() {
        return thisMonthTaskList;
    }

    public List<Task> getThisWeekTaskList() {
        return thisWeekTaskList;
    }

    public List<Task> getUpcomingTaskList() {
        return upcomingTaskList;
    }

    /////////////////////
    //// BUCKET TASKS

    public List<Task> getTasksInBucket(Bucket bucket) {

        List<Task> bucketList = new ArrayList<>();

        if (bucket.getDocumentId().equals("all_tasks")) {
            return allTaskList;
        }

        for (Task task : allTaskList) {
            if (task.getBucketId() != null && task.getBucketId().equals(bucket.getDocumentId())) {
                bucketList.add(task);
            }
        }

        Log.e(TAG, "organiseAllTasks: bucketList size : " + bucketList.size());

        return bucketList;
    }

    public int getBucketTasksCount(Bucket bucket) {

        int tasksCount = 0;

        if (bucket.getDocumentId().equals("all_tasks")) {
            return allTaskList.size();
        }

        for (Task task : allTaskList) {
            if (task.getBucketId() != null && task.getBucketId().equals( bucket.getDocumentId())) {
                tasksCount++;
            }
        }

        return tasksCount;
    }

    public int getBucketTasksDoneCount(Bucket bucket) {

        int tasksDoneCount = 0;

        if (bucket.getDocumentId().equals("all_tasks")) {
            for (Task task : allTaskList) {
                if (task.isTaskCompleted()) {
                    tasksDoneCount++;
                }
            }
            return tasksDoneCount;
        }

        for (Task task : allTaskList) {
            if (task.getBucketId() != null && task.getBucketId().equals( bucket.getDocumentId())) {
                if (task.isTaskCompleted()) {
                    tasksDoneCount++;
                }
            }
        }

        return tasksDoneCount;
    }

    //////////////////////////////////////
    /////// TASK UPDATES
    //////////////////////////////////////

    public void deleteBucket(Bucket bucket) {
        if (FirestoreManager.getInstance().getHasMapBucket().containsKey(bucket.getDocumentId())) {
            Bucket bucket1 = FirestoreManager.getInstance().getHasMapBucket().get(bucket.getDocumentId());
            bucket1.setDeleted(true);
            FirestoreManager.getInstance().updateBucket(bucket1);
        }
    }

    public void deleteTask(Task task) {
        if (FirestoreManager.getInstance().getHasMapTask().containsKey(task.getDocumentId())) {
            Task task1 = FirestoreManager.getInstance().getHasMapTask().get(task.getDocumentId());
            task1.setDeleted(true);
            organiseAllTasks();
            FirestoreManager.getInstance().updateTask(task1);
        }
    }
}
