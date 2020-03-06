package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.ItemTouchHelperAdapter;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.events.sidekick.UpdateSubtasksEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.sidekicks.Subtask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class SubtasksRecyclerAdapter extends RecyclerView.Adapter<SubtasksRecyclerAdapter.PlaceViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "SubtasksRecyclerAdapter";
    private List<Subtask> subtaskList = new ArrayList<>();
    private Task task;
    private Context context;

    public SubtasksRecyclerAdapter(Context context, List<Subtask> taskList, Task task) {
        this.context = context;
        subtaskList = taskList;
        this.task = task;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_subtask, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void updateList() {

        notifyDataSetChanged();

        /*subtaskList.clear();
        subtaskList = new ArrayList<>();

        for (Subtask subtask : task.getSubtasks().subtaskList) {
            if (!subtask.isDeleted()) {
                subtaskList.add(subtask);
            }
        }

        //organise index based on index
        Collections.sort(subtaskList, new Comparator<Subtask>() {
            @Override
            public int compare(Subtask o1, Subtask o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        */

    }

    public void notifyTaskAdded(List<Subtask> taskList) {
        this.subtaskList.clear();
        this.subtaskList.addAll(taskList);
        notifyDataSetChanged();
        notifyItemInserted(taskList.size() - 1);
    }

    public void updateList(List<Subtask> taskList) {

        Log.e(TAG, "updateList: data size" + this.subtaskList.size());

        this.subtaskList.clear();
        this.subtaskList.addAll(taskList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder h, int position) {

        Subtask subtask = subtaskList.get(position);

        h.isChecked = subtask.isTaskCompleted();

        if (h.isChecked) {
            h.lottieSubtasks.setProgress(1.0f);
        } else {
            h.lottieSubtasks.setProgress(0.0f);
        }

        h.lottieSubtasks.pauseAnimation();

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

        h.tvSubtaskName.setText(subtask.getTitle());

        h.lottieSubtasks.addValueCallback(
                new KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(getColor(task.getBucketColor()), PorterDuff.Mode.SRC_ATOP)
        );

        h.lottieSubtasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (h.isChecked) {
                    h.lottieSubtasks.setSpeed(-3.5f);
                    h.isChecked = false;
                } else {
                    h.lottieSubtasks.setSpeed(3.5f);
                    h.isChecked = true;
                }

                subtask.setTaskCompleted(h.isChecked);
                FirestoreManager.getInstance().updateTask(task);

                h.lottieSubtasks.playAnimation();
            }
        });

        h.ibDeleteSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtaskList.remove(h.getAdapterPosition());
                notifyItemRemoved(h.getAdapterPosition());
                FirestoreManager.getInstance().updateTask(task);
                EventBus.getDefault().post(new UpdateSubtasksEvent());
                Utils.hideKeyboard(context, h.tvSubtaskName);
                h.tvSubtaskName.clearFocus();
            }
        });

        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        h.tvSubtaskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    h.tvSubtaskName.clearFocus();
                    subtask.title = h.tvSubtaskName.getText().toString();
                    Utils.hideKeyboard(context, h.tvSubtaskName);
                    notifyDataSetChanged();
                }
                return true;
            }
        });

        h.tvSubtaskName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    h.ibDeleteSubtask.setVisibility(View.VISIBLE);
                } else {
                    h.ibDeleteSubtask.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return subtaskList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(subtaskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(subtaskList, i, i - 1);
            }
        }

        Log.e(TAG, "onItemMove: from : " + fromPosition);
        subtaskList.get(toPosition).setIndex(toPosition);
        FirestoreManager.getInstance().updateTask(task);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemMoved() {

    }

    @Override
    public void onItemDismiss(int position) {
        Log.e(TAG, "onItemDismiss: " + position );
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lottie_check_view_subtask)
        LottieAnimationView lottieSubtasks;

        @BindView(R.id.iv_check_subtask)
        public ImageView ivCheck;

        @BindView(R.id.tv_subtask_name)
        public TextView tvSubtaskName;

        @BindView(R.id.ib_deletesubTask)
        public ImageButton ibDeleteSubtask;

        public View item;

        public boolean isChecked = false;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieSubtasks.setAnimation("anim_check2.json");
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
