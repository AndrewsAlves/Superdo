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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.ItemTouchHelperAdapter;
import com.andysapps.superdo.todo.enums.CPMD;
import com.andysapps.superdo.todo.events.ShowSnakeBarEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.events.update.UpdateUiAllTasksEvent;
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SuperdoAudioManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.Task;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

/**
 * Created by Andrews on 15,August,2019
 */

public class BucketTasksRecyclerAdapter extends RecyclerView.Adapter<BucketTasksRecyclerAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";
    public List<Task> taskList;
    public Bucket bucket;

    private Context context;
    private Handler viewUpdateHandler;

    public BucketTasksRecyclerAdapter(Context context, List<Task> taskList, Bucket bucket) {
        this.context = context;
        this.taskList = new ArrayList<>();
        this.taskList.addAll(taskList);
        this.bucket = bucket;
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
                int position = i;
                notifyItemRemoved(i);
                EventBus.getDefault().post(new ShowSnakeBarEvent(context.getString(R.string.snackbar_moved_to_bin), v -> undoMovedToBin(task ,position)));
                this.taskList.remove(i);
            }
        }
    }

    public void updateList(List<Task> taskList) {

        Log.e(TAG, "updateList: data size " + this.taskList.size());

        this.taskList.clear();
        this.taskList.addAll(taskList);
        this.notifyDataSetChanged();
    }

    public void undoTaskCompleted(Task task,int position) {
        taskList.add(position, task);
        task.setTaskAction(false);
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

    @Override
    public void onBindViewHolder(TaskViewHolder h, int position) {

        /// last postion as completed task list
        /*if (position == taskList.size()) {

            h.parentTask.setVisibility(View.GONE);
            h.lastSpace.setVisibility(View.VISIBLE);
            h.tvCompletedTask.setVisibility(View.VISIBLE);

            PushDownAnim.setPushDownAnimTo(h.tvCompletedTask)
                    .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                    .setOnClickListener(v -> {
                        EventBus.getDefault().post(new OpenFragmentEvent(
                                CPMDTasksFragment.Companion.instance(CPMD.COMPLETED, bucket),
                                false,
                                CPMDTasksFragment.TAG, true));
                    });
            return;
        }*/

        h.parentTask.setVisibility(View.VISIBLE);
        h.lastSpace.setVisibility(View.GONE);
        h.tvCompletedTask.setVisibility(View.GONE);

        Task task = taskList.get(position);

        h.tvTaskName.setText(task.getTitle());

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

        h.parentIcons.setVisibility(View.VISIBLE);
        h.ivDoDate.setVisibility(View.GONE);
        h.tvDoDate.setVisibility(View.VISIBLE);

        if (task.getDoDate() != null) {
            if (Utils.isSuperDateIsPast(task.getDoDate())) {
                h.ivDoDate.setVisibility(View.VISIBLE);
                h.ivDoDate.setImageResource(R.drawable.ic_missed_mini);
                h.tvDoDate.setTextColor(context.getResources().getColor(R.color.lightRed));
                h.tvDoDate.setText(task.getDoDate().getSuperDateString());
            } else {
                h.ivDoDate.setImageResource(R.drawable.ic_dodate_mini);
                h.tvDoDate.setTextColor(context.getResources().getColor(R.color.grey2));
                h.tvDoDate.setText("Do " + task.getDoDate().getSuperDateString());
            }
        } else {
            h.ivDoDate.setImageResource(R.drawable.ic_dodate_mini);
            h.tvDoDate.setText("Do Someday");
        }

        h.ivRepeat.setVisibility(View.GONE);
        h.ivDeadline.setVisibility(View.GONE);
        h.ivSubtasks.setVisibility(View.GONE);
        h.ivRemind.setVisibility(View.GONE);
        h.ivFocus.setVisibility(View.GONE);

        if (task.getRepeat() != null) {
            h.ivRepeat.setVisibility(View.VISIBLE);
        }

        if (task.getDeadline() != null) {
            h.ivDeadline.setVisibility(View.VISIBLE);
        }

        if (task.getSubtasks() != null && task.getSubtasks().subtaskList.size() > 0) {
            h.ivSubtasks.setVisibility(View.VISIBLE);
        }

        if (task.isToRemind()) {
            h.ivRemind.setVisibility(View.VISIBLE);
        }

        if (task.getFocus() != null) {
            h.ivFocus.setVisibility(View.VISIBLE);
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
                    strikeInText(h);
                } else {
                    h.lottieCheckView.setSpeed(2.0f);
                    h.isChecked = true;
                    strikeOutText(h, 500);
                    SuperdoAudioManager.getInstance().playTaskCompleted();
                }
                h.lottieCheckView.playAnimation();

                task.setTaskAction(h.isChecked);

                if (h.isChecked) {
                    viewUpdateHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTaskCompleted(h.getAdapterPosition(), task);
                        }
                    }, 500);
                }
            }
        });

        PushDownAnim.setPushDownAnimTo(h.itemView)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(v -> {
                    EventBus.getDefault().post(new OpenEditTaskEvent(task));
                });
    }

    public void setTaskCompleted(int position, Task task) {
        taskList.remove(position);
        EventBus.getDefault().post(new UpdateUiAllTasksEvent());
        notifyItemRemoved(position);
        FirestoreManager.getInstance().updateTask(task);
        TaskOrganiser.getInstance().organiseAllTasks();
        EventBus.getDefault().post(new ShowSnakeBarEvent(context.getString(R.string.snackbar_taskcompleted), v -> undoTaskCompleted(task, position)));
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
        return taskList.size() ;
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
    public void onItemMoved() { }

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

        @BindView(R.id.ll_parent_task)
        public LinearLayout parentTask;

        @BindView(R.id.last_element_space)
        public FrameLayout lastSpace;

        @BindView(R.id.btn_tv_completedtask)
        public TextView tvCompletedTask;

        @BindView(R.id.tv_task_dodate)
        public TextView tvDoDate;

        @BindView(R.id.iv_date)
        public ImageView ivDoDate;

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
