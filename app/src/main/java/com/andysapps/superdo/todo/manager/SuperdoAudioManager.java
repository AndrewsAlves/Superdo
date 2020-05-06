package com.andysapps.superdo.todo.manager;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.andysapps.superdo.todo.R;

/**
 * Created by Admin on 24,March,2020
 */
public class SuperdoAudioManager {
    private static SuperdoAudioManager ourInstance;

    public SoundPool soundPool;
    public Vibrator vibrator;

    int soundIdTaskCompleted;
    int soundIdNotification;

    public static SuperdoAudioManager getInstance() {
        return ourInstance;
    }

    private SuperdoAudioManager(Context context) {
        initSounds(context);
        initVibrate(context);
    }

    public static void init(Context context) {
        ourInstance = new SuperdoAudioManager(context);
    }

    public void initSounds(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, android.media.AudioManager.STREAM_MUSIC, 1);
        }

        soundIdTaskCompleted = soundPool.load(context, R.raw.task_completed2, 1);
    }

    public void initVibrate(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void playTaskCompleted() {
        soundPool.play(soundIdTaskCompleted, 1, 1, 0, 0, 1);
    }

    public void releaseSoundPlayer() {
        soundPool.release();
        soundPool = null;
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(50);
        }
    }


}
