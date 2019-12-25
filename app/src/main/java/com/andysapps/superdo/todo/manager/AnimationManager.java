package com.andysapps.superdo.todo.manager;

/**
 * Created by Andrews on 21,December,2019
 */
public class AnimationManager {
    private static final AnimationManager ourInstance = new AnimationManager();

    public static AnimationManager getInstance() {
        return ourInstance;
    }

    private AnimationManager() {
    }
}
