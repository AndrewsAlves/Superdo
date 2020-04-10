package com.andysapps.superdo.todo.fragments.task


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.events.SetTasksFragment
import com.andysapps.superdo.todo.fragments.bucket.BucketTasksFragment
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Bucket
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * A simple [Fragment] subclass.
 */

class TasksFragment : Fragment() {

    companion object {

        var taskBucket : Bucket? = null

        fun getBucket() : Bucket{
            if (taskBucket == null) {
                return TaskOrganiser.getInstance().bucketList[0]
            } else {
               return taskBucket as Bucket
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        onMessageEvent(SetTasksFragment(null))
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SetTasksFragment) {

        taskBucket = event.bucket

        val ft = fragmentManager!!.beginTransaction()

        if (event.bucket == null) {
            ft.replace(R.id.fl_fragment_container_tasks, AllTasksFragment(), BucketTasksFragment.TAG)
        } else {
            ft.replace(R.id.fl_fragment_container_tasks, BucketTasksFragment.getInstance(event.bucket), BucketTasksFragment.TAG)
        }

        ft.commitAllowingStateLoss() // save the change
    }

}
