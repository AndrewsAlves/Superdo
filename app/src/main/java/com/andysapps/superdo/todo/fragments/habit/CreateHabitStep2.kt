package com.andysapps.superdo.todo.fragments.habit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.HabitGoalDay
import com.andysapps.superdo.todo.model.Habit
import com.andysapps.superdo.todo.model.SuperDate
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_create_habit.*
import kotlinx.android.synthetic.main.fragment_create_habit_step2.*
import kotlinx.android.synthetic.main.fragment_dlg_repeat.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class CreateHabitStep2 : Fragment(), TimePickerDialog.OnTimeSetListener {

    var habit = Habit()
    var isEditing : Boolean = false

    var lastHabitGoalDay = HabitGoalDay.EVERYDAY

    companion object {
        fun instance(habit : Habit) : Fragment {
            val fragment = CreateHabitStep2()
            fragment.habit = habit
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_habit_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initClicks()
        updateUi()
    }

    fun initUi() {
        if (!isEditing) {
            habit.habitGoalDay = HabitGoalDay.EVERYDAY
            habit.superTime = SuperDate(9, 0)
        } else {
            lastHabitGoalDay = habit.habitGoalDay
        }

        ch_lottie_everyday.setAnimation("check_box2.json")
        ch_lottie_everyday.speed = 1.5f

        ch_lottie_once_in_two_days.setAnimation("check_box2.json")
        ch_lottie_once_in_two_days.speed = 1.5f

        ch_lottie_weekly.setAnimation("check_box2.json")
        ch_lottie_weekly.speed = 1.5f

        ch_lottie_remind.setAnimation("check_box2.json")
        ch_lottie_remind.speed = 1.5f

        ch_lottie_everyday.progress = 0.0f
        ch_lottie_once_in_two_days.progress = 0.0f
        ch_lottie_weekly.progress = 0.0f
        ch_ll_weekdays.visibility = View.GONE


        when (habit.habitGoalDay) {
            HabitGoalDay.EVERYDAY -> {
                ch_lottie_everyday.progress = 0.5f
            }
            HabitGoalDay.ONCE_IN_TWO_DAYS -> {
                ch_lottie_once_in_two_days.progress = 0.5f
            }
            HabitGoalDay.WEEKLY -> {
                ch_lottie_weekly.progress = 0.5f
                ch_ll_weekdays.visibility = View.VISIBLE
            }
        }
    }

    fun updateUi() {
        if (habit.superTime != null) {
            ch_tv_time.text = habit.superTime.timeString
        }

        ch_tv_habit_desc.setText(habit.habitString)

        updateWeeklyUi()
    }

    fun updateWeeklyUi() {

        ch_weekly__btn_sunday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_sunday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_monday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_monday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_tuesday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_tuesday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_wednesday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_wednesday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_thursday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_thursday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_friday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_friday.setBackgroundResource(R.drawable.bg_grey_low)
        ch_weekly__btn_saturday.setTextColor(resources.getColor(R.color.grey4))
        ch_weekly__btn_saturday.setBackgroundResource(R.drawable.bg_grey_low)

        if (habit.isOnSunday) {
            ch_weekly__btn_sunday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_sunday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnMonday) {
            ch_weekly__btn_monday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_monday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnTuesday) {
            ch_weekly__btn_tuesday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_tuesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnWednesday) {
            ch_weekly__btn_wednesday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_wednesday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnThursday) {
            ch_weekly__btn_thursday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_thursday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnFriday) {
            ch_weekly__btn_friday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_friday.setBackgroundResource(R.drawable.bg_red_low)
        }
        if (habit.isOnSaturday) {
            ch_weekly__btn_saturday.setTextColor(resources.getColor(R.color.white))
            ch_weekly__btn_saturday.setBackgroundResource(R.drawable.bg_red_low)
        }
    }

    private fun initClicks() {
        ch_lottie_everyday.setOnClickListener {
            habit.habitGoalDay = HabitGoalDay.EVERYDAY
            updateLottie()
            updateUi()
        }
        ch_lottie_once_in_two_days.setOnClickListener {
            habit.habitGoalDay = HabitGoalDay.ONCE_IN_TWO_DAYS
            updateLottie()
            updateUi()
        }
        ch_lottie_weekly.setOnClickListener {
            habit.habitGoalDay = HabitGoalDay.WEEKLY
            if (habit.weeklyCount == 0) {
                habit.isOnMonday = true
                habit.isOnWednesday = true
                habit.isOnFriday = true
            }
            updateLottie()
            updateUi()
        }

        ch_tv_time.setOnClickListener {
            showTimePicker()
        }

        ch_weekly__btn_sunday.setOnClickListener {
            habit.isOnSunday = !habit.isOnSunday
            updateUi()
        }
        ch_weekly__btn_monday.setOnClickListener {
            habit.isOnMonday = !habit.isOnMonday
            updateUi()
        }
        ch_weekly__btn_tuesday.setOnClickListener {
            habit.isOnTuesday = !habit.isOnTuesday
            updateUi()
        }
        ch_weekly__btn_wednesday.setOnClickListener {
            habit.isOnWednesday = !habit.isOnWednesday
            updateUi()
        }
        ch_weekly__btn_thursday.setOnClickListener {
            habit.isOnThursday = !habit.isOnThursday
            updateUi()
        }
        ch_weekly__btn_friday.setOnClickListener {
            habit.isOnFriday = !habit.isOnFriday
            updateUi()
        }
        ch_weekly__btn_saturday.setOnClickListener {
            habit.isOnSaturday = !habit.isOnSaturday
            updateUi()
        }

        ch_lottie_remind.setOnClickListener {
            if (habit.isRemind) {
                habit.isRemind = false
                ch_lottie_remind.setMinAndMaxProgress(0.70f, 1.0f) // off
            } else {
                habit.isRemind = true
                ch_lottie_remind.setMinAndMaxProgress(0.20f, 0.50f) // on
            }

            ch_lottie_remind.playAnimation()

        }
    }

    fun updateLottie() {

        when (lastHabitGoalDay) {
            HabitGoalDay.EVERYDAY -> {
                ch_lottie_everyday.setMinAndMaxProgress(0.70f, 1.0f) // off
                ch_lottie_everyday.playAnimation()
            }
            HabitGoalDay.ONCE_IN_TWO_DAYS -> {
                ch_lottie_once_in_two_days.setMinAndMaxProgress(0.70f, 1.0f) // off
                ch_lottie_once_in_two_days.playAnimation()
            }
            HabitGoalDay.WEEKLY -> {
                ch_lottie_weekly.setMinAndMaxProgress(0.70f, 1.0f) // off
                ch_lottie_weekly.playAnimation()
                ch_ll_weekdays.visibility = View.GONE
            }
        }

        lastHabitGoalDay = habit.habitGoalDay

        when (habit.habitGoalDay) {
            HabitGoalDay.EVERYDAY -> {
                ch_lottie_everyday.setMinAndMaxProgress(0.20f, 0.50f) // on
                ch_lottie_everyday.playAnimation()
            }
            HabitGoalDay.ONCE_IN_TWO_DAYS -> {
                ch_lottie_once_in_two_days.setMinAndMaxProgress(0.20f, 0.50f) // on
                ch_lottie_once_in_two_days.playAnimation()
            }
            HabitGoalDay.WEEKLY -> {
                ch_lottie_weekly.setMinAndMaxProgress(0.20f, 0.50f) // on
                ch_lottie_weekly.playAnimation()
                ch_ll_weekdays.visibility = View.VISIBLE
            }
        }
    }

    fun showTimePicker() {

        var now = Calendar.getInstance()
        var hours : Int = now.get(Calendar.HOUR)
        var min : Int = now.get(Calendar.MINUTE)

        if (habit.superTime != null) {
            hours = habit.superTime.hours
            min = habit.superTime.minutes
        }

        var dpd = TimePickerDialog.newInstance(
                this,
                hours,
                min,
                false)

        dpd.accentColor = resources.getColor(R.color.lightRed)
        dpd.show(fragmentManager!!, "Datepickerdialog")
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        habit.superTime = SuperDate(hourOfDay, minute)
        updateUi()
    }


}
