package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.adapters.BucketsRecyclerAdapter;
import com.andysapps.superdo.todo.enums.BucketUpdateType;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.ClickBucketEvent;
import com.andysapps.superdo.todo.events.OpenAddBucketFragmentEvent;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.FetchBucketEvent;
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Bucket;
import com.google.firebase.firestore.DocumentChange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */

public class BucketFragment extends Fragment {

    @BindView(R.id.recyclerView_bucket_list)
    RecyclerView recyclerView;

    BucketsRecyclerAdapter adapter;

    List<Bucket> bucketList;

    @BindView(R.id.ll_noBuckets)
    LinearLayout llNoTasks;

    public BucketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_BUCKET));

        bucketList = new ArrayList<>();
        bucketList.addAll(TaskOrganiser.getInstance().bucketList);
        if (bucketList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BucketsRecyclerAdapter(getContext(), bucketList);
        recyclerView.setAdapter(adapter);

        Log.e( getClass().getName(), "onCreateView: bucket fragment" );

        return v;
    }
    
    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_TASK));
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchBucketEvent event) {
        if (event.isSuccess()) {
            adapter.updateList(TaskOrganiser.getInstance().bucketList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BucketUpdatedEvent event) {
       if (event.getDocumentChange() == BucketUpdateType.Added) {
           adapter.notifyBucketAdded(TaskOrganiser.getInstance().bucketList);
       } else if (event.getDocumentChange() == BucketUpdateType.Deleted){
           adapter.notifyBucketRemoved(event.getBucket());
       } else {
           adapter.updateList(TaskOrganiser.getInstance().bucketList);
       }
    }

    @OnClick(R.id.ib_close_buckets)
    public void clickClose() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickBucketEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenAddBucketFragmentEvent event) {
        new AddBucketFragment().show(getFragmentManager(), "add bucket");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent() {

    }

}


