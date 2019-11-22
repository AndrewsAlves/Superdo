package com.andysapps.superdo.todo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.Bucket;
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

    public void notifyTaskAdded(List<Task> taskList) {
        this.taskList.clear();
        this.taskList.addAll(taskList);
        notifyDataSetChanged();
        notifyItemInserted(taskList.size() - 1);
    }

    public void notifyTaskRemoved(Task task) {
        for (int i = 0 ; i < this.taskList.size() ; i++) {
            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                notifyItemRemoved(i);
                this.taskList.remove(i);
            }
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


        if (task.getBucketColor() != null) {
            //h.ivCheck.setColorFilter(Color.parseColor(task.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
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

        if (task.getBucketId() != null) {
            switch (BucketColors.valueOf(task.getBucketColor())) {
                case Red:
                    h.ivCheck.setImageResource(R.drawable.img_oval_thin_red);
                    break;
                case Green:
                    h.ivCheck.setImageResource(R.drawable.img_oval_thin_green);
                    break;
                case SkyBlue:
                    h.ivCheck.setImageResource(R.drawable.img_oval_thin_skyblue);
                    break;
                case InkBlue:
                    h.ivCheck.setImageResource(R.drawable.img_oval_thin_inkblue);
                    break;
                case Orange:
                    h.ivCheck.setImageResource(R.drawable.img_oval_thin_orange);
                    break;
            }
        } else {
            h.ivCheck.setImageResource(R.drawable.img_oval_thin_grey3);
        }

        h.lottieCheckView.addValueCallback(
                new KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return new PorterDuffColorFilter(getColor(task.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
                    }
                }
        );

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

    public int getColor(String colors) {

        if (colors == null) {
            return  context.getResources().getColor(R.color.grey4);
        }

        BucketColors colors1 = BucketColors.valueOf(colors);

        int color = R.color.grey4;
        switch (colors1) {
            case Red:
                color = R.color.lightRed;
                break;
            case Green:
                color = R.color.green;
                break;
            case Orange:
                color = R.color.orange;
                break;
            case SkyBlue:
                color = R.color.skyblue;
                break;
            case InkBlue:
                color = R.color.inkBlue;
                break;

        }

        return context.getResources().getColor(color);
    }
}
