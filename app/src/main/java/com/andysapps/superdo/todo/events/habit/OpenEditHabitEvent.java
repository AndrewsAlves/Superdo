package com.andysapps.superdo.todo.events.habit;

import com.andysapps.superdo.todo.model.Habit;

/**
 * Created by Andrews on 03,February,2020
 */
public class OpenEditHabitEvent {
    public Habit habit;

    public OpenEditHabitEvent(Habit habit) {
        this.habit = habit;
    }
}
