package com.andysapps.superdo.todo.events;

import com.andysapps.superdo.todo.enums.MoonButtonType;

/**
 * Created by Andrews on 16,November,2019
 */

public class UpdateMoonButtonType {
    public MoonButtonType moonButtonType;

    public UpdateMoonButtonType(MoonButtonType moonButtonType) {
        this.moonButtonType = moonButtonType;
    }
}
