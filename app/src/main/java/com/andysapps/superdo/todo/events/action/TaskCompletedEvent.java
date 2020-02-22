package com.andysapps.superdo.todo.events.action;

import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter;

/**
 * Created by Andrews on 21,February,2020
 */
public class TaskCompletedEvent {

    public boolean isCompleted;
    public TasksRecyclerAdapter adapter;

    public TaskCompletedEvent(TasksRecyclerAdapter adapter, boolean isCompleted) {
        this.adapter = adapter;
        this.isCompleted = isCompleted;
    }
}
