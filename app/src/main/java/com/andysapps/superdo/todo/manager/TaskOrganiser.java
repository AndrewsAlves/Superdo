package com.andysapps.superdo.todo.manager;

import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Admin on 06,November,2019
 */
public class TaskOrganiser {

    private static TaskOrganiser ourInstance = new TaskOrganiser();

    public List<Task> todayTaskList;
    public List<Task> tomorrowTaskList;
    public List<Task> someDayTaskList;



    public static TaskOrganiser getInstance() {
        return ourInstance;
    }

    private TaskOrganiser() {
    }

    public void organiseAllTasks() {

        todayTaskList = new ArrayList<>();
        tomorrowTaskList = new ArrayList<>();
        someDayTaskList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        List<Task> allTasks = FirestoreManager.getInstance().getAllTasks();

        for (Task task : allTasks) {

            if (task.getListedIn() == TaskListing.TODAY) {
                todayTaskList.add(task);
            }

            if (task.getListedIn() == TaskListing.TOMORROW) {

                if (task.getDoDate().getTime() < Calendar.getInstance().getTime().getTime()) {

                    todayTaskList.add(task);

                } else {

                    int taskDate = getDateFromTimeStamp(task.getDoDate().getTime());
                    int todayDate = Calendar.getInstance().get(Calendar.DATE);

                    if (taskDate <= todayDate) {
                        todayTaskList.add(task);
                    } else {
                        tomorrowTaskList.add(task);
                    }

                }
            }

        }

        for (int i = 0 ; i < allTasks.size() ; i++) {
            if (allTasks)
        }

    }

    public int getDateFromTimeStamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString  = dateFormat.format(timestamp);

        String[] dateParts = dateString.split("-");

        return Integer.parseInt(dateParts[0]);
    }


}
