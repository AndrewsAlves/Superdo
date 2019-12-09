package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Repeat;

public class SetRepeatEvent {
    public Repeat repeat;

    public SetRepeatEvent(Repeat repeat) {
        this.repeat = repeat;
    }
}
