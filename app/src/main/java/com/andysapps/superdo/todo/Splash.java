package com.andysapps.superdo.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.activity.start_screens.WelcomeActivity;
import com.andysapps.superdo.todo.events.FetchUserFailureEvent;
import com.andysapps.superdo.todo.events.FetchUserSuccessEvent;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(this::openDesiredActivity, 500);
    }

    public void openDesiredActivity() {
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
        FirestoreManager.getInstance().fetchUserData(this, false);
        Intent intent =  new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchUserFailureEvent event) {
        if (!Utils.isNetworkConnected(this)) {
            finish();
        }
    }
}
