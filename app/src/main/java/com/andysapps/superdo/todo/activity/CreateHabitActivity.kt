package com.andysapps.superdo.todo.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.viewpageradapter.CreateHabitPagerAdapter
import com.andysapps.superdo.todo.adapters.viewpageradapter.MainViewPagerAdapter
import kotlinx.android.synthetic.main.activity_create_habit.*

class CreateHabitActivity : AppCompatActivity() {

    var pagerAdapter = CreateHabitPagerAdapter(supportFragmentManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
        initUi()
        initClicks()

    }

    fun initUi() {
        pagerAdapter = CreateHabitPagerAdapter(supportFragmentManager)
        ch_viewpager.adapter = pagerAdapter

        ch_viewpager.currentItem = 0
        ch_btn_laststep.visibility = View.GONE
        ch_btn_nextstep.visibility = View.VISIBLE

    }

    fun initClicks() {
        ch_btn_nextstep.setOnClickListener {
            ch_viewpager.currentItem = 1
            updateBottomUi()
        }

        ch_btn_laststep.setOnClickListener {
            ch_viewpager.currentItem = 0
            updateBottomUi()
        }
    }

    fun updateBottomUi() {

        when (ch_viewpager.currentItem) {
            0 -> {
                ch_btn_laststep.visibility = View.GONE
                ch_btn_nextstep.visibility = View.VISIBLE
                ch_iv_step1.setImageResource(R.drawable.img_dot_orange)
                ch_iv_step2.setImageResource(R.drawable.img_dot_grey1)
            }

            1-> {
                ch_btn_laststep.visibility = View.VISIBLE
                ch_btn_nextstep.visibility = View.GONE
                ch_iv_step1.setImageResource(R.drawable.img_dot_grey1)
                ch_iv_step2.setImageResource(R.drawable.img_dot_orange)
            }
        }
    }

}
