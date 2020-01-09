package com.andysapps.superdo.todo.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.fragments.EditTaskFragment.Companion.instance
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep1
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep2
import com.andysapps.superdo.todo.model.Habit
import kotlinx.android.synthetic.main.activity_create_habit.*

class CreateHabitActivity : AppCompatActivity() {

    var habit : Habit? = null

    var isStep1 : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
        initUi()
        initClicks()
    }

    fun initUi() {

        habit = Habit()

        var fragment = CreateHabitStep1.instance(habit!!)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.ch_habitstep_container, fragment)
        ft.addToBackStack(fragment.javaClass.name)
        ft.commitAllowingStateLoss() // save the changes

        ch_btn_laststep.visibility = View.GONE
        ch_btn_nextstep.visibility = View.VISIBLE

    }

    fun initClicks() {

        ch_btn_nextstep.setOnClickListener {
            if (habit!!.name != null && habit!!.name.isNotEmpty()) {
                isStep1 = false
                var fragment = CreateHabitStep2.instance(habit!!)
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.ch_habitstep_container, fragment)
                ft.addToBackStack(fragment.javaClass.name)
                ft.commitAllowingStateLoss() // save the changes
                updateBottomUi()
            }
        }

        ch_btn_laststep.setOnClickListener {
            isStep1 = true
            supportFragmentManager.popBackStack()
            updateBottomUi()
        }

        ch_btn_create.setOnClickListener {

        }
    }

    fun updateBottomUi() {

        when (isStep1) {
            true -> {
                ch_btn_laststep.visibility = View.GONE
                ch_btn_nextstep.visibility = View.VISIBLE
                ch_iv_step1.setImageResource(R.drawable.img_dot_orange)
                ch_iv_step2.setImageResource(R.drawable.img_dot_grey1)
                ch_btn_create.visibility = View.GONE
            }

            false -> {
                ch_btn_laststep.visibility = View.VISIBLE
                ch_btn_nextstep.visibility = View.GONE
                ch_iv_step1.setImageResource(R.drawable.img_dot_grey1)
                ch_iv_step2.setImageResource(R.drawable.img_dot_orange)
                ch_btn_create.visibility = View.VISIBLE
            }
        }
    }

}
