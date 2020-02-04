package com.andysapps.superdo.todo.adapters.MainAdapters;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import com.andysapps.superdo.todo.events.habit.OpenEditHabitEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Habit;
import com.andysapps.superdo.todo.model.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

/**
 * Created by Andrews on 15,August,2019
 */

public class HabitRecyclerAdapter extends RecyclerView.Adapter<HabitRecyclerAdapter.PlaceViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "TasksRecyclerAdapter";
    private List<Habit> habitList;

    private Context context;

    public HabitRecyclerAdapter(Context context, List<Habit> habitList) {
        this.habitList = habitList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_habit, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void notifyHabitAdded(List<Habit> habitList) {
        this.habitList.clear();
        this.habitList.addAll(habitList);
        notifyDataSetChanged();
        notifyItemInserted(habitList.size() - 1);
    }

    public void notifyHabitRemoved(Habit habit) {
        for (int i = 0; i < this.habitList.size() ; i++) {
            if (this.habitList.get(i).getDocumentId().equals(habit.getDocumentId())) {
                notifyItemRemoved(i);
                this.habitList.remove(i);
            }
        }
    }

    public void updateList(List<Habit> habitList) {

        Log.e(TAG, "updateList: data size" + this.habitList.size());

        this.habitList.clear();
        this.habitList.addAll(habitList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder h, int position) {

        Habit habit = habitList.get(position);

        ////////
        // UI

        h.tvHabitTitle.setText(habit.getHabitTitle());
        h.isChecked = habit.isHabitDone();

        if (h.isChecked) {
            h.lottieCheckView.setMinAndMaxProgress(1.0f, 1.0f);
        } else {
            h.lottieCheckView.setMinAndMaxProgress(0.0f, 0.0f);
        }

        h.lottieCheckView.playAnimation();
        h.lottieHabitProgress.playAnimation();

        h.lottieCheckView.addValueCallback(
                new KeyPath("Shape Layer 1", "**"),
                LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return new PorterDuffColorFilter(Utils.getColor(context, habit.getBucketColor()), PorterDuff.Mode.SRC_ATOP);
                    }
                }
        );

        if (habit.getBucketId() != null) {
            switch (BucketColors.valueOf(habit.getBucketColor())) {
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

        switch (habit.getHabitCategory()) {
            case Health:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_health_on);
                h.tvHabitCategory.setText("Health");
                break;
            case finance:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_finance_on);
                h.tvHabitCategory.setText("Finance");
                break;
            case Productivity:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_productivity_on);
                h.tvHabitCategory.setText("Productivity");
                break;
            case DigitalWellbeign:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_digital_wellbeing_on);
                h.tvHabitCategory.setText("Digital Wellbeing");
                break;
            case Mindfullness:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_mindfullness_on);
                h.tvHabitCategory.setText("Mindfullness");
                break;
            case Addiction:
                h.ivHabitCategory.setImageResource(R.drawable.ic_ch_addiction_on);
                h.tvHabitCategory.setText("Addiction");
                break;
        }

        ///////
        // ACTIONS

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

                habit.setHabitDone(h.isChecked);
                FirestoreManager.getInstance().updateHabit(habit);

                h.lottieCheckView.playAnimation();
            }
        });

        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenEditHabitEvent(habit));
            }
        });
    }

    private void strikeOutText(PlaceViewHolder holder) {

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

    private void strikeInText(PlaceViewHolder holder) {
        holder.painting.clearStrikeThrough();
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(habitList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(habitList, i, i - 1);
            }
        }

        for (Habit task : habitList) {
            task.setHabitIndex(habitList.indexOf(task));
            FirestoreManager.getInstance().updateHabit(task);
        }

        Log.e(TAG, "onItemMove: from : " + fromPosition);
        TaskOrganiser.getInstance().organiseAllTasks();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Log.e(TAG, "onItemDismiss: " + position );
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lottie_check_view)
        LottieAnimationView lottieCheckView;

        @BindView(R.id.lottie_habit_progress)
        LottieAnimationView lottieHabitProgress;

        @BindView(R.id.iv_check_habit)
        public ImageView ivCheck;

        @BindView(R.id.tv_habit_name)
        public ExTextView tvHabitTitle;

        @BindView(R.id.tv_habit_category)
        public TextView tvHabitCategory;

        @BindView(R.id.iv_habit_category)
        public ImageView ivHabitCategory;

        @BindView(R.id.parent_ll_habit_icons)
        public LinearLayout parentIcons;

        StrikeThroughPainting painting;

        public View item;

        public boolean isChecked = false;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);

            lottieCheckView.setAnimation("anim_check2.json");
            lottieHabitProgress.setAnimation("habit_arrow_up_green.json");
            painting = new StrikeThroughPainting(tvHabitTitle);
        }

    }
}
