package com.andysapps.superdo.todo.fragments.task


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback
import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter
import com.andysapps.superdo.todo.enums.TaskListing
import com.andysapps.superdo.todo.enums.TaskUpdateType
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
        } else {
            ll_notasks.visibility = View.GONE
        }

        adapter!!.updateList(todayTaskList)
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
            else -> {
                updateUi()
            }
        }

    }

}
