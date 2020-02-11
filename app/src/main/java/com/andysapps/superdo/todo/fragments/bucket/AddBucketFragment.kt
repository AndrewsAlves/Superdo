package com.andysapps.superdo.todo.fragments.bucket


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.enums.BucketColors
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.model.Bucket
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentChange
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddBucketFragment : BottomSheetDialogFragment() {


   /* var bucketColor : BucketColors = BucketColors.Red

    // this method is important to show full bottom sheet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bucket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        updateUi()
        initUi()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    fun initUi() {
        color1.setOnClickListener(this)
        color2.setOnClickListener(this)
        color3.setOnClickListener(this)
        color4.setOnClickListener(this)
        color5.setOnClickListener(this)
        color6.setOnClickListener(this)
        color7.setOnClickListener(this)
        ib_add_bucket.setOnClickListener(this)
        Utils.showSoftKeyboard(context, et_add_bucket)
    }

    fun updateUi() {

        color1.alpha = 0.5f
        color2.alpha = 0.5f
        color3.alpha = 0.5f
        color4.alpha = 0.5f
        color5.alpha = 0.5f
        color6.alpha = 0.5f
        color7.alpha = 0.5f

        when (bucketColor) {
            BucketColors.Red -> color1.alpha = 1.0f
            BucketColors.Yellow -> color2.alpha = 1.0f
            BucketColors.Green -> color3.alpha = 1.0f
            BucketColors.SkyBlue -> color4.alpha = 1.0f
            BucketColors.InkBlue -> color5.alpha = 1.0f
            BucketColors.Rosa -> color6.alpha = 1.0f
            BucketColors.Orange -> color7.alpha = 1.0f
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            color1.id -> bucketColor = BucketColors.Red
            color2.id -> bucketColor = BucketColors.Yellow
            color3.id -> bucketColor = BucketColors.Green
            color4.id -> bucketColor = BucketColors.SkyBlue
            color5.id -> bucketColor = BucketColors.InkBlue
            color6.id -> bucketColor = BucketColors.Rosa
            color7.id -> bucketColor = BucketColors.Orange

            ib_add_bucket.id -> {
                if (et_add_bucket.text.isNotEmpty()) {
                    var bucket = Bucket()
                    bucket.name = et_add_bucket.text.toString()
                    bucket.tagColor = bucketColor.toString()
                    bucket.userId = FirestoreManager.getInstance().userId
                    bucket.created = Calendar.getInstance().time
                    FirestoreManager.getInstance().uploadBucket(bucket)
                    Log.e("bucket", "new bucket added")
                }
            }
        }

        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BucketUpdatedEvent) {
        if (event.documentChange == DocumentChange.Type.ADDED) {
            dismiss()
        }
    }
*/
}
