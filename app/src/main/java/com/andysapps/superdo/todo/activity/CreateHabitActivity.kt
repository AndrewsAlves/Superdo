package com.andysapps.superdo.todo.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.enums.TaskListing
import com.andysapps.superdo.todo.events.habit.UploadHabitSuccessEvent
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep1
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep2
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Habit
import kotlinx.android.synthetic.main.activity_create_habit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CreateHabitActivity : AppCompatActivity() {

    var habit : Habit? = null

    var isStep1 : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // EventBus.getDefault().register(this)
        setContentView(R.layout.activity_create_habit)
        initUi()
        initClicks()
    }

    override fun onDestroy() {
        //EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!isStep1) {
            isStep1 = true
            updateBottomUi()
        } else {
            finish()
        }
        super.onBackPressed()
    }

    fun initUi() {

        habit = Habit()
        habit!!.userId = FirestoreManager.getInstance().userId

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
            if (habit!!.habitTitle != null && habit!!.habitTitle.isNotEmpty()) {
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
            habit!!.habitIndex = TaskOrganiser.getInstance().getHabits(TaskListing.TODAY).size + 1
            FirestoreManager.getInstance().uploatHabit(habit)
            finish()
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
