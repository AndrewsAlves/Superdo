package com.andysapps.superdo.todo.fragments.task;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.dialog.SelectBucketDialogFragment;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.action.SelectBucketEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.events.ui.DialogDismissEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnDismissListener {

    private static final String TAG = "Add Task Fragment";
    @BindView(R.id.et_add_task)
    EditText etTaskName;

    @BindView(R.id.ib_add_task)
    ImageButton ibAddTask;

    @BindView(R.id.btn_today)
    LinearLayout btnToday;

    //@BindView(R.id.iv_dot_today)
    //ImageView ivDotToday;

    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.btn_tomorrow)
    LinearLayout btnTomorrow;

  //  @BindView(R.id.iv_dot_tomorrow)
   // ImageView ivDotTomorrow;
    @BindView(R.id.tv_tomorrow)
    TextView tvTomorrow;

    //@BindView(R.id.iv_dot_someday)
   // ImageView ivDotSomeday;
    @BindView(R.id.btn_someday)
    LinearLayout btnSomeday;

    @BindView(R.id.tv_someday)
    TextView tvSomeday;

    @BindView(R.id.lv_remind)
    LottieAnimationView lvRemind;

    /*@BindView(R.id.iv_do_date)
    ImageView ivDoDate;
    @BindView(R.id.ll_bg_do_date)
    LinearLayout bgDoDate;

    @BindView(R.id.add_task_tv_time)
    TextView tvTime;

    @BindView(R.id.add_task_iv_time)
    ImageView ivTime;


    @BindView(R.id.tv_do_date)
    TextView tvDoDate;*/

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
    public void setupDialog(Dialog dialog, int style) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        bottomSheetDialog.setContentView(R.layout.fragment_add_task);

        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

        initUi();
        Utils.showSoftKeyboard(getContext(), etTaskName);

        if (Utils.getDefaultTime() > 18) {
            SuperDate date = Utils.getSuperdateTomorrow();
            date.setTime(9,0);
            task.setDoDate(date);
            updateUi();
        } else {
            SuperDate date = Utils.getSuperdateToday();
            date.setTime(Utils.getDefaultTime(), 0);
            task.setDoDate(date);
            updateUi();
        }

        // Inflate the layout for this fragment
        return v;
    }

    public void initUi() {

        task = new Task();

        task.setBucketId(TasksFragment.Companion.getBucket().getDocumentId());

        etTaskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clickAddTask();
                    if (validate()){
                        //dismiss();
                    }
                }
                return true;
            }
        });

        lvRemind.setAnimation("check_box2.json");
        lvRemind.setSpeed(1.5f);

        lvRemind.setOnClickListener(v -> {
            task.setToRemind(!task.isToRemind());

            updateUi();
            //TaskOrganiser.getInstance().organiseAllTasks();
            //FirestoreManager.getInstance().updateTask(task);

            if (task.isToRemind()) {
                lvRemind.setMinAndMaxProgress(0.20f, 0.50f); // on
            } else {
                lvRemind.setMinAndMaxProgress(0.65f, 1.0f); // off
            }

            lvRemind.playAnimation();
        });

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void updateUi() {

      //  ivDotToday.setImageResource(R.drawable.bg_oval_grey);
       // ivDotTomorrow.setImageResource(R.drawable.bg_oval_grey);
      //  ivDotSomeday.setImageResource(R.drawable.bg_oval_grey);

        tvToday.setText("Today");
        tvTomorrow.setText("Tomorrow");
        tvSomeday.setText("Set Date");

        btnToday.getBackground().setColorFilter(getResources().getColor(R.color.grey1), PorterDuff.Mode.SRC_ATOP);
        btnTomorrow.getBackground().setColorFilter(getResources().getColor(R.color.grey1), PorterDuff.Mode.SRC_ATOP);
        btnSomeday.getBackground().setColorFilter(getResources().getColor(R.color.grey1), PorterDuff.Mode.SRC_ATOP);

        if (task.getDoDate() != null) {

            if (Utils.isSuperDateToday(task.getDoDate())) {
                tvToday.setText("Today by " + task.getDoDate().getTimeString());
                btnToday.getBackground().setColorFilter(getResources().getColor(R.color.lightRed), PorterDuff.Mode.SRC_ATOP);
            } else if (Utils.isSuperDateTomorrow(task.getDoDate())) {
                tvTomorrow.setText("Tomorrow by " + task.getDoDate().getTimeString());
                btnTomorrow.getBackground().setColorFilter(getResources().getColor(R.color.lightRed), PorterDuff.Mode.SRC_ATOP);
            } else {
                tvSomeday.setText("Do " + task.getDoDate().getSuperDateString() + " "+ task.getDoDate().getTimeString());
                btnSomeday.getBackground().setColorFilter(getResources().getColor(R.color.lightRed), PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            tvSomeday.setText("Do Someday");
            btnSomeday.getBackground().setColorFilter(getResources().getColor(R.color.lightRed), PorterDuff.Mode.SRC_ATOP);
        }

       if (task.getBucket() != null) {
           bucketName.setText(task.getBucket().getName());
           ivTag.setImageResource(Constants.bucketIcons[task.getBucket().getBucketIcon()]);
       }
    }

    public void showDatePicker() {
        Calendar now = Calendar.getInstance();

        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        if (task.getDoDate() != null) {
            day = task.getDoDate().date;
            month = task.getDoDate().month;
            year = task.getDoDate().year;
            Log.e(TAG, "clickTomorrow: tomorrow date in show dialog: " + day);
        }

        DatePickerDialog dpd  = DatePickerDialog.newInstance(
                this,
                year,
                month - 1,
                day
        );

        dpd.setAccentColor(getResources().getColor(R.color.lightRed));
        dpd.setMinDate(Utils.getStartDate());
        dpd.setMaxDate(Utils.getEndDate());
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void showTimePicker(int hours , int min) {
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                this,
                hours,
                min,
                false);
        dpd.setAccentColor(getResources().getColor(R.color.lightRed));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btn_today)
    public void clickToday() {

        if (!Utils.isSuperDateToday(task.getDoDate())) {
            SuperDate date = Utils.getSuperdateToday();
            date.setTime(Utils.getDefaultTime(),0);
            task.setDoDate(date);
        }

        showTimePicker(task.getDoDate().hours, task.getDoDate().minutes);
        updateUi();

        /*if (!Utils.isSuperDateToday(task.getDoDate())) {
            task.setDoDate(new SuperDate());
        }

        SuperDate date = Utils.getSuperdateToday();
        if (!isTimeSet) {
            date.setTime(Utils.getDefaultTime(), 0);
        } else {
            date.setTime(task.getDoDate().hours, task.getDoDate().minutes);
        }
        task.setDoDate(date);
        showTimePicker(task.getDoDate().hours, task.getDoDate().minutes);
        updateUi();*/
    }

    @OnClick(R.id.btn_tomorrow)
    public void clickTomorrow() {

        if (!Utils.isSuperDateTomorrow(task.getDoDate())) {
            SuperDate date = Utils.getSuperdateTomorrow();
            date.setTime(9,0);
            task.setDoDate(date);
        }

        showTimePicker(task.getDoDate().hours, task.getDoDate().minutes);
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickSomeday() {
        task.setListedIn(TaskListing.UPCOMING);
        task.setDoDate(null);
        isTimeSet = false;
        showDatePicker();
        updateUi();
    }

    @OnClick(R.id.btn_buckets)
    public void clickBuckets() {
        etTaskName.clearFocus();
        new SelectBucketDialogFragment().show(getFragmentManager(), SelectBucketDialogFragment.class.getName());
    }

    @OnClick(R.id.ib_add_task)
    public void clickAddTask() {
        if (validate()) {

            Task uploadingTask = new Task();
            uploadingTask.setUserId(FirestoreManager.getInstance().user.getUserId());
            uploadingTask.setTitle(etTaskName.getText().toString().trim());
            uploadingTask.setBucketId(task.getBucketId());
            if (task.getDoDate() == null) {
                uploadingTask.setDoDate(null);
            } else {
                uploadingTask.setDoDate(new SuperDate(Utils.getCalenderFromSuperDate(task.getDoDate())));
            }

            uploadingTask.setListedIn(Utils.getTaskListed(task.getDoDate()));
            uploadingTask.setTaskIndex(TaskOrganiser.getInstance().getTasks(uploadingTask.getListedIn()).size());
            uploadingTask.setToRemind(task.isToRemind());
            uploadingTask.setCreated(Calendar.getInstance().getTime());

            String id = FirestoreManager.getInstance().uploadTask(uploadingTask);
            if (task.isToRemind()) {
                SuperdoAlarmManager.getInstance().setRemind(getContext(), uploadingTask);
            }
        }
    }

    public boolean validate() {
        if (etTaskName.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public void showKeyboradAsync() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showSoftKeyboard(getContext(), etTaskName);
            }
        }, 50);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SuperDate date = new SuperDate(dayOfMonth, monthOfYear + 1, year);

        if (task.getDoDate() != null) {
            task.getDoDate().setDoDate(dayOfMonth, monthOfYear + 1, year);
        } else {
            task.setDoDate(date);
        }

        updateUi();

        if (!task.getDoDate().hasTime) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int min = Calendar.getInstance().get(Calendar.MINUTE);
            showTimePicker(hour, min);
            return;
        }

        showKeyboradAsync();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        SuperDate time = new SuperDate(hourOfDay, minute);
        if (task.getDoDate() != null) {
            task.getDoDate().setTime(hourOfDay, minute);
        } else {
            task.setDoDate(time);
        }
        isTimeSet = true;
        updateUi();
        showKeyboradAsync();
    }

    /////////////
    ////// EVENTS
    /////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskUpdatedEvent event) {
        if (event.getDocumentChange() != TaskUpdateType.Added) {
            return;
        }

        task.setTitle("");
        etTaskName.getText().clear();
        updateUi();
        Toast.makeText(getContext(), "Task added!",Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SelectBucketEvent event) {
        task.setBucketId(event.getBucket().getDocumentId());
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DialogDismissEvent event) {
        showKeyboradAsync();
    }
}
