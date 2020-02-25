package com.andysapps.superdo.todo.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment
import com.andysapps.superdo.todo.manager.TaskOrganiser
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
        profile_btn_completed_tasks.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(CPMD.COMPLETED), false, CPMDTasksFragment.TAG))
        }
        profile_btn_pending_tasks.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(CPMD.PENDING), false, CPMDTasksFragment.TAG))
        }
        profile_btn_missed_tasks.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(CPMD.MISSED), false, CPMDTasksFragment.TAG))
        }
        profile_btn_archived_tasks.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(CPMD.DELETED), false, CPMDTasksFragment.TAG))
        }
    }

    fun updateUi() {
        profile_tv_completed_tasks.text = TaskOrganiser.getInstance().getCompletedTaskList().size.toString()
        profile_tv_pending_tasks.text = TaskOrganiser.getInstance().getPendingTaskList().size.toString()
        profile_tv_missing_tasks.text = TaskOrganiser.getInstance().getMissedTaskList().size.toString()
        profile_tv_deleted_tasks.text = TaskOrganiser.getInstance().getDeletedTaskList().size.toString()
    }
}
