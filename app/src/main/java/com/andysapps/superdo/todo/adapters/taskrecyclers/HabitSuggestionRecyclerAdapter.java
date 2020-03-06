package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.events.habit.SelectedHabitSuggestionEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class HabitSuggestionRecyclerAdapter extends RecyclerView.Adapter<HabitSuggestionRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "BucketRecyclerAdapter";
    private List<String> suggestionList;

    private Context context;

    public HabitSuggestionRecyclerAdapter(Context context, List<String> bucketList) {
        this.suggestionList = bucketList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_habit_suggestion, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    public void notifyBucketAdded(List<String> bucketList) {
        this.suggestionList.clear();
        this.suggestionList.addAll(bucketList);
        notifyDataSetChanged();
        notifyItemInserted(bucketList.size() - 1);
    }

    public void updateList(List<String> taskList) {

        Log.e(TAG, "updateList: data size" + this.suggestionList.size());

        this.suggestionList.clear();
        this.suggestionList.addAll(taskList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int i) {
        final int position = i;
        holder.tvHabitName.setText(suggestionList.get(position));
        holder.item.setOnClickListener(v -> EventBus.getDefault().post(new SelectedHabitSuggestionEvent(suggestionList.get(position))));
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_habit_name)
        public TextView tvHabitName;

        public View item;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
