package com.andysapps.superdo.todo.dialog.sidekicks


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.enums.RepeatType
import com.andysapps.superdo.todo.model.sidekicks.Repeat
import com.andysapps.superdo.todo.model.sidekicks.WeekDays
import kotlinx.android.synthetic.main.fragment_dlg_repeat.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */

class RepeatDialog : DialogFragment(), OnItemSelectedListener, View.OnClickListener{

    var repeat : Repeat = Repeat()
    val repeatType = arrayOf(RepeatType.Day.name, RepeatType.Week.name, RepeatType.Month.name)
    val monthDates = arrayOf("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th",
            "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th", "31st")


    companion object {
        fun instance(repeat : Repeat) : RepeatDialog {
            val repeatDialog = RepeatDialog()
            repeatDialog.repeat = repeat.clone()
            return repeatDialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_repeat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniiUi()
        updateUi()
    }

    fun iniiUi() {

        val repeatTypes = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, repeatType)
        repeatTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dlg_repeat_spinner_dwm.onItemSelectedListener = this
        dlg_repeat_spinner_dwm.setAdapter(repeatTypes)
        dlg_repeat_spinner_dwm.setSelection(0)
        
        val monthDates = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, monthDates)
        monthDates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dlg_repeat_spinner_monthdate.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        })

        dlg_repeat_spinner_monthdate.adapter = monthDates
        dlg_repeat_spinner_monthdate.setSelection(0)

        dlg_repeat__btn_monday.setOnClickListener(this)
        dlg_repeat__btn_tuesday.setOnClickListener(this)
        dlg_repeat__btn_wednesday.setOnClickListener(this)
        dlg_repeat__btn_thursday.setOnClickListener(this)
        dlg_repeat__btn_friday.setOnClickListener(this)
        dlg_repeat__btn_saturday.setOnClickListener(this)
        dlg_repeat__btn_sunday.setOnClickListener(this)

    }

    fun updateUi() {

        if (repeat.repeatType == RepeatType.Week) {
            dlg_repeat_ll_weekdays.visibility = View.VISIBLE
            repeat.weekDays = WeekDays()
            updateWeedaysUi()
        } else {
            dlg_repeat_ll_weekdays.visibility = View.GONE
        }

        if (repeat.repeatType == RepeatType.Month) {
            dlg_repeat_spinner_monthdate.visibility = View.VISIBLE
            dlg_repeat_et_days.visibility = View.GONE
        } else {
            dlg_repeat_spinner_monthdate.visibility = View.GONE
            dlg_repeat_et_days.visibility = View.VISIBLE
        }

        if (repeat.startDate == null) {
            repeat.startDate = Utils.getSuperdateFromTimeStamp(Calendar.getInstance().time.time)
            repeat.startDate.hasDate = true
        }

        dlg_repeat_iv_startdate.setImageResource(R.drawable.ic_do_date_on)
        dlg_repeat_tv_startdate.setText(repeat.startDate.superDateString)
        dlg_repeat_tv_startdate.setTextColor(resources.getColor(R.color.grey4))

    }

    fun updateWeedaysUi() {

        dlg_repeat__btn_sunday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_sunday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_monday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_monday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_tuesday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_tuesday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_wednesday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_wednesday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_thursday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_thursday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_friday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_friday.setBackgroundResource(R.drawable.bg_grey_low)
        dlg_repeat__btn_saturday.setTextColor(resources.getColor(R.color.grey4))
        dlg_repeat__btn_saturday.setBackgroundResource(R.drawable.bg_grey_low)

        if (repeat.weekDays.isOnSunday) {
            dlg_repeat__btn_sunday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_sunday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnMonday) {
            dlg_repeat__btn_monday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_monday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnTuesday) {
            dlg_repeat__btn_tuesday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_tuesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnWednesday) {
            dlg_repeat__btn_wednesday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_wednesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnThursday) {
            dlg_repeat__btn_thursday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_thursday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnFriday) {
            dlg_repeat__btn_friday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_friday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.weekDays.isOnSaturday) {
            dlg_repeat__btn_saturday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_saturday.setBackgroundResource(R.drawable.bg_red_low)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        repeat.repeatType = RepeatType.valueOf(repeatType[position])
        updateUi()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            dlg_repeat__btn_monday.id -> {
                repeat.weekDays.isOnMonday = !repeat.weekDays.isOnMonday
            }
            dlg_repeat__btn_tuesday.id -> {
                repeat.weekDays.isOnTuesday = !repeat.weekDays.isOnTuesday
            }
            dlg_repeat__btn_wednesday.id -> {
                repeat.weekDays.isOnWednesday = !repeat.weekDays.isOnWednesday
            }
            dlg_repeat__btn_thursday.id -> {
                repeat.weekDays.isOnThursday = !repeat.weekDays.isOnThursday
            }
            dlg_repeat__btn_friday.id -> {
                repeat.weekDays.isOnFriday = !repeat.weekDays.isOnFriday
            }
            dlg_repeat__btn_saturday.id -> {
                repeat.weekDays.isOnSaturday = !repeat.weekDays.isOnSaturday
            }
            dlg_repeat__btn_sunday.id -> {
                repeat.weekDays.isOnSunday = !repeat.weekDays.isOnSunday
            }
        }
        updateWeedaysUi()
    }

}
