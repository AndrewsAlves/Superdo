package com.andysapps.superdo.todo.fragments


import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */
class EditTaskFragment : Fragment() , TextWatcher {



    val TAG : String = "EditTaskFragment"
    var task : Task = Task()

    companion object {
        fun instance(task : Task) : EditTaskFragment {
            val fragment = EditTaskFragment()
            fragment.task = task
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_edit_task, container, false)
        ButterKnife.bind(this,v)

        // Inflate the layout for this fragment
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        updateUi()
    }

    private fun initUi() {

        editTask_et_taskName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        editTask_et_desc.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun updateUi() {

        Log.e(TAG, " task name ${task.name}")
        editTask_et_taskName.setText(task.name)

        // setDescription
        if (task.description != null) {
            editTask_iv_desc.setImageResource(R.drawable.ic_desc_on)
            editTask_et_desc.setText(task.description)
        }

        // set Duedate
        if (task.dueDate != null) {
            editTask_iv_deadline.setImageResource(R.drawable.ic_duedate_on)
            editTask_tv_deadline.text = task.dueDateString
            editTask_tv_deadline.setTextColor(resources.getColor(R.color.grey4))

            editTask_iv_time.setImageResource(R.drawable.ic_time_on)
            editTask_tv_time.text = task.getTimeString(task.dueDate)
            editTask_tv_time.setTextColor(resources.getColor(R.color.grey4))

        } else {
            editTask_iv_deadline.setImageResource(R.drawable.ic_duedate_off)
            editTask_tv_deadline.text = task.dueDateString
            editTask_tv_deadline.setTextColor(resources.getColor(R.color.grey2))

            editTask_iv_time.setImageResource(R.drawable.ic_time_off)
            editTask_tv_time.text = task.getTimeString(task.dueDate)
            editTask_tv_time.setTextColor(resources.getColor(R.color.grey2))
        }


        // set bucket name
        if (task.bucketId == null) {
            editTask_iv_bucket.drawable.setColorFilter(resources.getColor(R.color.lightRed), PorterDuff.Mode.SRC_IN)
            editTask_tv_bucketName.text = "All Tasks"
            editTask_tv_bucketName.setTextColor(resources.getColor(R.color.lightRed))
        } else {
            editTask_iv_bucket.drawable.setColorFilter(Color.parseColor(task.bucketColor), PorterDuff.Mode.SRC_IN)
            editTask_tv_bucketName.text = task.bucketName
            editTask_tv_bucketName.setTextColor(Color.parseColor(task.bucketColor))
        }

        // remind
        editTask_cb_remind.isChecked = task.isToRemind

        when (task.priority) {
            0 -> {
                editTask_iv_priority.setImageResource(R.drawable.ic_priority_low)
                editTask_tv_priority.text = "Priotirty Low"
            }
            1 -> {
                editTask_iv_priority.setImageResource(R.drawable.ic_priority_medium)
                editTask_tv_priority.text = "Priotirty Medium"
            }
            2 -> {
            editTask_iv_priority.setImageResource(R.drawable.ic_priority_high)
                editTask_tv_priority.text = "Priotirty Hign"
            }
        }

        // Task created date
        editTask_tv_createdDate.text = Utils.getDateString(task.created)

    }

    @OnClick(R.id.editTask_close)
    fun clickClose() {
        EventBus.getDefault().post(RemoveFragmentEvents())
    }

    override fun afterTextChanged(s: Editable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
