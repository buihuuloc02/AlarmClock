package alarmclock.app.com.alarmclock.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Administrator on 5/8/2018.
 */

public class ItemAlarm implements Serializable {
    private String hour;
    private String minute;
    private String second;
    private String title;
    private String format;

    public String getNameTone() {
        return nameTone;
    }

    public void setNameTone(String nameTone) {
        this.nameTone = nameTone;
    }

    private String nameTone;
    private String status;
    private int dayCreate;
    private int monthCreate;

    public Uri getUriCustom() {
        return uri;
    }

    public void setUriCustom(Uri uriCustom) {
        this.uri = uriCustom;
    }

    private Uri uri;

    public int getDayCreate() {
        return dayCreate;
    }

    public void setDayCreate(int dayCreate) {
        this.dayCreate = dayCreate;
    }

    public int getMonthCreate() {
        return monthCreate;
    }

    public void setMonthCreate(int monthCreate) {
        this.monthCreate = monthCreate;
    }

    private int repeatMo;
    private int repeatTu;
    private int repeatWe;
    private int repeatTh;
    private int repeatFr;
    private int repeatSa;
    private int repeatSu;

    public int getRepeatMo() {
        return repeatMo;
    }

    public void setRepeatMo(int repeatMo) {
        this.repeatMo = repeatMo;
    }

    public int getRepeatTu() {
        return repeatTu;
    }

    public void setRepeatTu(int repeatTu) {
        this.repeatTu = repeatTu;
    }

    public int getRepeatWe() {
        return repeatWe;
    }

    public void setRepeatWe(int repeatWe) {
        this.repeatWe = repeatWe;
    }

    public int getRepeatTh() {
        return repeatTh;
    }

    public void setRepeatTh(int repeatTh) {
        this.repeatTh = repeatTh;
    }

    public int getRepeatFr() {
        return repeatFr;
    }

    public void setRepeatFr(int repeatFr) {
        this.repeatFr = repeatFr;
    }

    public int getRepeatSa() {
        return repeatSa;
    }

    public void setRepeatSa(int repeatSa) {
        this.repeatSa = repeatSa;
    }

    public int getRepeatSu() {
        return repeatSu;
    }

    public void setRepeatSu(int repeatSu) {
        this.repeatSu = repeatSu;
    }

    private int id;

    public ItemAlarm() {
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
