package com.andysapps.superdo.todo.dialog.sidekicks.childfragment


import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.andysapps.superdo.todo.Utils.monthDates
import com.andysapps.superdo.todo.enums.RepeatFragmentType
import com.andysapps.superdo.todo.enums.RepeatType
import com.andysapps.superdo.todo.events.sidekick.SetRemindRepeatEvent
import com.andysapps.superdo.todo.events.sidekick.SetRepeatEvent
import com.andysapps.superdo.todo.events.ui.DismissRemindDialogEvent
import com.andysapps.superdo.todo.model.SuperDate
import com.andysapps.superdo.todo.model.sidekicks.Repeat
import com.andysapps.superdo.todo.model.sidekicks.WeekDays
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_dlg_deadline.*
import kotlinx.android.synthetic.main.fragment_dlg_repeat.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.math.min


/**
 * A simple [Fragment] subclass.
 */

class RepeatFragment : Fragment(), OnItemSelectedListener, View.OnClickListener, TextWatcher, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    var repeat : Repeat = Repeat()
    val repeatType = arrayOf(RepeatType.Day.name, RepeatType.Week.name, RepeatType.Month.name)
    var repeatFragmentType : RepeatFragmentType = RepeatFragmentType.REPEAT_REMINDER

    companion object {
        fun instance(repeat : Repeat, fragmentType: RepeatFragmentType) : RepeatFragment {
            val repeatDialog = RepeatFragment()
            repeatDialog.repeat = repeat.clone()
            repeatDialog.repeatFragmentType = fragmentType
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
        dlg_repeat_spinner_dwm.adapter = repeatTypes

        val monthDates = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, monthDates)
        monthDates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dlg_repeat_spinner_monthdate.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                repeat.monthDaysIndex = position
                dlg_repeat_tv_monthdates.text = monthDates.getItem(position)
                updateUi()
            }
        })

        dlg_repeat_spinner_monthdate.adapter = monthDates

        dlg_repeat_spinner_monthdate.setSelection(0)

        ////// Ui
        dlg_repeat_et_days.addTextChangedListener(this)

        dlg_repeat__btn_monday.setOnClickListener(this)
        dlg_repeat__btn_tuesday.setOnClickListener(this)
        dlg_repeat__btn_wednesday.setOnClickListener(this)
        dlg_repeat__btn_thursday.setOnClickListener(this)
        dlg_repeat__btn_friday.setOnClickListener(this)
        dlg_repeat__btn_saturday.setOnClickListener(this)
        dlg_repeat__btn_sunday.setOnClickListener(this)

        dlg_repeat_b_positive.setOnClickListener {
            when(repeatFragmentType) {
                RepeatFragmentType.REPEAT_REMINDER -> {
                    EventBus.getDefault().post(SetRemindRepeatEvent(repeat))
                }
            }
            //dismiss()
        }

        dlg_repeat_b_negative.setOnClickListener {
            when(repeatFragmentType) {
                RepeatFragmentType.REPEAT_REMINDER -> {
                    EventBus.getDefault().post(DismissRemindDialogEvent())
                }
            }
           // dismiss()
        }

        dlg_repeat_delete_repeat.setOnClickListener {
            EventBus.getDefault().post(SetRepeatEvent(Repeat(true)))
            // dismiss()
        }

        dlg_repeat_btn_date.setOnClickListener {
            showDatePicker()
        }

        dlg_repeat_btn_time.setOnClickListener {
            showTimePicker()
        }



        //////// UPDATE UI ////////

        if (repeat.repeatType != null) {

            if (repeat.times == 0) {
                repeat.times = 1
            }

            when (repeat.repeatType) {
                RepeatType.Day -> {
                    dlg_repeat_spinner_dwm.setSelection(0)
                }
                RepeatType.Week -> {
                    dlg_repeat_spinner_dwm.setSelection(1)
                }
                RepeatType.Month -> {
                    dlg_repeat_spinner_dwm.setSelection(2)
                    dlg_repeat_spinner_monthdate.setSelection(repeat.monthDaysIndex)
                }
            }

            dlg_repeat_delete_repeat.visibility = View.VISIBLE

        } else {
            repeat.times = 1
            dlg_repeat_delete_repeat.visibility = View.GONE
        }
    }

    fun updateUi() {

        if (repeat.repeatType == RepeatType.Week) {
            dlg_repeat_ll_weekdays.visibility = View.VISIBLE
            updateWeedaysUi()
        } else {
            dlg_repeat_ll_weekdays.visibility = View.GONE
        }

        if (repeat.repeatType == RepeatType.Month) {
            dlg_repeat_spinner_monthdate.visibility = View.VISIBLE
            dlg_repeat_et_days.visibility = View.GONE
            //dlg_repeat_tv_monthdates.visibility = View.VISIBLE
            dlg_repeat_tv_monthdates.text = monthDates[repeat.monthDaysIndex]
        } else {
            dlg_repeat_spinner_monthdate.visibility = View.GONE
            dlg_repeat_tv_monthdates.visibility = View.GONE
            dlg_repeat_et_days.visibility = View.VISIBLE
        }

        if (repeat.startDate == null) {
            repeat.startDate = Utils.getSuperdateFromTimeStamp(Calendar.getInstance().time.time)
            repeat.startDate.hasDate = true
            repeat.startDate.setTime(9, 0)
        }

        if (!repeat.startDate.hasDate) {
            dlg_repeat_iv_startdate.setImageResource(R.drawable.ic_do_date_off_grey_2)
            dlg_repeat_tv_startdate.text = repeat.startDate.superDateString
            dlg_repeat_tv_startdate.setTextColor(resources.getColor(R.color.grey1))
        } else {
            dlg_repeat_iv_startdate.setImageResource(R.drawable.ic_do_date_on)
            dlg_repeat_tv_startdate.text = repeat.startDate.superDateString
            dlg_repeat_tv_startdate.setTextColor(resources.getColor(R.color.grey4))
        }

        if (!repeat.startDate.hasTime) {
            dlg_repeat_iv_time.setImageResource(R.drawable.ic_time_off)
            dlg_repeat_tv_time.text = repeat.startDate.timeString
            dlg_repeat_tv_time.setTextColor(resources.getColor(R.color.grey2))
        } else {
            dlg_repeat_iv_time.setImageResource(R.drawable.ic_time_on)
            dlg_repeat_tv_time.text = repeat.startDate.timeString
            dlg_repeat_tv_time.setTextColor(resources.getColor(R.color.grey4))
        }

        dlg_repeat_tv_repeatString.text = repeat.repeatString


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

        if (repeat.isOnSunday) {
            dlg_repeat__btn_sunday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_sunday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnMonday) {
            dlg_repeat__btn_monday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_monday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnTuesday) {
            dlg_repeat__btn_tuesday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_tuesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnWednesday) {
            dlg_repeat__btn_wednesday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_wednesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnThursday) {
            dlg_repeat__btn_thursday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_thursday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnFriday) {
            dlg_repeat__btn_friday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_friday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (repeat.isOnSaturday) {
            dlg_repeat__btn_saturday.setTextColor(resources.getColor(R.color.white))
            dlg_repeat__btn_saturday.setBackgroundResource(R.drawable.bg_red_low)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        repeat.repeatType = RepeatType.valueOf(repeatType[position])
        if (repeat.repeatType == RepeatType.Week) {
            updateWeedaysUi()
        }
        dlg_repeat_et_days.setText(repeat.times.toString())
        updateUi()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            dlg_repeat__btn_monday.id -> {
                repeat.isOnMonday = !repeat.isOnMonday
            }
            dlg_repeat__btn_tuesday.id -> {
                repeat.isOnTuesday = !repeat.isOnTuesday
            }
            dlg_repeat__btn_wednesday.id -> {
                repeat.isOnWednesday = !repeat.isOnWednesday
            }
            dlg_repeat__btn_thursday.id -> {
                repeat.isOnThursday = !repeat.isOnThursday
            }
            dlg_repeat__btn_friday.id -> {
                repeat.isOnFriday = !repeat.isOnFriday
            }
            dlg_repeat__btn_saturday.id -> {
                repeat.isOnSaturday = !repeat.isOnSaturday
            }
            dlg_repeat__btn_sunday.id -> {
                repeat.isOnSunday = !repeat.isOnSunday
            }
        }

        updateWeedaysUi()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s!!.isNotEmpty()) {
            repeat.times = s.toString().toInt()
        }
        updateUi()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


    ///////////////
    ////// TIME PICKER
    ///////////////


    fun showDatePicker() {
        val now = Calendar.getInstance()
        var day = now.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR)

        if (repeat.startDate != null && repeat.startDate.hasDate) {
            day = repeat.startDate.date
            month = repeat.startDate.month
            year = repeat.startDate.year
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


        if (repeat.startDate != null && repeat.startDate.hasTime) {
            hours = repeat.startDate.hours
            min = repeat.startDate.minutes
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
        repeat.startDate.setDoDate(dayOfMonth, monthOfYear + 1, year)
        repeat.startDate.hasDate = true
        updateUi()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        repeat.startDate.setTime(hourOfDay, minute)
        repeat.startDate.hasTime = true
        updateUi()
    }

}
