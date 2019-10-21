package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andysapps.superdo.todo.R;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bucket, container, false);
    }

}
