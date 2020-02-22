package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.bucket.BucketTasksFragment;
import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.task.TasksDayFragment;

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
                return new TasksDayFragment();
            case 1:
                return new BucketTasksFragment();
            case 2:
                profileFragment = new ProfileFragment();
                return profileFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }
}
