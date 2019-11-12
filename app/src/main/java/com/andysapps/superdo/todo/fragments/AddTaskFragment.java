package com.andysapps.superdo.todo.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.events.ui.TaskAddedEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends BottomSheetDialogFragment implements  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "Add Task Fragment";
    @BindView(R.id.et_add_task)
    EditText etTaskName;

    @BindView(R.id.ib_add_task)
    ImageButton ibAddTask;

    @BindView(R.id.btn_today)
    LinearLayout btnToday;

    @BindView(R.id.iv_dot_today)
    ImageView ivDotToday;

    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.btn_tomorrow)
    LinearLayout btnTomorrow;

    @BindView(R.id.iv_dot_tomorrow)
    ImageView ivDotTomorrow;
    @BindView(R.id.tv_tomorrow)
    TextView tvTomorrow;

    @BindView(R.id.iv_dot_someday)
    ImageView ivDotSomeday;
    @BindView(R.id.tv_someday)
    TextView tvSomeday;

    @BindView(R.id.iv_do_date)
    ImageView ivDoDate;
    @BindView(R.id.ll_bg_do_date)
    LinearLayout bgDoDate;

    @BindView(R.id.add_task_tv_time)
    TextView tvTime;

    @BindView(R.id.add_task_iv_time)
    ImageView ivTime;


    @BindView(R.id.tv_do_date)
    TextView tvDoDate;

    @BindView(R.id.btn_buckets)
    LinearLayout btnBuckets;

    @BindView(R.id.iv_tag)
    ImageView ivTag;

    @BindView(R.id.bucket_name)
    TextView bucketName;

    Task task;

    boolean isTimeSet = false;

    public AddTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialogStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);

        Utils.showSoftKeyboard(getContext(), etTaskName);

        task = new Task();
        clickToday();
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void updateUi() {

        ivDotToday.setImageResource(R.drawable.bg_oval_grey);
        ivDotTomorrow.setImageResource(R.drawable.bg_oval_grey);
        ivDotSomeday.setImageResource(R.drawable.bg_oval_grey);

        tvToday.setTextColor(getResources().getColor(R.color.grey2));
        tvTomorrow.setTextColor(getResources().getColor(R.color.grey2));
        tvSomeday.setTextColor(getResources().getColor(R.color.grey2));

        switch (task.getListedIn()) {

            case TODAY:

            default:
                ivDotToday.setImageResource(R.drawable.bg_oval_light_red);
                tvToday.setTextColor(getResources().getColor(R.color.lightRed));
                break;

            case TOMORROW:
                ivDotTomorrow.setImageResource(R.drawable.bg_oval_light_red);
                tvTomorrow.setTextColor(getResources().getColor(R.color.lightRed));
                break;

            case SOMEDAY:
                ivDotSomeday.setImageResource(R.drawable.bg_oval_light_red);
                tvSomeday.setTextColor(getResources().getColor(R.color.lightRed));
                break;

        }

        if (task.getDoDate() == null) {
            ivDoDate.setImageResource(R.drawable.ic_duedate_off);
            tvDoDate.setText("No Date");
            tvDoDate.setTextColor(getResources().getColor(R.color.grey2));
            bgDoDate.setBackground(null);
        } else {
            ivDoDate.setImageResource(R.drawable.ic_duedate_on_red);
            tvDoDate.setText(task.getDoDateString());
            tvDoDate.setTextColor(getResources().getColor(R.color.white));
            bgDoDate.setBackgroundResource(R.drawable.bg_light_red);
        }

        if (task.getBucketId() == null) {
            ivTag.getDrawable().setColorFilter(getResources().getColor(R.color.lightRed), PorterDuff.Mode.SRC_IN);
            bucketName.setText("All Tasks");
            bucketName.setTextColor(getResources().getColor(R.color.lightRed));
        } else {
            ivTag.getDrawable().setColorFilter(Color.parseColor(task.getBucketColor()), PorterDuff.Mode.SRC_IN);
            bucketName.setText(task.getBucketName());
            bucketName.setTextColor(Color.parseColor(task.getBucketColor()));
        }

       if (task.getDoDate() != null) {
           int hours = task.getDoDate().getHours();
           String meridien = " am";

           if (task.getDoDate().getHours() > 12) {
               hours = task.getDoDate().getHours() - 12;
               meridien = " pm";
           }

           // format to two decimal
           String hour =  new DecimalFormat("00").format(hours);
           String min =  new DecimalFormat("00").format(task.getDoDate().getMinutes());
           String time = hour + ":" + min + meridien;

           tvTime.setText(time);
           ivTime.setImageResource(Utils.getTimeIcon(task.getDoDate().getHours()));
       } else {
           tvTime.setText("No Time");
           ivTime.setImageResource(R.drawable.ic_time_off);
       }

    }

    public Calendar getTomorrow() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        return c;
    }

    public void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddTaskFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setAccentColor(getResources().getColor(R.color.lightRed));
        dpd.setMinDate(Utils.getStartDate());
        dpd.setMaxDate(Utils.getEndDate());
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void showTimePicker() {
        Date date = new Date();
        date.getTime();
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                AddTaskFragment.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                false
        );

        dpd.setAccentColor(getResources().getColor(R.color.lightRed));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btn_today)
    public void clickToday() {
        task.setListedIn(TaskListing.TODAY);
        SuperDate date = new SuperDate(Calendar.getInstance().getTime());
        date.setDate(Calendar.getInstance().get(Calendar.DATE));
        date.setMonth(Calendar.getInstance().get(Calendar.MONTH));
        date.setYear(Calendar.getInstance().get(Calendar.YEAR));
        if (!isTimeSet) {
            date.setTime(Utils.getDefaultTime(task.getListedIn()), 0);
        }
        task.setDoDate(date);
        updateUi();
    }

    @OnClick(R.id.btn_tomorrow)
    public void clickTomorrow() {
        task.setListedIn(TaskListing.TOMORROW);
        Calendar tomorrow = getTomorrow();
        SuperDate date = new SuperDate(tomorrow.getTime());
        date.setDate(tomorrow.get(Calendar.DATE));
        date.setMonth(tomorrow.get(Calendar.MONTH));
        date.setYear(tomorrow.get(Calendar.YEAR));
        if (!isTimeSet) {
            date.setTime(Utils.getDefaultTime(task.getListedIn()), 0);
        }
        task.setDoDate(date);
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickSomeday() {
        task.setListedIn(TaskListing.SOMEDAY);
        task.setDoDate(null);
        updateUi();
    }

    @OnClick(R.id.tv_do_date)
    public void clickDuedate() {
        showDatePicker();
    }

    @OnClick(R.id.add_task_ll_time_btn)
    public void clickTime() {
        if (task.getDoDate() == null) {
            showDatePicker();
            return;
        }
        showTimePicker();
    }

    @OnClick(R.id.btn_buckets)
    public void clickBuckets() {

    }

    @OnClick(R.id.ib_add_task)
    public void clickAddTask() {
        if (validate()) {
            task.setUserId(FirestoreManager.getInstance().userId);
            task.setName(etTaskName.getText().toString());
            task.setTaskIndex(TaskOrganiser.getInstance().getTaskIndex(task.getListedIn()));
            FirestoreManager.getInstance().uploadTask(task);
        }
    }

    public boolean validate() {
        if (etTaskName.getText().toString().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SuperDate date = new SuperDate(dayOfMonth, monthOfYear + 1, year);
        task.setDoDate(date);
        updateUi();


        Utils.showSoftKeyboard(getContext(), etTaskName);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        isTimeSet = true;
        task.getDoDate().setTime(hourOfDay, minute);
        updateUi();

        Utils.showSoftKeyboard(getContext(), etTaskName);


    }

    /////////////
    ////// EVENTS
    /////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskAddedEvent event) {
        TaskListing lastTaskListing = task.getListedIn();
        task = new Task();
        task.setListedIn(lastTaskListing);
        etTaskName.getText().clear();
        updateUi();
    }


}
