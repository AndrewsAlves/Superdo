package com.andysapps.superdo.todo.model.sidekicks;

import com.andysapps.superdo.todo.enums.RemindType;
import com.andysapps.superdo.todo.model.SuperDate;

/**
 * Created by Andrews on 27,November,2019
 */

public class Remind implements Cloneable {

    public boolean isEnabled;

    RemindType remindType;

    SuperDate remindOnce;

    Repeat remindRepeat;

    public Remind() {}

    public Remind(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public Remind clone() throws CloneNotSupportedException {
        return (Remind) super.clone();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public RemindType getRemindType() {
        return remindType;
    }

    public void setRemindType(RemindType remindType) {
        this.remindType = remindType;
    }

    public SuperDate getRemindOnce() {
        return remindOnce;
    }

    public void setRemindOnce(SuperDate remindOnce) {
        this.remindOnce = remindOnce;
    }

    public Repeat getRemindRepeat() {
        return remindRepeat;
    }

    public void setRemindRepeat(Repeat remindRepeat) {
        this.remindRepeat = remindRepeat;
    }

    public String getRemindString() {

        String remindString = "When to remind?";

        switch (remindType) {
            case REMIND_ONCE:
                remindString = "Remind Once " + remindOnce.getSuperDateString() + " at " + remindOnce.getTimeString();
                return remindString;
            case REMIND_REPEAT:
                remindString = remindRepeat.getRepeatString();
        }

        return remindString;
    }
}
