package com.andysapps.superdo.todo.fragments.bucket

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.andysapps.superdo.todo.Constants
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.adapters.taskrecyclers.BucketTasksRecyclerAdapter
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.enums.MoonButtonType
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.UpdateMoonButtonType
import com.andysapps.superdo.todo.events.bucket.UpdateBucketTasksEvent
import com.andysapps.superdo.todo.events.bucket.UpdateBucketTasksUiEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent
import com.andysapps.superdo.todo.fragments.bucket.CreateNewBucketFragment.Companion.instance
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Bucket
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_bucket_tasks.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class BucketTasksFragment : Fragment() {

    var isEditing = false

    var adapter: BucketTasksRecyclerAdapter? = null

    var bucket: Bucket? = null

    var taskList: ArrayList<Task>? = null

    companion object {
        const val TAG = "BucketTaskFragment"
        fun getInstance(bucket: Bucket?): BucketTasksFragment {
            val fragment = BucketTasksFragment()
            fragment.bucket = bucket
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_bucket_tasks, container, false)
        ButterKnife.bind(this, v)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initClicks()
        updateUi()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    fun initUi() {

        EventBus.getDefault().register(this)
        EventBus.getDefault().post(UpdateMoonButtonType(MoonButtonType.ADD_TASK))

        if (bucket == null) {
            bucket = FirestoreManager.getAllTasksBucket()
            et_bucket_desc.isFocusable = false
            ib_edit_bucket.visibility = View.GONE
        }

        taskList = ArrayList()

        if (!TaskOrganiser.getInstance().getTasksInBucket(bucket, false).isEmpty()) {
            taskList!!.addAll(TaskOrganiser.getInstance().getTasksInBucket(bucket, false))
        }

        recyclerView_task_list.layoutManager = LinearLayoutManager(context)
        adapter = BucketTasksRecyclerAdapter(context, taskList, bucket)
        recyclerView_task_list.adapter = adapter

        et_bucket_desc.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (v.id == et_bucket_desc.id) {
                if (hasFocus) {
                    isEditing = true
                    updateUi()
                } else {
                    isEditing = false
                    updateUi()
                }
            }
        }
    }

    fun initClicks() {

        btn_save__bucket.setOnClickListener {
            bucket!!.description = et_bucket_desc.text.toString()
            FirestoreManager.getInstance().updateBucket(bucket)
            isEditing = false
            updateUi()
        }

        ib_close_edit_bucket_list.setOnClickListener {
            isEditing = false
            updateUi()
        }

        ib_edit_bucket.setOnClickListener {
            Utils.hideKeyboard(context, et_bucket_desc)
            EventBus.getDefault().post(OpenFragmentEvent(instance(bucket!!, true),
                    true,
                    CreateNewBucketFragment.TAG,
                    true))
        }

        ib_bucketList.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(BucketFragment(), true, BucketFragment.TAG))
        }

        btn_rl_completedtask.setOnClickListener {
            EventBus.getDefault().post(OpenFragmentEvent(
                    CPMDTasksFragment.Companion.instance(CPMD.COMPLETED, bucket!!),
                    false,
                    CPMDTasksFragment.TAG, true));
        }
    }

    fun updateUi() {

        tv_bucket_name.text = bucket!!.name
        et_bucket_desc.setText(bucket!!.description)

        if (isEditing) {

            btn_save__bucket.visibility = View.VISIBLE
            ib_close_edit_bucket_list.visibility = View.VISIBLE
            ib_edit_bucket.visibility = View.GONE
            ib_bucketList.visibility = View.GONE

        } else {

            Utils.hideKeyboard(context, et_bucket_desc)
            et_bucket_desc.clearFocus()

            btn_save__bucket.visibility = View.GONE
            ib_close_edit_bucket_list.visibility = View.GONE
            ib_edit_bucket.visibility = View.VISIBLE
            ib_bucketList.visibility = View.VISIBLE
            tv_bucket_name.visibility = View.VISIBLE
        }

        iv_bucket_type.setImageResource(Constants.bucketIcons[bucket!!.bucketIcon])
        Log.e(javaClass.name, "updateUi: $isEditing")
        ll_notasks.visibility = View.GONE

        tv_completetask_count.text = TaskOrganiser.getInstance().getTasksInBucket(bucket,true).size.toString()

        if (adapter!!.taskList == null || adapter!!.taskList.isEmpty()) {
            ll_notasks.visibility = View.VISIBLE
        }

    }

    fun updateList() {
        adapter!!.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false))
        updateUi()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateBucketTasksEvent?) {
        isEditing = false
        adapter!!.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false))
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateBucketTasksUiEvent?) {
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TaskUpdatedEvent) {

        if (event.task.bucketId != bucket!!.documentId) {
            if (adapter!!.taskList.contains(event.task)) {
                updateList()
            }
            return
        }

        when (event.documentChange) {
            TaskUpdateType.Added -> {
                var pos = TaskOrganiser.getInstance().getTasksInBucket(bucket, false).indexOf(event.task)
                adapter!!.addTask(event.task, pos)
            }
            TaskUpdateType.Deleted -> {
                adapter!!.removeTask(event.task)
            }
            TaskUpdateType.Task_Completed -> {
                var index = adapter!!.taskList.indexOf(event.task)
                if (index != -1) {
                    var handler = Handler()
                    handler.postDelayed({adapter!!.setTaskCompleted(index, event.task) }, 200)
                }
            }
            else -> {
                updateList()
            }
        }

        updateUi()
    }
}