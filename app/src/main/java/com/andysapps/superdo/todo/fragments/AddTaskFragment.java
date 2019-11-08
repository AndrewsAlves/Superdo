package com.andysapps.superdo.todo.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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
import com.andysapps.superdo.todo.model.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends BottomSheetDialogFragment implements  DatePickerDialog.OnDateSetListener {


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

    @BindView(R.id.iv_due_date)
    ImageView ivDueDate;
    @BindView(R.id.ll_bg_duedate)
    LinearLayout btnBgDuedate;
    @BindView(R.id.ib_clearDeadLine)
    ImageButton btnClearDeadLine;


    @BindView(R.id.tv_due_date)
    TextView tvDueDate;
    @BindView(R.id.btn_buckets)
    LinearLayout btnBuckets;

    @BindView(R.id.iv_tag)
    ImageView ivTag;

    @BindView(R.id.bucket_name)
    TextView bucketName;

    Task task;

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
        task.setListedIn(TaskListing.TODAY);

        updateUi();
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

        if (task.getDueDateString() == null) {

            ivDueDate.setImageResource(R.drawable.ic_duedate_off);
            tvDueDate.setText("No deadline");
            tvDueDate.setTextColor(getResources().getColor(R.color.grey2));

            btnBgDuedate.setBackgroundResource(R.color.transparent);
            btnClearDeadLine.setVisibility(View.GONE);

        } else {

            ivDueDate.setImageResource(R.drawable.ic_duedate_on);
            tvDueDate.setText(task.getDueDateString());
            tvDueDate.setTextColor(getResources().getColor(R.color.white));

            btnBgDuedate.setBackgroundResource(R.drawable.bg_light_red);
            btnClearDeadLine.setVisibility(View.VISIBLE);
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

    }

    public Date getTomorrow() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();

        return dt;
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

    @OnClick(R.id.btn_today)
    public void clickToday() {
        task.setListedIn(TaskListing.TODAY);
        task.setDoDate(Calendar.getInstance().getTime());
        updateUi();
    }

    @OnClick(R.id.btn_tomorrow)
    public void clickTomorrow() {
        task.setListedIn(TaskListing.TOMORROW);
        task.setDoDate(getTomorrow());
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickSomeday() {
        task.setListedIn(TaskListing.SOMEDAY);
        updateUi();
    }

    @OnClick(R.id.ll_bg_duedate)
    public void clickDuedate() {
        showDatePicker();
    }

    @OnClick(R.id.ib_clearDeadLine)
    public void clickClearDeadLine() {
        task.setDueDate(null);
        updateUi();
    }

    @OnClick(R.id.btn_buckets)
    public void clickBuckets() {

    }

    @OnClick(R.id.ib_add_task)
    public void clickAddTask() {
        if (validate()) {
            task.setUserId(FirestoreManager.getInstance().userId);
            task.setName(etTaskName.getText().toString());
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
        int[] date = {dayOfMonth, monthOfYear + 1, year};
        task.setDueDate(date);
        updateUi();
        Log.e(TAG, "onDateSet: " + monthOfYear + " : " + dayOfMonth + " : " + year);
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
