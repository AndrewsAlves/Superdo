package com.andysapps.superdo.todo.adapters.upcoming;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;

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

public class UpcomingManualAdapter extends RecyclerView.Adapter<UpcomingManualAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";

    private static final String dummyWeekTitle = "week";
    private static final String dummyMonthTitle = "month";
    private static final String dummyUpcomingTitle = "upcoming";

    Task weekDumTask = new Task();
    Task monthDumTask = new Task();
    Task upcomingDumTask = new Task();

    private List<Task> taskList;

    List<Task> weekTaskList;
    List<Task> monthTaskList;
    List<Task> upcomingTasklist;

    Handler viewUpdateHandler;

    private Context context;

    public UpcomingManualAdapter(Context context) {
        this.context = context;
        this.viewUpdateHandler = new Handler();

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
    }

    public void refreshList() {
        taskList = new ArrayList<>();
        taskList.addAll(weekTaskList);
        taskList.addAll(monthTaskList);
        taskList.addAll(upcomingTasklist);
    }

    public void removeTask(Task task) {

        for (int i = 0 ; i < this.taskList.size() ; i++) {

            if (taskList.get(i).getTaskIndex() < 0) {
                continue;
            }

            if (this.taskList.get(i).getDocumentId().equals(task.getDocumentId())) {
                this.taskList.remove(i);
                notifyItemRemoved(i);

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

        reaarageGroupTasks();
    }

    public void updateList(List<Task> taskList) {

        Log.e(TAG, "updateList: data size" + this.taskList.size());

        this.taskList.clear();
        this.taskList.addAll(taskList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TaskViewHolder h, int position) {

        Task task = taskList.get(position);

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

        if (task.getDocumentId().equals(dummyWeekTitle)
                || task.getDocumentId().equals(dummyMonthTitle)
                || task.getDocumentId().equals(dummyUpcomingTitle) ) {
            h.tvUpcomingTitle.setVisibility(View.VISIBLE);
            h.itemView.setClickable(false); // disable click for
            h.parentTaskItem.setVisibility(View.GONE);
            return;
        }

        h.isChecked = task.isTaskCompleted();

        h.tvTaskName.setText(task.getName());

        if (task.getDoDate() == null) {
            h.tvUpcomingDate.setVisibility(View.GONE);
        } else {
            switch (task.getListedIn()) {
                case THIS_WEEK:
                    h.tvUpcomingDate.setText(Utils.getWeekDay(task.getDoDate()));
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

        if (task.getRepeat() != null && task.getRepeat().isEnabled) {
            h.ivRepeat.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getDeadline() != null && task.getDeadline().isEnabled) {
            h.ivDeadline.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getSubtasks() != null && task.getSubtasks().isEnabled) {
            h.ivSubtasks.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getRemind() != null && task.getRemind().isEnabled) {
            h.ivRemind.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
        }

        if (task.getFocus() != null && task.getFocus().isEnabled) {
            h.ivFocus.setVisibility(View.VISIBLE);
            h.parentIcons.setVisibility(View.VISIBLE);
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
                        return new PorterDuffColorFilter(Utils.getColor(context, task.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
                    }
                }
        );

        h.lottieCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.lottieCheckView.setMinAndMaxProgress(0.0f, 1.0f);
                if (h.isChecked) {
                    h.lottieCheckView.setSpeed(-2f);
                    h.isChecked = false;
                    strikeInText(h);
                } else {
                    h.lottieCheckView.setSpeed(1.5f);
                    h.isChecked = true;
                    strikeOutText(h);
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

    private void strikeOutText(TaskViewHolder holder) {

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
                .totalTime(500)
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

        updateTasks();
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateTasks() {
        viewUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reaarageGroupTasks();
            }
        }, 500);
    }

    public void reaarageGroupTasks() {

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

        for (Task task : taskList) {
            //Log.e(TAG, "Task name and index : " + task.getName() + " " + task.getTaskIndex());
            Log.e(TAG, "Task name and index : " + task.getDocumentId() + " " + task.getTaskIndex());
        }

        notifyDataSetChanged();



       // notifyItemChanged(0);
       // notifyItemChanged(weekTaskList.size());
        //notifyItemChanged(weekTaskList.size() + monthTaskList.size());
    }

    public void setTaskListingAndDate(List<Task> taskList, TaskListing taskListing) {
        for (Task task : taskList) {
            task.setTaskIndex(taskList.indexOf(task));
            task.setListedIn(taskListing);

            if (Utils.getTaskListed(task.getDoDate()) != taskListing) {

                switch (Utils.getTaskListed(task.getDoDate())) {
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

            //Log.e(TAG, "Task name and index : " + task.getName() + " " + task.getTaskIndex());
        }
    }

    @Override
    public void onItemMoved() {
        //reaarageGroupTasks();

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

        boolean isDragging;

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
