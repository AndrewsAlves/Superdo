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
import com.andysapps.superdo.todo.enums.UndoType;
import com.andysapps.superdo.todo.events.ShowSnakeBarEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.update.UpdateUiAllTasksEvent;
import com.andysapps.superdo.todo.events.update.UpdateUiCPMDEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SuperdoAudioManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

/**
 * Created by Andrews on 15,August,2019
 */

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";
    public List<Task> taskList;

    private Context context;
    private Handler viewUpdateHandler;

    public TasksRecyclerAdapter(Context context, List<Task> taskList) {
        this.taskList = new ArrayList<>();
        this.taskList.addAll(taskList);
        this.context = context;
        viewUpdateHandler = new Handler();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
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
                EventBus.getDefault().post(new ShowSnakeBarEvent(TasksRecyclerAdapter.this, null, task, i, UndoType.MOVED_TO_BIN));
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
        TaskOrganiser.getInstance().organiseAllTasks();
        EventBus.getDefault().post(new UpdateUiAllTasksEvent());
    }

    public void undoMovedToBin(Task task,int position) {
        taskList.add(position, task);
        task.setMovedToBin(false);
        notifyItemInserted(position);
        FirestoreManager.getInstance().updateTask(task);
        EventBus.getDefault().post(new UpdateUiAllTasksEvent());
    }

    public int getTaskIndex(Task task) {
        for (int i = 0 ; i < this.taskList.size() ; i++) {
            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                return i;
            }
        }
        return -1;
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
            h.lottieCheckView.setVisibility(View.VISIBLE);
            h.lottieCheckView.setMinAndMaxProgress(1.0f, 1.0f);
            strikeOutText(h, 0);
        } else {
            h.lottieCheckView.setVisibility(View.INVISIBLE);
            h.lottieCheckView.setMinAndMaxProgress(0.0f, 0.0f);
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

        if (task.getSubtasks() != null && task.getSubtasks().subtaskList.size() > 0) {
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

        h.lottieCheckView.addValueCallback(new KeyPath("Shape Layer 1", "**"), LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(context.getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_ATOP)
        );

        h.btnTaskCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.lottieCheckView.setVisibility(View.VISIBLE);
                h.lottieCheckView.setMinAndMaxProgress(0.0f, 1.0f);
                if (h.isChecked) {
                    h.lottieCheckView.setSpeed(-2.0f);
                    h.isChecked = false;
                    //strikeInText(h);
                } else {
                    h.lottieCheckView.setSpeed(2.0f);
                    h.isChecked = true;
                    strikeOutText(h, 500);
                    SuperdoAudioManager.getInstance().playTaskCompleted();
                }

                task.setTaskCompleted(h.isChecked);

                //// SET TASK COMPLETED
                if (h.isChecked) {
                    viewUpdateHandler.postDelayed(() -> setTaskCompleted(h.getAdapterPosition(), task), 500);

                }

                h.lottieCheckView.playAnimation();
            }
        });

        PushDownAnim.setPushDownAnimTo(h.itemView)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(v -> {
                    EventBus.getDefault().post(new OpenEditTaskEvent(task));
                });
    }

    public void setTaskCompleted(int position, Task task) {
        Log.e(TAG, "run: position " + position);
        taskList.remove(position);
        task.setTaskCompletedDate(Calendar.getInstance().getTime());
        EventBus.getDefault().post(new UpdateUiAllTasksEvent());
        EventBus.getDefault().post(new ShowSnakeBarEvent(TasksRecyclerAdapter.this, null, task, position, UndoType.TASK_COMPLETED));
        notifyItemRemoved(position);
        FirestoreManager.getInstance().updateTask(task);
        TaskOrganiser.getInstance().organiseAllTasks();
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

        StrikeThroughPainting painting;

        public View item;

        public boolean isChecked = false;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this, itemView);

            lottieCheckView.setAnimation("anim_check2.json");
        }
    }
}
