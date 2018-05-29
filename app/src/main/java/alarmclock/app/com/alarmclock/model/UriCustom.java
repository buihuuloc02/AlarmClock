package alarmclock.app.com.alarmclock.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Administrator on 5/28/2018.
 */

public class UriCustom implements Serializable{
    private Uri uri;
    private boolean selected;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UriCustom() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
