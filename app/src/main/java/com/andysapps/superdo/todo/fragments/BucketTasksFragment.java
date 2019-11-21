package com.andysapps.superdo.todo.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.activity.ProfileActivity;
import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.events.ui.SetBucketTaskListEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Bucket;
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

public class BucketTasksFragment extends Fragment {

    @BindView(R.id.recyclerView_task_list)
    RecyclerView recyclerView;

    @BindView(R.id.tv_bucket_name)
    TextView tvBucketName;

    @BindView(R.id.ib_profile)
    ImageButton ibProfile;

    @BindView(R.id.ib_bucketList)
    ImageButton ibBuckets;

    @BindView(R.id.tv_save)
    TextView tvSave;

    @BindView(R.id.ib_close_edit_bucket_list)
    ImageButton ibClose;

    @BindView(R.id.ll_notasks)
    LinearLayout llNoTasks;

    @BindView(R.id.et_bucket_desc)
    EditText etBucketDesc;

    @BindView(R.id.et_bucket_name)
    EditText etBucketName;

    boolean isEditing;

    TasksRecyclerAdapter adapter;
    Bucket bucket;

    List<Task> taskList;

    public BucketTasksFragment() {
        // Required empty public constructor
    }

    public static BucketTasksFragment getInstance(Bucket bucket) {
        BucketTasksFragment fragment = new BucketTasksFragment();
        fragment.bucket = bucket;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket_tasks, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);

        if (bucket == null) {
            bucket = FirestoreManager.getAllTasksBucket(getContext());
        }

        taskList = new ArrayList<>();
        taskList.addAll(TaskOrganiser.getInstance().getTasksInBucket(bucket));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        etBucketName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        etBucketDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etBucketName.setVisibility(View.VISIBLE);
                    tvBucketName.setVisibility(View.GONE);

                    isEditing = true;
                    updateUi();
                }
            }
        });

        tvBucketName.setText(bucket.getName());
        etBucketName.setText(bucket.getName());
        etBucketDesc.setText(bucket.getDescription());

        if (isEditing) {
            tvSave.setVisibility(View.VISIBLE);
            ibClose.setVisibility(View.VISIBLE);
            ibProfile.setVisibility(View.GONE);
            ibBuckets.setVisibility(View.GONE);
        } else {

            Utils.hideKeyboard(getContext(), etBucketDesc);
            Utils.hideKeyboard(getContext(), etBucketName);

            etBucketDesc.clearFocus();
            etBucketName.clearFocus();

            tvSave.setVisibility(View.GONE);
            ibClose.setVisibility(View.GONE);
            ibProfile.setVisibility(View.VISIBLE);
            ibBuckets.setVisibility(View.VISIBLE);

            tvBucketName.setVisibility(View.VISIBLE);
            etBucketName.setVisibility(View.GONE);
        }

        if(taskList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.tv_bucket_name)
    public void clickBucketName() {
        etBucketName.setVisibility(View.VISIBLE);
        tvBucketName.setVisibility(View.GONE);

        Utils.showSoftKeyboard(getContext(), etBucketName);

        isEditing = true;
        updateUi();
    }

    @OnClick(R.id.tv_save)
    public void saveEditing() {

        // update firestore

        bucket.setName(etBucketName.getText().toString());
        bucket.setDescription(etBucketDesc.getText().toString());

        isEditing = false;
        updateUi();
    }

    @OnClick(R.id.ib_close_edit_bucket_list)
    public void clickClose() {
        isEditing = false;
        updateUi();
    }

    @OnClick(R.id.ib_profile)
    public void clickProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ib_bucketList)
    public void clickBucket() {
        EventBus.getDefault().post(new OpenFragmentEvent(new BucketFragment(), true));
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_BUCKET));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetBucketTaskListEvent event) {
        getFragmentManager().popBackStack();
        bucket = event.getBucket();
        adapter.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket));
        updateUi();
    }

}
