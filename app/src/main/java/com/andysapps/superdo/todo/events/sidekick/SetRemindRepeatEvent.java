package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.taskfeatures.Repeat;

public class SetRemindRepeatEvent {

    public Repeat repeat;
    public boolean deleted;

    public SetRemindRepeatEvent(Repeat repeat, boolean deleted) {
        this.repeat = repeat;
        this.deleted = deleted;
    }
}
