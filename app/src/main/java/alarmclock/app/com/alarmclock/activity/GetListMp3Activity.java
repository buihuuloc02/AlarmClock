package alarmclock.app.com.alarmclock.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.UriCustom;
import butterknife.BindView;
import butterknife.ButterKnife;

import static alarmclock.app.com.alarmclock.activity.AddAlarmActivity.EXTRA_NAME_SOUND_SELECT;
import static alarmclock.app.com.alarmclock.activity.AddAlarmActivity.EXTRA_PATH_SOUND_SELECT;

/**
 * Created by Administrator on 5/30/2018.
 */

public class GetListMp3Activity extends BaseActivity {
    final String MEDIA_PATH = new String("/sdcard/");
    public final static String EXTRA_NAME_SOUND = "EXTRA_NAME_SOUND";
    @BindView(R.id.listViewMp3)
    ListView listViewMp3;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    ArrayList<String> listNames;
    ArrayList<UriCustom> uriCustoms;
    ArrayList<String> paths;

    ArrayAdapter<String> adapter;
    MediaPlayer mediaPlayer;
    ContentResolver contentResolver;
    Cursor cursor;
    Uri uri;
    private String nameSelected = "";
    private String pathSelected = "";
    private MenuItem menuItemDone;
    private UriCustom uriCustomSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_file_mp3);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        setTitle(getResources().getString(R.string.text_select_alarm));
        ButterKnife.bind(this);
        if (getIntent().hasExtra(EXTRA_NAME_SOUND)) {
            nameSelected = getIntent().getStringExtra(EXTRA_NAME_SOUND);
        }
        getListAlarm();
        GetAllMediaMp3Files();
        setAdapterListView();
        listViewMp3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nameSelected = listNames.get(i);
                pathSelected = paths.get(i);
                uriCustomSelected = uriCustoms.get(i);
                playSound(uriCustomSelected.getUri());
            }
        });
        setDisplayNoData();
    }

    private void setAdapterListView() {
        listViewMp3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMp3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listNames));
        if (nameSelected != null) {
            int indexSelected = indexToneInList(nameSelected);
            if (indexSelected != -1) {
                listViewMp3.setItemChecked(indexSelected, true);
                listViewMp3.smoothScrollToPosition(indexSelected);
                listViewMp3.setSelection(indexSelected);
            }
        }
    }

    private int indexToneInList(String nameFile) {
        for (int i = 0; listNames != null && i < listNames.size(); i++) {
            String str = listNames.get(i);
            if (str.equals(nameFile)) {
                return i;
            }
        }
        return -1;
    }

    private void setDisplayNoData() {
        tvNoData.setVisibility(View.VISIBLE);
        listViewMp3.setVisibility(View.VISIBLE);
        if (listNames != null && listNames.size() > 0) {
            tvNoData.setVisibility(View.GONE);
            if (menuItemDone != null) {
                menuItemDone.setVisible(true);
            }
        } else {
            listViewMp3.setVisibility(View.GONE);
            if (menuItemDone != null) {
                menuItemDone.setVisible(false);
            }

        }
    }

    private ArrayList<UriCustom> getListAlarm() {


        RingtoneManager ringtoneMgr = new RingtoneManager(this);
        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        int alarmsCount = alarmsCursor.getCount();
        if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
            return null;
        }
        listNames = new ArrayList<>();
        paths = new ArrayList<>();
        uriCustoms = new ArrayList<>();

        // add item NONE to list
        // listNames.add(getResources().getString(R.string.text_none));
        ArrayList<UriCustom> alarms = new ArrayList<UriCustom>();
        //UriCustom uri = new UriCustom();
        //uri.setName(getResources().getString(R.string.text_none));
        //uri.setUri(null);
        //alarms.add(uri);
        // end

        while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
            int currentPosition = alarmsCursor.getPosition();
            UriCustom uriCustom = new UriCustom();
            uriCustom.setUri(ringtoneMgr.getRingtoneUri(currentPosition));
            paths.add(ringtoneMgr.getRingtoneUri(currentPosition).toString());
            uriCustom.setSelected(false);
            String name = RingtoneManager.getRingtone(this, ringtoneMgr.getRingtoneUri(currentPosition)).getTitle(this);
            listNames.add(name);
            uriCustom.setName(name);
            alarms.add(uriCustom);
            uriCustoms.add(uriCustom);
        }
        //alarmsCursor.close();

        return alarms;
    }

    /**
     * Stop sound playing
     */
    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    /**
     * Play sound by uri
     *
     * @param uri: Uri
     */
    private void playSound(Uri uri) {
        try {
            stopSound();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_screen_add_alarm, menu);
        menuItemDone = menu.findItem(R.id.actionDone);
        setDisplayNoData();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionDone:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_NAME_SOUND_SELECT, nameSelected);
                returnIntent.putExtra(EXTRA_PATH_SOUND_SELECT, pathSelected);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void GetAllMediaMp3Files() {
        contentResolver = getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        if (!checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            return;
        }
        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
        } else if (!cursor.moveToFirst()) {
        } else {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String SongTitle = cursor.getString(title);
                String SongPath = cursor.getString(path);

                UriCustom uriCustom = new UriCustom();
                uriCustom.setName(SongTitle);
                uriCustom.setUri(Uri.parse(SongPath));
                listNames.add(SongTitle);
                paths.add(SongPath);
                uriCustoms.add(uriCustom);

            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getListAlarm();
                    GetAllMediaMp3Files();
                    setAdapterListView();
                } else {
                    Toast.makeText(GetListMp3Activity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSound();
    }
}
