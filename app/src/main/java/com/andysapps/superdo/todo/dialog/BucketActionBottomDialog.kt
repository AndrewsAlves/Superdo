package com.andysapps.superdo.todo.dialog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.BucketUpdateType
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent
import com.andysapps.superdo.todo.fragments.CreateNewBucketFragment
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Bucket
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bucket_bottom_dialog.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */

class BucketActionBottomDialog : BottomSheetDialogFragment() {

    var bucket : Bucket = Bucket()

    companion object {
        fun instance(bucket : Bucket) : BucketActionBottomDialog {
            val fragment = BucketActionBottomDialog()
            fragment.bucket = bucket
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bucket_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_edit__BucketDialog.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(CreateNewBucketFragment.instance(bucket, true), true))
            dismiss()
        }

        btn_delete__BucketDialog.setOnClickListener {
            TaskOrganiser.getInstance().deleteBucket(bucket)
            EventBus.getDefault().post(BucketUpdatedEvent(BucketUpdateType.Deleted, bucket))
            dismiss()
        }

        btn_cancel__BucketDialog.setOnClickListener {
            dismiss()
        }
    }

}
