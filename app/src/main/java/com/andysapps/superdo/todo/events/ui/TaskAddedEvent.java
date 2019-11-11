package com.andysapps.superdo.todo.events.ui;

import com.andysapps.superdo.todo.model.Task;

/**
 * Created by Andrews on 08,November,2019
 */
public class TaskAddedEvent {

    public Task task;

    public TaskAddedEvent(Task task) {
        this.task = task;
    }
}
