package com.andysapps.superdo.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.MainViewPagerAdapter;
import com.andysapps.superdo.todo.enums.MainTabs;
import com.andysapps.superdo.todo.manager.TimeManager;
import com.kuassivi.component.RipplePulseRelativeLayout;

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

    boolean isNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
    }
}
