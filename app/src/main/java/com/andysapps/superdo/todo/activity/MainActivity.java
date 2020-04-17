package com.andysapps.superdo.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.viewpageradapter.MainViewPagerAdapter;
import com.andysapps.superdo.todo.enums.MainTabs;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.events.OpenBottomFragmentEvent;
import com.andysapps.superdo.todo.events.ShowSnakeBarEvent;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.UpdateTaskListEvent;
import com.andysapps.superdo.todo.events.firestore.AddNewBucketEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.events.ui.RemoveFragmentEvents;
import com.andysapps.superdo.todo.events.update.UpdateProfileEvent;
import com.andysapps.superdo.todo.events.update.UpdateUiEvent;
import com.andysapps.superdo.todo.fragments.bucket.BucketFragment;
import com.andysapps.superdo.todo.fragments.task.AddTaskFragment;
import com.andysapps.superdo.todo.fragments.bucket.CreateNewBucketFragment;
import com.andysapps.superdo.todo.fragments.task.CPMDTasksFragment;
import com.andysapps.superdo.todo.fragments.task.EditTaskFragment;
import com.andysapps.superdo.todo.manager.AnimationManager;
import com.andysapps.superdo.todo.manager.TimeManager;
import com.andysapps.superdo.todo.model.Bucket;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.kuassivi.component.RipplePulseRelativeLayout;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    @BindView(R.id.ib_today)
    ImageView imgDayNight;

    /*@BindView(R.id.ib_tasks)
    ImageView imgTasks;*/

    @BindView(R.id.ib_profile)
    ImageView imgProfile;

    @BindView(R.id.vp_main)
    ViewPager mainViewPager;

    @BindView(R.id.tv_today_msg)
    TextView tvMsg;

    @BindView(R.id.btn_add_task)
    RelativeLayout moonButton;

    @BindView(R.id.iv_moonbutton)
    ImageView moonIcon;

    @BindView(R.id.cv_maintab)
    CardView cvMainTab;

    @BindView(R.id.rl_parent_moonbutton)
    RelativeLayout parentMoonButton;

    @BindView(R.id.parent_coodinator_with_moonbtn)
    CoordinatorLayout fragmentContainerMoonBtn;

    @BindView(R.id.pulseLayout)
    RipplePulseRelativeLayout rippleBackground;

    MainViewPagerAdapter viewPagerAdapter;
    MainTabs mainTabs;

    boolean pressedBack = false;

    public static MoonButtonType moonButtonType = MoonButtonType.ADD_TASK;

    boolean isNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyUser();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initUi();
        clickToday();
    }

    public void verifyUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initUi() {
        PushDownAnim.setPushDownAnimTo(moonButton)
                .setScale(MODE_SCALE, 0.97f  )
                .setOnClickListener(view -> clickAddTask());

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
                    /*case 1:
                        mainTabs = MainTabs.BUCKET_TASKS;
                        rippleBackground.stopPulse();
                        break;*/
                    case 1:
                        mainTabs = MainTabs.PROFILE;
                        rippleBackground.stopPulse();
                        break;
                }

                updateTabUi(mainTabs);
            }
            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(CreateNewBucketFragment.TAG) != null) {
            super.onBackPressed();
            return;
        }

        if (fragmentManager.findFragmentByTag(BucketFragment.TAG) != null) {
            BucketFragment fragment = (BucketFragment) fragmentManager.findFragmentByTag(BucketFragment.TAG);
            if (!fragment.animationEnded) {
                fragment.exitCircularReveal();
                return;
            }
        }

        if (fragmentManager.findFragmentByTag(CPMDTasksFragment.TAG)  != null) {
            CPMDTasksFragment fragment = (CPMDTasksFragment)fragmentManager.findFragmentByTag(CPMDTasksFragment.TAG);
            if (fragment.getSelectingTasks()) {
                fragment.clearSelection();
                return;
            }
        }

        /*if (!pressedBack) {
            pressedBack = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> pressedBack = false, 3000);
            return;
        }*/

        super.onBackPressed();
    }

    @OnClick(R.id.tab_1)
    public void clickToday() {
        moonButton.setClickable(true);
        mainTabs = MainTabs.TODAY_TASKS;
        mainViewPager.setCurrentItem(0);
        AnimationManager.getInstance().showMoonButton(moonButton);
        updateTabUi(mainTabs);
        updateAllTasks();
    }

    public void updateAllTasks() {
        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.TODAY));
        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.TOMORROW));
        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.UPCOMING));
    }

   /* @OnClick(R.id.tab_2)
    public void clickTasks() {

        moonButton.setClickable(true);

        mainTabs = MainTabs.BUCKET_TASKS;
        mainViewPager.setCurrentItem(1);
        AnimationManager.getInstance().showMoonButton(moonButton);
        updateTabUi(mainTabs);
    }*/

    @OnClick(R.id.tab_3)
    public void clickProfile() {
        mainTabs = MainTabs.PROFILE;
        mainViewPager.setCurrentItem(1);
        viewPagerAdapter.getProfileFragment().updateUi();
        AnimationManager.getInstance().hideMoonButton(moonButton);
        moonButton.setClickable(false);
        updateTabUi(mainTabs);
    }

    public void clickAddTask() {
        switch (moonButtonType) {
            case ADD_TASK:
                AddTaskFragment bottomSheetFragment = new AddTaskFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
            case ADD_BUCKET:
                onMessageEvent(new OpenFragmentEvent(CreateNewBucketFragment.Companion.instance(new Bucket(), false),
                        true,
                        CreateNewBucketFragment.TAG, true));
                break;
            case SAVE_BUCKET:
                EventBus.getDefault().post(new AddNewBucketEvent());
                break;
        }

    }

    public void updateTabUi(MainTabs tabs) {

        imgDayNight.setImageResource(R.drawable.ic_today_tasks_off);
        //imgTasks.setImageResource(R.drawable.ic_bucket_tasks_off);
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
           // case BUCKET_TASKS:
            //    imgTasks.setImageResource(R.drawable.ic_bucket_tasks_on);
           //     break;
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

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.getTag() != null) {
                switch (fragment.getTag()) {
                    case CreateNewBucketFragment.TAG:
                    case BucketFragment.TAG:
                    case EditTaskFragment.TAG:
                    case CPMDTasksFragment.TAG:
                        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                        break;
                }
            }
        }


       /* if (fragmentManager.findFragmentById(R.id.fl_fragment_container) != null) {

            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentById(R.id.fl_fragment_container))
                    .commitAllowingStateLoss();
        }

        if (fragmentManager.findFragmentById(R.id.fl_fragment_container_behind_add) != null) {

            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out,R.anim.fragment_fade_in, R.anim.fragment_fade_out);
            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentById(R.id.fl_fragment_container_behind_add))
                    .commitAllowingStateLoss();
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMoonButtonType event) {
        moonButtonType = event.moonButtonType;

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.getTag() != null) {
                switch (fragment.getTag()) {
                    case CreateNewBucketFragment.TAG:
                        moonButtonType = MoonButtonType.SAVE_BUCKET;
                        break;
                    case BucketFragment.TAG:
                        moonButtonType = MoonButtonType.ADD_BUCKET;
                        break;
                        default:
                            moonButtonType = MoonButtonType.ADD_TASK;
                }
            }
        }

        switch (moonButtonType) {
            case ADD_TASK:
                AnimationManager.getInstance().animateIconTransition(moonIcon, R.drawable.ic_mb_add_task);
                //moonIcon.setImageResource(R.drawable.ic_mb_add_task);
                break;
            case ADD_BUCKET:
                AnimationManager.getInstance().animateIconTransition(moonIcon, R.drawable.ic_add_bucket);
                //moonIcon.setImageResource(R.drawable.ic_add_bucket);
                break;
            case SAVE_BUCKET:
                AnimationManager.getInstance().animateIconTransition(moonIcon, R.drawable.ic_tick_create_bucket);
                //moonIcon.setImageResource(R.drawable.ic_tick_create_bucket);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateUiEvent event) {
        Log.e(TAG, "onMessageEvent: UpdateUi");
       switch (mainTabs) {
           case TODAY_TASKS:
               updateAllTasks();
               break;
           case PROFILE:
               EventBus.getDefault().post(new UpdateProfileEvent());
               EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.CPMD));
               break;
       }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenEditTaskEvent event) {
        EditTaskFragment fragment = EditTaskFragment.Companion.instance(event.getTask());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out,R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        ft.add(R.id.fl_fragment_container, fragment, EditTaskFragment.TAG);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commitAllowingStateLoss(); // save the changes
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenFragmentEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out,R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        if (event.behindMoonButton) {
            ft.add(R.id.fl_fragment_container_behind_add, event.fragment, event.tag);
        } else {
            ft.add(R.id.fl_fragment_container, event.fragment, event.tag);
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss(); // save the change
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenBottomFragmentEvent event) {
       event.getFragment().show(getSupportFragmentManager(), event.getFragment().getClass().getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShowSnakeBarEvent event) {

        Snackbar snackbar = Snackbar.make(fragmentContainerMoonBtn, event.getSnackbarTitle(), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.WHITE);
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.lightRed));

        snackbar.setActionTextColor(getResources().getColor(R.color.lightRed));
        snackbar.setAction("Undo", event.getOnClickListener());
        /*snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event.getUndoType()) {
                    case TASK_COMPLETED:
                        if (event.getTasksRecyclerAdapter() != null) {
                            event.getTasksRecyclerAdapter().undoTaskCompleted(event.getTask(), event.getPosition());
                        } else if (event.getUpcomingRecyclerAdapter() != null) {
                            event.getUpcomingRecyclerAdapter().undoTaskCompleted(event.getTask(), event.getPosition());
                        }
                        break;
                    case MOVED_TO_BIN:
                        if (event.getTasksRecyclerAdapter() != null) {
                            event.getTasksRecyclerAdapter().undoMovedToBin(event.getTask(), event.getPosition());
                        } else if (event.getUpcomingRecyclerAdapter() != null) {
                            event.getUpcomingRecyclerAdapter().undoMovedToBin(event.getTask(), event.getPosition());
                        }
                        break;
                }
            }
        });*/

        snackbar.show();
    }
}
