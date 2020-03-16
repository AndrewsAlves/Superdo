package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.task.AllTasksFragment;
import com.andysapps.superdo.todo.fragments.task.TasksFragment;

/**
 * Created by Andrews on 10,October,2019
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    ProfileFragment profileFragment;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new TasksFragment();
            case 1:
                profileFragment = new ProfileFragment();
                return profileFragment;
            case 2:

        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }
}
