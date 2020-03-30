package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.dialog.BucketActionBottomDialog;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.BucketType;
import com.andysapps.superdo.todo.events.ExitBucketTaskFragment;
import com.andysapps.superdo.todo.events.OpenBottomFragmentEvent;
import com.andysapps.superdo.todo.events.SetTasksFragment;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.SetBucketTaskListEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Bucket;
import com.thekhaeng.pushdownanim.PushDownAnim;

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

        String taskDone = TaskOrganiser.getInstance().getBucketTasksDoneCount(bucket) + " / " +
                TaskOrganiser.getInstance().getBucketTasksCount(bucket);

        holder.tvNoTasks.setText(taskDone);

        PushDownAnim.setPushDownAnimTo(holder.itemView)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(v -> {
                    if (holder.getAdapterPosition() == 0) {
                        EventBus.getDefault().post(new SetTasksFragment(null));
                    } else {
                        EventBus.getDefault().post(new SetTasksFragment(bucket));
                    }
                    EventBus.getDefault().post(new ExitBucketTaskFragment());
                });

        if (position == 0) {
            return;
        }

        holder.ibBucketActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenBottomFragmentEvent(BucketActionBottomDialog.Companion.instance(bucket)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bucketList.size() + 1;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ic_bucket_actions)
        ImageButton ibBucketActions;

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
