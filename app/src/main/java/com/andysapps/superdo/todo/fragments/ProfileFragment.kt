package com.andysapps.superdo.todo.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        updateUi()
    }

    fun initUi() {

        PushDownAnim.setPushDownAnimTo(profile_btn_completed_tasks,
                profile_btn_pending_tasks,
                profile_btn_missed_tasks,
                profile_btn_recurring_tasks,
                profile_btn_deadline_tasks,
                profile_btn_archived_tasks)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(fun(view: View) {

                    var cpmd = CPMD.COMPLETED

                    when(view) {
                        profile_btn_completed_tasks -> cpmd = CPMD.COMPLETED
                        profile_btn_pending_tasks -> cpmd = CPMD.PENDING
                        profile_btn_missed_tasks -> cpmd = CPMD.MISSED
                        profile_btn_recurring_tasks -> cpmd = CPMD.RECURRING
                        profile_btn_deadline_tasks -> cpmd = CPMD.DEADLINED
                        profile_btn_archived_tasks -> cpmd = CPMD.DELETED
                    }

                    EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(cpmd), false, CPMDTasksFragment.TAG, true))
                })
    }

    fun updateUi() {
        profile_tv_completed_tasks.text = TaskOrganiser.getInstance().getCompletedTaskList().size.toString()
        profile_tv_pending_tasks.text = TaskOrganiser.getInstance().getPendingTaskList().size.toString()
        profile_tv_missing_tasks.text = TaskOrganiser.getInstance().getMissedTaskList().size.toString()
        profile_tv_recurring_tasks.text = TaskOrganiser.getInstance().getRecurringTask().size.toString()
        profile_tv_deadline_tasks.text = TaskOrganiser.getInstance().getDeadlineTasks().size.toString()
        profile_tv_deleted_tasks.text = TaskOrganiser.getInstance().getDeletedTaskList().size.toString()
    }
}
