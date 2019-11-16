package com.andysapps.superdo.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.MainViewPagerAdapter;
import com.andysapps.superdo.todo.enums.MainTabs;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.firestore.AddNewBucketEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents;
import com.andysapps.superdo.todo.fragments.AddTaskFragment;
import com.andysapps.superdo.todo.fragments.CreateNewBucketFragment;
import com.andysapps.superdo.todo.fragments.EditTaskFragment;
import com.andysapps.superdo.todo.manager.TimeManager;
import com.kuassivi.component.RipplePulseRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ib_today)
    ImageView imgDayNight;

    @BindView(R.id.ib_tasks)
    ImageView imgTasks;

    @BindView(R.id.vp_main)
    ViewPager mainViewPager;

    @BindView(R.id.tv_tab1_name)
    TextView tvToday;

    @BindView(R.id.tv_today_msg)
    TextView tvMsg;

    @BindView(R.id.pulseLayout)
    RipplePulseRelativeLayout rippleBackground;

    MainViewPagerAdapter viewPagerAdapter;
    MainTabs mainTabs;

    public static MoonButtonType moonButtonType = MoonButtonType.ADD_TASK;

    boolean isNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(viewPagerAdapter);

        if (TimeManager.getHour() >= 18 || TimeManager.getHour() <= 6 ) {
            isNight = true;
        }

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int i) {

                switch (i) {
                    case 0:
                        mainTabs = MainTabs.TODAY;
                        rippleBackground.startPulse();
                        break;
                    case 1:
                        mainTabs = MainTabs.TASKS;
                        rippleBackground.stopPulse();
                        break;
                }

                updateTabUi(mainTabs);
            }
            @Override
            public void onPageScrollStateChanged(int i) { }
        });

        clickToday();

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.tab_1)
    public void clickToday() {
        mainTabs = MainTabs.TODAY;
        mainViewPager.setCurrentItem(0);
        updateTabUi(mainTabs);
    }

    @OnClick(R.id.tab_2)
    public void clickTasks() {
        mainTabs = MainTabs.TASKS;
        mainViewPager.setCurrentItem(1);
        updateTabUi(mainTabs);
    }

    @OnClick(R.id.btn_add_task)
    public void clickAddTask() {
        switch (moonButtonType) {
            case ADD_TASK:
                AddTaskFragment bottomSheetFragment = new AddTaskFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
            case ADD_BUCKET:
                onMessageEvent(new OpenFragmentEvent(new CreateNewBucketFragment(), true));
                break;
            case SAVE_BUCKET:
                EventBus.getDefault().post(new AddNewBucketEvent());
                break;
        }

    }

    public void updateTabUi(MainTabs tabs) {

        imgDayNight.setImageResource(R.drawable.ic_day_off);
        imgTasks.setImageResource(R.drawable.ic_tasks_off);
        tvToday.setTextColor(getResources().getColor(R.color.grey2));

        tvMsg.setText(" ");

        if (isNight) {
            imgDayNight.setImageResource(R.drawable.ic_night_off);
        }

        switch (tabs) {
            case TODAY:
                imgDayNight.setImageResource(R.drawable.ic_day_on);
                tvToday.setTextColor(getResources().getColor(R.color.black));
                if (isNight) {
                    imgDayNight.setImageResource(R.drawable.ic_night_on);
                }

                break;
            case TASKS:
                imgTasks.setImageResource(R.drawable.ic_tasks_on);
                break;
        }

        onMessageEvent(new RemoveFragmentEvents()); // occupied fragments
    }


    //////////////////////
    /////// EVENTS
    /////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RemoveFragmentEvents events) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.fl_fragment_container) != null) {

            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentById(R.id.fl_fragment_container))
                    .commitAllowingStateLoss();
        }

        if (fragmentManager.findFragmentById(R.id.fl_fragment_container_behind_add) != null) {

            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentById(R.id.fl_fragment_container_behind_add))
                    .commitAllowingStateLoss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMoonButtonType event) {
        this.moonButtonType = event.moonButtonType;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenEditTaskEvent event) {
        EditTaskFragment fragment = EditTaskFragment.Companion.instance(event.getTask());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_fragment_container, fragment);
        ft.commitAllowingStateLoss(); // save the changes
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenFragmentEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (event.behindMoonButton) {
            ft.replace(R.id.fl_fragment_container_behind_add, event.fragment);
        } else {
            ft.replace(R.id.fl_fragment_container, event.fragment);
        }
        ft.commitAllowingStateLoss(); // save the changes
    }




}
