package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Remind;

public class SetRemindOnceEvent {

    public Remind remind;

    public SetRemindOnceEvent(Remind repeat) {
        this.remind = repeat;
    }
}
