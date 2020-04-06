package com.andysapps.superdo.todo.activity.start_screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andysapps.superdo.todo.R
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private var slide = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        initUi()
    }

    fun initUi() {
        slide = 1
        transitionOne()

        btn_tv_letsgetstarted.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun transitionOne() {
        textTransitionOne()
        ViewAnimator
                .animate(iv_welcome_pic)
                .alpha(1.0f,0f)
                .translationX(0f, -500f)
                .onStop {
                    transitionTwo()
                    textTransitionTwo()
                }
                .accelerate()
                .duration(500)
                .startDelay(2000)
                .start()
    }

    fun transitionTwo() {

        slide++

        if (slide > 3) {
            slide = 1
        }

        when (slide) {
            1 -> iv_welcome_pic.setImageResource(R.drawable.img_takeaction)
            2 -> iv_welcome_pic.setImageResource(R.drawable.img_neverforget)
            3 -> iv_welcome_pic.setImageResource(R.drawable.img_enhance)
        }

        ViewAnimator
                .animate(iv_welcome_pic)
                .alpha(0.0f,1.0f)
                .translationX(500f, 0f)
                .onStop {
                    transitionOne()
                    textTransitionOne()
                }
                .decelerate()
                .duration(500)
                .start()
    }

    private fun textTransitionOne() {
        ViewAnimator
                .animate(ll_welcome_texts)
                .alpha(1.0f,0f)
                .translationY(0.0f, 30f)
                .accelerate()
                .duration(500)
                .startDelay(2000)
                .start()
    }

    fun textTransitionTwo() {

        when (slide) {
            1 -> {
                tv_welcome_title.text = getString(R.string.welcome_title1)
                tv_welcome_description.text = getString(R.string.welcome_description1)
            }
            2 -> {
                tv_welcome_title.text = getString(R.string.welcome_title2)
                tv_welcome_description.text = getString(R.string.welcome_description2)
            }
            3 -> {
                tv_welcome_title.text = getString(R.string.welcome_title3)
                tv_welcome_description.text = getString(R.string.welcome_description3)
            }
        }

        ViewAnimator
                .animate(ll_welcome_texts)
                .alpha(0.0f,1.0f)
                .translationY(30f, 0f)
                .decelerate()
                .duration(500)
                .start()
    }
}
