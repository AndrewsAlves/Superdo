package com.andysapps.superdo.todo.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.manager.TaskOrganiser
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    fun initUi() {

    }

    fun updateUi() {

        profile_tv_completed_tasks.text = TaskOrganiser.getInstance().getCompletedTaskList().size.toString()
        profile_tv_pending_tasks.text = TaskOrganiser.getInstance().getPendingTaskList().size.toString()
        profile_tv_missing_tasks.text = TaskOrganiser.getInstance().getMissedTaskList().size.toString()
        profile_tv_deleted_tasks.text = TaskOrganiser.getInstance().getDeletedTaskList().size.toString()

    }


}
