package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Remind;

public class SetRemindEvent {
    public Remind remind;

    public SetRemindEvent(Remind repeat) {
        this.remind = repeat;
    }
}
