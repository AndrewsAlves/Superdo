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

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.events.action.SelectBucketEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.Bucket;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class SelectBucketRecyclerAdapter extends RecyclerView.Adapter<SelectBucketRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "BucketRecyclerAdapter";
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

    public void notifyBucketAdded(List<Bucket> bucketList) {
        this.bucketList.clear();
        this.bucketList.addAll(bucketList);
        notifyDataSetChanged();
        notifyItemInserted(bucketList.size() - 1);
    }

    public void notifyBucketRemoved(Bucket bucket) {
        for (int i = 0 ; i < this.bucketList.size() ; i++) {
            if (this.bucketList.get(i).getDocumentId().equals(bucket.getDocumentId())) {
                notifyItemRemoved(i + 1);
                this.bucketList.remove(i);
            }
        }
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

        Bucket bucket;

        if (position == 0) {
            bucket = FirestoreManager.getAllTasksBucket(context);
        } else {
            bucket = bucketList.get(position - 1);
        }

        holder.tvBucketName.setText(bucket.getName());

        switch (BucketColors.valueOf(bucket.getTagColor())) {
            case Red:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_red);
                break;
            case Green:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_green);
                break;
            case SkyBlue:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_skyblue);
                break;
            case InkBlue:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_inkblue);
                break;
            case Orange:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_orange);
                break;
        }

        switch (BucketType.valueOf(bucket.getBucketType())) {
            case Tasks:
                holder.ivBucketIcon.setImageResource(R.drawable.ic_bc_tasks_on);
                break;
            case Personal:
                holder.ivBucketIcon.setImageResource(R.drawable.ic_bc_personal_on);
                break;
            case Gym:
                holder.ivBucketIcon.setImageResource(R.drawable.ic_bc_gym_on);
                break;
            case Work:
                holder.ivBucketIcon.setImageResource(R.drawable.ic_bc_briefcase_on);
                break;
            case House:
                holder.ivBucketIcon.setImageResource(R.drawable.ic_bc_house_on);
                break;
        }

        holder.item.setOnClickListener(v -> EventBus.getDefault().post(new SelectBucketEvent(bucket)));

        if (position == 0) {
            return;
        }

    }

    @Override
    public int getItemCount() {
        return bucketList.size() + 1;
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
