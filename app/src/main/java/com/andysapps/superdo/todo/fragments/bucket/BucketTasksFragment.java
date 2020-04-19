package com.andysapps.superdo.todo.fragments.bucket;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.taskrecyclers.BucketTasksRecyclerAdapter;

import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.UpdateTaskListEvent;
import com.andysapps.superdo.todo.events.bucket.UpdateBucketTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
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

    public static final String TAG = "BucketTaskFragment";

    @BindView(R.id.recyclerView_task_list)
    RecyclerView recyclerView;

    @BindView(R.id.tv_bucket_name)
    TextView tvBucketName;

    @BindView(R.id.ib_edit_bucket)
    ImageButton ibEdit;

    @BindView(R.id.ib_bucketList)
    ImageButton ibBuckets;

    @BindView(R.id.btn_save__bucket)
    ImageButton tvSave;

    @BindView(R.id.ib_close_edit_bucket_list)
    ImageButton ibClose;

    @BindView(R.id.iv_bucket_type)
    ImageView ivBucketType;

    @BindView(R.id.ll_notasks)
    LinearLayout llNoTasks;

    @BindView(R.id.et_bucket_desc)
    EditText etBucketDesc;

    boolean isEditing = false;

    BucketTasksRecyclerAdapter adapter;
    Bucket bucket;

    public List<Task> taskList;

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
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_TASK));

        if (bucket == null) {
            bucket = FirestoreManager.getAllTasksBucket();
            etBucketDesc.setFocusable(false);
            ibEdit.setVisibility(View.GONE);
        }

        taskList = new ArrayList<>();

        if (!TaskOrganiser.getInstance().getTasksInBucket(bucket, false).isEmpty()) {
            taskList.addAll(TaskOrganiser.getInstance().getTasksInBucket(bucket, false));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BucketTasksRecyclerAdapter(getContext(), taskList, bucket);
        recyclerView.setAdapter(adapter);

        initUi();
        updateUi();

        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void initUi() {
        etBucketDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == etBucketDesc.getId()) {
                    if (hasFocus) {
                        isEditing = true;
                        updateUi();
                    } else {
                        isEditing = false;
                        updateUi();
                    }
                }
            }
        });
    }

    public void updateUi() {

        tvBucketName.setText(bucket.getName());
        etBucketDesc.setText(bucket.getDescription());

        if (isEditing) {
            tvSave.setVisibility(View.VISIBLE);
            ibClose.setVisibility(View.VISIBLE);
            ibEdit.setVisibility(View.GONE);
            ibBuckets.setVisibility(View.GONE);
        } else {

            Utils.hideKeyboard(getContext(), etBucketDesc);
            etBucketDesc.clearFocus();

            tvSave.setVisibility(View.GONE);
            ibClose.setVisibility(View.GONE);
            ibEdit.setVisibility(View.VISIBLE);
            ibBuckets.setVisibility(View.VISIBLE);

            tvBucketName.setVisibility(View.VISIBLE);
        }

        ivBucketType.setImageResource(Constants.bucketIcons[bucket.getBucketIcon()]);

        Log.e(getClass().getName(), "updateUi: " + isEditing);

        llNoTasks.setVisibility(View.GONE);
        if(adapter.taskList == null || adapter.taskList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_save__bucket)
    public void saveEditing() {
        // Update firestore
        bucket.setDescription(etBucketDesc.getText().toString());
        FirestoreManager.getInstance().updateBucket(bucket);
        isEditing = false;
        updateUi();
    }

    @OnClick(R.id.ib_close_edit_bucket_list)
    public void clickClose() {
        isEditing = false;
        updateUi();
    }

    @OnClick(R.id.ib_edit_bucket)
    public void clickProfile() {
        Utils.hideKeyboard(getContext(), etBucketDesc);
        EventBus.getDefault().post(new OpenFragmentEvent(CreateNewBucketFragment.Companion.instance(bucket, true),
                true,
                CreateNewBucketFragment.TAG,
                 true));
    }

    @OnClick(R.id.ib_bucketList)
    public void clickBucket() {
        EventBus.getDefault().post(new OpenFragmentEvent(new BucketFragment(), true, BucketFragment.TAG));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetBucketTaskListEvent event) {
        getFragmentManager().popBackStack();
        bucket = event.getBucket();
        adapter.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false));
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateBucketTasksEvent event) {
        isEditing = false;
        adapter.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false));
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTaskListEvent event) {
        adapter.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false));
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessageEvent(TaskUpdatedEvent event) {
        if (!bucket.getId().equals(event.getTask().getBucketId())) return;
        adapter.updateList(TaskOrganiser.getInstance().getTasksInBucket(bucket, false));
        updateUi();
    }
}


