package com.andysapps.superdo.todo.events.sidekick;

import com.andysapps.superdo.todo.model.SuperDate;

public class SetDoDateEvent {
    public SuperDate superDate;

    public SetDoDateEvent(SuperDate superDate) {
        this.superDate = superDate;
    }
}
