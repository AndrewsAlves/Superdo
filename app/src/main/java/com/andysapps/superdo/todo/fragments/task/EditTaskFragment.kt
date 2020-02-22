package com.andysapps.superdo.todo.fragments.task


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.SimpleLottieValueCallback
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback
import com.andysapps.superdo.todo.adapters.sidekicks.SubtasksRecyclerAdapter
import com.andysapps.superdo.todo.dialog.DeleteTaskDialog
import com.andysapps.superdo.todo.dialog.SelectBucketDialogFragment
import com.andysapps.superdo.todo.dialog.SelectSideKickDialog
import com.andysapps.superdo.todo.dialog.sidekicks.DeadlineDialog
import com.andysapps.superdo.todo.dialog.sidekicks.DoDateDialog
import com.andysapps.superdo.todo.dialog.sidekicks.RemindDialog
import com.andysapps.superdo.todo.dialog.sidekicks.RepeatDialog
import com.andysapps.superdo.todo.enums.BucketColors
import com.andysapps.superdo.todo.enums.TaskListing
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.UpdateTaskListEvent
import com.andysapps.superdo.todo.events.action.SelectBucketEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.events.sidekick.SetDoDateEvent
import com.andysapps.superdo.todo.events.sidekick.SetRemindEvent
import com.andysapps.superdo.todo.events.sidekick.SetRepeatEvent
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents
import com.andysapps.superdo.todo.events.ui.SideKicksSelectedEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.SuperDate
import com.andysapps.superdo.todo.model.Task
import com.andysapps.superdo.todo.model.sidekicks.Subtask
import com.andysapps.superdo.todo.model.sidekicks.Subtasks
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */

class EditTaskFragment : Fragment(), View.OnFocusChangeListener {


    val TAG : String = "EditTaskFragment"
    var task : Task = Task()
    var nonEditedTask : Task = Task()
    var isChecked : Boolean = false
    var subtaskAdapter : SubtasksRecyclerAdapter? = null
    var editing : Boolean = false

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

        editTask_lottie_anim.setAnimation("anim_check2_1.json")

        editTask_et_taskName.imeOptions = EditorInfo.IME_ACTION_DONE
        //editTask_et_taskName.setRawInputType(InputType.TYPE_CLASS_TEXT)

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

        editTask_et_taskName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                editing = true
            }
        }

        editTask_lottie_anim.setOnClickListener(View.OnClickListener {

            editTask_lottie_anim.setMinAndMaxProgress(0.0f, 1.0f)

            if (isChecked) {
                editTask_lottie_anim.speed = -2f
                isChecked = false
            } else {
                editTask_lottie_anim.speed = 1.5f
                isChecked = true
            }

            task.isTaskCompleted = isChecked
            FirestoreManager.getInstance().updateTask(task)

            editTask_lottie_anim.playAnimation()
        })

        editTask_tick_save.setOnClickListener {
            editTask_et_taskName.clearFocus()
            editTask_et_desc.clearFocus()
            Utils.hideKeyboard(context, editTask_et_taskName)
            Utils.hideKeyboard(context, editTask_et_desc)
        }

        ///////////////
        ///// SUBTASK RECYCLER
        //////////////

        editTask_et_add_subtask.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if( editTask_et_add_subtask.text.trim().isNotEmpty()){
                    var subtask = Subtask()
                    subtask.index = task.subtasks.subtaskList.size - 1
                    subtask.isTaskCompleted = false
                    subtask.title = editTask_et_add_subtask.text.toString()

                    task.subtasks.subtaskList.add(subtask)
                    subtaskAdapter!!.updateList()
                    FirestoreManager.getInstance().updateTask(task)
                    subtaskAdapter!!.notifyItemInserted(task.subtasks.subtaskList.size - 1)

                    editTask_et_add_subtask.setText("")
                } else {
                    Utils.hideKeyboard(context, editTask_et_add_subtask)
                    editTask_et_add_subtask.clearFocus()
                }
            }

            true
        })

        if (task.subtasks == null) {
            task.subtasks = Subtasks(false)
        }

        if (task.subtasks.subtaskList == null) {
            Log.e("null, find ", " todod ")
            task.subtasks.subtaskList = ArrayList()
        }

        editTask_rv_subtasks.setLayoutManager(LinearLayoutManager(activity))
        subtaskAdapter = SubtasksRecyclerAdapter(context, task.subtasks.subtaskList, task)
        val callback: ItemTouchHelper.Callback = LongItemTouchHelperCallback(subtaskAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(editTask_rv_subtasks)
        editTask_rv_subtasks.setAdapter(subtaskAdapter)
    }

    private fun updateUi() {

        Log.e(TAG, " task name ${task.name}")
        editTask_et_taskName.setText(task.name)

        // setDescription
        if (task.description != null) {
            editTask_iv_desc.setImageResource(R.drawable.ic_desc_on)
            editTask_et_desc.setText(task.description)
        }

        //////////////
        //// BUCKET & TASK COMPLETED UI

        if (task.bucketId != null) {
            editTask_tv_bucketName.setText(task.bucketName)
            editTask_tv_bucketName.setTextColor(resources.getColor(R.color.black))
            when (BucketColors.valueOf(task.bucketColor)) {
                BucketColors.Red -> {
                    editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_red)
                    iv_check_task.setImageResource(R.drawable.img_oval_light_red)
                }
                BucketColors.Green -> {
                    editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_green)
                    iv_check_task.setImageResource(R.drawable.img_oval_light_green)
                }
                BucketColors.SkyBlue -> {
                    editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_skyblue)
                    iv_check_task.setImageResource(R.drawable.img_oval_light_skyblue)
                }
                BucketColors.InkBlue -> {
                    editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_inkblue)
                    iv_check_task.setImageResource(R.drawable.img_oval_light_inkblue)
                }
                BucketColors.Orange -> {
                    editTask_iv_bucket.setImageResource(R.drawable.img_oval_light_orange)
                    iv_check_task.setImageResource(R.drawable.img_oval_light_orange)
                }
            }

            editTask_lottie_anim.addValueCallback(KeyPath("Shape Layer 1", "**"), LottieProperty.COLOR_FILTER, SimpleLottieValueCallback {
                PorterDuffColorFilter(Utils.getColor(context, task.getBucketColor()), PorterDuff.Mode.SRC_ATOP)
            })
        }

        isChecked = task.isTaskCompleted

        if (isChecked) {
            editTask_lottie_anim.setMinAndMaxProgress(1.0f, 1.0f)
        } else {
            editTask_lottie_anim.setMinAndMaxProgress(0.0f, 0.0f)
            Log.e("tag", "false task")
        }

        editTask_lottie_anim.playAnimation()

        //////////////
        //// DO DATE

        if (task.doDate != null) {
            editTask_iv_do_date.setImageResource(R.drawable.ic_do_date_on)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey4))
        } else {
            editTask_iv_do_date.setImageResource(R.drawable.ic_do_date_off)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey2))
        }

        //if (task.remind == null) {
        //    task.remind = Repeat(true)
       // }

        if (task.repeat != null && task.repeat.isEnabled) {
            editTask_tv_repeat_name.visibility
            editTask_rl_btn_repeat.visibility = View.VISIBLE

            if (task.repeat.repeatType != null) {
                editTask_iv_repeat.setImageResource(R.drawable.ic_repeat_on)
                editTask_tv_repeat.setText(task.repeat.repeatString)
            } else {
                editTask_iv_repeat.setImageResource(R.drawable.ic_repeat_off)
                editTask_tv_repeat.setText("No Repeat")
            }

        } else {
            editTask_rl_btn_repeat.visibility = View.GONE
        }

        //////////////
        //// REMIND

        if (task.remind != null && task.remind.isEnabled) {
            editTask_rl_btn_remind.visibility = View.VISIBLE

            if (task.remind.remindType != null) {
                editTask_iv_remind.setImageResource(R.drawable.ic_remind_on)
                editTask_tv_remind.setText(task.remind.remindString)
            } else {
                editTask_iv_remind.setImageResource(R.drawable.ic_remind_off)
                editTask_tv_remind.setText("When to remind?")
            }

        } else {
            editTask_rl_btn_remind.visibility = View.GONE
        }

        //////////////
        //// DEADLINE

        if (task.deadline != null && task.deadline.isEnabled) {
            editTask_rl_btn_deadline.visibility = View.VISIBLE

            if (task.deadline.hasDate) {
                editTask_iv_deadline.setImageResource(R.drawable.ic_deadline_on)
            } else {
                editTask_iv_deadline.setImageResource(R.drawable.ic_deadline_off)
            }

            editTask_tv_deadline.setText(task.deadline.doDateStringMain)

        } else {
            editTask_rl_btn_deadline.visibility = View.GONE
        }

        //////////////
        //// SUBTASKS

        if (task.subtasks != null && task.subtasks.isEnabled) {
            editTask_ll_subtasks.visibility = View.VISIBLE
        } else {
            editTask_ll_subtasks.visibility = View.GONE
        }

        //////////////
        //// FOCUS

        if (task.focus != null && task.focus.isEnabled) {
            editTask_rl_btn_focus.visibility = View.VISIBLE
            editTask_btn_start_focus.visibility = View.VISIBLE
        } else {
            editTask_rl_btn_focus.visibility = View.GONE
            editTask_btn_start_focus.visibility = View.GONE
        }

        //////////////
        //// CREATED

        // Task created date
        if (task.created != null) {
            editTask_tv_createdDate.text = Utils.getDateString(task.created)
        }

    }

    private fun initClicks() {

        editTask_rl_btn_do_date.setOnClickListener {
            DoDateDialog.instance(task.doDate).show(fragmentManager!!, DoDateDialog().javaClass.name)
        }

        editTask_rl_btn_select_bucket.setOnClickListener {
            SelectBucketDialogFragment().show(fragmentManager!!, SelectBucketDialogFragment().javaClass.name)
        }

        editTask_rl_btn_deadline.setOnClickListener {
            DeadlineDialog.instance(task.deadline).show(fragmentManager!!, DeadlineDialog().javaClass.name)
        }

        editTask_rl_btn_repeat.setOnClickListener {
            RepeatDialog.instance(task.repeat).show(fragmentManager!!, RepeatDialog().javaClass.name)
        }

        editTask_rl_btn_remind.setOnClickListener {
            RemindDialog.instance(task.remind).show(fragmentManager!!, RemindDialog().javaClass.name)
        }

        ///// DELETE task
        editTask_deleteTask.setOnClickListener {
            DeleteTaskDialog().show(fragmentManager!!, "deleteBucket")
        }

        editTask_btn_add_sidekicks.setOnClickListener {
            SelectSideKickDialog.instance(task).show(fragmentManager!!, SelectSideKickDialog().javaClass.name)
        }

        ///// Close Fragment
        editTask_close.setOnClickListener {
            if (shouldUpdate()) {
                FirestoreManager.getInstance().updateTask(task)
            }
            fragmentManager!!.popBackStack()
            EventBus.getDefault().post(RemoveFragmentEvents())
        }

        ///// Mark done
    }

    private fun shouldUpdate() : Boolean {
        if (task.name.isEmpty()) {
            task.name = nonEditedTask.name
            return true
        }
        return false
    }

    fun updateTasks() {
        EventBus.getDefault().post(UpdateTaskListEvent(TaskListing.TODAY))
        EventBus.getDefault().post(UpdateTaskListEvent(TaskListing.TOMORROW))
        EventBus.getDefault().post(UpdateTaskListEvent(TaskListing.UPCOMING))
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            editing = true
            updateEditing()
        } else {
            editing = false
            updateEditing()
        }
    }

    fun updateEditing() {
        editTask_close.visibility = View.GONE
        editTask_deleteTask.visibility = View.GONE
        editTask_tick_save.visibility = View.GONE
        if (editing) {
            editTask_tick_save.visibility = View.VISIBLE
        } else {
            editTask_close.visibility = View.VISIBLE
            editTask_deleteTask.visibility = View.VISIBLE
        }
    }

    ///////////
    /// EVENTS
    ///////////

    @Subscribe
    fun onMessageEvent(event : DeleteTaskEvent) {
        if (event.isPositive) {
            TaskOrganiser.getInstance().deleteTask(task)
            fragmentManager!!.popBackStack()
            EventBus.getDefault().post(TaskUpdatedEvent(TaskUpdateType.Deleted, task))
        }
    }

    @Subscribe
    fun onMeessageEvent(event : SideKicksSelectedEvent) {
        task = event.task.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SelectBucketEvent) {
        task.bucketColor = event.bucket.tagColor
        task.bucketId = event.bucket.documentId
        task.bucketName = event.bucket.name
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    //////////////
    /// SIDE KICK EVENT
    /////////////

    @Subscribe
    fun onMeessageEvent(event : SetDoDateEvent) {
        task.doDate = event.superDate.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
        TaskOrganiser.getInstance().organiseAllTasks()
        updateTasks()
    }

    @Subscribe
    fun onMeessageEvent(event : SetDeadlineEvent) {
        task.deadline = event.deadline.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    @Subscribe
    fun onMeessageEvent(event : SetRepeatEvent) {
        task.repeat = event.repeat.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    @Subscribe
    fun onMeessageEvent(event : SetRemindEvent) {
        task.remind = event.remind.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }


}
