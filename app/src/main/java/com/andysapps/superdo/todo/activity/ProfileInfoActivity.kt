package com.andysapps.superdo.todo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.events.CreateOrUpdateUserFailureEvent
import com.andysapps.superdo.todo.events.CreateOrUpdateUserSuccessEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.model.User
import kotlinx.android.synthetic.main.activity_profile_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProfileInfoActivity : AppCompatActivity() {

    var user : User? = null

    var pressedBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)
        EventBus.getDefault().register(this)
        user = FirestoreManager.getInstance().user
        init()
        updateUi()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!pressedBack) {
            pressedBack = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG).show()
            var handler = Handler()
            handler.postDelayed(Runnable {  pressedBack = false}, 3000)
            return
        }
        super.onBackPressed()
    }

    fun init() {

        if (user!!.firstName != null) {
            et_firstname.setText(user!!.firstName)
        }

        if (user!!.lastName != null) {
            et_lastname.setText(user!!.lastName)
        }

        btn_iv_avatar1.setOnClickListener {
            user!!.avatarIndex = 0
            updateUi()
        }
        btn_iv_avatar2.setOnClickListener {
            user!!.avatarIndex = 1
            updateUi()
        }
        btn_iv_avatar3.setOnClickListener {
            user!!.avatarIndex = 2
            updateUi()
        }
        btn_iv_avatar4.setOnClickListener {
            user!!.avatarIndex = 3
            updateUi()
        }
        btn_iv_avatar5.setOnClickListener {
            user!!.avatarIndex = 4
            updateUi()
        }
        btn_iv_avatar6.setOnClickListener {
            user!!.avatarIndex = 5
            updateUi()
        }

        ib_save_profileinfo.setOnClickListener(fun(it: View) {
            if (Utils.isNetworkConnected(this)) {
                when {
                    et_firstname.text.trim().isEmpty() -> {
                        Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                    et_lastname.text.trim().isEmpty() -> {
                        Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        user!!.firstName = et_firstname.text.toString()
                        user!!.lastName = et_lastname.text.toString()
                        FirestoreManager.getInstance().createOrUpdateUser(user)
                    }
                }
            }
        })
    }

    fun updateUi() {
        btn_iv_avatar1.background = null
        btn_iv_avatar2.background = null
        btn_iv_avatar3.background = null
        btn_iv_avatar4.background = null
        btn_iv_avatar5.background = null
        btn_iv_avatar6.background = null

        when (user!!.avatarIndex) {
            0 -> btn_iv_avatar1.background = getDrawable(R.drawable.bg_avatar)
            1 -> btn_iv_avatar2.background = getDrawable(R.drawable.bg_avatar)
            2 -> btn_iv_avatar3.background = getDrawable(R.drawable.bg_avatar)
            3 -> btn_iv_avatar4.background = getDrawable(R.drawable.bg_avatar)
            4 -> btn_iv_avatar5.background = getDrawable(R.drawable.bg_avatar)
            5 -> btn_iv_avatar6.background = getDrawable(R.drawable.bg_avatar)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : CreateOrUpdateUserSuccessEvent) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : CreateOrUpdateUserFailureEvent) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
