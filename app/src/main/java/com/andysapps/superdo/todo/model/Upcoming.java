package com.andysapps.superdo.todo.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Andrews on 10,February,2020
 */

public class Upcoming extends ExpandableGroup<Task> {

    public Upcoming(String title, List<Task> items) {
        super(title, items);
    }
}
