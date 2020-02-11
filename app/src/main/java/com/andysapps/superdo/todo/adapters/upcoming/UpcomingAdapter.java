package com.andysapps.superdo.todo.adapters.upcoming;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.adapters.TasksRecyclerAdapter;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.Upcoming;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

/**
 * Created by Andrews on 10,February,2020
 */

public class UpcomingAdapter extends ExpandableRecyclerViewAdapter<UpcomingAdapter.UpcomingViewHolder, UpcomingAdapter.UpcomingChildTaskViewHolder> {

    Context context;

    List<Task> thisWeekTaskList;
    List<Task> thisMonthTaskList;
    List<Task> upcomingTaskList;

    public UpcomingAdapter(Context context,List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
        thisWeekTaskList = TaskOrganiser.getInstance().getThisWeekTaskList();
        thisMonthTaskList = TaskOrganiser.getInstance().getThisMonthTaskList();
        upcomingTaskList = TaskOrganiser.getInstance().getUpcomingTaskList();
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public UpcomingViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upcoming_group, parent, false);
        return new UpcomingViewHolder(view);
    }

    @Override
    public UpcomingAdapter.UpcomingChildTaskViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new UpcomingChildTaskViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(UpcomingViewHolder holder, int flatPosition, ExpandableGroup group) {
        switch (flatPosition) {
            case 0:
                holder.tvUpcomingGroup.setText("This week");
                break;
            case 1:
                holder.tvUpcomingGroup.setText("This month");
                break;
            case 2:
                holder.tvUpcomingGroup.setText("Upcoming");
                break;
        }
    }

    @Override
    public void onBindChildViewHolder(UpcomingAdapter.UpcomingChildTaskViewHolder h, int flatPosition, ExpandableGroup group, int childIndex) {

        Task task;

        switch (flatPosition) {
            case 0:
                task = thisWeekTaskList.get(childIndex);
                break;
            case 1:
                task = thisMonthTaskList.get(childIndex);
                break;
            case 2:
                task = upcomingTaskList.get(childIndex);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + flatPosition);
        }

        h.tvTaskName.setText(task.getName());

        /*if (task.getBucketColor() != null) {
            h.ivCheck.setColorFilter(Color.parseColor(task.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
        }*/

        h.isChecked = task.isTaskCompleted();

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

    public class UpcomingViewHolder extends GroupViewHolder {

        @BindView(R.id.tv_upcoming_group)
        TextView tvUpcomingGroup;

        public UpcomingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    /////////
    ///// CHILD VIEW HOLDER
    /////////

    public class UpcomingChildTaskViewHolder extends ChildViewHolder {

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

        StrikeThroughPainting painting;

        public View item;

        public boolean isChecked = false;

        public UpcomingChildTaskViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieCheckView.setAnimation("anim_check2.json");
            painting = new StrikeThroughPainting(tvTaskName);
        }
    }

    private void strikeOutText(UpcomingChildTaskViewHolder holder) {

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

    private void strikeInText(UpcomingChildTaskViewHolder holder) {
        holder.painting.clearStrikeThrough();
    }

}
