package com.andysapps.superdo.todo.dialog.alert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.BucketUpdateType
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.LogoutEvent
import com.andysapps.superdo.todo.events.RestoreTaskEvent
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_alert_logout_dialog.*
import kotlinx.android.synthetic.main.fragment_delete_task_dialog.*
import kotlinx.android.synthetic.main.fragment_restore_tasks_dialog.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */

class LogoutAlertDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_logout_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {

        logout_b_possitive.setOnClickListener {
            EventBus.getDefault().post(LogoutEvent())
            dismiss()
        }

        logout_b_negative.setOnClickListener {
            dismiss()
        }
    }
}
