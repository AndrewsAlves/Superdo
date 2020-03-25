package com.andysapps.superdo.todo.fragments.task;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.viewpageradapter.TasksDayPagerAdapter;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.UpdateTaskListEvent;
import com.andysapps.superdo.todo.events.firestore.FetchTasksEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.events.ui.OpenEditTaskEvent;
import com.andysapps.superdo.todo.events.ui.OpenFragmentEvent;
import com.andysapps.superdo.todo.fragments.bucket.BucketFragment;
import com.andysapps.superdo.todo.fragments.bucket.BucketTasksFragment;
import com.andysapps.superdo.todo.model.Bucket;
import com.github.florent37.viewanimator.ViewAnimator;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

/**
 * A simple {@link Fragment} subclass.
 */

public class AllTasksFragment extends Fragment {

    @BindView(R.id.tv_bucket_title)
    TextView tvTitle;

    @BindView(R.id.btn_today)
    TextView tvToday;

    @BindView(R.id.btn_tomorrow)
    TextView tvTomorrow;

    @BindView(R.id.btn_someday)
    TextView tvSomeday;

    @BindView(R.id.btn_save__bucket)
    ImageButton btnSave;

    @BindView(R.id.btn_close__bucket)
    ImageButton btnClose;

    @BindView(R.id.ib_bucketList)
    ImageButton btnBucketList;

    @BindView(R.id.viewpager_tasks)
    ViewPager viewPagerTasks;

    @BindView(R.id.tasks_parent)
    LinearLayout parentView;

    @BindView(R.id.ll_today_tomorrow_later)
    LinearLayout parentTodayTomorrow;


    TasksDayPagerAdapter viewPagerAdapter;

    BucketTasksFragment bucketTasksFragment;

    int[] gradientColor = new int[2];

    Typeface fontBold;
    Typeface fontRegular;

    TaskListing listing;

    TaskListing lasttaskListing;

    Bucket bucket;

    boolean isEditing = false;

    public AllTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_tasks, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_TASK));
        initUi();

        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void initUi() {
        fontBold = ResourcesCompat.getFont(getContext(), R.font.montserrat_bold);
        fontRegular = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);

        viewPagerAdapter = new TasksDayPagerAdapter(getChildFragmentManager());
        viewPagerTasks.setAdapter(viewPagerAdapter);

        viewPagerTasks.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int i) {

                switch (i) {
                    case 0:
                        listing = TaskListing.TODAY;
                        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.TODAY));
                        break;
                    case 1:
                        listing = TaskListing.TOMORROW;
                        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.TOMORROW));
                        break;
                    case 2:
                        listing = TaskListing.UPCOMING;
                        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.TODAY));
                        break;
                }

                updateUi();
            }
            @Override
            public void onPageScrollStateChanged(int i) { }
        });

        PushDownAnim.setPushDownAnimTo(btnBucketList)
                .setScale(MODE_SCALE, 0.96f  )
                .setOnClickListener(view -> EventBus.getDefault().post(new OpenFragmentEvent(new BucketFragment(), true, BucketFragment.TAG)));

        clickToday();

        listing= TaskListing.TODAY;
        lasttaskListing = TaskListing.TOMORROW;
        updateUi();
        lasttaskListing = TaskListing.UPCOMING;
        updateUi();



    }

    public void updateUi() {

        gradientColor[0] = Color.parseColor( "#F64F59");
        gradientColor[1] = Color.parseColor( "#FF8B57");

        btnSave.setVisibility(View.GONE);
        btnClose.setVisibility(View.GONE);
        btnBucketList.setVisibility(View.VISIBLE);

        if (isEditing) {
            btnSave.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.VISIBLE);
            btnBucketList.setVisibility(View.GONE);
        }

        animateTodayViews();
    }

    @OnClick(R.id.btn_today)
    public void clickToday() {
        listing = TaskListing.TODAY;
        viewPagerTasks.setCurrentItem(0);
        updateUi();
    }

    @OnClick(R.id.btn_tomorrow)
    public void clickTomorrow() {
        listing = TaskListing.TOMORROW;
        viewPagerTasks.setCurrentItem(1);
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickUpcoming() {
        listing = TaskListing.UPCOMING;
        viewPagerTasks.setCurrentItem(2);
        EventBus.getDefault().post(new UpdateTaskListEvent(TaskListing.UPCOMING));
        updateUi();
    }

    // bucket click listener is arranger to pushAnimation

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchTasksEvent event) {
        updateUi();
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetBucketTaskListEvent event) {

        if (event.getBucket() == null) {
            bucket = null;
            tvTitle.setText("All Tasks");
            parentTodayTomorrow.setVisibility(View.VISIBLE);

            if (getChildFragmentManager().findFragmentById(R.id.fl_bucket_fragment_container) != null) {
                getChildFragmentManager().beginTransaction().
                        remove(getChildFragmentManager().findFragmentById(R.id.fl_bucket_fragment_container))
                        .commitAllowingStateLoss();
            }

            getFragmentManager().popBackStack();

            return;
        }

        parentTodayTomorrow.setVisibility(View.GONE);
        tvTitle.setText(event.getBucket().getName());
        bucket = event.getBucket();
        bucketTasksFragment = BucketTasksFragment.getInstance(event.getBucket());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fl_bucket_fragment_container, bucketTasksFragment, BucketTasksFragment.TAG);
        ft.commitAllowingStateLoss(); // save the change

        getFragmentManager().popBackStack();
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskUpdatedEvent event) {

        switch (event.getTask().getListedIn()) {
            case TODAY:
                break;
            case TOMORROW:
                clickTomorrow();
                break;
            case UPCOMING:
            case THIS_WEEK:
            case THIS_MONTH:
                clickUpcoming();
                break;

        }
    }

    /////////////////////
    /////// ANIMATION
    ////////////////////

    public void animateTodayViews() {

        float red =  1.0f;
        float grey =  0.3f;

        if (lasttaskListing != null && listing != lasttaskListing) {
            switch (lasttaskListing) {
                case TODAY:
                    zoomAnimation(1.2f, 1.0f, tvToday, grey);
                    break;
                case TOMORROW:
                    zoomAnimation(1.2f, 1.0f, tvTomorrow, grey);
                    break;
                case UPCOMING:
                    zoomAnimation(1.2f, 1.0f, tvSomeday, grey);
                    break;
            }
        }

        if (listing != lasttaskListing) {
            switch (listing) {
                case TODAY:
                    zoomAnimation(1.0f, 1.2f, tvToday, red);
                    break;
                case TOMORROW:
                    zoomAnimation(1.0f, 1.2f, tvTomorrow, red);
                    break;
                case UPCOMING:
                    zoomAnimation(1.0f, 1.2f, tvSomeday, red);
                    break;
            }
        }

        lasttaskListing = listing;
    }

    public void zoomAnimation(float start, float end, View view, float alpha) {
        ViewAnimator
                .animate(view)
                //.scale(start, end)
                .alpha(alpha)
                .decelerate()
                .duration(200)
                .start();
    }
}
