package com.andysapps.superdo.todo.events.habit;

/**
 * Created by Admin on 07,January,2020
 */
public class SelectedHabitSuggestionEvent {
    public String suggestion;

    public SelectedHabitSuggestionEvent(String suggestion) {
        this.suggestion = suggestion;
    }
}
