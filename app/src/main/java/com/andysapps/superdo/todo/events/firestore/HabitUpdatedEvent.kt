package com.andysapps.superdo.todo.events.firestore

import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.model.Habit
import com.andysapps.superdo.todo.model.Task
import com.google.firebase.firestore.DocumentChange

/**
 * Created by Andrews on 14,November,2019
 */

class HabitUpdatedEvent (var documentChange: TaskUpdateType, var task : Habit)