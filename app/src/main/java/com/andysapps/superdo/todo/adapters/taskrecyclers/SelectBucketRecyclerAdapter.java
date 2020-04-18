package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.events.action.SelectBucketEvent;
import com.andysapps.superdo.todo.model.Bucket;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class SelectBucketRecyclerAdapter extends RecyclerView.Adapter<SelectBucketRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "SelectBucketAdapter";
    private List<Bucket> bucketList;

    private Context context;

    public SelectBucketRecyclerAdapter(Context context, List<Bucket> bucketList) {
        this.bucketList = bucketList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_select_bucket, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void updateList(List<Bucket> taskList) {

        Log.e(TAG, "updateList: data size" + this.bucketList.size());

        this.bucketList.clear();
        this.bucketList.addAll(taskList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int i) {
        final int position = i;

        Bucket bucket = bucketList.get(position);

        holder.tvBucketName.setText(bucket.getName());

        holder.ivBucketIcon.setImageResource(Constants.bucketIcons[bucket.getBucketIcon()]);

        holder.item.setOnClickListener(v -> EventBus.getDefault().post(new SelectBucketEvent(bucket)));

    }

    @Override
    public int getItemCount() {
        return bucketList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_bucketname__selectbucket_item)
        public TextView tvBucketName;

        @BindView(R.id.iv_bucket_type__selectbucket_item)
        ImageView ivBucketIcon;

        @BindView(R.id.iv_colortag__selectbucket_item)
        public ImageView ivTag;

        public View item;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
