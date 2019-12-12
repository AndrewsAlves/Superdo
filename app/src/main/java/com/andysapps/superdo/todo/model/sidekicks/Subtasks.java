package com.andysapps.superdo.todo.model.sidekicks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrews on 27,November,2019
 */

public class Subtasks {

    public boolean isEnabled;

    public List<Subtask> subtaskList;

    public Subtasks() {
    }

    public Subtasks(boolean isEnabled) {
        this.isEnabled = isEnabled;
        subtaskList = new ArrayList<>();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }
}
