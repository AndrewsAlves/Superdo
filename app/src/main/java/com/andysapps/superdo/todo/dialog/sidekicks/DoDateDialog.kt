package com.andysapps.superdo.todo.dialog.sidekicks


import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.dialog.alert.DeleteTaskDialog
import com.andysapps.superdo.todo.dialog.alert.DoDateWithRepeatAlertDialog
import com.andysapps.superdo.todo.events.ActionRemoveRepeatTask
import com.andysapps.superdo.todo.events.sidekick.SetDeadlineEvent
import com.andysapps.superdo.todo.events.sidekick.SetDoDateEvent
import com.andysapps.superdo.todo.model.SuperDate
import com.andysapps.superdo.todo.model.Task
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_dlg_deadline.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DoDateDialog : DialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var doDate : SuperDate = SuperDate()
    var task : Task? = null

    companion object {
        fun instance(superdate : SuperDate?, task: Task) : DoDateDialog {
            val fragment = DoDateDialog()
            if (superdate != null) {
                fragment.doDate = superdate.clone()
                fragment.task = task
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_dodate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        initUi()
        updateUi()
    }

    override fun onDismiss(dialog: DialogInterface) {
        EventBus.getDefault().unregister(this)
        super.onDismiss(dialog)
    }

    fun updateUi() {

        if (!doDate.isHasDate) {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_deadline_tv_date.setText(doDate.superDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_date.setImageResource(R.drawable.ic_do_date_on)
            dlg_deadline_tv_date.setText(doDate.superDateString)
            dlg_deadline_tv_date.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!doDate.isHasTime) {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_deadline_tv_time.setText(doDate.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_deadline_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_deadline_tv_time.setText(doDate.timeString)
            dlg_deadline_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }

        if (doDate.isHasDate) {
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
            if (task!!.repeat != null) {
                DoDateWithRepeatAlertDialog().show(fragmentManager!!, "removerepeat")
            } else {
                EventBus.getDefault().post(SetDoDateEvent(doDate))
                dismiss()
            }
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

        if (doDate.date != 0) {
            day = doDate.date
            month = doDate.month
            year = doDate.year
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

        hours = doDate.hours
        min = doDate.minutes

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
        doDate.setDoDate(dayOfMonth, monthOfYear + 1, year)
        doDate.hasDate = true
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        doDate.setTime(hourOfDay, minute)
        doDate.hasTime = true
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : ActionRemoveRepeatTask) {
        if (event.isPositive) {
            task!!.repeat = null
            EventBus.getDefault().post(SetDoDateEvent(doDate))
            dismiss()
        }
    }

}
