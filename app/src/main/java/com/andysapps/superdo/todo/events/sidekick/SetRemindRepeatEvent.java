package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Repeat;

public class SetRemindRepeatEvent {

    public Repeat repeat;

    public SetRemindRepeatEvent(Repeat repeat) {
        this.repeat = repeat;
    }
}
