package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.BucketsRecyclerAdapter;
import com.andysapps.superdo.todo.events.ClickBucketEvent;
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents;
import com.andysapps.superdo.todo.model.Bucket;

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

        bucketList = new ArrayList<>();
        if (bucketList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BucketsRecyclerAdapter(getContext(), bucketList);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @OnClick(R.id.ib_close)
    public void clickClose() {
        EventBus.getDefault().post(new RemoveFragmentEvents());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickBucketEvent event) {

    }

}
