package alarmclock.app.com.alarmclock.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import alarmclock.app.com.alarmclock.R;

/**
 * Created by Administrator on 6/13/2018.
 */

public class WeatherPreferenceActivity  extends PreferenceActivity {


    @Override
    public void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String action = getIntent().getAction();

        addPreferencesFromResource(R.xml.waeaher_prefs);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // We set the current values in the description
        Preference prefLocation = getPreferenceScreen().findPreference("swa_loc");
        Preference prefTemp = getPreferenceScreen().findPreference("swa_temp_unit");

        prefLocation.setSummary(getResources().getText(R.string.summary_loc) + " " + prefs.getString("cityName", null) + "," + prefs.getString("country", null));

        String unit = prefs.getString("swa_temp_unit", null) != null ? "°" + prefs.getString("swa_temp_unit", null).toUpperCase() : "";
        prefTemp.setSummary(getResources().getText(R.string.summary_temp) + " " + unit);


        //getPreferenceScreen().findPreference("swa_temp_unit").setDefaultValue(valTemp);
        //getPreferenceScreen().findPreference("swa_system").setDefaultValue(unitSys);


    }
}