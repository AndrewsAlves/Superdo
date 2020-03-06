package com.andysapps.superdo.todo.events

import com.andysapps.superdo.todo.adapters.taskrecyclers.TasksRecyclerAdapter
import com.andysapps.superdo.todo.adapters.taskrecyclers.UpcomingManualAdapter
import com.andysapps.superdo.todo.enums.UndoType
import com.andysapps.superdo.todo.model.Task

/**
 * Created by Admin on 24,February,2020
 */
class ShowSnakeBarEvent (var tasksRecyclerAdapter: TasksRecyclerAdapter?, var upcomingRecyclerAdapter: UpcomingManualAdapter?, var task: Task, var position : Int, var undoType : UndoType)