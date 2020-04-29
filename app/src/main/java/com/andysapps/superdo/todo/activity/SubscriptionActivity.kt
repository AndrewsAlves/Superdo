package com.andysapps.superdo.todo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.manager.PurchaseManager
import kotlinx.android.synthetic.main.activity_subscription.*

class SubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        initView()
        updateViews()
    }

    fun initView() {
        btn_rl_sub_monthly.setOnClickListener {
            PurchaseManager.getInstance().purchaseSubMonthly(this)
        }

        btn_rl_sub_yearly.setOnClickListener {
            PurchaseManager.getInstance().purchaseSubYearly(this)
        }

        ib_back_subscription.setOnClickListener {
            onBackPressed()
        }
    }

    fun updateViews() {

    }




}
