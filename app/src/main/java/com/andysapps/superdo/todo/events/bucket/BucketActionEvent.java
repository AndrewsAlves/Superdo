package com.andysapps.superdo.todo.events.bucket;

import com.andysapps.superdo.todo.enums.BucketUpdateType;

/**
 * Created by Andrews on 20,November,2019
 */

public class BucketActionEvent {

    BucketUpdateType actions;

    public BucketActionEvent(BucketUpdateType actions) {
        this.actions = actions;
    }
}
