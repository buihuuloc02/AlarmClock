package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alarmclock.app.com.alarmclock.BuildConfig;
import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.adapter.WeatherRecyclerAdapter;
import alarmclock.app.com.alarmclock.model.Weather;
import alarmclock.app.com.alarmclock.util.Constant;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import alarmclock.app.com.alarmclock.util.UnitConvertor;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 6/12/2018.
 */

public class WeatherActivity extends BaseActivity {
    private RequestQueue requestQueue;
    private SharedPreferences prefs;
    private MenuItem refreshItem;
    TextView todayTemperature;
    TextView todayDescription;
    TextView todayWind;
    TextView todayPressure;
    TextView todayHumidity;
    TextView todaySunrise;
    TextView todaySunset;
    TextView lastUpdate;
    TextView todayIcon;

    TabLayout tabLayout;
    TextView tvNameCity;
    ImageView imgSearchCity;
    WeatherRecyclerAdapter weatherRecyclerAdapter;
    RecyclerView recyclerView;
    View appView;
    View viewWeather;
    WebView wvNews;
    private IabHelper mHelper;
    ProgressDialog progressDialog;
    SharePreferenceHelper sharePreferenceHelper;
    SharedPreferences prefDefault;
    String cityDefault = "";
    private String cityInputSearch = "";
    Weather todayWeather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SwA", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setTitle(R.string.text_title_scree_news_and_weather);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharePreferenceHelper = getSharePreferences();
        prefDefault = PreferenceManager.getDefaultSharedPreferences(this);


        todayTemperature = (TextView) findViewById(R.id.todayTemperature);
        todayDescription = (TextView) findViewById(R.id.todayDescription);
        todayWind = (TextView) findViewById(R.id.todayWind);
        todayPressure = (TextView) findViewById(R.id.todayPressure);
        todayHumidity = (TextView) findViewById(R.id.todayHumidity);
        todaySunrise = (TextView) findViewById(R.id.todaySunrise);
        todaySunset = (TextView) findViewById(R.id.todaySunset);
        tvNameCity = (TextView) findViewById(R.id.tvNameCity);
        imgSearchCity = (ImageView) findViewById(R.id.imgSearchCity);
        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        todayIcon = (TextView) findViewById(R.id.todayIcon);
        appView = (View) findViewById(R.id.viewApp);
        viewWeather = (View) findViewById(R.id.viewWeather);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Typeface weatherFont = ResourcesCompat.getFont(this, R.font.weather);
        todayIcon.setTypeface(weatherFont);
        viewWeather.setVisibility(View.GONE);
        // Initialize viewPager


        imgSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCities();
            }
        });


        cityDefault = sharePreferenceHelper.getString(SharePreferenceHelper.Key.DEFAULT_CITY, Constant.DEFAULT_CITY);
        tvNameCity.setText(cityDefault);
        appView.setVisibility(View.GONE);
    }

    private void loadWebView() {
        wvNews.getSettings().setJavaScriptEnabled(true); // enable javascript


        wvNews.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(WeatherActivity.this, description, Toast.LENGTH_SHORT).show();
                }
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        wvNews.loadUrl("https://news.zing.vn/");
    }


    public static long saveLastUpdateTime(SharedPreferences sp) {
        Calendar now = Calendar.getInstance();
        sp.edit().putLong("lastUpdate", now.getTimeInMillis()).apply();
        return now.getTimeInMillis();
    }

    private void updateLastUpdateTime() {
        updateLastUpdateTime(
                PreferenceManager.getDefaultSharedPreferences(this).getLong("lastUpdate", -1)
        );
    }

    private void updateLastUpdateTime(long timeInMillis) {
        if (timeInMillis < 0) {
            // No time
            lastUpdate.setText("");
        } else {
            lastUpdate.setText(getString(R.string.last_update, formatTimeWithDayIfNotToday(this, timeInMillis)));
        }
    }

    public static String formatTimeWithDayIfNotToday(Context context, long timeInMillis) {
        Calendar now = Calendar.getInstance();
        Calendar lastCheckedCal = new GregorianCalendar();
        lastCheckedCal.setTimeInMillis(timeInMillis);
        Date lastCheckedDate = new Date(timeInMillis);
        String timeFormat = android.text.format.DateFormat.getTimeFormat(context).format(lastCheckedDate);
        if (now.get(Calendar.YEAR) == lastCheckedCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == lastCheckedCal.get(Calendar.DAY_OF_YEAR)) {
            // Same day, only show time
            return timeFormat;
        } else {
            return android.text.format.DateFormat.getDateFormat(context).format(lastCheckedDate) + " " + timeFormat;
        }
    }

    @SuppressLint("RestrictedApi")
    private void searchCities() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getString(R.string.search_title));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setSingleLine(true);
        alert.setView(input, 32, 0, 32, 0);
        alert.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (!result.isEmpty()) {
                    cityInputSearch = result;
                    new getWeatherAsyncTask().execute();
                    new getWeatherLongTeamAsyncTask().execute();

                }
            }
        });
        alert.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void saveLocation(String result) {

        String recentCity = sharePreferenceHelper.getString(SharePreferenceHelper.Key.DEFAULT_CITY, Constant.DEFAULT_CITY);
        if (!recentCity.equals(result)) {
            sharePreferenceHelper.put(SharePreferenceHelper.Key.DEFAULT_CITY, result);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            // Log.d("SwA", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memu_screen_weather, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (isNetworkEnabled()) {
            appView.setVisibility(View.VISIBLE);
            new getWeatherAsyncTask().execute();
            new getWeatherLongTeamAsyncTask().execute();
        } else {
            showDialog(WeatherActivity.this, getResources().getString(R.string.text_no_internet),
                    false, getResources().getString(R.string.text_button_ok), "", new CallBackDismiss() {
                        @Override
                        public void callBackDismiss() {
                        }
                    });
            appView.setVisibility(View.GONE);
        }
    }

    private float convertToC(String unit, float val) {
        if (unit.equalsIgnoreCase("°C"))
            return val;
        return (float) ((val - 32) / 1.8);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionRefresh:
                loadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    private URL provideURL(Activity activity, String[] coords, String citySelect, boolean longTime) throws UnsupportedEncodingException, MalformedURLException {
        String apiKey = prefDefault.getString("apiKey", activity.getResources().getString(R.string.apiKey));

        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/");
        if (longTime) {
            urlBuilder.append("forecast").append("?");
        } else {
            urlBuilder.append("weather").append("?");
        }
        if (coords.length == 2) {
            urlBuilder.append("lat=").append(coords[0]).append("&lon=").append(coords[1]);
        } else {
            if (!citySelect.isEmpty() && !citySelect.toLowerCase().equals(cityDefault.toLowerCase())) {
                cityDefault = citySelect;
            }
            urlBuilder.append("q=").append(URLEncoder.encode(cityDefault, "UTF-8"));
        }
        urlBuilder.append("&lang=").append("en");
        urlBuilder.append("&mode=json");
        urlBuilder.append("&appid=").append(apiKey);

        return new URL(urlBuilder.toString());
    }


    class getWeatherAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int result = 0;
            String[] coords = new String[]{};
            URL url = null;
            String response = "";
            try {
                url = provideURL(WeatherActivity.this, coords, cityInputSearch, false);
            } catch (UnsupportedEncodingException e) {
                result = -1;
            } catch (MalformedURLException e) {
                result = -1;
            }
            Log.i("URL", url.toString());
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                result = -1;
            }
            try {
                if (urlConnection.getResponseCode() == 200) {
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BufferedReader r = new BufferedReader(inputStreamReader);

                    String line = null;
                    while ((line = r.readLine()) != null) {
                        response += line + "\n";
                    }

                    urlConnection.disconnect();
                    // Background work finished successfully
                    Log.d("Task", "done successfully");
                    Log.d("response", response);
                    parseTodayJson(response);
                    saveLastUpdateTime(prefDefault);
                    saveLocation(cityDefault);
                    result = 1;
                } else if (urlConnection.getResponseCode() == 429) {
                    // Too many requests
                    Snackbar.make(appView, getString(R.string.msg_city_input_incorrect), Snackbar.LENGTH_LONG).show();

                } else {
                    // Bad response from server
                    Log.d("Task", "bad response " + urlConnection.getResponseCode());
                    Snackbar.make(appView, getString(R.string.msg_city_input_incorrect), Snackbar.LENGTH_LONG).show();

                }
            } catch (IOException e) {
                Snackbar.make(appView, getString(R.string.msg_city_input_incorrect), Snackbar.LENGTH_LONG).show();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(WeatherActivity.this);
            progressDialog.setMessage(WeatherActivity.this.getResources().getString(R.string.msg_downloading_data));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != -1) {
                viewWeather.setVisibility(View.VISIBLE);
            }
            updateTodayWeatherUI();
            updateLastUpdateTime();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }


        }
    }

    class getWeatherLongTeamAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int result = 0;
            String[] coords = new String[]{};
            URL url = null;
            String response = "";
            try {
                url = provideURL(WeatherActivity.this, coords, cityInputSearch, true);
            } catch (UnsupportedEncodingException e) {
                result = -1;
            } catch (MalformedURLException e) {
                result = -1;
            }
            Log.d("URL", url.toString());
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                result = -1;
            }
            try {
                if (urlConnection.getResponseCode() == 200) {
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BufferedReader r = new BufferedReader(inputStreamReader);
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        response += line + "\n";
                    }
                    urlConnection.disconnect();
                    // Background work finished successfully
                    Log.d("Task", "done successfully");
                    Log.d("response", response);
                    parseLongTermJson(response);
                    result = 1;
                } else if (urlConnection.getResponseCode() == 429) {
                    Snackbar.make(appView, getString(R.string.msg_city_input_incorrect), Snackbar.LENGTH_LONG).show();
                } else {
                    Log.d("Task", "bad response " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(WeatherActivity.this, longTermWeather);
            recyclerView.setAdapter(weatherRecyclerAdapter);
        }
    }

    private List<Weather> longTermWeather = new ArrayList<>();

    public void parseLongTermJson(String result) {
        int i;
        try {
            JSONObject reader = new JSONObject(result);
            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                if (longTermWeather == null) {
                    longTermWeather = new ArrayList<>();
                }
            }
            longTermWeather = new ArrayList<>();
            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
                Weather weather = new Weather();

                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem.getJSONObject("main");
                weather.setDate(listItem.getString("dt"));
                weather.setTemperature(main.getString("temp"));
                weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                JSONObject windObj = listItem.optJSONObject("wind");
                if (windObj != null) {
                    weather.setWind(windObj.getString("speed"));
                    weather.setWindDirectionDegree(windObj.getDouble("deg"));
                }
                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));
                JSONObject rainObj = listItem.optJSONObject("rain");
                String rain = "";
                if (rainObj != null) {
                    rain = getRainString(rainObj);
                } else {
                    JSONObject snowObj = listItem.optJSONObject("snow");
                    if (snowObj != null) {
                        rain = getRainString(snowObj);
                    } else {
                        rain = "0";
                    }
                }
                weather.setRain(rain);
                final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                weather.setId(idString);
                final String dateMsString = listItem.getString("dt") + "000";
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(dateMsString));
                weather.setIcon(setWeatherIcon(Integer.parseInt(idString), cal.get(Calendar.HOUR_OF_DAY)));

                Calendar today = Calendar.getInstance();
                if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    longTermWeather.add(weather);
                }
            }
            SharedPreferences.Editor editor = prefDefault.edit();
            editor.putString("lastLongterm", result);
            editor.commit();
        } catch (JSONException e) {
            Log.e("JSONException Data", result);

        }

    }

    ;

    private void parseTodayJson(String result) {
        try {
            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");


            String city = reader.getString("name");
            String country = "";
            JSONObject countryObj = reader.optJSONObject("sys");
            if (countryObj != null) {
                country = countryObj.getString("country");
                todayWeather.setSunrise(countryObj.getString("sunrise"));
                todayWeather.setSunset(countryObj.getString("sunset"));
            }
            todayWeather.setCity(city);
            todayWeather.setCountry(country);

            JSONObject coordinates = reader.getJSONObject("coord");
            if (coordinates != null) {
                prefDefault.edit().putFloat("latitude", (float) coordinates.getDouble("lon")).putFloat("longitude", (float) coordinates.getDouble("lat")).commit();
            }

            JSONObject main = reader.getJSONObject("main");

            todayWeather.setTemperature(main.getString("temp"));
            todayWeather.setDescription(reader.getJSONArray("weather").getJSONObject(0).getString("description"));
            JSONObject windObj = reader.getJSONObject("wind");
            todayWeather.setWind(windObj.getString("speed"));
            if (windObj.has("deg")) {
                todayWeather.setWindDirectionDegree(windObj.getDouble("deg"));
            } else {
                Log.e("parseTodayJson", "No wind direction available");
                todayWeather.setWindDirectionDegree(null);
            }
            todayWeather.setPressure(main.getString("pressure"));
            todayWeather.setHumidity(main.getString("humidity"));

            JSONObject rainObj = reader.optJSONObject("rain");
            String rain;
            if (rainObj != null) {
                rain = getRainString(rainObj);
            } else {
                JSONObject snowObj = reader.optJSONObject("snow");
                if (snowObj != null) {
                    rain = getRainString(snowObj);
                } else {
                    rain = "0";
                }
            }
            todayWeather.setRain(rain);

            final String idString = reader.getJSONArray("weather").getJSONObject(0).getString("id");
            todayWeather.setId(idString);
            todayWeather.setIcon(setWeatherIcon(Integer.parseInt(idString), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));

            SharedPreferences.Editor editor = prefDefault.edit();
            editor.putString("lastToday", result);
            editor.commit();

        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();

        }


    }

    public static String getRainString(JSONObject rainObj) {
        String rain = "0";
        if (rainObj != null) {
            rain = rainObj.optString("3h", "fail");
            if ("fail".equals(rain)) {
                rain = rainObj.optString("1h", "0");
            }
        }
        return rain;
    }

    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    private void updateTodayWeatherUI() {
        try {
            if (todayWeather.getCountry().isEmpty()) {

                return;
            }
        } catch (Exception e) {

            return;
        }
        String city = todayWeather.getCity();
        String country = todayWeather.getCountry();
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        tvNameCity.setText(city + (country.isEmpty() ? "" : ", " + country));


        // Temperature
        float temperature = UnitConvertor.convertTemperature(Float.parseFloat(todayWeather.getTemperature()), prefDefault);
        if (prefDefault.getBoolean("temperatureInteger", false)) {
            temperature = Math.round(temperature);
        }

        // Rain
        double rain = Double.parseDouble(todayWeather.getRain());
        String rainString = UnitConvertor.getRainString(rain, prefDefault);

        // Wind
        double wind;
        try {
            wind = Double.parseDouble(todayWeather.getWind());
        } catch (Exception e) {
            e.printStackTrace();
            wind = 0;
        }
        wind = UnitConvertor.convertWind(wind, prefDefault);
        // Pressure
        double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(todayWeather.getPressure()), prefDefault);

        todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + prefDefault.getString("unit", "°C"));
        todayDescription.setText(todayWeather.getDescription().substring(0, 1).toUpperCase() +
                todayWeather.getDescription().substring(1) + rainString);
        if (prefDefault.getString("speedUnit", "m/s").equals("bft")) {
            todayWind.setText(getString(R.string.wind) + ": " +
                    UnitConvertor.getBeaufortName((int) wind) +
                    (todayWeather.isWindDirectionAvailable() ? " " + getWindDirectionString(prefDefault, this, todayWeather) : ""));
        } else {
            todayWind.setText(getString(R.string.wind) + ": " + new DecimalFormat("0.0").format(wind) + " " +
                    localize(prefDefault, "speedUnit", "m/s") +
                    (todayWeather.isWindDirectionAvailable() ? " " + getWindDirectionString(prefDefault, this, todayWeather) : ""));
        }
        todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                localize(prefDefault, "pressureUnit", "hPa"));
        todayHumidity.setText(getString(R.string.humidity) + ": " + todayWeather.getHumidity() + " %");
        todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(todayWeather.getSunrise()));
        todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(todayWeather.getSunset()));
        todayIcon.setText(todayWeather.getIcon());
    }

    private String localize(SharedPreferences sp, String preferenceKey, String defaultValueKey) {
        return localize(sp, this, preferenceKey, defaultValueKey);
    }

    private static Map<String, Integer> speedUnits = new HashMap<>(3);
    private static Map<String, Integer> pressUnits = new HashMap<>(3);

    public static void initMappings() {

        speedUnits.put("m/s", R.string.speed_unit_mps);
        speedUnits.put("kph", R.string.speed_unit_kph);
        speedUnits.put("mph", R.string.speed_unit_mph);
        speedUnits.put("kn", R.string.speed_unit_kn);

        pressUnits.put("hPa", R.string.pressure_unit_hpa);
        pressUnits.put("kPa", R.string.pressure_unit_kpa);
        pressUnits.put("mm Hg", R.string.pressure_unit_mmhg);
    }

    public static String localize(SharedPreferences sp, Context context, String preferenceKey, String defaultValueKey) {
        String preferenceValue = sp.getString(preferenceKey, defaultValueKey);
        String result = preferenceValue;
        if ("speedUnit".equals(preferenceKey)) {
            if (speedUnits.containsKey(preferenceValue)) {
                result = context.getString(speedUnits.get(preferenceValue));
            }
        } else if ("pressureUnit".equals(preferenceKey)) {
            if (pressUnits.containsKey(preferenceValue)) {
                result = context.getString(pressUnits.get(preferenceValue));
            }
        }
        return result;
    }

    public static String getWindDirectionString(SharedPreferences sp, Context context, Weather weather) {
        try {
            if (Double.parseDouble(weather.getWind()) != 0) {
                String pref = sp.getString("windDirectionFormat", null);
                if ("arrow".equals(pref)) {
                    return weather.getWindDirection(8).getArrow(context);
                } else if ("abbr".equals(pref)) {
                    return weather.getWindDirection().getLocalizedString(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
