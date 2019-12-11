package com.andysapps.superdo.todo.dialog.sidekicks.childfragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.dialog.sidekicks.RemindDialog
import com.andysapps.superdo.todo.enums.RemindType
import com.andysapps.superdo.todo.model.sidekicks.Remind
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_dlg_remind.*
import kotlinx.android.synthetic.main.fragment_repeat_once.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class RepeatOnceFragment : Fragment() ,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    var remind : Remind = Remind()

    companion object {
        fun instance(remind : Remind) : RepeatOnceFragment {
            val fragment = RepeatOnceFragment()
            fragment.remind = remind.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repeat_once, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateUi()
    }

    fun initView() {

        if (remind.remindType == null) {
            remind.remindType = RemindType.REMIND_ONCE
        }

        /////////
        // CLICKS

        dlg_remind_btn_date.setOnClickListener {
            showDatePicker()
        }

        dlg_remind_btn_time.setOnClickListener {
            showTimePicker()
        }

        dlg_remind_b_positive.setOnClickListener {
            // EventBus.getDefault().post(SetDeadlineEvent(deadline))
        }

        dlg_remind_b_negative.setOnClickListener {
           // dismiss()
        }

    }

    fun updateUi() {

        ///////////
        /// REPEAT ONCE UI
        ///

        if (remind.remindOnce == null) {
            remind.remindOnce = Utils.getSuperdateFromTimeStamp(Utils.getTomorrow().timeInMillis)
            remind.remindOnce.hasDate = true
            remind.remindOnce.setTime(9, 0)
        }

        if (!remind.remindOnce.hasDate) {
            dlg_remind_iv_date.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_remind_tv_date.setText(remind.remindOnce.superDateString)
            dlg_remind_tv_date.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_remind_iv_date.setImageResource(R.drawable.ic_do_date_on)
            dlg_remind_tv_date.setText(remind.remindOnce.superDateString)
            dlg_remind_tv_date.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!remind.remindOnce.hasTime) {
            dlg_remind_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_remind_tv_time.setText(remind.remindOnce.timeString)
            dlg_remind_tv_time.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_remind_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_remind_tv_time.setText(remind.remindOnce.timeString)
            dlg_remind_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }


        // button ui
        if (remind.remindOnce.hasDate) {
            dlg_remind_b_positive.isClickable = true
            dlg_remind_b_positive.alpha = 1f
        } else {
            dlg_remind_b_positive.isClickable = false
            dlg_remind_b_positive.alpha = 0.5f
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
