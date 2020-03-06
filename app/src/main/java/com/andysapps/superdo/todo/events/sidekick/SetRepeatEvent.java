package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.sidekicks.Repeat;

public class SetRepeatEvent {
    public Repeat repeat;
    public boolean deleted;

    public SetRepeatEvent(Repeat repeat, boolean deleted) {
        this.repeat = repeat;
        this.deleted = deleted;
    }
}
