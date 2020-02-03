package com.andysapps.superdo.todo.fragments;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback;
import com.andysapps.superdo.todo.adapters.MainAdapters.TasksRecyclerAdapter;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;
import com.github.florent37.viewanimator.ViewAnimator;

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

    Typeface fontBold;
    Typeface fontRegular;

    TaskListing listing;

    TaskListing lasttaskListing;

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);

        fontBold = ResourcesCompat.getFont(getContext(), R.font.montserrat_bold);
        fontRegular = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);

        taskList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TasksRecyclerAdapter(getContext(), taskList);

        ItemTouchHelper.Callback callback = new LongItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        listing= TaskListing.TODAY;
        lasttaskListing = TaskListing.TOMORROW;
        updateUi();
        lasttaskListing = TaskListing.SOMEDAY;
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

        taskList = TaskOrganiser.getInstance().getTasks(listing);
        animateTodayViews();

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
    public void onMessageEvent(FetchTasksEvent event) {
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskUpdatedEvent event) {

        if (event.getTask().getListedIn() == listing) {
            if (event.getDocumentChange() == TaskUpdateType.Added) {
                adapter.notifyTaskAdded(TaskOrganiser.getInstance().getTasks(listing));
            } else if (event.getDocumentChange() == TaskUpdateType.Deleted){
                adapter.notifyTaskRemoved(event.getTask());
            } else {
                adapter.updateList(TaskOrganiser.getInstance().getTasks(listing));
            }
        } else {
            listing = event.getTask().getListedIn();
            updateUi();
        }
    }

    /////////////////////
    /////// ANIMATION
    ////////////////////

    public void animateTodayViews() {

        float red =  1.0f;
        float grey =  0.3f;

        if (lasttaskListing != null && listing != lasttaskListing) {
            switch (lasttaskListing) {
                case TODAY:
                    zoomAnimation(1.2f, 1.0f, tvToday, grey);
                    break;
                case TOMORROW:
                    zoomAnimation(1.2f, 1.0f, tvTomorrow, grey);
                    break;
                case SOMEDAY:
                    zoomAnimation(1.2f, 1.0f, tvSomeday, grey);
                    break;
            }
        }

        if (listing != lasttaskListing) {
            switch (listing) {
                case TODAY:
                    zoomAnimation(1.0f, 1.2f, tvToday, red);
                    break;
                case TOMORROW:
                    zoomAnimation(1.0f, 1.2f, tvTomorrow, red);
                    break;
                case SOMEDAY:
                    zoomAnimation(1.0f, 1.2f, tvSomeday, red);
                    break;
            }
        }

        lasttaskListing = listing;
    }

    public void zoomAnimation(float start, float end, View view, float alpha) {
        ViewAnimator
                .animate(view)
                //.scale(start, end)
                .alpha(alpha)
                .decelerate()
                .duration(200)
                .start();

       /* ViewAnimator
                .animate(view)
                .textColor(color)
                .decelerate()
                .duration(1000)
                .start();*/
    }
}
