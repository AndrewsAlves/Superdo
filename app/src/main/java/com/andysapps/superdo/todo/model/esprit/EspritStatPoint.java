package com.andysapps.superdo.todo.model.esprit;

import com.andysapps.superdo.todo.model.SuperDate;

/**
 * Created by Admin on 14,April,2020
 */
public class EspritStatPoint {

    SuperDate date;
    int totalEsprit;
    int totalTasksCompleted;

    public EspritStatPoint(SuperDate date, int totalEsprit, int totalTasksCompleted) {
        this.date = date;
        this.totalEsprit = totalEsprit;
        this.totalTasksCompleted = totalTasksCompleted;
    }

    public SuperDate getDate() {
        return date;
    }

    public void setDate(SuperDate date) {
        this.date = date;
    }

    public int getTotalEsprit() {
        return totalEsprit;
    }

    public void setTotalEsprit(int totalEsprit) {
        this.totalEsprit = totalEsprit;
    }

    public int getTotalTasksCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalTasksCompleted(int totalTasksCompleted) {
        this.totalTasksCompleted = totalTasksCompleted;
    }
}
