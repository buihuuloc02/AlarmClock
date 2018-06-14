package alarmclock.app.com.alarmclock.provider;

import android.graphics.Bitmap;

import com.android.volley.RequestQueue;

/**
 * Created by Administrator on 6/13/2018.
 */

public abstract class IWeatherImageProvider {
    public abstract Bitmap getImage(int code, RequestQueue requestQueue, WeatherImageListener listener);


    public static interface WeatherImageListener {
        public void onImageReady(Bitmap image);
    }
}
