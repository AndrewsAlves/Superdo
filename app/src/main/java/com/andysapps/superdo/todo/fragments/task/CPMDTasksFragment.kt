package com.andysapps.superdo.todo.fragments.task


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.CPMDRecyclerAdapter
import com.andysapps.superdo.todo.dialog.DeleteTaskDialog
import com.andysapps.superdo.todo.dialog.RestoreTaskDialog
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.enums.TaskListing
import com.andysapps.superdo.todo.enums.UndoType
import com.andysapps.superdo.todo.events.*
import com.andysapps.superdo.todo.events.profile.SelectProfileTaskEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_cpmdtasks.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */
class CPMDTasksFragment : Fragment() {

    var cpmd : CPMD = CPMD.COMPLETED

    var taskList : List<Task>? = null
    var adapter: CPMDRecyclerAdapter? = null

    public var selectingTasks = false
    var selectAll = false

    companion object {

        val TAG : String = "CPMDTasksFragment"

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
        recyclerView_task_list.layoutManager = LinearLayoutManager(activity)
        adapter = CPMDRecyclerAdapter(context, taskList, cpmd)
        recyclerView_task_list.adapter = adapter
        setTaskList()

        ib_cpdm_select_all_tasks.setOnClickListener {
            selectAll = !selectAll
            adapter!!.selectAll(selectAll)
        }

        ib_cpdm_delete_tasks.setOnClickListener {
            DeleteTaskDialog().show(fragmentManager!!, "deleteTask")
        }

        ib_cpdm_restore_tasks.setOnClickListener {
            RestoreTaskDialog().show(fragmentManager!!, "restoreTask")
        }

        ib_close_cpdm.setOnClickListener {
            if (selectingTasks) {
                selectingTasks = false
                adapter!!.clearSelection()
                updateUi()
            } else {
                fragmentManager!!.popBackStack()
            }
        }
    }

    fun setTaskList() {
        taskList = ArrayList()

        when(cpmd) {
            CPMD.COMPLETED -> {
                taskList = TaskOrganiser.getInstance().getCompletedTaskList()
            }
            CPMD.PENDING -> {
                taskList = TaskOrganiser.getInstance().getPendingTaskList()
            }
            CPMD.MISSED -> {
                taskList = TaskOrganiser.getInstance().getMissedTaskList()
            }
            CPMD.DELETED -> {
                taskList = TaskOrganiser.getInstance().getDeletedTaskList()
            }
        }

        adapter!!.updateList(taskList)
    }

    fun updateUi() {
        ll_notasks.visibility = View.GONE
        if (taskList!!.isEmpty()) {
            ll_notasks.visibility = View.VISIBLE
        }

        ib_close_cpdm.setImageResource(R.drawable.ic_back_red)
        ib_cpdm_delete_tasks.visibility = View.GONE
        ib_cpdm_select_all_tasks.visibility = View.GONE
        ib_cpdm_restore_tasks.visibility = View.GONE

        if (selectingTasks) {
            ib_close_cpdm.setImageResource(R.drawable.ic_close_rted)
            ib_cpdm_delete_tasks.visibility = View.VISIBLE
            ib_cpdm_select_all_tasks.visibility = View.VISIBLE
            if (cpmd == CPMD.DELETED) {
                ib_cpdm_restore_tasks.visibility = View.VISIBLE
            }
        }

        when(cpmd) {
            CPMD.COMPLETED -> {
                tv_cpmd_name.text = "Completed tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.PENDING -> {
                tv_cpmd_name.text = "Pending tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.MISSED -> {
                tv_cpmd_name.text = "Missed tasks" + " (" + taskList!!.size + ")"
            }
            CPMD.DELETED -> {
                tv_cpmd_name.text = "Deleted tasks" + " (" + taskList!!.size + ")"
            }
        }
    }

    @Subscribe
    fun onMessageEvent(event : SelectProfileTaskEvent) {
        selectingTasks = event.selecting
        updateUi()
    }

    fun clearSelection() {
        selectingTasks = false
        adapter!!.clearSelection()
        updateUi()
    }

    @Subscribe
    fun onMessageEvent(event : DeleteTaskEvent) {
        if (event.isPositive) {
           for (task in adapter!!.selectedTask) {
               TaskOrganiser.getInstance().permanentDeleteTask(task.value)
           }
            TaskOrganiser.getInstance().organiseAllTasks()
            setTaskList()
            adapter!!.clearSelection()
            selectingTasks = false
            updateUi()
        }
    }

    @Subscribe
    fun onMessageEvent(event : UpdateCpmdTitleEvent) {
        tv_cpmd_name.text = "Selected " + " (" + adapter!!.selectedTask.size + ")"
    }

    @Subscribe
    fun onMessageEvent(event : RestoreTaskEvent) {
        for (task in adapter!!.selectedTask) {
            task.value.isMovedToBin = false
            FirestoreManager.getInstance().updateTask(task.value)
        }
        TaskOrganiser.getInstance().organiseAllTasks()
        setTaskList()
        adapter!!.clearSelection()
        selectingTasks = false
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateTaskListEvent) {
        setTaskList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ShowSnakeBarCPMDEvent) {

        var title = "Error"
        title = when (event.undoType) {
            UndoType.TASK_COMPLETED -> "Moved to completed task"
            UndoType.TASK_NOT_COMPLETED -> "Moved to pending task"
            else -> ""
        }

        val snackbar = Snackbar.make(parent_coodinator, title, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.WHITE)
        val tv = snackbar.view.findViewById<View>(R.id.snackbar_text) as TextView
        tv.setTextColor(resources.getColor(R.color.lightRed))
        snackbar.setActionTextColor(resources.getColor(R.color.lightRed))

        snackbar.setAction("Undo") {
            when (event.undoType) {
                UndoType.TASK_COMPLETED -> event.cpmdRecyclerAdapter.undoTaskCompleted(event.task, event.position)
                UndoType.TASK_NOT_COMPLETED -> event.cpmdRecyclerAdapter.undoTaskNotCompleted(event.task, event.position)
            }
        }
        snackbar.show()
    }
}
