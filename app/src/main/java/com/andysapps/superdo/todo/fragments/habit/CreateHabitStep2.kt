package com.andysapps.superdo.todo.fragments.habit


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.HabitCategory
import com.andysapps.superdo.todo.model.Habit
import kotlinx.android.synthetic.main.fragment_create_habit_step1.*
import kotlinx.android.synthetic.main.fragment_create_habit_step2.*

/**
 * A simple [Fragment] subclass.
 */
class CreateHabitStep2 : Fragment() {

    var habit = Habit()
    var isEditing : Boolean = false

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

    fun updateUi() {

    }

    fun initUi() {
        if (!isEditing) {
            habit.isEveryDay = true
        }

        ch_lottie_everyday.setAnimation("check_box2.json")
        ch_lottie_everyday.setSpeed(1.5f)

        ch_lottie_once_in_two_days.setAnimation("check_box2.json")
        ch_lottie_once_in_two_days.setSpeed(1.5f)

        ch_lottie_weekly.setAnimation("check_box2.json")
        ch_lottie_weekly.setSpeed(1.5f)

        ch_lottie_remind.setAnimation("check_box2.json")
        ch_lottie_remind.setSpeed(1.5f)

        ch_lottie_everyday.progress = 0.0f
        ch_lottie_once_in_two_days.progress = 0.0f
        ch_lottie_weekly.progress = 0.0f

        if (habit.isEveryDay) {
            ch_lottie_everyday.progress = 0.5f
        } else if (habit.isOnceInTwoDays) {
            ch_lottie_once_in_two_days.progress = 0.5f
        } else if (habit.isWeekly) {
            ch_lottie_weekly.progress = 0.5f
            ch_ll_weekdays.visibility = View.VISIBLE
        }

    }

    fun initClicks() {
        ch_btn_health.setOnClickListener {
            habit.habitCategory = HabitCategory.Health
            updateUi()
        }
        ch_btn_relationship.setOnClickListener {
            habit.habitCategory = HabitCategory.Releationship
            updateUi()
        }
        ch_btn_productivity.setOnClickListener {
            habit.habitCategory = HabitCategory.Productivity
            updateUi()
        }
        ch_btn_digital_well.setOnClickListener {
            habit.habitCategory = HabitCategory.DigitalWellbeign
            updateUi()
        }
        ch_btn_mindfullness.setOnClickListener {
            habit.habitCategory = HabitCategory.Mindfullness
            updateUi()
        }
        ch_btn_addiction.setOnClickListener {
            habit.habitCategory = HabitCategory.Addiction
            updateUi()
        }

        ch_lottie_create_habit.setOnClickListener {
            if (!habit.isCreatehabit) {
                habit.isCreatehabit = true
                updateLottie()
            }
        }

        ch_lottie_destroy_habit.setOnClickListener {
            if (habit.isCreatehabit) {
                habit.isCreatehabit = false
                updateLottie()
            }
        }
    }

    fun updateLottie() {
        if (habit.isCreatehabit) {
            ch_lottie_destroy_habit.setMinAndMaxProgress(0.70f, 1.0f)
            ch_lottie_create_habit.setMinAndMaxProgress(0.20f, 0.50f)
        } else {
            ch_lottie_create_habit.setMinAndMaxProgress(0.70f, 1.0f)
            ch_lottie_destroy_habit.setMinAndMaxProgress(0.20f, 0.50f)

        }

        ch_lottie_destroy_habit.playAnimation()
        ch_lottie_create_habit.playAnimation()
    }


}
