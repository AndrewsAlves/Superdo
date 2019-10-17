package com.andysapps.superdo.todo.enums;

/**
 * Created by Andrews on 10,August,2019
 */

public enum MainTabs {
    TODAY(0),
    TASKS(1);

    private final int value;
    private MainTabs(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
