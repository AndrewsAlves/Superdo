package com.andysapps.superdo.todo.fragments.task


import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import com.airbnb.lottie.value.LottieFrameInfo
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback
import com.andysapps.superdo.todo.adapters.taskrecyclers.SubtasksRecyclerAdapter
import com.andysapps.superdo.todo.dialog.SelectBucketDialogFragment
import com.andysapps.superdo.todo.dialog.SelectSideKickDialog
import com.andysapps.superdo.todo.dialog.sidekicks.DeadlineDialog
import com.andysapps.superdo.todo.dialog.sidekicks.DoDateDialog
import com.andysapps.superdo.todo.dialog.sidekicks.RepeatDialog
import com.andysapps.superdo.todo.enums.BucketType
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.action.SelectBucketEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.events.sidekick.SetDoDateEvent
import com.andysapps.superdo.todo.events.sidekick.SetRepeatEvent
import com.andysapps.superdo.todo.events.sidekick.UpdateSubtasksEvent
import com.andysapps.superdo.todo.events.ui.SideKicksSelectedEvent
import com.andysapps.superdo.todo.events.update.UpdateUiEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.SuperdoAudioManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Task
import com.andysapps.superdo.todo.model.sidekicks.Subtask
import com.andysapps.superdo.todo.model.sidekicks.Subtasks
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */

class EditTaskFragment : Fragment(), View.OnFocusChangeListener {

    var task : Task = Task()
    var nonEditedTask : Task = Task()
    var isChecked : Boolean = false
    var subtaskAdapter : SubtasksRecyclerAdapter? = null
    var editing : Boolean = false

    companion object {

        const val TAG : String = "EditTaskFragment"

        fun instance(task : Task) : EditTaskFragment {
            val fragment = EditTaskFragment()
            fragment.task = task
            fragment.nonEditedTask = task.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
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
        //updateTasks()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    private fun initUi() {

        editTask_lottie_anim.setAnimation("anim_check2_1.json")

        editTask_lottie_anim.addValueCallback(
                KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                fun(it: LottieFrameInfo<ColorFilter>): PorterDuffColorFilter {
                    return PorterDuffColorFilter(context!!.resources.getColor(R.color.grey4), PorterDuff.Mode.SRC_ATOP)
                }
        )

        lv_remind.setAnimation("check_box2.json")
        lv_remind.speed = 1.5f

        if (task.isToRemind) {
            lv_remind.progress = 0.60f // on
        }

        editTask_et_taskName.imeOptions = EditorInfo.IME_ACTION_DONE
        editTask_et_taskName.setRawInputType(InputType.TYPE_CLASS_TEXT)

        editTask_et_taskName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                task.name = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        editTask_et_taskName.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editTask_tick_save.performClick()
            }
                true
        })

        editTask_et_desc.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                task.description = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTask_et_taskName.onFocusChangeListener = this
        editTask_et_desc.onFocusChangeListener = this

        editTask_lottie_anim.setOnClickListener {

            editTask_lottie_anim.setMinAndMaxProgress(0.0f, 1.0f)

            if (isChecked) {
                isChecked = false
                editTask_lottie_anim.progress = 0.0f
                editTask_lottie_anim.speed = -2f
                editTask_lottie_anim.playAnimation()
            } else {
                isChecked = true
                editTask_lottie_anim.progress = 1.0f
            }

            task.isTaskCompleted = isChecked
            FirestoreManager.getInstance().updateTask(task)
            TaskOrganiser.getInstance().organiseAllTasks()

            if (task.isTaskCompleted) {
                SuperdoAudioManager.getInstance().playTaskCompleted()
                FirestoreManager.getInstance().updateTask(task)
                EventBus.getDefault().post(TaskUpdatedEvent(TaskUpdateType.Task_Completed, task))
                fragmentManager!!.popBackStack()
            } else {
                updateTasks()
            }
        }

        editTask_tick_save.setOnClickListener {
            validate()
            editTask_et_taskName.clearFocus()
            editTask_et_desc.clearFocus()
            Utils.hideKeyboard(context, editTask_et_taskName)
            Utils.hideKeyboard(context, editTask_et_desc)
            FirestoreManager.getInstance().updateTask(task)
            updateUi()
            updateTasks()
        }

        if (task.isMovedToBin) {
            editTask_deleteTask.visibility = View.GONE
        } else {
            editTask_deleteTask.visibility = View.VISIBLE
        }

        ///////////////
        ///// SUBTASK RECYCLER
        //////////////

        editTask_et_add_subtask.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if( editTask_et_add_subtask.text.trim().isNotEmpty()) {

                    if (task.subtasks == null) {
                        task.subtasks = Subtasks()
                        task.subtasks.subtaskList = ArrayList()

                        setSubtaskRecycler()
                    }

                    var subtask = Subtask()
                    subtask.index = task.subtasks.subtaskList.size - 1
                    subtask.isTaskCompleted = false
                    subtask.title = editTask_et_add_subtask.text.toString()

                    task.subtasks.subtaskList.add(subtask)
                    subtaskAdapter!!.updateList()
                    FirestoreManager.getInstance().updateTask(task)
                    subtaskAdapter!!.notifyItemInserted(task.subtasks.subtaskList.size - 1)
                    editTask_et_add_subtask.setText("")
                    updateUi()
                } else {
                    Utils.hideKeyboard(context, editTask_et_add_subtask)
                    editTask_et_add_subtask.clearFocus()
                }
            }

            true
        }

        if (task.subtasks != null) {
            setSubtaskRecycler()
        }

    }

    fun setSubtaskRecycler() {

        editTask_rv_subtasks.layoutManager = LinearLayoutManager(activity)
        subtaskAdapter = SubtasksRecyclerAdapter(context, task.subtasks.subtaskList, task)
        val callback: ItemTouchHelper.Callback = LongItemTouchHelperCallback(subtaskAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(editTask_rv_subtasks)
        editTask_rv_subtasks.adapter = subtaskAdapter
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

        if (task.bucket != null) {
            editTask_tv_bucketName.text = task.bucket.name
            editTask_tv_bucketName.setTextColor(resources.getColor(R.color.black))

            when (BucketType.valueOf(task.bucket.bucketType)) {
                BucketType.Tasks -> editTask_iv_bucket.setImageResource(R.drawable.ic_bc_tasks_on)
                BucketType.Gym -> editTask_iv_bucket.setImageResource(R.drawable.ic_bc_gym_on)
                BucketType.Work -> editTask_iv_bucket.setImageResource(R.drawable.ic_bc_briefcase_on)
                BucketType.House -> editTask_iv_bucket.setImageResource(R.drawable.ic_bc_house_on)
                BucketType.Personal -> editTask_iv_bucket.setImageResource(R.drawable.ic_bc_personal_on)
            }
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

        if (task.repeat != null && task.repeat.repeatType != null) {
            editTask_iv_repeat.setImageResource(R.drawable.ic_repeat_on)
            editTask_tv_repeat.alpha = 1.0f
            editTask_tv_repeat.text = task.repeat.repeatString
        } else {
            editTask_iv_repeat.setImageResource(R.drawable.ic_repeat_off)
            editTask_tv_repeat.alpha = 0.5f
            editTask_tv_repeat.text = "Set Repeat Task"
        }

        //////////////
        //// REMIND

        if (task.isToRemind) {
            editTask_iv_remind.setImageResource(R.drawable.ic_remind_on)
            editTask_tv_remind.alpha = 1.0f
        } else {
            editTask_iv_remind.setImageResource(R.drawable.ic_remind_off)
            editTask_tv_remind.alpha = 0.5f
        }

        //////////////
        //// DEADLINE

        if (task.deadline != null && task.deadline.hasDate) {
            editTask_iv_deadline.setImageResource(R.drawable.ic_deadline_on)
            editTask_tv_deadline.alpha = 1.0f
            editTask_tv_deadline.text = task.deadline.deadlineStringMain
        } else {
            editTask_iv_deadline.setImageResource(R.drawable.ic_deadline_off)
            editTask_tv_deadline.alpha = 0.5f
            editTask_tv_deadline.text = "Set Deadline"
        }

        if (task.subtasks != null) {
            editTask_iv_subtasks.setImageResource(R.drawable.ic_subtasks_on)
            editTask_tv_subtasks.alpha = 1.0f
            ib_clear_subtasks.visibility = View.VISIBLE
        } else {
            editTask_iv_subtasks.setImageResource(R.drawable.ic_subtasks_off)
            editTask_tv_subtasks.alpha = 0.5f
            ib_clear_subtasks.visibility = View.GONE
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
            DoDateDialog.instance(task.doDate, task).show(fragmentManager!!, DoDateDialog().javaClass.name)
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

        ib_clear_subtasks.setOnClickListener {
            task.subtasks.subtaskList.clear()
            subtaskAdapter!!.updateList()
            task.subtasks = null
            editTask_et_add_subtask.clearFocus()
            FirestoreManager.getInstance().updateTask(task)
            updateUi()
        }

        lv_remind.setOnClickListener {
            task.isToRemind = !task.isToRemind

            updateUi()
            TaskOrganiser.getInstance().organiseAllTasks()
            FirestoreManager.getInstance().updateTask(task)

            if (task.isToRemind) {
                lv_remind.setMinAndMaxProgress(0.20f, 0.50f) // on

                SuperdoAlarmManager.getInstance().setRemind(context, task)
                if (task.deadline != null) {
                    SuperdoAlarmManager.getInstance().setDeadlineRemind(context, task)
                }

            } else {
                lv_remind.setMinAndMaxProgress(0.65f, 1.0f) // off
            }

            lv_remind.playAnimation()
        }

        ///// DELETE task
        editTask_deleteTask.setOnClickListener {
            TaskOrganiser.getInstance().moveToBin(task)
            EventBus.getDefault().post(TaskUpdatedEvent(TaskUpdateType.Deleted, task))
            fragmentManager!!.popBackStack()
        }

        editTask_btn_add_sidekicks.setOnClickListener {
            SelectSideKickDialog.instance(task).show(fragmentManager!!, SelectSideKickDialog().javaClass.name)
        }

        ///// Close Fragment
        editTask_close.setOnClickListener {
            FirestoreManager.getInstance().updateTask(task)
            fragmentManager!!.popBackStack()
        }

        ///// Mark done
    }

    fun validate() {
        if (task.name.isEmpty()) {
            task.name = nonEditedTask.name
            updateUi()
        }
    }

    fun updateTasks() {
        EventBus.getDefault().post(UpdateUiEvent())
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
    fun onMeessageEvent(event : SideKicksSelectedEvent) {
        task = event.task.clone()
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SelectBucketEvent) {
        task.bucketId = event.bucket.documentId
        updateUi()
        FirestoreManager.getInstance().updateTask(task)
    }

    //////////////
    /// SIDE KICK EVENT
    /////////////

    @Subscribe
    fun onMeessageEvent(event : UpdateSubtasksEvent) {
        if (task.subtasks.getSubtaskList().size == 0) {
            task.subtasks = null
            FirestoreManager.getInstance().updateTask(task)
        }
       updateUi()
    }

    @Subscribe
    fun onMeessageEvent(event : SetDoDateEvent) {
        task.doDate = event.superDate.clone()
        FirestoreManager.getInstance().updateTask(task)
        TaskOrganiser.getInstance().organiseAllTasks()
        updateUi()
        updateTasks()
    }

    @Subscribe
    fun onMeessageEvent(event : SetDeadlineEvent) {
        task.deadline = event.deadline.clone()

        if (event.deleted) {
            task.deadline = null
            FirestoreManager.getInstance().updateTask(task)
            updateUi()
            SuperdoAlarmManager.getInstance().clearAlarm(context, task.deadline.deadlineRequestCode)
            SuperdoAlarmManager.getInstance().clearAlarm(context, task.deadline.deadlineRequestCode + 1)
            SuperdoAlarmManager.getInstance().clearAlarm(context, task.deadline.deadlineRequestCode + 2)
            return
        }

        FirestoreManager.getInstance().updateTask(task)
        TaskOrganiser.getInstance().organiseAllTasks()
        updateUi()
        updateTasks()
    }

    @Subscribe
    fun onMeessageEvent(event : SetRepeatEvent) {
        task.repeat = event.repeat.clone()
        Utils.setNextDoDate(task)
        if (event.deleted) {
            task.repeat = null
        }
        FirestoreManager.getInstance().updateTask(task)
        TaskOrganiser.getInstance().organiseAllTasks()
        updateUi()
        updateTasks()
    }

    /*@Subscribe
    fun onMeessageEvent(event : SetRemindEvent) {
        task.remind = event.remind.clone()

        if (event.deleted) {

            task.remind = null
            FirestoreManager.getInstance().updateTask(task)
            updateUi()

            if (event.remind != null) {
                if (event.remind.remindType == RemindType.REMIND_REPEAT.name) {
                    SuperdoAlarmManager.getInstance().clearRemindRepeatAlarm(context, event.remind.remindRequestCode)
                } else {
                    SuperdoAlarmManager.getInstance().clearAlarm(context, event.remind.remindRequestCode)
                }
            }

            return
        }

        if (task.remind.remindRequestCode == 0) {
            task.remind.generateNewRequestCode()
        }
        FirestoreManager.getInstance().updateTask(task)
        SuperdoAlarmManager.getInstance().setRemindAlarm(context, task)

        updateUi()
    }*/


}
