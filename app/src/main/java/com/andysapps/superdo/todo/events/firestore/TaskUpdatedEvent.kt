package com.andysapps.superdo.todo.events.firestore

import com.andysapps.superdo.todo.model.Task
import com.google.firebase.firestore.DocumentChange

/**
 * Created by Andrews on 14,November,2019
 */

class TaskUpdatedEvent (var documentChange: DocumentChange.Type, var task : Task)