package com.andysapps.superdo.todo.fragments


import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.dialog.DeleteTaskDialog
import com.andysapps.superdo.todo.enums.BucketColors
import com.andysapps.superdo.todo.enums.BucketUpdateType
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Task
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * A simple [Fragment] subclass.
 */
class EditTaskFragment : Fragment() {


    val TAG : String = "EditTaskFragment"
    var task : Task = Task()
    var nonEditedTask : Task = Task()


    companion object {
        fun instance(task : Task) : EditTaskFragment {
            val fragment = EditTaskFragment()
            fragment.task = task
            fragment.nonEditedTask = task.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_edit_task, container, false)

        // Inflate the layout for this fragment
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this,view)
        EventBus.getDefault().register(this)
        initUi()
        initClicks()
        updateUi()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    private fun initUi() {

        editTask_et_taskName.imeOptions = EditorInfo.IME_ACTION_DONE
        editTask_et_taskName.setRawInputType(InputType.TYPE_CLASS_TEXT)

        editTask_et_taskName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                task.name = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        editTask_et_desc.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                task.description = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
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

        // set Dodate
        if (task.doDate != null) {
            editTask_iv_do_date.setImageResource(R.drawable.ic_duedate_on_red)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey4))

            editTask_iv_do_time.setImageResource(Utils.getTimeIcon(task.doDate.hours))
            editTask_tv_do_time.text = task.getTimeString()
            editTask_tv_do_time.setTextColor(resources.getColor(R.color.grey4))

        } else {
            editTask_iv_do_date.setImageResource(R.drawable.ic_duedate_off)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey2))

            editTask_iv_do_time.setImageResource(R.drawable.ic_time_off)
            editTask_tv_do_time.text = task.getTimeString()
            editTask_tv_do_time.setTextColor(resources.getColor(R.color.grey2))
        }

        if (task.bucketId != null) {
            editTask_tv_bucketName.setText(task.bucketName)
            editTask_tv_bucketName.setTextColor(resources.getColor(R.color.black))
            when (BucketColors.valueOf(task.bucketColor)) {
                BucketColors.Red -> editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_red)
                BucketColors.Green -> editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_green)
                BucketColors.SkyBlue -> editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_skyblue)
                BucketColors.InkBlue -> editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_inkblue)
                BucketColors.Orange -> editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_orange)
            }
        }

        // remind
        //editTask_cb_remind.isChecked = task.isToRemind

       /* when (task.priority) {


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
        } */

        // Task created date
        if (task.created != null) {
            editTask_tv_createdDate.text = Utils.getDateString(task.created)
        }

    }

    private fun initClicks() {

        ///// DELETE task
        editTask_deleteTask.setOnClickListener {
            DeleteTaskDialog().show(fragmentManager!!, "deleteBucket")
        }

        ///// Close Fragment
        editTask_close.setOnClickListener {
            if (shouldUpdate()) {
                FirestoreManager.getInstance().updateTask(task)
            }
            fragmentManager!!.popBackStack()
            EventBus.getDefault().post(RemoveFragmentEvents())
        }
    }

    private fun shouldUpdate() : Boolean {
        if (task.name.isEmpty()) {
            task.name = nonEditedTask.name
            return true
        }
        return false
    }

    ///////////
    /// EVENTS
    ///////////

    @Subscribe
    fun onMessageEvent(event : DeleteTaskEvent) {
        if (event.isPositive) {
            TaskOrganiser.getInstance().deleteTask(task)
            fragmentManager!!.popBackStack()
            EventBus.getDefault().post(TaskUpdatedEvent(TaskUpdateType.Added, task))

            //EventBus.getDefault().post(RemoveFragmentEvents())
        }
    }

}
