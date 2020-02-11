package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.bucket.BucketTasksFragment;
import com.andysapps.superdo.todo.fragments.task.TasksDayFragment;
import com.andysapps.superdo.todo.fragments.task.TodayFragment;
import com.andysapps.superdo.todo.fragments.task.TomorrowFragment;
import com.andysapps.superdo.todo.fragments.task.UpcomingTasksFragment;

/**
 * Created by Andrews on 10,October,2019
 */

public class TasksDayPagerAdapter extends FragmentPagerAdapter {

    public TasksDayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new TodayFragment();
            case 1:
                return new TomorrowFragment();
            case 2:
                return new UpcomingTasksFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
