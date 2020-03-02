package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Remind;

public class SetRemindOnceEvent {

    public Remind remind;
    public boolean deleted;

    public SetRemindOnceEvent(Remind repeat, boolean deleted) {
        this.remind = repeat;
        this.deleted = deleted;
    }
}
