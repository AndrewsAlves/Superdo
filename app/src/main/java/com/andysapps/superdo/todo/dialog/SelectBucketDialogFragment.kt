package com.andysapps.superdo.todo.dialog


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.SelectBucketRecyclerAdapter
import com.andysapps.superdo.todo.events.action.SelectBucketEvent
import com.andysapps.superdo.todo.events.ui.DialogDismissEvent
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.Bucket
import kotlinx.android.synthetic.main.fragment_select_bucket_dialog.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */
class SelectBucketDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_bucket_dialog, container, false)
    }

    var adapter: SelectBucketRecyclerAdapter? = null

    var bucketList: ArrayList<Bucket> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        bucketList.addAll(TaskOrganiser.getInstance().bucketList)
        initUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        EventBus.getDefault().post(DialogDismissEvent())
    }

    fun initUi() {
        rv_selectbucket.layoutManager = LinearLayoutManager(context)
        adapter = SelectBucketRecyclerAdapter(context, bucketList)
        rv_selectbucket.adapter = adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SelectBucketEvent) {
       dismiss()
    }


}
