package com.andysapps.superdo.todo.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.model.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    @BindView(R.id.tv_todaytitle)
    TextView tvTitle;

    @BindView(R.id.ll_notasks)
    LinearLayout linearNoTasks;

    int[] gradientColor = new int[2];

    public ArrayList<Task> todoList;

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, v);

        todoList = new ArrayList<>();

        updateUi();

        return v;
    }

    public void updateUi() {

        gradientColor[0] = Color.parseColor( "#F64F59");
        gradientColor[1] = Color.parseColor( "#FF8B57");

        if (todoList.isEmpty()) {
            linearNoTasks.setVisibility(View.VISIBLE);
        }

    }

}
