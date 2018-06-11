package alarmclock.app.com.alarmclock.model;

import java.io.Serializable;

/**
 * Created by Administrator on 6/11/2018.
 */

public class PhoneCustom implements Serializable {
    String name;
    String number;

    public PhoneCustom() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
