package alarmclock.app.com.alarmclock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.adapter.ListNewsAdapter;
import alarmclock.app.com.alarmclock.model.News;
import alarmclock.app.com.alarmclock.util.Constant;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 6/19/2018.
 */

public class NewsActivity extends BaseActivity {

    @BindView(R.id.listNews)
    ListView listViewNews;

    @BindView(R.id.loader)
    ProgressBar loader;

    @BindView(R.id.spinnerNews)
    Spinner spinnerNews;

    String API_KEY = "63a4e183e1c1455e8f0bc80001022ebd";
    String NEWS_SOURCE = "bbc-news";
    int indexDataSourceSelected = 0;

    ArrayList<News> listNews = new ArrayList<News>();
    ArrayList<DataSource> listDataSource = new ArrayList<DataSource>();
    ArrayList<String> listNameDataSource = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ButterKnife.bind(this);
        listViewNews.setEmptyView(loader);
        setTitle(R.string.text_title_news);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        initDataSource();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listNameDataSource);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerNews.setAdapter(dataAdapter);
        spinnerNews.setSelection(indexDataSourceSelected);
        spinnerNews.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                indexDataSourceSelected = i;
                loader.setVisibility(View.VISIBLE);
                String dataSource = listDataSource.get(i).getLink();
                if (isNetworkEnabled()) {
                    new DownloadNews().execute(dataSource);
                } else {
                    showDialog(NewsActivity.this, getResources().getString(R.string.text_no_internet),
                            false, getResources().getString(R.string.text_button_ok), "", new CallBackDismiss() {
                                @Override
                                public void callBackDismiss() {
                                }
                            });
                }
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initDataSource() {
        listDataSource = new ArrayList<>();
        listNameDataSource = new ArrayList<>();


        DataSource dataSource11 = new DataSource("Google News", "google-news");
        listDataSource.add(dataSource11);
        listNameDataSource.add("Google News");

        DataSource dataSource12 = new DataSource("Reuters", "reuters");
        listDataSource.add(dataSource12);
        listNameDataSource.add("Reuters");

        DataSource dataSource10 = new DataSource("Fox Sports", "fox_sports");
        listDataSource.add(dataSource10);
        listNameDataSource.add("Fox Sports");

        DataSource dataSource1 = new DataSource("Recode", "recode");
        listDataSource.add(dataSource1);
        listNameDataSource.add("Recode");

        DataSource dataSource2 = new DataSource("RTE", "rte");
        listDataSource.add(dataSource2);
        listNameDataSource.add("RTE");

        DataSource dataSource3 = new DataSource("RBC", "rbc");
        listDataSource.add(dataSource3);
        listNameDataSource.add("RBC");

        DataSource dataSource4 = new DataSource("Vice News", "vice-news");
        listDataSource.add(dataSource4);
        listNameDataSource.add("Vice News");

        DataSource dataSource5 = new DataSource("Ynet", "ynet");
        listDataSource.add(dataSource5);
        listNameDataSource.add("Ynet");

        DataSource dataSource6 = new DataSource("Time", "time");
        listDataSource.add(dataSource6);
        listNameDataSource.add("Time");

        DataSource dataSource7 = new DataSource("BBC News", "bbc-news");
        listDataSource.add(dataSource7);
        listNameDataSource.add("BBC News");


        DataSource dataSource8 = new DataSource("USA Today", "usa-today");
        listDataSource.add(dataSource8);
        listNameDataSource.add("USA Today");


        DataSource dataSource9 = new DataSource("The New York Times", "the-new-york-times");
        listDataSource.add(dataSource9);
        listNameDataSource.add("The New York Times");


    }

    public static String excuteGet(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("POST");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8");


            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);


            InputStream is;

            int status = connection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)
                is = connection.getErrorStream();
            else
                is = connection.getInputStream();


            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {


            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String dataSource = args[0];
            String xml = "";
            String urlParameters = "";
            xml = excuteGet("https://newsapi.org/v1/articles?source=" + dataSource + "&sortBy=top&apiKey=" + API_KEY, urlParameters);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (xml.length() > 10) { // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");
                    listNews = new ArrayList<>();
                    for (int i = 0; jsonArray != null && i < jsonArray.length(); i++) {
                        News news = new News();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        news.setAuthor(jsonObject.optString(Constant.KEY_AUTHOR).toString());
                        news.setTitle(jsonObject.optString(Constant.KEY_TITLE).toString());
                        news.setDescription(jsonObject.optString(Constant.KEY_DESCRIPTION).toString());
                        news.setUrl(jsonObject.optString(Constant.KEY_URL).toString());
                        news.setUrlToImage(jsonObject.optString(Constant.KEY_URLTOIMAGE).toString());
                        news.setPublishedAt(jsonObject.optString(Constant.KEY_PUBLISHEDAT).toString());
                        listNews.add(news);
                    }
                } catch (JSONException e) {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }
                loader.setVisibility(View.GONE);
                ListNewsAdapter adapter = new ListNewsAdapter(NewsActivity.this, listNews);
                listViewNews.setAdapter(adapter);

                listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(NewsActivity.this, DetailNewsActivity.class);
                        i.putExtra("url", listNews.get(position).getUrl());
                        startActivity(i);
                    }
                });

            } else {
                loader.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionRefresh:
                //loadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class DataSource {
        String name;
        String link;

        public DataSource(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public DataSource() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

}
