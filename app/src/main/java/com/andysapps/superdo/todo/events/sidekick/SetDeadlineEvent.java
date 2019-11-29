package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Deadline;

public class SetDeadlineEvent {
    public Deadline deadline;

    public SetDeadlineEvent(Deadline deadline) {
        this.deadline = deadline;
    }
}
