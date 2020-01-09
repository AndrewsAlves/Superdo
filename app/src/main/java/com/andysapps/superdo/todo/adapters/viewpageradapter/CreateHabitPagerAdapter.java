package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.BucketTasksFragment;
import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.TodayFragment;
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep1;
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep2;
import com.andysapps.superdo.todo.model.Habit;

/**
 * Created by Andrews on 10,October,2019
 */

public class CreateHabitPagerAdapter extends FragmentPagerAdapter {

    Habit habit;

    public CreateHabitPagerAdapter(FragmentManager fm, Habit habit) {
        super(fm);
        this.habit = habit;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return CreateHabitStep1.Companion.instance(habit);
            case 1:
                return CreateHabitStep2.Companion.instance(habit);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
