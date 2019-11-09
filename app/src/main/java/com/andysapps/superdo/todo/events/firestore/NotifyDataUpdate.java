package com.andysapps.superdo.todo.events.firestore;

import com.google.firebase.firestore.DocumentChange;

/**
 * Created by Andrews on 08,November,2019
 */
public class NotifyDataUpdate {

    public DocumentChange.Type changeType;

    public NotifyDataUpdate(DocumentChange.Type changeType) {
        this.changeType = changeType;
    }
}
