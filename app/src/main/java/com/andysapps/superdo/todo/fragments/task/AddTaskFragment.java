package com.andysapps.superdo.todo.fragments.task;


import android.app.Dialog;
import android.content.DialogInterface;
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

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.dialog.SelectBucketDialogFragment;
import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.enums.TaskUpdateType;
import com.andysapps.superdo.todo.events.action.SelectBucketEvent;
import com.andysapps.superdo.todo.events.firestore.TaskUpdatedEvent;
import com.andysapps.superdo.todo.events.ui.DialogDismissEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
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
public class AddTaskFragment extends BottomSheetDialogFragment implements  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnDismissListener {

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

        task = new Task();
        clickToday();
        // Inflate the layout for this fragment
        return v;
    }

    public void initUi() {
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

            case THIS_WEEK:
            case THIS_MONTH:
            case UPCOMING:
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

       if (task.getDoDate() != null) {
           tvTime.setText(task.getTimeString());
           ivTime.setImageResource(Utils.getTimeIcon(task.getDoDate().getHours()));
       } else {
           tvTime.setText("No Time");
           ivTime.setImageResource(R.drawable.ic_time_off);
       }

       if (task.getBucketId() != null) {

           bucketName.setText(task.getBucketName());

           switch (BucketColors.valueOf(task.getBucketColor())) {
               case Red:
                   ivTag.setImageResource(R.drawable.img_oval_light_red_mini);
                   break;
               case Green:
                   ivTag.setImageResource(R.drawable.img_oval_light_green_mini);
                   break;
               case SkyBlue:
                   ivTag.setImageResource(R.drawable.img_oval_light_skyblue_mini);
                   break;
               case InkBlue:
                   ivTag.setImageResource(R.drawable.img_oval_light_inkblue_mini);
                   break;
               case Orange:
                   ivTag.setImageResource(R.drawable.img_oval_light_orange_mini);
                   break;
           }
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

    public void showTimePicker() {
        Calendar now = Calendar.getInstance();
        int hours = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);

        if (task.getDoDate() != null) {
            hours = task.getDoDate().hours;
            min = task.getDoDate().minutes;
        }

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
        task.setListedIn(TaskListing.TODAY);
        SuperDate date = new SuperDate(Calendar.getInstance().getTime());
        date.setDate(Calendar.getInstance().get(Calendar.DATE));
        date.setMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
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
        Calendar tomorrow = Utils.getTomorrow();
        SuperDate date = new SuperDate(tomorrow.getTime());
        date.setDate(tomorrow.get(Calendar.DATE));
        date.setMonth(tomorrow.get(Calendar.MONTH) + 1);
        date.setYear(tomorrow.get(Calendar.YEAR));
        if (!isTimeSet) {
            date.setTime(Utils.getDefaultTime(task.getListedIn()), 0);
        }
        task.setDoDate(date);
        updateUi();
    }

    @OnClick(R.id.btn_someday)
    public void clickSomeday() {
        task.setListedIn(TaskListing.UPCOMING);
        task.setDoDate(null);
        updateUi();
        showDatePicker();
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
        etTaskName.clearFocus();
        new SelectBucketDialogFragment().show(getFragmentManager(), SelectBucketDialogFragment.class.getName());
    }

    @OnClick(R.id.ib_add_task)
    public void clickAddTask() {
        if (validate()) {
            task.setUserId(FirestoreManager.getInstance().userId);
            task.setName(etTaskName.getText().toString());
            task.setTaskIndex(TaskOrganiser.getInstance().getTaskSize(task.getListedIn()));
            FirestoreManager.getInstance().uploadTask(task);
        }
    }

    public boolean validate() {
        if (etTaskName.getText().toString().isEmpty()) {
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

        if (Utils.isSuperDateToday(date)) {
            clickToday();
            return;
        } else if (Utils.isSuperDateTomorrow(date)) {
            clickTomorrow();
            return;
        } else if(Utils.isSuperdateThisWeek(date)) {
            task.setListedIn(TaskListing.THIS_WEEK);
        } else if(Utils.isSuperdateThisMonth(date)) {
            task.setListedIn(TaskListing.THIS_MONTH);
        } else if(Utils.isSuperdateIsUpcoming(date)) {
            task.setListedIn(TaskListing.UPCOMING);
        }

        if (task.getDoDate() != null) {
            task.getDoDate().setDoDate(dayOfMonth, monthOfYear + 1, year);
        } else {
            task.setDoDate(date);
        }

        if (!task.getDoDate().hasTime) {
            showTimePicker();
        }

        updateUi();
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
        //task = new Task();
        task.setName("");
        //task.setHabitCategory(event.getTask().getHabitCategory());
        //task.setDoDate(event.getTask().getDoDate());
        etTaskName.getText().clear();
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SelectBucketEvent event) {
        task.setBucketColor(event.getBucket().getTagColor());
        task.setBucketId(event.getBucket().getDocumentId());
        task.setBucketName(event.getBucket().getName());
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DialogDismissEvent event) {
        showKeyboradAsync();
    }

}
