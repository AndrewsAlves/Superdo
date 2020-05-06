package com.andysapps.superdo.todo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.andysapps.superdo.todo.Constants
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.events.PremiumPurchaseUpdateEvent
import com.andysapps.superdo.todo.events.update.UpdateProfileEvent
import com.andysapps.superdo.todo.manager.PurchaseManager
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.activity_subscription.tv_price_monthy
import kotlinx.android.synthetic.main.activity_subscription.tv_price_yearly
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        EventBus.getDefault().register(this)
        initView()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
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

        if (PurchaseManager.getInstance().skuMonthly != null) {
            tv_price_monthy.text = PurchaseManager.getInstance().skuMonthly.price
        }

        if (PurchaseManager.getInstance().skuYearly != null) {
            tv_price_yearly.text = PurchaseManager.getInstance().skuYearly.price
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PremiumPurchaseUpdateEvent) {
        if (event.STATUS == Constants.PURCHASED) {
            Toast.makeText(this, "Thank you for purchasing Superdo Premium!", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "You are awesome :)", Toast.LENGTH_SHORT).show()
            EventBus.getDefault().post(UpdateProfileEvent())
        } else if (event.STATUS == Constants.PENDING) {
            Toast.makeText(this, "You purchase is in pending", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "purchase declined", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
