package com.andysapps.superdo.todo.model.taskfeatures;

import com.andysapps.superdo.todo.enums.RemindType;
import com.andysapps.superdo.todo.model.SuperDate;

import java.util.Random;

/**
 * Created by Andrews on 27,November,2019
 */

public class Remind implements Cloneable {

    public boolean isEnabled;

    String remindType;

    SuperDate remindDate;

    Repeat remindRepeat;

    int remindRequestCode;

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

    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    public SuperDate getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(SuperDate remindDate) {
        this.remindDate = remindDate;
    }

    public Repeat getRemindRepeat() {
        return remindRepeat;
    }

    public void setRemindRepeat(Repeat remindRepeat) {
        this.remindRepeat = remindRepeat;
    }

    public int getRemindRequestCode() {
        return remindRequestCode;
    }

    public void setRemindRequestCode(int remindRequestCode) {
        this.remindRequestCode = remindRequestCode;
    }

    public int generateNewRequestCode() {
        Random random = new Random();
        remindRequestCode = random.nextInt(1000) * random.nextInt(10);
        return remindRequestCode;
    }

    public String getRemindString() {

        String remindString = "When to remind?";

        if (remindType == null) {
            return remindString;
        }

        switch (RemindType.valueOf(remindType)) {
            case REMIND_ONCE:
                remindString = "Remind Once " + remindDate.getSuperDateString() + " at " + remindDate.getTimeString();
                return remindString;
            case REMIND_REPEAT:
                remindString = remindRepeat.getRepeatString();
        }

        return remindString;
    }
}
