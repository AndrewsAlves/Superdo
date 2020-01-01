package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.BucketTasksFragment;
import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.TodayFragment;
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep1;
import com.andysapps.superdo.todo.fragments.habit.CreateHabitStep2;

/**
 * Created by Andrews on 10,October,2019
 */

public class CreateHabitPagerAdapter extends FragmentPagerAdapter {

    public CreateHabitPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new CreateHabitStep1();
            case 1:
                return new CreateHabitStep2();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
