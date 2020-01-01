package com.andysapps.superdo.todo.adapters.viewpageradapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.andysapps.superdo.todo.fragments.BucketFragment;
import com.andysapps.superdo.todo.fragments.BucketTasksFragment;
import com.andysapps.superdo.todo.fragments.ProfileFragment;
import com.andysapps.superdo.todo.fragments.TodayFragment;

/**
 * Created by Andrews on 10,October,2019
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new TodayFragment();
            case 1:
                return new BucketTasksFragment();
            case 2:
                return new ProfileFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
