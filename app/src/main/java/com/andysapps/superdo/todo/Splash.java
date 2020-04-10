package com.andysapps.superdo.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.activity.WelcomeActivity;
import com.andysapps.superdo.todo.events.FetchUserFailureEvent;
import com.andysapps.superdo.todo.events.FetchUserSuccessEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    @BindView(R.id.tv_plan)
    TextView tvPlan;

    @BindView(R.id.tv_prioritize)
    TextView tvPrio;

    @BindView(R.id.tv_takeaction)
    TextView tvTakeAction;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(this::openDesiredActivity, 500);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void openDesiredActivity() {
        //auth.signOut();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(Splash.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            FirestoreManager.getInstance().fetchUser();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchUserSuccessEvent event) {
        Intent intent =  new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchUserFailureEvent event) {
        Intent intent =  new Intent(Splash.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
