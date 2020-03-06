package com.andysapps.superdo.todo.events

import com.andysapps.superdo.todo.adapters.taskrecyclers.CPMDRecyclerAdapter
import com.andysapps.superdo.todo.enums.UndoType
import com.andysapps.superdo.todo.model.Task

/**
 * Created by Andrews on 24,February,2020
 */
class ShowSnakeBarCPMDEvent (var cpmdRecyclerAdapter : CPMDRecyclerAdapter, var task: Task, var position : Int, var undoType : UndoType)