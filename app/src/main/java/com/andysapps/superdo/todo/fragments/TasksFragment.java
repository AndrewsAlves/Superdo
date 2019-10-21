package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andysapps.superdo.todo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {



    public TasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.bind(this, v);


        return v;
    }

    @OnClick(R.id.ib_buckets)
    public void clickBuckets() {
        BucketFragment fragment = new BucketFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_bucket_fragment_container, fragment);
        ft.commitAllowingStateLoss(); // save the changes
    }

}
