package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
public class BucketFragment extends Fragment {

    public BucketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.ib_close)
    public void clickClose() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.fl_bucket_fragment_container) != null) {

            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentById(R.id.fl_bucket_fragment_container))
                    .commitAllowingStateLoss();
        }
    }

}
