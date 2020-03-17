package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.ItemTouchHelperAdapter;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.CPMD;
import com.andysapps.superdo.todo.enums.UndoType;
import com.andysapps.superdo.todo.events.ShowSnakeBarCPMDEvent;
import com.andysapps.superdo.todo.events.UpdateCpmdTitleEvent;
import com.andysapps.superdo.todo.events.profile.SelectProfileTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

/**
 * Created by Andrews on 15,August,2019
 */

public class CPMDRecyclerAdapter extends RecyclerView.Adapter<CPMDRecyclerAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";
    private List<Task> taskList;

    Task lastCompletedTask;
    int lastCompletedTaskPos;
    boolean isSeleting;
    boolean selectAll;

    HashMap<String, Task> selectedTask;
    CPMD cpmd;

    private Context context;
    private Handler viewUpdateHandler;

    public CPMDRecyclerAdapter(Context context, List<Task> taskList, CPMD cpmd) {
        this.taskList = taskList;
        this.context = context;
        this.cpmd = cpmd;
        viewUpdateHandler = new Handler();
        selectedTask = new HashMap<>();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task_cpdm, viewGroup, false);
        return new TaskViewHolder(view);
    }

    public void addTask(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    public void removeTask(Task task) {
        for (int i = 0 ; i < this.taskList.size() ; i++) {
            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                notifyItemRemoved(i);
                this.taskList.remove(i);
            }
        }
    }

    public void notifyModifiedItem(Task task) {
        for (int i = 0 ; i < this.taskList.size() ; i++) {
            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                taskList.set(i, task);
                notifyItemChanged(i);
            }
        }
    }

    public void updateList(List<Task> taskList) {

        Log.e(TAG, "updateList: data size" + this.taskList.size());

        this.taskList.clear();
        this.taskList.addAll(taskList);
        notifyDataSetChanged();
    }

    public void undoTaskCompleted(Task task,int position) {
        taskList.add(position, task);
        task.setTaskCompleted(false);
        notifyItemInserted(position);
        FirestoreManager.getInstance().updateTask(task);
    }

    public void undoTaskNotCompleted(Task task,int position) {
        taskList.add(position, task);
        task.setTaskCompleted(true);
        notifyItemInserted(position);
        FirestoreManager.getInstance().updateTask(task);
    }

    public void clearSelection() {
        isSeleting = false;
        selectedTask.clear();
        notifyDataSetChanged();
    }

    public void selectAll(boolean selectAll) {
        this.selectAll = selectAll;
        if (selectAll) {
            selectedTask.clear();
            for (Task task : taskList) {
                selectedTask.put(task.getDocumentId(), task);
            }
        } else {
            selectedTask.clear();
        }
        notifyDataSetChanged();
    }

    public HashMap<String, Task> getSelectedTask() {
        return selectedTask;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder h, int position) {

        Task task = taskList.get(position);

        h.tvTaskName.setText(task.getName());

        if (h.painting != null) {
            h.painting.clearStrikeThrough();
        }

        h.painting = new StrikeThroughPainting(h.tvTaskName);

        h.isChecked = task.isTaskCompleted();

        if (h.isChecked) {
            h.lottieCheckView.setMinAndMaxProgress(1.0f, 1.0f);
            strikeOutText(h, 0);
        } else {
            h.lottieCheckView.setMinAndMaxProgress(0.0f, 0.0f);
            h.painting.clearStrikeThrough();
        }

        h.lottieCheckView.playAnimation();

        h.ivRepeat.setVisibility(View.GONE);
        h.ivDeadline.setVisibility(View.GONE);
        h.ivSubtasks.setVisibility(View.GONE);
        h.ivRemind.setVisibility(View.GONE);
        h.ivFocus.setVisibility(View.GONE);
        h.parentIcons.setVisibility(View.GONE);

        if (task.getRepeat() != null) {
            h.ivRepeat.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getDeadline() != null) {
            h.ivDeadline.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getSubtasks() != null) {
            h.ivSubtasks.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.isToRemind()) {
            h.ivRemind.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getFocus() != null) {
            h.ivFocus.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        h.ivCheck.setImageResource(R.drawable.img_oval_thin_grey3);

        h.lottieCheckView.addValueCallback(
                new KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(context.getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_ATOP)
        );

        if (!isSeleting) {
            h.cbSelection.setOnCheckedChangeListener(null);
        } else {
            h.cbSelection.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTask.put(taskList.get(position).getDocumentId(), taskList.get(position));
                    EventBus.getDefault().post(new UpdateCpmdTitleEvent());
                } else {
                    selectedTask.remove(taskList.get(position).getDocumentId());
                    EventBus.getDefault().post(new UpdateCpmdTitleEvent());
                }
            });
        }

        h.btnTaskCompleted.setOnClickListener(v -> {

            if (isSeleting) {
                return;
            }

            h.lottieCheckView.setMinAndMaxProgress(0.0f, 1.0f);
            if (h.isChecked) {
                h.lottieCheckView.setSpeed(-2.0f);
                h.isChecked = false;
                strikeInText(h);
            } else {
                h.lottieCheckView.setSpeed(2.0f);
                h.isChecked = true;
                strikeOutText(h, 500);
            }

            task.setTaskCompleted(h.isChecked);

            //// SET TASK COMPLETED

            if (!h.isChecked && cpmd == CPMD.COMPLETED) {
                setTaskNotCompleted(h.getAdapterPosition(), task);
            }

            if (h.isChecked && cpmd == CPMD.PENDING) {
                setTaskCompleted(h.getAdapterPosition(), task);
            }

            if (h.isChecked && cpmd == CPMD.MISSED) {
                setTaskCompleted(h.getAdapterPosition(), task);
            }

            FirestoreManager.getInstance().updateTask(task);
            TaskOrganiser.getInstance().organiseAllTasks();

            h.lottieCheckView.playAnimation();
        });

        h.itemView.setOnClickListener(v -> {
            if (isSeleting) {
                h.cbSelection.setChecked(!h.cbSelection.isChecked());
                return;
            }
            EventBus.getDefault().post(new OpenEditTaskEvent(task));
        });

        if (isSeleting) {
            h.cbSelection.setVisibility(View.VISIBLE);
            if (selectAll) {
                h.cbSelection.setChecked(true);
            }
        } else {
            h.cbSelection.setChecked(false);
            h.cbSelection.setVisibility(View.GONE);
        }

        if (selectedTask.containsKey(taskList.get(position).getDocumentId())) {
            h.cbSelection.setChecked(true);
            Log.e(TAG, "item selected:");
        } else {
            h.cbSelection.setChecked(false);
        }

        h.itemView.setOnLongClickListener(v -> {
            if (isSeleting) {
                return false;
            }
            isSeleting = true;
            selectedTask.clear();
            h.cbSelection.setChecked(true);
            notifyDataSetChanged();
            EventBus.getDefault().post(new SelectProfileTaskEvent(true));
            EventBus.getDefault().post(new UpdateCpmdTitleEvent());
            return true;
        });
    }

    public void setTaskCompleted(int position, Task task) {
        viewUpdateHandler.postDelayed(() -> {
            taskList.remove(position);
            task.setTaskCompletedDate(Calendar.getInstance().getTime());
            EventBus.getDefault().post(new ShowSnakeBarCPMDEvent(CPMDRecyclerAdapter.this, task, position, UndoType.TASK_COMPLETED));
            notifyItemRemoved(position);
        },500);

        Log.e(TAG, "run: position " + position);
    }

    public void setTaskNotCompleted(int position, Task task) {
        viewUpdateHandler.postDelayed(() -> {
            taskList.remove(position);
            task.setTaskCompletedDate(null);
            EventBus.getDefault().post(new ShowSnakeBarCPMDEvent(CPMDRecyclerAdapter.this, task, position, UndoType.TASK_NOT_COMPLETED));
            notifyItemRemoved(position);
        },500);
    }

    private void strikeOutText(TaskViewHolder holder, int speed) {

        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1,
                context.getResources().getDisplayMetrics());

        holder.painting.cutTextEdge(true)
                // default to Color.BLACK
                .color(context.getResources().getColor(R.color.grey3))
                // default to 2F px
                .strokeWidth(pix)
                // default to StrikeThroughPainting.MODE_DEFAULT
                .mode(StrikeThroughPainting.MODE_DEFAULT)
                // default to 0.65F
                .linePosition(0.8F)
                // default to 0.6F, since the first line is calculated
                // differently to the following lines
                .firstLinePosition(0.6F)
                // default to 1_000 milliseconds, aka 1s
                .totalTime(speed)
                // do the draw!
                .strikeThrough();
    }

    private void strikeInText(TaskViewHolder holder) {
        holder.painting.clearStrikeThrough();
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

        for (Task task : taskList) {
            task.setTaskIndex(taskList.indexOf(task));
            FirestoreManager.getInstance().updateTask(task);
        }

        Log.e(TAG, "onItemMove: from : " + fromPosition);
        TaskOrganiser.getInstance().organiseAllTasks();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemMoved() {

    }

    @Override
    public void onItemDismiss(int position) {
        Log.e(TAG, "onItemDismiss: " + position );
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lottie_check_view)
        LottieAnimationView lottieCheckView;

        @BindView(R.id.iv_check_task)
        public ImageView ivCheck;

        @BindView(R.id.tv_tasks_name)
        public ExTextView tvTaskName;

        @BindView(R.id.iv_repeat)
        public ImageView ivRepeat;

        @BindView(R.id.btn_click_task_completed)
        public ImageButton btnTaskCompleted;

        @BindView(R.id.iv_deadline)
        public ImageView ivDeadline;

        @BindView(R.id.iv_subtasks)
        public ImageView ivSubtasks;

        @BindView(R.id.iv_remind)
        public ImageView ivRemind;

        @BindView(R.id.iv_focus)
        public ImageView ivFocus;

        @BindView(R.id.parent_ll_task_icons)
        public LinearLayout parentIcons;

        @BindView(R.id.cb_selected)
        public CheckBox cbSelection;

        StrikeThroughPainting painting;

        public View item;

        public boolean isChecked = false;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieCheckView.setAnimation("anim_check2.json");
            painting = new StrikeThroughPainting(tvTaskName);
        }

    }
}
