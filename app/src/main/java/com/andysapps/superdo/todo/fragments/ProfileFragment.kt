package com.andysapps.superdo.todo.fragments


import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.Constants
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.activity.EditProfileActivity
import com.andysapps.superdo.todo.activity.ProductivityActivity
import com.andysapps.superdo.todo.activity.SubscriptionActivity
import com.andysapps.superdo.todo.activity.WelcomeActivity
import com.andysapps.superdo.todo.dialog.alert.LogoutAlertDialog
import com.andysapps.superdo.todo.enums.CPMD
import com.andysapps.superdo.todo.events.LogoutEvent
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent
import com.andysapps.superdo.todo.events.update.UpdateProfileEvent
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.PurchaseManager
import com.andysapps.superdo.todo.manager.SharedPrefsManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager
import com.google.firebase.auth.FirebaseAuth
import com.hadiidbouk.charts.BarData
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

   companion object {
       const val TAG = "ProfileFragment"
   }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        initUi()
        updateUi()
    }

    fun initUi() {

        PushDownAnim.setPushDownAnimTo(profile_btn_completed_tasks,
                profile_btn_pending_tasks,
                profile_btn_missed_tasks,
                profile_btn_recurring_tasks,
                profile_btn_deadline_tasks,
                profile_btn_archived_tasks)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(fun(view: View) {

                    var cpmd = CPMD.COMPLETED

                    when(view) {
                        profile_btn_completed_tasks -> cpmd = CPMD.COMPLETED
                        profile_btn_pending_tasks -> cpmd = CPMD.PENDING
                        profile_btn_missed_tasks -> cpmd = CPMD.MISSED
                        profile_btn_recurring_tasks -> cpmd = CPMD.RECURRING
                        profile_btn_deadline_tasks -> cpmd = CPMD.DEADLINED
                        profile_btn_archived_tasks -> cpmd = CPMD.DELETED
                    }

                    EventBus.getDefault().post(OpenFragmentEvent(CPMDTasksFragment.instance(cpmd), false, CPMDTasksFragment.TAG, true))
                })

        PushDownAnim.setPushDownAnimTo(profile_btn_productivity)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(fun(view: View) {
                    startActivity(Intent(context, ProductivityActivity::class.java))
                })

        PushDownAnim.setPushDownAnimTo(profile_btn_sub_monthy,
                profile_btn_sub_yearly)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(fun(view: View) {
                    startActivity(Intent(context, SubscriptionActivity::class.java))
                })

        profile_iv_editprofile.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        profile_btn_logout.setOnClickListener {
            LogoutAlertDialog().show(fragmentManager!!, "alert_logout")
        }

        profile_btn_support.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.setDataAndType(Uri.parse("actioonsapps@gmail.com"), "text/html")
            val pm: PackageManager = context!!.packageManager
            val matches = pm.queryIntentActivities(emailIntent, 0)
            var className: String? = null
            for (info in matches) {
                if (info.activityInfo.packageName == "com.google.android.gm") {
                    className = info.activityInfo.name
                    if (className != null && !className.isEmpty()) {
                        break
                    }
                }
            }

            if (className != null) {
                emailIntent.setClassName("com.google.android.gm", className)

                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Gmail not fount!", Toast.LENGTH_LONG).show()
                }
            }
        }

        var barlist : ArrayList<BarData> = ArrayList()
        var max = 0.0f
        ChartProgressBar.setMaxValue(max)
        ChartProgressBar.setDataList(barlist)
        ChartProgressBar.build()
    }

    fun updateUi() {

        if (FirestoreManager.getInstance().user.firstName != null) {
            profile_tv_account_name.text = FirestoreManager.getInstance().user.firstName
        }

        profile_iv_premium.visibility = View.GONE
        if (FirestoreManager.getInstance().isUserPremium) {
            val user = FirestoreManager.getInstance().user
            if ( user.purchaseDetails.status == Constants.PURCHASED) {
                profile_iv_premium.visibility = View.VISIBLE
                when(user.purchaseDetails.skyId) {
                    PurchaseManager.sku_monthly -> profile_iv_premium.setImageResource(R.drawable.ic_crown_monthly)
                    PurchaseManager.sku_yearly -> profile_iv_premium.setImageResource(R.drawable.ic_crown_yearly)
                }
            }
        }

        when (FirestoreManager.getInstance().user!!.avatarIndex) {
            0 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_1)
            1 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_2)
            2 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_3)
            3 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_4)
            4 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_5)
            5 -> profile_iv_avatar.setImageResource(R.drawable.img_avatar_6)
        }

        profile_tv_completed_tasks.text = TaskOrganiser.getInstance().getCompletedTaskList().size.toString()
        profile_tv_pending_tasks.text = TaskOrganiser.getInstance().getPendingTaskList().size.toString()
        profile_tv_missing_tasks.text = TaskOrganiser.getInstance().getMissedTaskList().size.toString()
        profile_tv_recurring_tasks.text = TaskOrganiser.getInstance().getRecurringTask().size.toString()
        profile_tv_deadline_tasks.text = TaskOrganiser.getInstance().getDeadlineTasks().size.toString()
        profile_tv_deleted_tasks.text = TaskOrganiser.getInstance().getDeletedTaskList().size.toString()

        var points = FirestoreManager.getInstance().user.espritPoints

        for(x in 0..9) {
            if (points >= Constants.trophyPoints[x]) {
                profile_iv_trophy.setImageResource(Constants.trophySrc[x])
            }
        }

        profile_tv_espritpoints.text = points.toString()

        var barlist : ArrayList<BarData> = TaskOrganiser.getInstance().barDataForThisWeek
        var max = 0.0f

        for (bardate in barlist) {
            if (bardate.barValue > max) {
                max = bardate.barValue
            }
        }

        if (max <= 0.0f) {
            max = 5.0f
        }

        ChartProgressBar.setMaxValue(max)
        ChartProgressBar.setDataList(barlist)
        ChartProgressBar.build()


        ////////////
        //// UPDATE IN APP SUBS
        ////////////
        if (FirestoreManager.getInstance().isUserPremium) {
            tv_go_premium.visibility = View.GONE
            hsv_go_premium.visibility = View.GONE
            return
        }

        tv_go_premium.visibility = View.VISIBLE
        hsv_go_premium.visibility = View.VISIBLE

        if (PurchaseManager.getInstance().skuMonthly != null) {
            tv_price_monthy.text = PurchaseManager.getInstance().skuMonthly.price
        }

        if (PurchaseManager.getInstance().skuYearly != null) {
            tv_price_yearly.text = PurchaseManager.getInstance().skuYearly.price
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateProfileEvent) {
        updateUi()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogoutEvent) {
        FirestoreManager.destroy()
        TaskOrganiser.destroy()
        SuperdoAlarmManager.destroy()
        SuperdoNotificationManager.destroy()
        SharedPrefsManager.deleteAllData(context)
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(context, WelcomeActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}
