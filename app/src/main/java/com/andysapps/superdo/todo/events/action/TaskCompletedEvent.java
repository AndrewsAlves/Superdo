package com.andysapps.superdo.todo.events.action;

/**
 * Created by Admin on 21,February,2020
 */
public class TaskCompletedEvent {

    public boolean isCompleted;

    public TaskCompletedEvent(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
