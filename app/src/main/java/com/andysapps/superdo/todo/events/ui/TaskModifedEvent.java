package com.andysapps.superdo.todo.events.ui;

import com.andysapps.superdo.todo.model.Task;

/**
 * Created by Admin on 10,November,2019
 */
public class TaskModifedEvent {

    public Task task;

    public TaskModifedEvent(Task task) {
        this.task = task;
    }
}
