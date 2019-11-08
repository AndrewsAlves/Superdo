package com.andysapps.superdo.todo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.model.Task;
import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class TasksRecyclerAdapter extends GestureAdapter<Task, TasksRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "TasksRecyclerAdapter";
    private List<Task> taskList;

    private Context context;

    public TasksRecyclerAdapter(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void updateList(List<Task> taskList) {
        setData(new ArrayList<>(taskList));
        Log.e(TAG, "updateList: data size" + getDataCount() );
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Task task = taskList.get(position);

        holder.isChecked = task.isTaskCompleted();

        holder.tvTaskName.setText(task.getName());

        holder.ivCheck.getDrawable().setColorFilter(context.getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_ATOP);

        if (task.getBucketColor() != null) {
            holder.ivCheck.setColorFilter(Color.parseColor(task.getBucketColor()), PorterDuff.Mode.SRC);
        }

        if (task.getDueDate() != null) {
            String dueDate = task.getDueDate()[1] + " " + Utils.getMonthString(task.getDueDate()[1]);
            holder.tvDueDate.setText(dueDate);

            holder.tvDueDate.setVisibility(View.VISIBLE);
        }

        if (task.getRepeat() != null) {
            holder.ivRepeat.setVisibility(View.VISIBLE);
        }

        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            holder.ivSubtasks.setVisibility(View.VISIBLE);
        }

        holder.lottieCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.isChecked) {
                    holder.lottieCheckView.setSpeed(-2f);
                    holder.isChecked = false;
                } else {
                    holder.lottieCheckView.setSpeed(1.5f);
                    holder.isChecked = true;
                }

                holder.lottieCheckView.playAnimation();
            }
        });
    }

    @Override
    public List<Task> getData() {
        return super.getData();
    }

    public int getDataCount() {
        return getData().size();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class PlaceViewHolder extends GestureViewHolder<Task> {

        @BindView(R.id.lottie_check_view)
        LottieAnimationView lottieCheckView;

        @BindView(R.id.iv_check_task)
        public ImageView ivCheck;

        @BindView(R.id.tv_tasks_name)
        public TextView tvTaskName;

        @BindView(R.id.tv_due_date)
        public TextView tvDueDate;

        @BindView(R.id.iv_repeat)
        public ImageView ivRepeat;

        @BindView(R.id.iv_sub_tasks)
        public ImageView ivSubtasks;

        public View item;

        public boolean isChecked = false;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieCheckView.setAnimation("anim_check2.json");
        }

        @Override
        public boolean canDrag() {
            return false;
        }

        @Override
        public boolean canSwipe() {
            return false;
        }
    }
}
