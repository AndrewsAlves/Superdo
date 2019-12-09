package com.andysapps.superdo.todo.dialog.sidekicks


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.enums.RemindType
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.model.sidekicks.Remind
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_dlg_deadline.*
import kotlinx.android.synthetic.main.fragment_dlg_remind.*
import kotlinx.android.synthetic.main.fragment_dlg_remind.dlg_deadline_iv_date
import kotlinx.android.synthetic.main.fragment_dlg_remind.dlg_deadline_iv_time
import kotlinx.android.synthetic.main.fragment_dlg_remind.dlg_deadline_tv_date
import kotlinx.android.synthetic.main.fragment_dlg_remind.dlg_deadline_tv_time
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class RemindDialog : DialogFragment() ,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    var remind : Remind = Remind()

    companion object {
        fun instance(remind : Remind) : RemindDialog {
            val fragment = RemindDialog()
            fragment.remind = remind.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_remind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun initView() {

        /////////
        // CLICKS

        dlg_remind_btn_remind_once.setOnClickListener {
            remind.repeatType = RemindType.REMIND_ONCE
            updateUi()
        }

        dlg_remind_btn_repeat_remind.setOnClickListener {
            remind.repeatType = RemindType.REMIND_REPEAT
            updateUi()
        }

        dlg_deadline_btn_date.setOnClickListener {
            showDatePicker()
        }

        dlg_deadline_btn_time.setOnClickListener {
            showTimePicker()
        }

        dlg_remind_b_positive.setOnClickListener {
           // EventBus.getDefault().post(SetDeadlineEvent(deadline))
            dismiss()
        }

        dlg_remind_b_negative.setOnClickListener {
            dismiss()
        }

    }

    fun updateUi() {

        dlg_remind_btn_remind_once.setImageResource(R.drawable.ic_dlg_remind_off)
        dlg_remind_btn_repeat_remind.setImageResource(R.drawable.ic_dlg_repeat_remind_off)

        when (remind.repeatType) {

            RemindType.REMIND_ONCE -> {
                dlg_remind_btn_remind_once.setImageResource(R.drawable.ic_dlg_remind_on)
                dlg_remind_remind_once_ll_parent.visibility = View.VISIBLE
                dlg_remind_repeat_remind_ll_parent.visibility = View.GONE

            }

            RemindType.REMIND_REPEAT -> {
                dlg_remind_btn_repeat_remind.setImageResource(R.drawable.ic_dlg_repeat_remind_on)
                dlg_remind_remind_once_ll_parent.visibility = View.VISIBLE
                dlg_remind_repeat_remind_ll_parent.visibility = View.GONE
            }
        }


        ///////////
        /// REPEAT ONCE UI
        ///

        if (!remind.remindOnce.hasDate) {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_deadline_tv_date.setText(remind.remindOnce.superDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_on)
            dlg_deadline_tv_date.setText(remind.remindOnce.superDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!remind.remindOnce.hasTime) {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_deadline_tv_time.setText(remind.remindOnce.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_deadline_tv_time.setText(remind.remindOnce.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }


        // button ui
        if (remind.remindOnce.hasDate) {
            dlg_deadline_b_positive.isClickable = true
            dlg_deadline_b_positive.alpha = 1f
        } else {
            dlg_deadline_b_positive.isClickable = false
            dlg_deadline_b_positive.alpha = 0.5f
        }

    }

    fun showDatePicker() {
        val now = Calendar.getInstance()
        var day = now.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR)

        if (remind.remindOnce.date != 0) {
            day = remind.remindOnce.date
            month = remind.remindOnce.month
            year = remind.remindOnce.year
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

        hours = remind.remindOnce.hours
        min = remind.remindOnce.minutes

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
        remind.remindOnce.setDoDate(dayOfMonth, monthOfYear + 1, year)
        remind.remindOnce.hasDate = true
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        remind.remindOnce.setTime(hourOfDay, minute)
        remind.remindOnce.hasTime = true
        updateUi()
    }


}
