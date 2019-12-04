package com.andysapps.superdo.todo.dialog.sidekicks


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.dialog.SelectSideKickDialog
import com.andysapps.superdo.todo.events.DeleteTaskEvent
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.model.SuperDate
import com.andysapps.superdo.todo.model.Task
import com.andysapps.superdo.todo.model.sidekicks.Deadline
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_delete_task_dialog.*
import kotlinx.android.synthetic.main.fragment_dlg_deadline.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DeadlineDialog : DialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    var deadline : Deadline = Deadline()

    companion object {
        fun instance(deadline : Deadline) : DeadlineDialog {
            val fragment = DeadlineDialog()
            fragment.deadline = deadline.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_deadline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        updateUi()
    }


    fun updateUi() {

        if (!deadline.hasDate) {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_deadline_tv_date.setText(deadline.deadlineDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_on)
            dlg_deadline_tv_date.setText(deadline.deadlineDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!deadline.hasTime) {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_deadline_tv_time.setText(deadline.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_deadline_tv_time.setText(deadline.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }

        if (deadline.hasDate) {
            dlg_deadline_b_positive.isClickable = true
            dlg_deadline_b_positive.alpha = 1f
        } else {
            dlg_deadline_b_positive.isClickable = false
            dlg_deadline_b_positive.alpha = 0.5f
        }

    }

    private fun initUi() {

        dlg_deadline_btn_date.setOnClickListener {
            showDatePicker()
        }

        dlg_deadline_btn_time.setOnClickListener {
            showTimePicker()
        }

        dlg_deadline_b_positive.setOnClickListener {
            EventBus.getDefault().post(SetDeadlineEvent(deadline))
            dismiss()
        }

        dlg_deadline_b_negative.setOnClickListener {
            dismiss()
        }
    }

    fun showDatePicker() {
        val now = Calendar.getInstance()
        var day = now.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR)

        if (deadline.date != 0) {
            day = deadline.date
            month = deadline.month
            year = deadline.year
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

        hours = deadline.hours
        min = deadline.minutes

        if (hours != 0) {

        }

        var dpd = TimePickerDialog.newInstance(
              this,
                hours,
                min,
                false)

        dpd.accentColor = resources.getColor(R.color.lightRed)
        dpd.show(fragmentManager!!, "Datepickerdialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        deadline.setDoDate(dayOfMonth, monthOfYear + 1, year)
        deadline.hasDate = true
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        deadline.setTime(hourOfDay, minute)
        deadline.hasTime = true
        updateUi()
    }

}
