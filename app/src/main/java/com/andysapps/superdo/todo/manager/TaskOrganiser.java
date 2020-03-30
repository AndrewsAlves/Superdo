package com.andysapps.superdo.todo.manager;

import android.util.Log;

import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.grpc.okhttp.internal.Util;

/**
 * Created by Andrews on 06,November,2019
 */
public class TaskOrganiser {

    private static final String TAG = "TaskOrganiser";
    private static TaskOrganiser ourInstance = new TaskOrganiser();

    public List<Task> allTaskList;
    public List<Task> todayTaskList;
    public List<Task> tomorrowTaskList;
    public List<Task> weekTaskList;
    public List<Task> monthTaskList;
    public List<Task> upcomingTaskList;

    /// profile list
    public List<Task> completedTaskList;
    public List<Task> missedTaskList;
    public List<Task> recurringTask;
    public List<Task> deadlineTasks;
    public List<Task> remindingTasks;
    public List<Task> pendingTaskList;
    public List<Task> deletedTaskList;

    public List<Bucket> bucketList;

    public static TaskOrganiser getInstance() {
        return ourInstance;
    }

    private TaskOrganiser() {
        allTaskList = new ArrayList<>();
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        weekTaskList = new ArrayList<>();
        monthTaskList = new ArrayList<>();
        upcomingTaskList = new ArrayList<>();

        completedTaskList = new ArrayList<>();
        missedTaskList = new ArrayList<>();
        recurringTask = new ArrayList<>();
        deadlineTasks = new ArrayList<>();
        remindingTasks = new ArrayList<>();
        pendingTaskList = new ArrayList<>();
        deletedTaskList = new ArrayList<>();
    }

    public void organiseAllTasks() {

        allTaskList = new ArrayList<>();
        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        weekTaskList = new ArrayList<>();
        monthTaskList = new ArrayList<>();
        upcomingTaskList = new ArrayList<>();

        completedTaskList = new ArrayList<>();
        missedTaskList = new ArrayList<>();
        recurringTask = new ArrayList<>();
        deadlineTasks = new ArrayList<>();
        remindingTasks = new ArrayList<>();
        pendingTaskList = new ArrayList<>();
        deletedTaskList = new ArrayList<>();

        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();

        if (allTasks == null) {
            return;
        }

        // Divided the tasks based on listing

        for (Task task : allTasks.values()) {

            if (task.isDeleted()) {
                continue;
            }

            if (task.isMovedToBin()) {
                deletedTaskList.add(task);
                continue;
            }

            allTaskList.add(task);

            if(task.getDeadline() != null) {
                deadlineTasks.add(task);
            }

            if(task.isToRemind()) {
                remindingTasks.add(task);
            }

            /*if (Utils.shouldAddTaskRepeat(task)) {
                task.setListedIn(TaskListing.TODAY);
                todayTaskList.add(task);
                continue;
            } else if (task.getRepeat() != null) {
                continue;
            }*/

            if (task.getRepeat() != null) {
                Utils.setNextDoDate(task);
                recurringTask.add(task);
                if (Utils.isSuperDateToday(task.getDoDate())) {
                    todayTaskList.add(task);
                }
                continue;
            }

            if (task.isTaskCompleted()) {
                completedTaskList.add(task);
                continue;
            }

            pendingTaskList.add(task);

            if (Utils.isSuperDateToday(task.getDoDate())) {
                task.setListedIn(TaskListing.TODAY);
                todayTaskList.add(task);
                continue;
            }

            // add all past tasks to today tasks
            if (Utils.isSuperDateIsPast(task.getDoDate())) {
                task.setListedIn(TaskListing.TODAY);
                todayTaskList.add(task);
                missedTaskList.add(task);
                continue;
            }

            if (Utils.isSuperDateTomorrow(task.getDoDate())) {
                task.setListedIn(TaskListing.TOMORROW);
                tomorrowTaskList.add(task);
                continue;
            }

            if (Utils.isSuperdateThisWeek(task.getDoDate())) {
                task.setListedIn(TaskListing.THIS_WEEK);
                weekTaskList.add(task);
                continue;
            }

            if (Utils.isSuperdateThisMonth(task.getDoDate())) {
                task.setListedIn(TaskListing.THIS_MONTH);
                monthTaskList.add(task);
                continue;
            }

            if (Utils.isSuperdateIsUpcoming(task.getDoDate())) {
                task.setListedIn(TaskListing.UPCOMING);
                upcomingTaskList.add(task);
            }
        }

        // Sort the task by indexing

        sortTasks();
        organiseBuckets();

        Log.e(TAG, "organiseAllTasks: tasks size : " + allTasks.size());
    }

    public void sortTasks() {
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

        Collections.sort(weekTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getTaskIndex() - o2.getTaskIndex();
            }
        });

        Collections.sort(monthTaskList, new Comparator<Task>() {
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

        Collections.sort(completedTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o2.getTaskCompletedDate().compareTo(o1.getTaskCompletedDate());
            }
        });

    }

    public void organiseBuckets() {

        bucketList = new ArrayList<>();

        for (Bucket bucket : FirestoreManager.getInstance().getHasMapBucket().values()) {
            if (bucket.getDeleted()) {
                continue;
            }
            bucketList.add(bucket);
        }

       Collections.sort(bucketList, new Comparator<Bucket>() {
           @Override
           public int compare(Bucket o1, Bucket o2) {
               return o1.getCreated().compareTo(o2.getCreated());
           }
       });
    }

    public List<Task> getCompletedTaskList() {
        return completedTaskList;
    }

    public List<Task> getMissedTaskList() {
        return missedTaskList;
    }

    public List<Task> getPendingTaskList() {
        return pendingTaskList;
    }

    public List<Task> getDeletedTaskList() {
        return deletedTaskList;
    }

    public List<Task> getRecurringTask() {
        return recurringTask;
    }

    public List<Task> getDeadlineTasks() {
        return deadlineTasks;
    }

    public List<Task> getRemindingTasks() {
        return remindingTasks;
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
            case THIS_WEEK:
                return weekTaskList.size();
            case THIS_MONTH:
                return monthTaskList.size();
            case UPCOMING:
                return upcomingTaskList.size();
        }

        return allTaskList.size();
    }

    public List<Task> getMonthTaskList() {
        return monthTaskList;
    }

    public List<Task> getWeekTaskList() {
        return weekTaskList;
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
            if (task.getBucket() != null && task.getBucket().getId().equals(bucket.getId())) {
                bucketList.add(task);
            }
        }

        Log.e(TAG, "organiseAllTasks: bucketList size : " + bucketList.size());

        return bucketList;
    }

    public int getBucketTasksCount(Bucket bucket) {

        int tasksCount = 0;

        if (bucket == null || bucket.getDocumentId().equals("all_tasks")) {
            return allTaskList.size();
        }

        for (Task task : allTaskList) {
            if (task.getBucket() != null && task.getBucket().getId().equals( bucket.getId())) {
                tasksCount++;
            }
        }

        return tasksCount;
    }

    public int getBucketTasksDoneCount(Bucket bucket) {

        int tasksDoneCount = 0;

        if (bucket == null || bucket.getDocumentId().equals("all_tasks")) {
            for (Task task : allTaskList) {
                if (task.isTaskCompleted()) {
                    tasksDoneCount++;
                }
            }
            return tasksDoneCount;
        }

        for (Task task : allTaskList) {
            if (task.getBucket() != null && task.getBucket().getId().equals(bucket.getId())) {
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

            for (Task task : FirestoreManager.getInstance().getHasMapTask().values()) {
                if (task.getBucketId() != null) {
                    if (task.getBucketId().equals(bucket1.getId())) {
                        task.setDeleted(true);
                        FirestoreManager.getInstance().updateTask(task);
                    }
                }
            }

            organiseAllTasks();
            FirestoreManager.getInstance().updateBucket(bucket1);
        }
    }

    public void moveToBin(Task task) {
        if (FirestoreManager.getInstance().getHasMapTask().containsKey(task.getDocumentId())) {
            Task task1 = FirestoreManager.getInstance().getHasMapTask().get(task.getDocumentId());
            if (task1 != null) {
                task1.setMovedToBin(true);
                organiseAllTasks();
                FirestoreManager.getInstance().updateTask(task1);
            }
        }
    }

    public void permanentDeleteTask(Task task) {
        if (FirestoreManager.getInstance().getHasMapTask().containsKey(task.getDocumentId())) {
            Task task1 = FirestoreManager.getInstance().getHasMapTask().get(task.getDocumentId());
            if (task1 != null) {
                task1.setDeleted(true);
                organiseAllTasks();
                FirestoreManager.getInstance().updateTask(task1);
            }
        }
    }
}
