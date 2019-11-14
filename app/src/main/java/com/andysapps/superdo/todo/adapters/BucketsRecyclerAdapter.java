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
        holder.tvBucketName.setTextColor(Color.parseColor(bucket.getTagColor()));
        holder.ivHaveDesc.setVisibility(View.VISIBLE);
        holder.ivTag.getDrawable().mutate().setColorFilter(Color.parseColor(bucket.getTagColor()), PorterDuff.Mode.SRC_IN);

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenFragmentEvent(new BucketTasksFragment()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bucketList.size() + 1;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_create_bucket)
        public RelativeLayout createBucket;

        @BindView(R.id.cv_parentview)
        public CardView parentView;

        @BindView(R.id.tv_bucket_name)
        public TextView tvBucketName;

        @BindView(R.id.iv_have_desc)
        public ImageView ivHaveDesc;

        @BindView(R.id.iv_tasks_tick)
        public ImageView ivTaskTick;

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
