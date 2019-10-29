package com.andysapps.superdo.todo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.model.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.truckio.findtrucks.R;
import com.truckio.findtrucks.events.PlaceSelectedEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.PlaceViewHolder> {

    private List<Task> placesList;

    private Context context;

    public TasksRecyclerAdapter(Context context, List<Task> placesList) {
        this.placesList = placesList;
        this.context = context;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int i) {
        final int position = i;

        Task task = placesList.get(position);

        holder.tvTaskName.setText(task.getName());

        if (task.getDueDate() != null) {
            String dueDate = task.getDueDate()[1] + " " + Utils.getMonthString(task.getDueDate()[1]);
            holder.tvDueDate.setText(dueDate);

            holder.tvDueDate.setVisibility(View.VISIBLE);
        }

        if (task.getRepeat() != null) {
            holder.ivRepeat.setVisibility(View.VISIBLE);
        }

        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            holder.ivSubtasks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_tasks_name)
        public TextView tvTaskName;

        @BindView(R.id.tv_due_date)
        public TextView tvDueDate;

        @BindView(R.id.iv_repeat)
        public ImageView ivRepeat;

        @BindView(R.id.iv_sub_tasks)
        public ImageView ivSubtasks;

        public View item;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
