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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.Task;
import com.google.firebase.firestore.DocumentChange;
import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.PlaceViewHolder> implements ItemTouchHelperAdapter {

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

    public void updateList(List<Task> taskList, DocumentChange.Type updateType, Task task) {
        Log.e(TAG, "updateList: data size" + this.taskList.size());

        this.taskList.clear();
        this.taskList.addAll(taskList);

        if (updateType == null) {
            notifyDataSetChanged();
            return;
        }

        switch (updateType) {
            case ADDED:
                notifyItemInserted(taskList.size() - 1);
                break;
            /*case REMOVED:
                notifyItemRemoved(task.getTaskSize());
                break;
            case MODIFIED:
                notifyDataSetChanged();
                break;*/
        }
    }

    public void updateList(List<Task> taskList) {

        Log.e(TAG, "updateList: data size" + this.taskList.size());

        this.taskList.clear();
        this.taskList.addAll(taskList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder h, int position) {

        Task task = taskList.get(position);

        h.isChecked = task.isTaskCompleted();

        if (h.isChecked) {
            h.lottieCheckView.setProgress(1.0f);
        } else {
            h.lottieCheckView.setProgress(0.0f);
        }

        h.lottieCheckView.pauseAnimation();

        h.tvTaskName.setText(task.getName());

        h.ivCheck.getDrawable().setColorFilter(context.getResources().getColor(R.color.grey3), PorterDuff.Mode.SRC_ATOP);

        if (task.getBucketColor() != null) {
            h.ivCheck.setColorFilter(Color.parseColor(task.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
        }

        if (task.getDueDate() != null) {
            String dueDate = task.getDueDate().getDate() + " " + task.getDueDate().getMonthString();
            h.tvDueDate.setText(dueDate);

            h.tvDueDate.setVisibility(View.VISIBLE);
        }

        if (task.getRepeat() != null) {
            h.ivRepeat.setVisibility(View.VISIBLE);
        }

        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            h.ivSubtasks.setVisibility(View.VISIBLE);
        }

        h.lottieCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (h.isChecked) {
                    h.lottieCheckView.setSpeed(-2f);
                    h.isChecked = false;
                } else {
                    h.lottieCheckView.setSpeed(1.5f);
                    h.isChecked = true;
                }

                task.setTaskCompleted(h.isChecked);
                FirestoreManager.getInstance().updateTask(task);

                h.lottieCheckView.playAnimation();
            }
        });

        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenEditTaskEvent(task));
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(taskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(taskList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

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

    }
}
