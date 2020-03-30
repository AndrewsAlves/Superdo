package com.andysapps.superdo.todo.dialog.alert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.dialog.SelectSideKickDialog
import com.andysapps.superdo.todo.enums.BucketUpdateType
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.DeleteBucketEvent
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.model.Bucket
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_delete_task_dialog.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */

class DeleteBucketDialog : DialogFragment() {

    var bucket : Bucket? = null

    companion object {
        fun instance(bucket : Bucket) : DeleteBucketDialog {
            val fragment = DeleteBucketDialog()
            fragment.bucket = bucket
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_bucket_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {

        deleteTask_b_possitive.setOnClickListener {
            EventBus.getDefault().post(DeleteBucketEvent(bucket!!,true))
            dismiss()
        }

        deleteTask_b_negative.setOnClickListener {
            dismiss()
        }
    }
}
