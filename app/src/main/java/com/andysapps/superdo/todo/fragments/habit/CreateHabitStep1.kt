package com.andysapps.superdo.todo.fragments.habit


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.HabitCategory
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.model.Habit
import kotlinx.android.synthetic.main.fragment_create_habit_step1.*

/**
 * A simple [Fragment] subclass.
 */
class CreateHabitStep1 : Fragment() {

    var habit = Habit()
    var isEditing : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_habit_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initClicks()
        updateUi()
    }

    fun updateUi() {

        ch_iv_health.setBackgroundResource(R.drawable.bg_capsul_grey)
        ch_iv_money.setBackgroundResource(R.drawable.bg_capsul_grey)
        ch_iv_releationship.setBackgroundResource(R.drawable.bg_capsul_grey)
        ch_iv_phone.setBackgroundResource(R.drawable.bg_capsul_grey)
        ch_iv_mindfullness.setBackgroundResource(R.drawable.bg_capsul_grey)
        ch_iv_addiction.setBackgroundResource(R.drawable.bg_capsul_grey)

        when(habit.habitCategory) {
            HabitCategory.Health -> {
                ch_iv_health.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
            HabitCategory.Productivity -> {
                ch_iv_money.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
            HabitCategory.Releationship -> {
                ch_iv_releationship.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
            HabitCategory.DigitalWellbeign -> {
                ch_iv_phone.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
            HabitCategory.Mindfullness -> {
                ch_iv_mindfullness.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
            HabitCategory.Addiction -> {
                ch_iv_addiction.setBackgroundResource(R.drawable.bg_capsul_lightred)
            }
        }

    }

    fun initUi() {
        if (!isEditing) {
            habit.habitCategory = HabitCategory.Health
            habit.isCreatehabit = true
        }

        ch_lottie_create_habit.setAnimation("check_box2.json")
        ch_lottie_destroy_habit.setAnimation("check_box2.json")
        ch_lottie_create_habit.setSpeed(1.5f)
        ch_lottie_destroy_habit.setSpeed(1.5f)



        if (habit.isCreatehabit) {
            ch_lottie_create_habit.progress = 0.5f
            ch_lottie_destroy_habit.progress = 0.0f
        } else {
            ch_lottie_create_habit.progress = 0.0f
            ch_lottie_destroy_habit.progress = 0.5f
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
