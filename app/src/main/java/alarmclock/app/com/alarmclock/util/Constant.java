package alarmclock.app.com.alarmclock.util;

/**
 * Created by Administrator on 5/22/2018.
 */

public interface Constant {
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String ACTION_ALARM_CLOCK = "ACTION_ALARM_CLOCK";

    public static final int REQUEST_CODE_PURCHASE = 1001;
    public static final int REQUEST_CODE_ADD_IMAGE_PAPER = 1002;
    public static final int REQUEST_CODE_SELECT_SOUND = 1003;
    public static final int REQUEST_CODE_SELECT_CONTACT = 1004;

    public static final String  DEFAULT_CITY = "Thanh Pho Ho Chi Minh";
    public static final int PURCHASED = 1;
    public static final int DO_NOT_PURCHASE = 0;
    public static final int TIME_CHECK_NOTIFICATION = 60000 * 5; // 5 MINUTE
}
