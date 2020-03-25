package com.andysapps.superdo.todo.fragments.task


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback
import com.andysapps.superdo.todo.adapters.taskrecyclers.TasksRecyclerAdapter
import com.andysapps.superdo.todo.enums.TaskListing
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.UpdateTaskListEvent
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_today.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TodayFragment : Fragment() {

    var adapter: TasksRecyclerAdapter? = null

    var todayTaskList: List<Task>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        initUi()
        updateUi()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    fun initUi() {
        todayTaskList = ArrayList()
        recyclerView_today.layoutManager = LinearLayoutManager(activity)
        adapter = TasksRecyclerAdapter(context, todayTaskList)

        val callback: ItemTouchHelper.Callback = LongItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView_today)
        recyclerView_today.adapter = adapter

    }

    fun updateUi() {

        todayTaskList = TaskOrganiser.getInstance().getTasks(TaskListing.TODAY)

        if (todayTaskList == null || todayTaskList!!.isEmpty()) {
            ll_notasks.visibility = View.VISIBLE
            tv_no_tasks.text = "No tasks for today? \n Try to do something..."
        } else {
            ll_notasks.visibility = View.GONE
        }

        adapter!!.updateList(todayTaskList)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FetchTasksEvent) {
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TaskUpdatedEvent) {

        if (event.task.listedIn != TaskListing.TODAY) {
            return
        }

        when (event.documentChange) {
            TaskUpdateType.Added -> {
                adapter!!.addTask(event.task)
            }
            TaskUpdateType.Deleted -> {
                adapter!!.removeTask(event.task)
            }
            TaskUpdateType.Task_Completed -> {
                var index = adapter!!.taskList.indexOf(event.task)
                if (index != -1) {
                    var handler = Handler()
                    handler.postDelayed({adapter!!.setTaskCompleted(index, event.task) }, 200)
                }
            }
            else -> {
                updateUi()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateTaskListEvent) {
        when (event.listType) {
            TaskListing.TODAY -> {
                updateUi()
            }
        }
    }

}
