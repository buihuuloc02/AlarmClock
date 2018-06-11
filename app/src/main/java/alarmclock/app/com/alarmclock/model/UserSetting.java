package alarmclock.app.com.alarmclock.model;

import java.io.Serializable;

/**
 * Created by Administrator on 6/9/2018.
 */

public class UserSetting implements Serializable {
    private int numberShake;
    private int speedShake;
    private int showButtonStop = 1; //0: hide; 1: show
    private int showNotification = 1; //0: hide; 1: show

    public UserSetting() {
    }

    public int getNumberShake() {
        return numberShake;
    }

    public void setNumberShake(int numberShake) {
        this.numberShake = numberShake;
    }

    public int getSpeedShake() {
        return speedShake;
    }

    public void setSpeedShake(int speedShake) {
        this.speedShake = speedShake;
    }

    public int getShowButtonStop() {
        return showButtonStop;
    }

    public void setShowButtonStop(int showButtonStop) {
        this.showButtonStop = showButtonStop;
    }

    public int getShowNotification() {
        return showNotification;
    }

    public void setShowNotification(int showNotification) {
        this.showNotification = showNotification;
    }
}
