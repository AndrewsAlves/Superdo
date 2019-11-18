package com.andysapps.superdo.todo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.events.OpenAddBucketFragmentEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.fragments.AddBucketFragment;
import com.andysapps.superdo.todo.fragments.BucketTasksFragment;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.google.firebase.firestore.DocumentChange;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class BucketsRecyclerAdapter extends RecyclerView.Adapter<BucketsRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "BucketRecyclerAdapter";
    private List<Bucket> bucketList;

    private Context context;

    public BucketsRecyclerAdapter(Context context, List<Bucket> bucketList) {
        this.bucketList = bucketList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bucket, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void updateList(List<Bucket> taskList, DocumentChange.Type updateType, Bucket task) {

        Log.e(TAG, "updateList: data size" + this.bucketList.size());

        this.bucketList.clear();
        this.bucketList.addAll(taskList);

        if (updateType == null) {
            notifyDataSetChanged();
            return;
        }

        switch (updateType) {
            case ADDED:
                notifyItemInserted(taskList.size() - 1);
                break;
           /* case REMOVED:
                break;
            case MODIFIED:
                notifyDataSetChanged();
                break;*/
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
                holder.ivTag.setImageResource(R.drawable.img_oval_light_red_mini);
                break;
            case Green:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_green_mini);
                break;
            case SkyBlue:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_skyblue_mini);
                break;
            case InkBlue:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_inkblue_mini);
                break;
            case Orange:
                holder.ivTag.setImageResource(R.drawable.img_oval_light_orange_mini);
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

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return bucketList.size() + 1;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv_parentview)
        public CardView parentView;

        @BindView(R.id.tv_bucket_name)
        public TextView tvBucketName;

        @BindView(R.id.iv_bucket_icon_1)
        ImageView ivBucketIcon;

        @BindView(R.id.tv_no_tasks_done)
        public TextView tvNoTasks;

        @BindView(R.id.iv_tag)
        public ImageView ivTag;

        public View item;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
