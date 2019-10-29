package com.andysapps.superdo.todo.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.activity.ProfileActivity;
import com.andysapps.superdo.todo.adapters.BucketsRecyclerAdapter;
import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

    @BindView(R.id.recyclerView_task_list)
    RecyclerView recyclerView;

    @BindView(R.id.tv_bucket_name)
    TextView tvBucketName;

    @BindView(R.id.ib_profile)
    ImageButton ibProfile;

    @BindView(R.id.ib_buckets)
    ImageButton ibBuckets;

    @BindView(R.id.tv_save)
    TextView tvSave;

    @BindView(R.id.ib_close_editing_bucket)
    TextView ibClose;

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

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.bind(this, v);

        updateUi();

        taskList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TasksRecyclerAdapter(getContext(), taskList);
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void updateUi() {

        tvBucketName.setText(bucket.getName());
        etBucketDesc.setText(bucket.getDescription());

        if (isEditing) {
            tvSave.setVisibility(View.VISIBLE);
            ibClose.setVisibility(View.VISIBLE);
            ibProfile.setVisibility(View.GONE);
            ibBuckets.setVisibility(View.GONE);
        } else {
            tvSave.setVisibility(View.GONE);
            ibClose.setVisibility(View.GONE);
            ibProfile.setVisibility(View.VISIBLE);
            ibBuckets.setVisibility(View.VISIBLE);
        }

        if(taskList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.tv_bucket_name)
    public void clickBucketName() {
        etBucketName.setVisibility(View.VISIBLE);
        tvBucketName.setVisibility(View.GONE);

        showSoftKeyboard(etBucketName);

        isEditing = true;
        updateUi();
    }

    @OnClick(R.id.et_bucket_desc)
    public void clickBucketDescription() {
        etBucketName.setVisibility(View.VISIBLE);
        tvBucketName.setVisibility(View.GONE);

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

    @OnClick(R.id.ib_close_editing_bucket)
    public void clickClose() {

        tvSave.setVisibility(View.GONE);
        ibClose.setVisibility(View.GONE);
        ibProfile.setVisibility(View.VISIBLE);
        ibBuckets.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.ib_profile)
    public void clickProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ib_buckets)
    public void clickBuckets() {
        BucketFragment fragment = new BucketFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_bucket_fragment_container, fragment);
        ft.commitAllowingStateLoss(); // save the changes
    }


    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
