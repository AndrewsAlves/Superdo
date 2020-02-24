package com.andysapps.superdo.todo.fragments.task


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.CPDMRecyclerAdapter
import com.andysapps.superdo.todo.dialog.DeleteTaskDialog
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.profile.SelectProfileTaskEvent
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_cpmdtasks.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * A simple [Fragment] subclass.
 */
class CPMDTasksFragment : Fragment() {

    var cpmd : CPMD = CPMD.COMPLETED

    var taskList : List<Task>? = null
    var adapter: CPDMRecyclerAdapter? = null

    var selectingTasks = false
    var selectAll = false


    companion object {
        fun instance(cpmd : CPMD) : CPMDTasksFragment {
            val fragment = CPMDTasksFragment()
            fragment.cpmd = cpmd
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cpmdtasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        initUI()
        updateUi()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    fun initUI () {

        taskList = ArrayList()

        when(cpmd) {
            CPMD.COMPLETED -> {
                taskList = TaskOrganiser.getInstance().getCompletedTaskList()
                tv_cpmd_name.text = "Completed tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.PENDING -> {
                taskList = TaskOrganiser.getInstance().getPendingTaskList()
                tv_cpmd_name.text = "Pending tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.MISSED -> {
                taskList = TaskOrganiser.getInstance().getMissedTaskList()
                tv_cpmd_name.text = "Missed tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.DELETED -> {
                taskList = TaskOrganiser.getInstance().getDeletedTaskList()
                tv_cpmd_name.text = "Deleted tasks" + " (" + taskList!!.size + ")"
            }
        }

        ///// CLICKS

        ib_cpdm_select_all_tasks.setOnClickListener {
            selectAll = !selectAll
            adapter!!.selectAll(selectAll)
        }

        ib_cpdm_delete_tasks.setOnClickListener {
            DeleteTaskDialog().show(fragmentManager!!, "deleteBucket")
        }

        ib_close_cpdm.setOnClickListener {
            if (selectingTasks) {
                selectingTasks = false
                updateUi()
                adapter!!.clearSelection()
            } else {
                fragmentManager!!.popBackStack()
            }
        }

        recyclerView_task_list.layoutManager = LinearLayoutManager(activity)
        adapter = CPDMRecyclerAdapter(context, taskList)
        recyclerView_task_list.adapter = adapter
    }

    fun updateUi() {
        ll_notasks.visibility = View.GONE
        if (taskList!!.isEmpty()) {
            ll_notasks.visibility = View.VISIBLE
        }

        ib_close_cpdm.setImageResource(R.drawable.ic_back_red)
        ib_cpdm_delete_tasks.visibility = View.GONE
        ib_cpdm_select_all_tasks.visibility = View.GONE

        if (selectingTasks) {
            ib_close_cpdm.setImageResource(R.drawable.ic_close_rted)
            ib_cpdm_delete_tasks.visibility = View.VISIBLE
            ib_cpdm_select_all_tasks.visibility = View.VISIBLE
        }
    }

    @Subscribe
    fun onMessageEvent(event : SelectProfileTaskEvent) {
        selectingTasks = event.selecting
        updateUi()
    }

    @Subscribe
    fun onMessageEvent(event : DeleteTaskEvent) {
        if (event.isPositive) {
           for (task in adapter!!.selectedTask) {
               TaskOrganiser.getInstance().permanentDeleteTask(task.value)
           }
        }
    }
}
