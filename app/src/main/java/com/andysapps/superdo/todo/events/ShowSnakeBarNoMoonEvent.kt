package com.andysapps.superdo.todo.events

import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter
import com.andysapps.superdo.todo.enums.UndoType
import com.andysapps.superdo.todo.model.Task

/**
 * Created by Admin on 24,February,2020
 */
class ShowSnakeBarNoMoonEvent (var tasksRecyclerAdapter: TasksRecyclerAdapter, var task: Task, var position : Int, var undoType : UndoType)