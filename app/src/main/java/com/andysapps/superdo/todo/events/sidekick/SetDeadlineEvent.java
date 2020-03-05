package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Deadline;

public class SetDeadlineEvent {
    public Deadline deadline;
    public boolean deleted;

    public SetDeadlineEvent(Deadline deadline, boolean deleted) {
        this.deadline = deadline;
        this.deleted = deleted;
    }
}
