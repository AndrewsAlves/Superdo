package com.andysapps.superdo.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.viewpageradapter.MainViewPagerAdapter;
import com.andysapps.superdo.todo.enums.MainTabs;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.OpenBottomFragmentEvent;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.firestore.AddNewBucketEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents;
import com.andysapps.superdo.todo.fragments.AddTaskFragment;
import com.andysapps.superdo.todo.fragments.CreateNewBucketFragment;
import com.andysapps.superdo.todo.fragments.EditTaskFragment;
import com.andysapps.superdo.todo.manager.TimeManager;
import com.andysapps.superdo.todo.model.Bucket;
import com.github.florent37.viewanimator.ViewAnimator;
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

    @BindView(R.id.ib_profile)
    ImageView imgProfile;

    @BindView(R.id.vp_main)
    ViewPager mainViewPager;

    @BindView(R.id.tv_today_msg)
    TextView tvMsg;

    @BindView(R.id.iv_moonbutton)
    ImageView moonButton;

    @BindView(R.id.main_ll_fab_add_task)
    LinearLayout fabTaskButton;

    @BindView(R.id.main_ll_fab_add_habit)
    LinearLayout fabHabitButton;

    @BindView(R.id.main_ll_fab_add_shopping_list)
    LinearLayout fabShoppingButton;

    boolean isShowingFab = false;

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
                        mainTabs = MainTabs.TODAY_TASKS;
                        rippleBackground.startPulse();
                        break;
                    case 1:
                        mainTabs = MainTabs.BUCKET_TASKS;
                        rippleBackground.stopPulse();
                        break;
                    case 2:
                        mainTabs = MainTabs.PROFILE;
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
        mainTabs = MainTabs.TODAY_TASKS;
        mainViewPager.setCurrentItem(0);
        updateTabUi(mainTabs);
    }

    @OnClick(R.id.tab_2)
    public void clickTasks() {
        mainTabs = MainTabs.BUCKET_TASKS;
        mainViewPager.setCurrentItem(1);
        updateTabUi(mainTabs);
    }

    @OnClick(R.id.tab_3)
    public void clickProfile() {
        mainTabs = MainTabs.PROFILE;
        mainViewPager.setCurrentItem(2);
        updateTabUi(mainTabs);
    }

    @OnClick(R.id.main_ll_fab_add_task)
    public void clickTask() {
        AddTaskFragment bottomSheetFragment = new AddTaskFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        closeFab();
    }

    @OnClick(R.id.main_ll_fab_add_habit)
    public void clickHabit() {
        Intent intent = new Intent(this, CreateHabitActivity.class);
        startActivity(intent);
        closeFab();
    }

    @OnClick(R.id.main_ll_fab_add_shopping_list)
    public void clickShopping() {
        closeFab();
    }

    @OnClick(R.id.btn_add_task)
    public void clickAddTask() {
        switch (moonButtonType) {
            case ADD_TASK:
                if (isShowingFab) {
                    closeFab();
                } else {
                    openFab();
                }
                break;
            case ADD_BUCKET:
                onMessageEvent(new OpenFragmentEvent(CreateNewBucketFragment.Companion.instance(new Bucket(), false), true));
                break;
            case SAVE_BUCKET:
                EventBus.getDefault().post(new AddNewBucketEvent());
                break;
        }

    }

    public void openFab() {
        fabTaskButton.setClickable(true);
        fabHabitButton.setClickable(true);
        fabShoppingButton.setClickable(true);
        animateFab(0.0f, 1.0f, fabTaskButton, 0,0,0, 300, 0);
        animateFab(0.0f, 1.0f, fabHabitButton, 75,300,0, 300, 0);
        animateFab(0.0f, 1.0f, fabShoppingButton, 150,300,0, 0, 0);
        isShowingFab = true;
    }

     public void closeFab() {
         fabTaskButton.setClickable(false);
         fabHabitButton.setClickable(false);
         fabShoppingButton.setClickable(false);
         animateFab(1.0f, 0.0f, fabTaskButton, 0,0, 0, 0, 300);
         animateFab(1.0f, 0.0f, fabHabitButton, 75,0,300, 0, 300);
         animateFab(1.0f, 0.0f, fabShoppingButton, 150, 0,300, 0, 0);
         isShowingFab = false;
     }

    public void updateTabUi(MainTabs tabs) {

        imgDayNight.setImageResource(R.drawable.ic_today_tasks_off);
        imgTasks.setImageResource(R.drawable.ic_bucket_tasks_off);
        imgProfile.setImageResource(R.drawable.ic_profile_off);

        tvMsg.setText(" ");

        if (isNight) {
            imgDayNight.setImageResource(R.drawable.ic_night_off);
        }

        switch (tabs) {
            case TODAY_TASKS:
                imgDayNight.setImageResource(R.drawable.ic_today_tasks_on);
                if (isNight) {
                    imgDayNight.setImageResource(R.drawable.ic_night_on);
                }

                break;
            case BUCKET_TASKS:
                imgTasks.setImageResource(R.drawable.ic_bucket_tasks_on);
                break;
            case PROFILE:
                imgProfile.setImageResource(R.drawable.ic_profile_on);
                break;
        }

        onMessageEvent(new RemoveFragmentEvents()); // occupied fragments
    }

    //////////////
    //// FAB ANIMATIONS
    //////////////

    public void animateFab(float start, float end, View fabButton, long delay,int x1, int x2, int y1, int y2) {

        ViewAnimator
                .animate(fabButton)
                .scale(start, end)
                .translationY(y1, y2)
                .translationX(x1, x2)
                .decelerate()
                .duration(250)
                .startDelay(delay)
                .start();
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

        switch (moonButtonType) {
            case ADD_TASK:
                moonButton.setImageResource(R.drawable.ic_mb_add_task);
                break;
            case ADD_BUCKET:
                moonButton.setImageResource(R.drawable.ic_add_bucket);
                break;
            case SAVE_BUCKET:
                moonButton.setImageResource(R.drawable.ic_tick_create_bucket);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenEditTaskEvent event) {
        EditTaskFragment fragment = EditTaskFragment.Companion.instance(event.getTask());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
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
        ft.addToBackStack(event.fragment.getClass().getName());
        ft.commitAllowingStateLoss(); // save the change
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenBottomFragmentEvent event) {
       event.getFragment().show(getSupportFragmentManager(), event.getFragment().getClass().getName());
    }

}
