package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.graphics.ColorFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.ItemTouchHelperAdapter;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.enums.UndoType;
import com.andysapps.superdo.todo.events.ShowSnakeBarEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
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

public class UpcomingManualAdapter extends RecyclerView.Adapter<UpcomingManualAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";

    private static final String dummyWeekTitle = "1";
    private static final String dummyMonthTitle = "2";
    private static final String dummyUpcomingTitle = "3";

    Task weekDumTask = new Task();
    Task monthDumTask = new Task();
    Task upcomingDumTask = new Task();

    public List<Task> taskList;

    List<Task> weekTaskList;
    List<Task> monthTaskList;
    List<Task> upcomingTasklist;

    Handler viewUpdateHandler;

    private Context context;

    public UpcomingManualAdapter(Context context) {
        this.context = context;
        this.viewUpdateHandler = new Handler();
        setUpcomingTaskList();
    }

    public void setUpcomingTaskList() {

        weekTaskList = new ArrayList<>();
        monthTaskList = new ArrayList<>();
        upcomingTasklist = new ArrayList<>();

        weekDumTask.setDocumentId(dummyWeekTitle);
        monthDumTask.setDocumentId(dummyMonthTitle);
        upcomingDumTask.setDocumentId(dummyUpcomingTitle);

        weekTaskList.add(weekDumTask);
        weekTaskList.addAll(TaskOrganiser.getInstance().getWeekTaskList());

        monthTaskList.add(monthDumTask);
        monthTaskList.addAll(TaskOrganiser.getInstance().getMonthTaskList());

        upcomingTasklist.add(upcomingDumTask);
        upcomingTasklist.addAll(TaskOrganiser.getInstance().getUpcomingTaskList());

        taskList = new ArrayList<>();
        taskList.addAll(weekTaskList);
        taskList.addAll(monthTaskList);
        taskList.addAll(upcomingTasklist);

        Log.e(TAG, "Task updated");
    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_upcoming_group, viewGroup, false);
        return new TaskViewHolder(view);
    }

    public void addTask(Task task) {

        int addIndex;

        Log.e(TAG, "addTask: upcoming add task ");

        switch (task.getListedIn()) {
            case THIS_WEEK:
                weekTaskList.add(task);
                addIndex = weekTaskList.size() - 1;
                //refreshList();
                taskList.add(addIndex, task);
                notifyItemInserted(addIndex);
                notifyItemChanged(0);
                break;
            case THIS_MONTH:
                monthTaskList.add(task);
                addIndex = weekTaskList.size() + monthTaskList.size() - 1;
                //refreshList();
                taskList.add(addIndex, task);
                notifyItemInserted(addIndex);
                notifyItemChanged(weekTaskList.size());
                break;
            case UPCOMING:
                upcomingTasklist.add(task);
                addIndex = weekTaskList.size() + monthTaskList.size() + upcomingTasklist.size() - 1;
                //refreshList();
                taskList.add(addIndex, task);
                notifyItemInserted(addIndex);
                notifyItemChanged(weekTaskList.size() + monthTaskList.size());
                break;
        }

        reaarageGroupTasks(-1);
    }

    public void removeTask(Task task) {

        for (int i = 0 ; i < this.taskList.size() ; i++) {

            if (taskList.get(i).getTaskIndex() < 0) {
                continue;
            }

            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                int position = i;
                this.taskList.remove(i);
                notifyItemRemoved(i);
                EventBus.getDefault().post(new ShowSnakeBarEvent(context.getString(R.string.snackbar_moved_to_bin), v -> undoMovedToBin(task ,position)));
                switch (task.getListedIn()) {
                    case THIS_WEEK:
                        notifyItemChanged(0);
                        break;
                    case THIS_MONTH:
                        notifyItemChanged(weekTaskList.size());
                        break;
                    case UPCOMING:
                        notifyItemChanged(weekTaskList.size() + monthTaskList.size());
                        break;
                }

            }
        }

        reaarageGroupTasks(-1);
    }

    public void undoTaskCompleted(Task task,int position) {
        taskList.add(position, task);
        task.setTaskAction(false);
        notifyItemInserted(position);
        reaarageGroupTasks(-1);
        FirestoreManager.getInstance().updateTask(task);
        TaskOrganiser.getInstance().organiseAllTasks();
    }

    public void undoMovedToBin(Task task,int position) {
        taskList.add(position, task);
        task.setMovedToBin(false);
        notifyItemInserted(position);
        reaarageGroupTasks(-1);
        FirestoreManager.getInstance().updateTask(task);
    }

    public void updateList() {
        setUpcomingTaskList();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TaskViewHolder h, int position) {

        Task task = taskList.get(position);

        if (task.getDocumentId().equals(dummyWeekTitle)
                || task.getDocumentId().equals(dummyMonthTitle)
                || task.getDocumentId().equals(dummyUpcomingTitle) ) {

            switch (task.getDocumentId()) {
                case dummyWeekTitle:
                    h.tvUpcomingTitle.setText("This week" + " " + "(" + (weekTaskList.size() - 1) + ")");
                    break;
                case dummyMonthTitle:
                    h.tvUpcomingTitle.setText("This month" + " " + "(" + (monthTaskList.size() - 1) + ")");
                    break;
                case dummyUpcomingTitle:
                    h.tvUpcomingTitle.setText("Upcoming" + " " + "(" + (upcomingTasklist.size() - 1) + ")");
                    break;
            }

            //Log.e(TAG, "Task name and index : " + task.getDocumentId() + " " + task.getTaskIndex());

            h.tvUpcomingTitle.setVisibility(View.VISIBLE);
            h.itemView.setClickable(false); // disable click for
            h.parentTaskItem.setVisibility(View.GONE);
            return;
        }

        if (h.painting != null) {
            h.painting.clearStrikeThrough();
        }

        h.painting = new StrikeThroughPainting(h.tvTaskName);

        h.tvUpcomingTitle.setVisibility(View.GONE);
        h.parentTaskItem.setVisibility(View.VISIBLE);

        h.isChecked = task.isTaskCompleted();

        h.tvTaskName.setText(task.getName());

        if (task.getDoDate() == null) {
            h.tvUpcomingDate.setVisibility(View.GONE);
        } else {
            h.tvUpcomingDate.setVisibility(View.VISIBLE);
            switch (task.getListedIn()) {
                case THIS_WEEK:
                    h.tvUpcomingDate.setText(Utils.getWeekDayStr(task.getDoDate()));
                    break;
                case THIS_MONTH:
                    h.tvUpcomingDate.setText(Utils.monthDates[task.getDoDate().getDate() - 1]);
                    break;
                case UPCOMING:
                    h.tvUpcomingDate.setText(Utils.getMonthString(task.getDoDate().getMonth()) + " " + Utils.monthDates[task.getDoDate().getDate() - 1]);
                    break;
            }


        }

        if (h.isChecked) {
            h.lottieCheckView.setMinAndMaxProgress(1.0f, 1.0f);
            strikeOutText(h, 0);
        } else {
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

        if (task.getSubtasks() != null) {
            h.ivSubtasks.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.isToRemind()) {
            h.ivRemind.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        h.ivCheck.setImageResource(R.drawable.img_oval_thin_grey3);

        h.lottieCheckView.addValueCallback(
                new KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(context.getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_ATOP)
        );

        h.btnTaskCompleted.setOnClickListener(v -> {
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

            Log.e(TAG, "onClick: clicked task completed in upcoming manager");
            task.setTaskAction(h.isChecked);

            //// SET TASK COMPLETED
            if (h.isChecked) {
                viewUpdateHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTaskCompleted(h.getAdapterPosition(), task);
                    }
                }, 500);

            }

            h.lottieCheckView.playAnimation();
        });

        PushDownAnim.setPushDownAnimTo(h.itemView)
                .setScale(PushDownAnim.MODE_SCALE, 0.96f)
                .setOnClickListener(v -> {
                    EventBus.getDefault().post(new OpenEditTaskEvent(task));
                });
    }

    public void setTaskCompleted(int position, Task task) {
        taskList.remove(position);
        reaarageGroupTasks(-1);
        task.setTaskCompletedDate(Calendar.getInstance().getTime());
        notifyItemRemoved(position);
        FirestoreManager.getInstance().updateTask(task);
        TaskOrganiser.getInstance().organiseAllTasks();
        EventBus.getDefault().post(new ShowSnakeBarEvent(context.getString(R.string.snackbar_taskcompleted), v -> undoTaskCompleted(task, position)));
    }

    private void strikeOutText(TaskViewHolder holder, long duration) {

        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2,
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
                .totalTime(duration)
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

        // dont move repeat tasks
        if (taskList.get(fromPosition).getRepeat() != null) {
            Toast.makeText(context, "Cannot move repeat task", Toast.LENGTH_SHORT).show();
            return;
        }

        if (toPosition == 0) {
            return;
        }

        viewUpdateHandler.removeCallbacksAndMessages(null);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(taskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(taskList, i, i - 1);
            }
        }

        updateTasks(toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateTasks(int toPostion) {
        viewUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reaarageGroupTasks(toPostion);
            }
        }, 800);
    }

    public void reaarageGroupTasks(int toPostion) {

        String curruntQueryGroup = dummyWeekTitle;

        weekTaskList.clear();
        monthTaskList.clear();
        upcomingTasklist.clear();
        weekTaskList.add(weekDumTask);
        monthTaskList.add(monthDumTask);
        upcomingTasklist.add(upcomingDumTask);

        for (Task task : taskList) {

            switch (task.getDocumentId()) {
                case dummyWeekTitle:
                    curruntQueryGroup = dummyWeekTitle;
                    continue;
                case dummyMonthTitle:
                    curruntQueryGroup = dummyMonthTitle;
                    continue;
                case dummyUpcomingTitle:
                    curruntQueryGroup = dummyUpcomingTitle;
                    continue;
            }

            switch (curruntQueryGroup) {
                case dummyWeekTitle:
                    weekTaskList.add(task);
                    break;
                case dummyMonthTitle:
                    monthTaskList.add(task);
                    break;
                case dummyUpcomingTitle:
                    upcomingTasklist.add(task);
                    break;
            }
        }

        setTaskListingAndDate(weekTaskList, TaskListing.THIS_WEEK);
        setTaskListingAndDate(monthTaskList, TaskListing.THIS_MONTH);
        setTaskListingAndDate(upcomingTasklist, TaskListing.UPCOMING);

        TaskOrganiser.getInstance().organiseAllTasks();

        notifyItemChanged(0);
        notifyItemChanged(weekTaskList.size());
        notifyItemChanged(weekTaskList.size() + monthTaskList.size());
        if (toPostion >= 0) {
            notifyItemChanged(toPostion);
        }
    }

    public void setTaskListingAndDate(List<Task> taskList, TaskListing taskListing) {
        for (Task task : taskList) {

            if (task.getDocumentId().equals(dummyWeekTitle)
                    || task.getDocumentId().equals(dummyMonthTitle)
                    || task.getDocumentId().equals(dummyUpcomingTitle) ) {

                continue;
            }

            task.setTaskIndex(taskList.indexOf(task));
            task.setListedIn(taskListing);

            if (Utils.getTaskListed(task.getDoDate()) != taskListing) {

                switch (taskListing) {
                    case THIS_WEEK:
                        task.setDoDate(Utils.getRandomWeekDay());
                        break;
                    case THIS_MONTH:
                        task.setDoDate(Utils.getRandomMonthDate());
                        break;
                    case UPCOMING:
                        task.setDoDate(null);
                        break;
                }
            }

            FirestoreManager.getInstance().updateTask(task);

            if (task.getDoDate() != null) {
                Log.e(TAG, "Task name and index : " + task.getTaskIndex() + " " + task.getListedIn() + task.getDoDate().getDate());
            }

        }
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

        @BindView(R.id.btn_click_task_completed)
        public ImageButton btnTaskCompleted;

        @BindView(R.id.iv_repeat)
        public ImageView ivRepeat;

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

        @BindView(R.id.ll_parent_upcomingtask)
        public LinearLayout parentTaskItem;

        @BindView(R.id.tv_upcoming_group)
        public TextView tvUpcomingTitle;

        @BindView(R.id.tv_upcoming_task_date)
        public TextView tvUpcomingDate;

        StrikeThroughPainting painting;
        public View item;

        public boolean isChecked = false;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieCheckView.setAnimation("anim_check2.json");
        }
    }
}
