package com.andysapps.superdo.todo.model.esprit;

import com.andysapps.superdo.todo.enums.EspritStatType;
import com.hadiidbouk.charts.BarData;

import java.util.ArrayList;

/**
 * Created by Admin on 14,April,2020
 */
public class EspritStatistics {

    public EspritStatType statType;
    public int totalEsprit;
    public int totalTasksCompleted;

    public ArrayList<BarData> barData;
    public  ArrayList<EspritStatPoint> espritStatPoints;

    public EspritStatistics() {
        barData = new ArrayList<>();
        espritStatPoints = new ArrayList<>();
    }

    public EspritStatistics(EspritStatType type, int totalEsprit, int totalTasksCompleted) {
        this.statType = type;
        this.totalEsprit = totalEsprit;
        this.totalTasksCompleted = totalTasksCompleted;
    }

    public EspritStatType getStatType() {
        return statType;
    }

    public void setStatType(EspritStatType statType) {
        this.statType = statType;
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

    public ArrayList<BarData> getBarData() {
        return barData;
    }

    public void setBarData(ArrayList<BarData> barData) {
        this.barData = barData;
    }

    public ArrayList<EspritStatPoint> getEspritStatPoints() {
        return espritStatPoints;
    }

    public void setEspritStatPoints(ArrayList<EspritStatPoint> espritStatPoints) {
        this.espritStatPoints = espritStatPoints;
    }
}
