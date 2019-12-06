package com.andysapps.superdo.todo.fragments


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
import com.andysapps.superdo.todo.dialog.SelectBucketDialogFragment
import com.andysapps.superdo.todo.dialog.SelectSideKickDialog
import com.andysapps.superdo.todo.dialog.sidekicks.DeadlineDialog
import com.andysapps.superdo.todo.dialog.sidekicks.RepeatDialog
import com.andysapps.superdo.todo.enums.BucketColors
import com.andysapps.superdo.todo.enums.TaskUpdateType
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.action.SelectBucketEvent
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents
import com.andysapps.superdo.todo.events.ui.SideKicksSelectedEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.SuperDate
import com.andysapps.superdo.todo.model.Task
import com.andysapps.superdo.todo.model.sidekicks.Deadline
import com.andysapps.superdo.todo.model.sidekicks.Repeat
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * A simple [Fragment] subclass.
 */

class EditTaskFragment : Fragment() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


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

        // set Dodate
        if (task.doDate != null) {
            editTask_iv_do_date.setImageResource(R.drawable.ic_do_date_on)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey4))
        } else {
            editTask_iv_do_date.setImageResource(R.drawable.ic_do_date_off)
            editTask_tv_do_date.text = task.doDateString2
            editTask_tv_do_date.setTextColor(resources.getColor(R.color.grey2))
        }

        if (task.repeat == null) {
            task.repeat = Repeat(true)
        }

        if (task.repeat != null && task.repeat.isEnabled) {
            editTask_tv_repeat_name.visibility
            editTask_rl_btn_repeat.visibility = View.VISIBLE
        } else {
            editTask_rl_btn_repeat.visibility = View.GONE
        }

        if (task.remind != null && task.remind.isEnabled) {
            editTask_rl_btn_remind.visibility = View.VISIBLE
        } else {
            editTask_rl_btn_remind.visibility = View.GONE
        }

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

        if (task.subtasks != null && task.subtasks.isEnabled) {
            editTask_ll_subtasks.visibility = View.VISIBLE
        } else {
            editTask_ll_subtasks.visibility = View.GONE
        }

        if (task.focus != null && task.focus.isEnabled) {
            editTask_rl_btn_focus.visibility = View.VISIBLE
            editTask_btn_start_focus.visibility = View.VISIBLE
        } else {
            editTask_rl_btn_focus.visibility = View.GONE
            editTask_btn_start_focus.visibility = View.GONE
        }

        // Task created date
        if (task.created != null) {
            editTask_tv_createdDate.text = Utils.getDateString(task.created)
        }

    }

    private fun initClicks() {

        editTask_rl_btn_do_date.setOnClickListener {
            showDatePicker()
        }

        editTask_rl_btn_select_bucket.setOnClickListener {
            SelectBucketDialogFragment().show(fragmentManager!!, SelectBucketDialogFragment().javaClass.name)
        }

        editTask_rl_btn_deadline.setOnClickListener {
            DeadlineDialog.instance(task.deadline).show(fragmentManager!!, DeadlineDialog().javaClass.name)
        }

        editTask_rl_btn_repeat.setOnClickListener {
            RepeatDialog.instance(task.repeat).show(fragmentManager!!, DeadlineDialog().javaClass.name)
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
    }

    fun showDatePicker() {
        val now = Calendar.getInstance()
        var day = now.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR)

        if (task.doDate != null) {
            day = task.doDate.date
            month = task.doDate.month
            year = task.doDate.year
        }

        val dpd = DatePickerDialog.newInstance(
                this,
                year,
                month - 1,
                day
        )

        dpd.accentColor = resources.getColor(R.color.lightRed)
        dpd.minDate = Utils.getStartDate()
        dpd.maxDate = Utils.getEndDate()
        dpd.show(fragmentManager!!, "Datepickerdialog")
    }

    fun showTimePicker() {

        var now = Calendar.getInstance()
        var hours : Int = now.get(Calendar.HOUR)
        var min : Int = now.get(Calendar.MINUTE)

        if (task.doDate != null) {
            hours = task.doDate.hours
            min = task.doDate.minutes
        }

        var dpd = TimePickerDialog.newInstance(
                this,
                hours,
                min,
                false)

        dpd.accentColor = resources.getColor(R.color.lightRed)
        dpd.show(fragmentManager!!, "Datepickerdialog")
    }

    private fun shouldUpdate() : Boolean {
        if (task.name.isEmpty()) {
            task.name = nonEditedTask.name
            return true
        }
        return false
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = SuperDate(dayOfMonth, monthOfYear + 1, year)
        if (task.doDate != null) {
            task.doDate.setDoDate(dayOfMonth, monthOfYear + 1, year)
        } else {
            task.doDate = date
        }
        task.doDate.hasDate = true
        showTimePicker()
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val time = SuperDate(hourOfDay, minute)
        if (task.doDate != null) {
            task.doDate.setTime(hourOfDay, minute)
        } else {
            task.doDate = time
        }
        task.doDate.hasTime = true
        updateUi()
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
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SelectBucketEvent) {
        task.bucketColor = event.bucket.tagColor
        task.bucketId = event.bucket.documentId
        task.bucketName = event.bucket.name
        updateUi()
    }

    //////////////
    /// SIDE KICK EVENT
    /////////////

    @Subscribe
    fun onMeessageEvent(event : SetDeadlineEvent) {
        task.deadline = event.deadline.clone()
        updateUi()
    }

}
