package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Remind;

public class SetRemindEvent {
    public Remind remind;
    public boolean deleted;

    public SetRemindEvent(Remind repeat, boolean deleted) {
        this.remind = repeat;
        this.deleted = deleted;
    }
}
