package com.andysapps.superdo.todo.manager;

import android.util.Log;

import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.EspritStatType;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.esprit.EspritStatPoint;
import com.andysapps.superdo.todo.model.esprit.EspritStatistics;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.hadiidbouk.charts.BarData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrews on 06,November,2019
 */
public class TaskOrganiser {

    private static final String TAG = "TaskOrganiser";
    private static TaskOrganiser ourInstance;

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

    public static void initialise() {
        ourInstance = new TaskOrganiser();
    }

    public static void destroy() {
        ourInstance = null;
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

        FirestoreManager.getInstance().user.setEspritPoints(0);

        for (Task task : allTasks.values()) {

            // set esprit points
            int totalEsprtiPoints = FirestoreManager.getInstance().user.getEspritPoints() + task.getEspritPoints();
            FirestoreManager.getInstance().user.setEspritPoints(totalEsprtiPoints);

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
        Log.e(TAG, "organiseAllTasks: Total esprit : " + FirestoreManager.getInstance().user.getEspritPoints());
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

    public ArrayList<BarData> getBarDataForThisWeek() {

        ArrayList<BarData> barDataList = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();

        BarData barData;
        for (int i = 0; i < 7; i++) {
            int taskPoint = 0;

            Log.e(TAG, "getBarDataForThisWeek: week days " + c.get(Calendar.DAY_OF_MONTH));

            for (Task task : allTasks.values()) {
                if (task.isTaskCompleted()) {
                    Calendar taskCompleteCalender = Calendar.getInstance();
                    taskCompleteCalender.setTime(task.getTaskCompletedDate());
                    if (Utils.isBothDateAreSameDay(taskCompleteCalender, c)) {
                        taskPoint += task.getEspritPoints();
                    }
                }
            }

            //Log.e(TAG, "getBarDataForThisWeek: task points " + taskPoint);

            barData = new BarData(Constants.weekShortTxt[i],taskPoint," ");
            barDataList.add(barData);
            c.add(Calendar.DATE, 1);
        }

        return barDataList;
    }

    public EspritStatistics getEspritStatistics(String statType) {

        Log.d(TAG, "getEspritStatistics() called with: statType = [" + statType + "]");

        EspritStatistics espritStatistics = new EspritStatistics();
        Calendar calender = Calendar.getInstance();
        int nod = 0;

        if (statType.equals(Constants.thisWeek)) {
            espritStatistics.setStatType(EspritStatType.this_week);
            calender.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            nod = 7;
        } else if (statType.equals(Constants.lastWeek)) {
            espritStatistics.setStatType(EspritStatType.last_week);
            calender.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calender.add(Calendar.DATE, -7);
            nod = 7;
        } else if (statType.equals(Constants.thisMonth)) {
            espritStatistics.setStatType(EspritStatType.this_month);
            calender.set(Calendar.DAY_OF_MONTH, 1);
            nod = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (statType.equals(Constants.lastMonth)) {
            espritStatistics.setStatType(EspritStatType.last_month);
            calender.add(Calendar.MONTH, -1);
            nod = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();

        BarData barData;
        EspritStatPoint espritStatPoint;

        for (int i = 0; i < nod; i++) {
            int espritPoint = 0;
            int taskCompletedCount = 0;
            for (Task task : allTasks.values()) {
                if (task.isTaskCompleted()) {
                    Calendar taskCompleteCalender = Calendar.getInstance();
                    taskCompleteCalender.setTime(task.getTaskCompletedDate());
                    if (Utils.isBothDateAreSameDay(taskCompleteCalender, calender)) {
                        espritPoint += task.getEspritPoints();
                        taskCompletedCount++;
                    }
                }
            }

            if (statType.equals(Constants.thisWeek) || statType.equals(Constants.lastWeek)) {
                barData = new BarData(Constants.weekShortTxt[i],espritPoint,String.valueOf(espritPoint));
            } else {
                barData = new BarData("",espritPoint,String.valueOf(espritPoint));
            }

            espritStatPoint = new EspritStatPoint(Utils.getSuperdateFromTimeStamp(calender.getTimeInMillis()), espritPoint, taskCompletedCount);

            espritStatistics.totalEsprit += espritPoint;
            espritStatistics.totalTasksCompleted += taskCompletedCount;
            espritStatistics.getBarData().add(barData);
            espritStatistics.getEspritStatPoints().add(espritStatPoint);

            // add next day to calender
            calender.add(Calendar.DATE, 1);
        }

        return espritStatistics;
    }

  /*  public EspritStatistics getStatForTheDate(SuperDate date) {
        int taskPoint = 0;
        int espritPoints = 0;
        HashMap<String, Task> allTasks = FirestoreManager.getInstance().getHasMapTask();
        for (Task task : allTasks.values()) {
            if (task.isTaskCompleted()) {
                if (task.isTaskCompleted()) {
                    Calendar taskCompleteCalender = Calendar.getInstance();
                    taskCompleteCalender.setTime(task.getTaskCompletedDate());
                    if (Utils.isBothDateAreSameDay(taskCompleteCalender, Utils.getCalenderFromSuperDate(date))) {
                        espritPoints += task.getEspritPoints();
                        taskPoint++;
                    }
                }
            }
        }

        return new EspritStatistics(date,espritPoints, taskPoint);
    }*/

    private boolean multiplesOfFive(int n) {
        while ( n > 0 ) {
            n = n - 5;

            if ( n == 0 ) return true;
        }
        return false;
    }

    /////////////////////
    //// BUCKET TASKS

    public List<Task> getTasksInBucket(Bucket bucket) {

        List<Task> bucketList = new ArrayList<>();

        if (bucket.getDocumentId().equals("all_tasks")) {
            return allTaskList;
        }

        for (Task task : allTaskList) {
            if (task.getBucket() != null && task.getBucket().getDocumentId().equals(bucket.getDocumentId())) {
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
            if (task.getBucket() != null && task.getBucket().getDocumentId().equals( bucket.getDocumentId())) {
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
            if (task.getBucket() != null && task.getBucket().getDocumentId().equals(bucket.getDocumentId())) {
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
                    if (task.getBucketId().equals(bucket1.getDocumentId())) {
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
