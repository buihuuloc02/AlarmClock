package alarmclock.app.com.alarmclock.model;

import java.io.Serializable;

/**
 * Created by Administrator on 6/11/2018.
 */

public class MethodStop implements Serializable {
    private int index;
    private String name;

    public MethodStop() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
