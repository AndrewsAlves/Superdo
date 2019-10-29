package com.andysapps.superdo.todo.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.model.Bucket;

import org.w3c.dom.Text;

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

    @BindView(R.id.tv_bucket_name)
    TextView tvBucketName;

    Bucket bucket;

    List tasks;

    public BucketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket, container, false);
        ButterKnife.bind(this, v);

        tvBucketName.setText(bucket.getName());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
