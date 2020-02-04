package com.andysapps.superdo.todo.adapters.MainAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.LongItemTouchHelperCallback;
import com.andysapps.superdo.todo.model.Habit;
import com.andysapps.superdo.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRecyclerAdapter extends RecyclerView.Adapter {

    List<Integer> lists;

    List<Task>  taskList;
    List<Habit>  habitList;
    List<Habit>  shoppingList;

    public ViewHolderHabits viewHolderHabits;
    public ViewHolderTasks viewHolderTasks;

    public Context ctx;

    public MainRecyclerAdapter(Context context,  List<Task> taskList, List<Habit> habitList, List<Habit> shoppingList) {
        this.ctx = context;
        this.taskList = taskList;
        this.habitList = habitList;
        this.shoppingList = shoppingList;

        lists = new ArrayList<>();
        lists.add(0);
        lists.add(1);
        lists.add(2);
    }

    public void updateAll(List<Task> taskList, List<Habit> habitList, List<Habit> shoppingList) {
        this.taskList.clear();
        this.habitList.clear();
        this.shoppingList.clear();
        this.taskList.addAll(taskList);
        this.habitList.addAll(habitList);
        this.shoppingList.addAll(shoppingList);

        if (viewHolderTasks == null) {
            return;
        }

        notifyDataSetChanged();

        //viewHolderTasks.updateAll();
        //viewHolderHabits.updateAll();
    }

    public void updateTaskList() {

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_main_tasks_holder, parent, false);
                viewHolderTasks = new ViewHolderTasks(view);
                return viewHolderTasks;

            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_main_habit_holder, parent, false);
                viewHolderHabits = new ViewHolderHabits(view);
                return viewHolderHabits;

            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_main_shopping_holder, parent, false);
                return new ViewHolderTasks(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return lists.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int type = lists.get(position);

        switch (type) {

            case 0:
                ((ViewHolderTasks) holder).setTasksList(position);
                break;
            case 1:
                ((ViewHolderHabits) holder).setHabitList(position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolderTasks extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_tasks)
        TextView tasks;

        @BindView(R.id.rv_tasks)
        RecyclerView recyclerView;

        public TasksRecyclerAdapter adapter;

        public ViewHolderTasks(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
        }

        public void setTasksList(int position) {
            adapter = new TasksRecyclerAdapter(ctx, taskList);
            ItemTouchHelper.Callback callback = new LongItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(adapter);
        }

        public void updateAll() {
            adapter.updateList(taskList);
        }
    }

    public class ViewHolderHabits extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_habits)
        TextView tasks;

        @BindView(R.id.rv_habits)
        RecyclerView recyclerView;

        public HabitRecyclerAdapter adapter;

        public ViewHolderHabits(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
        }

        public void setHabitList(int position) {
            adapter = new HabitRecyclerAdapter(ctx, habitList);
            recyclerView.setAdapter(adapter);
        }

        public void updateAll() {
            adapter.updateList(habitList);
        }
    }
}
