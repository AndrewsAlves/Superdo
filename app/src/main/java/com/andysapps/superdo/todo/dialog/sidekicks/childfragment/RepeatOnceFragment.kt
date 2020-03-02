package com.andysapps.superdo.todo.dialog.sidekicks.childfragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.enums.RemindType
import com.andysapps.superdo.todo.events.sidekick.SetRemindOnceEvent
import com.andysapps.superdo.todo.events.ui.DismissRemindDialogEvent
import com.andysapps.superdo.todo.model.sidekicks.Remind
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_remind_once.*
import org.greenrobot.eventbus.EventBus
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
        return inflater.inflate(R.layout.fragment_remind_once, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateUi()
    }

    fun initView() {

        if (remind.remindType == null) {
            remind.remindType = RemindType.REMIND_ONCE.name
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
             EventBus.getDefault().post(SetRemindOnceEvent(remind, false))
        }

        dlg_remind_b_negative.setOnClickListener {
            EventBus.getDefault().post(DismissRemindDialogEvent())
        }

        dlg_remind_delete_repeat.setOnClickListener {
            EventBus.getDefault().post(SetRemindOnceEvent(remind, true))
            EventBus.getDefault().post(DismissRemindDialogEvent())
        }

    }

    fun updateUi() {

        ///////////
        /// REPEAT ONCE UI
        ///

        if (remind.remindDate == null) {
            remind.remindDate = Utils.getSuperdateFromTimeStamp(Utils.getTomorrow().timeInMillis)
            remind.remindDate.hasDate = true
            remind.remindDate.setTime(9, 0)
        }

        if (!remind.remindDate.hasDate) {
            dlg_remind_iv_date.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_remind_tv_date.setText(remind.remindDate.superDateString)
            dlg_remind_tv_date.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_remind_iv_date.setImageResource(R.drawable.ic_do_date_on)
            dlg_remind_tv_date.setText(remind.remindDate.superDateString)
            dlg_remind_tv_date.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!remind.remindDate.hasTime) {
            dlg_remind_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_remind_tv_time.setText(remind.remindDate.timeString)
            dlg_remind_tv_time.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_remind_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_remind_tv_time.setText(remind.remindDate.timeString)
            dlg_remind_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }


        // button ui
        if (remind.remindDate.hasDate) {
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

        if (remind.remindDate.date != 0) {
            day = remind.remindDate.date
            month = remind.remindDate.month
            year = remind.remindDate.year
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

        hours = remind.remindDate.hours
        min = remind.remindDate.minutes

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
        remind.remindDate.setDoDate(dayOfMonth, monthOfYear + 1, year)
        remind.remindDate.hasDate = true
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        remind.remindDate.setTime(hourOfDay, minute)
        remind.remindDate.hasTime = true
        updateUi()
    }


}
