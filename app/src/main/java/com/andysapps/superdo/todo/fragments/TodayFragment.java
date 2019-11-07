package com.andysapps.superdo.todo.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.events.firestore.FetchUserDataSuccessEvent;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;

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

public class TodayFragment extends Fragment {

    @BindView(R.id.tv_todaytitle)
    TextView tvTitle;

    @BindView(R.id.btn_today)
    TextView tvToday;

    @BindView(R.id.btn_tomorrow)
    TextView tvTomorrow;

    @BindView(R.id.btn_someday)
    TextView tvSomeday;

    @BindView(R.id.ll_notasks)
    LinearLayout linearNoTasks;

    @BindView(R.id.recyclerView_today)
    RecyclerView recyclerView;

    TasksRecyclerAdapter adapter;

    int[] gradientColor = new int[2];

    public List<Task> taskList;

    TaskListing listing = TaskListing.TODAY;

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);


        taskList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TasksRecyclerAdapter(getContext(), taskList);
        recyclerView.setAdapter(adapter);

        updateUi();

        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void updateUi() {

        gradientColor[0] = Color.parseColor( "#F64F59");
        gradientColor[1] = Color.parseColor( "#FF8B57");

        tvToday.setTextColor(getResources().getColor(R.color.grey2));
        tvTomorrow.setTextColor(getResources().getColor(R.color.grey2));
        tvSomeday.setTextColor(getResources().getColor(R.color.grey2));

        switch (listing) {
            case TODAY:
                tvToday.setTextColor(getResources().getColor(R.color.lightRed));
                taskList = TaskOrganiser.getInstance().getTodayTaskList();
                break;
            case TOMORROW:
                tvTomorrow.setTextColor(getResources().getColor(R.color.lightRed));
                taskList = TaskOrganiser.getInstance().tomorrowTaskList;
                break;
            case SOMEDAY:
                tvSomeday.setTextColor(getResources().getColor(R.color.lightRed));
                taskList = TaskOrganiser.getInstance().someDayTaskList;
                break;
        }

        if (taskList == null || taskList.isEmpty()) {
            linearNoTasks.setVisibility(View.VISIBLE);
        } else {
            linearNoTasks.setVisibility(View.GONE);
        }

        adapter.updateList(taskList);
    }

    @OnClick(R.id.btn_today)
    public void clickToday() {
        listing = TaskListing.TODAY;
        updateUi();
    }

    @OnClick(R.id.btn_tomorrow)
    public void clickTomorrow() {
        listing = TaskListing.TOMORROW;
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickSomedat() {
        listing = TaskListing.SOMEDAY;
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchUserDataSuccessEvent event) {
        updateUi();
    }
}
