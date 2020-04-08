package com.andysapps.superdo.todo.fragments.bucket


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.activity.MainActivity
import com.andysapps.superdo.todo.enums.BucketColors
import com.andysapps.superdo.todo.enums.BucketType
import com.andysapps.superdo.todo.enums.MoonButtonType
import com.andysapps.superdo.todo.events.UpdateMoonButtonType
import com.andysapps.superdo.todo.events.bucket.UpdateBucketTasksEvent
import com.andysapps.superdo.todo.events.firestore.AddNewBucketEvent
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.model.Bucket
import kotlinx.android.synthetic.main.fragment_create_bucket.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class CreateNewBucketFragment : Fragment() , View.OnClickListener {

    var bucket : Bucket = Bucket()
    var isEditing : Boolean = false

    companion object {

        const val TAG : String = "CreateNewBucketFragment"

        fun instance(bucket : Bucket, isEditing: Boolean) : Fragment {
            val fragment = CreateNewBucketFragment()
            fragment.bucket = bucket
            fragment.isEditing = isEditing
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_bucket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(UpdateMoonButtonType(MoonButtonType.SAVE_BUCKET))
        initUi()
        if (!isEditing) {
            bucket.tagColor = BucketColors.Red.name
            bucket.bucketType = BucketType.Tasks.name
        } else {
            et_create_bucket_name.setText(bucket.name)
        }
        Utils.showSoftKeyboard(context, et_create_bucket_name)
        updateUI()
        ib_close_create_bucket
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().post(UpdateMoonButtonType(MoonButtonType.ADD_TASK))
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            bucket_icon_1.id -> {
                bucket.bucketType = BucketType.Tasks.name
            }
            bucket_icon_2.id -> {
                bucket.bucketType = BucketType.Personal.name
            }
            bucket_icon_3.id -> {
                bucket.bucketType = BucketType.Gym.name
            }
            bucket_icon_4.id -> {
                bucket.bucketType = BucketType.Work.name
            }
            bucket_icon_5.id -> {
                bucket.bucketType = BucketType.House.name
            }

           /* bucket_color_1.id -> {
                bucket.tagColor = BucketColors.Red.name
            }
            bucket_color_2.id -> {
                bucket.tagColor = BucketColors.SkyBlue.name
            }
            bucket_color_3.id -> {
                bucket.tagColor = BucketColors.Green.name
            }
            bucket_color_4.id -> {
                bucket.tagColor = BucketColors.InkBlue.name
            }
            bucket_color_5.id -> {
                bucket.tagColor = BucketColors.Orange.name
            }*/
            ib_close_create_bucket.id -> {
                activity!!.supportFragmentManager.popBackStack()
                //EventBus.getDefault().post(RemoveFragmentEvents())
            }

        }

        updateUI()
    }

    fun initUi() {
        bucket_icon_1.setOnClickListener(this)
        bucket_icon_2.setOnClickListener(this)
        bucket_icon_3.setOnClickListener(this)
        bucket_icon_4.setOnClickListener(this)
        bucket_icon_5.setOnClickListener(this)
       /* bucket_color_1.setOnClickListener(this)
        bucket_color_2.setOnClickListener(this)
        bucket_color_3.setOnClickListener(this)
        bucket_color_4.setOnClickListener(this)
        bucket_color_5.setOnClickListener(this)*/
        ib_close_create_bucket.setOnClickListener(this)
    }

    fun updateUI() {

        bucket_icon_1.setImageResource(R.drawable.ic_bc_tasks_off)
        bucket_icon_2.setImageResource(R.drawable.ic_bc_personal_off)
        bucket_icon_3.setImageResource(R.drawable.ic_bc_gym_off)
        bucket_icon_4.setImageResource(R.drawable.ic_bc_briefcase_off)
        bucket_icon_5.setImageResource(R.drawable.ic_bc_house_off)

        bg_bucket_icon_1.visibility = View.GONE
        bg_bucket_icon_2.visibility = View.GONE
        bg_bucket_icon_3.visibility = View.GONE
        bg_bucket_icon_4.visibility = View.GONE
        bg_bucket_icon_5.visibility = View.GONE

       /* bg_bucket_color_1.visibility = View.GONE
        bg_bucket_color_2.visibility = View.GONE
        bg_bucket_color_3.visibility = View.GONE
        bg_bucket_color_4.visibility = View.GONE
        bg_bucket_color_5.visibility = View.GONE*/

        when(BucketType.valueOf(bucket.bucketType)) {
            BucketType.Tasks -> {
                bucket_icon_1.setImageResource(R.drawable.ic_bc_tasks_on)
                bg_bucket_icon_1.visibility = View.VISIBLE
            }
            BucketType.Personal -> {
                bucket_icon_2.setImageResource(R.drawable.ic_bc_personal_on)
                bg_bucket_icon_2.visibility = View.VISIBLE
            }
            BucketType.Gym -> {
                bucket_icon_3.setImageResource(R.drawable.ic_bc_gym_on)
                bg_bucket_icon_3.visibility = View.VISIBLE
            }
            BucketType.Work -> {
                bucket_icon_4.setImageResource(R.drawable.ic_bc_briefcase_on)
                bg_bucket_icon_4.visibility = View.VISIBLE
            }
            BucketType.House -> {
                bucket_icon_5.setImageResource(R.drawable.ic_bc_house_on)
                bg_bucket_icon_5.visibility = View.VISIBLE
            }
        }

       /* when(BucketColors.valueOf(bucket.tagColor)) {
            BucketColors.Red -> bg_bucket_color_1.visibility = View.VISIBLE
            BucketColors.SkyBlue -> bg_bucket_color_2.visibility = View.VISIBLE
            BucketColors.Green -> bg_bucket_color_3.visibility = View.VISIBLE
            BucketColors.InkBlue -> bg_bucket_color_4.visibility = View.VISIBLE
            BucketColors.Orange -> bg_bucket_color_5.visibility = View.VISIBLE
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : AddNewBucketEvent) {
        if (et_create_bucket_name.text.isNotEmpty()) {
            bucket.name = et_create_bucket_name.text.toString()
            bucket.userId = FirestoreManager.getInstance().user.userId
            bucket.created = Calendar.getInstance().time
            bucket.id = UUID.randomUUID().toString()
            if (isEditing) {
                FirestoreManager.getInstance().updateBucket(bucket)
                EventBus.getDefault().post(UpdateBucketTasksEvent())
            } else {
                FirestoreManager.getInstance().uploadBucket(bucket)
            }
            fragmentManager!!.popBackStack()
        }
    }

}
