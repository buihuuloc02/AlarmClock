package alarmclock.app.com.alarmclock.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.adapter.CustomGridAdapter;
import alarmclock.app.com.alarmclock.model.Photo;
import butterknife.BindView;
import butterknife.ButterKnife;

import static alarmclock.app.com.alarmclock.activity.GetListMp3Activity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

/**
 * Created by Administrator on 6/4/2018.
 */

public class GetListImageActivity extends BaseActivity implements CustomGridAdapter.OnItemClick {

    private final static String TAG = GetListImageActivity.class.getSimpleName();
    public final static String EXTRA_PATH_IMAGE = "EXTRA_PATH_IMAGE";
    public final static String EXTRA_NAME_IMAGE = "EXTRA_NAME_IMAGE";

    @BindView(R.id.gridViewImage)
    GridView gridViewImage;

    @BindView(R.id.tvNoData)
    TextView tvNoData;


    private ArrayList<Photo> photos;
    private CustomGridAdapter customGridAdapter;
    private String pathImageSelected = "";
    private String nameImageSelected = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_file_image);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.text_select_image);

        ButterKnife.bind(this);
        photos = new ArrayList<>();
        photos = getPhoneAlbums(this);
        Log.d("size", photos.size() + "");



    }

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
                        getPhoneAlbums(GetListImageActivity.this);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {

            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoneAlbums(GetListImageActivity.this);
            }
        }
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

    public ArrayList<Photo> getPhoneAlbums(Context context) {
        // Creating vectors t
        // o hold the final albums objects and albums names
        ArrayList<Photo> phoneAlbums = new ArrayList<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };

        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if (!checkPermissionREAD_EXTERNAL_STORAGE(GetListImageActivity.this)) {
            return phoneAlbums;
        }
        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur != null && cur.getCount() > 0) {
            Log.d(TAG, " query count=" + cur.getCount());

            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.TITLE);

                int imageUriColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaStore.Images.Media._ID);

                do {
                    // Get the field values
                    bucketName = cur.getString(bucketNameColumn);
                    data = cur.getString(imageUriColumn);
                    imageId = cur.getString(imageIdColumn);

                    // Adding a new PhonePhoto object to phonePhotos vector
                    Photo photo = new Photo();
                    photo.setAlbumName(bucketName);
                    photo.setPhotoUri(data);
                    photo.setId(Integer.valueOf(imageId));

                    phoneAlbums.add(photo);

                } while (cur.moveToNext());
            }

            cur.close();

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.text_get_image_error), Toast.LENGTH_SHORT).show();
        }
        photos = phoneAlbums;
        if(photos != null) {
            customGridAdapter = new CustomGridAdapter(GetListImageActivity.this, photos, this);
            gridViewImage.setAdapter(customGridAdapter);
            setDisplayTextViewNoData();
        }else{
            tvNoData.setVisibility(View.VISIBLE);
            gridViewImage.setVisibility(View.GONE);
        }
        return phoneAlbums;
    }

    private void setDisplayTextViewNoData() {
        tvNoData.setVisibility(photos.size() > 0 ? View.GONE : View.VISIBLE);
        gridViewImage.setVisibility(photos.size() > 0 ? View.VISIBLE : View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_screen_add_alarm, menu);
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
                progressDone(pathImageSelected);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnClickItem(Photo photo, int position) {
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                Photo pt = photos.get(i);
                if (i != position) {
                    pt.setSelected(false);
                }
            }
            photos.get(position).setSelected(photo.isSelected());
            if (photo.isSelected()) {
                pathImageSelected = photo.getPhotoUri();
                nameImageSelected = photo.getAlbumName();
            } else {
                pathImageSelected = "";
                nameImageSelected = "";
            }
        }
        customGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnClickCheckBox(final Photo photo, int position) {
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                Photo pt = photos.get(i);
                if (i != position) {
                    pt.setSelected(false);
                }
            }
            photos.get(position).setSelected(photo.isSelected());
            if (photo.isSelected()) {
                pathImageSelected = photo.getPhotoUri();
                nameImageSelected = photo.getAlbumName();
            } else {
                pathImageSelected = "";
                nameImageSelected = "";
            }
        }
        customGridAdapter.notifyDataSetChanged();
    }

    private boolean checkHasSelectImage(String text) {
        if (!TextUtils.isEmpty(text)) {
            return true;
        }
        return false;
    }

    private void progressDone(String text) {
        if (checkHasSelectImage(text)) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_PATH_IMAGE, pathImageSelected);
            intent.putExtra(EXTRA_NAME_IMAGE, nameImageSelected);
            setResult(Activity.RESULT_OK, intent);
            onBackPressed();
        } else {
            showDialog(GetListImageActivity.this, getResources().getString(R.string.text_confirm_do_not_select_image), false,
                    getResources().getString(R.string.text_button_ok),
                    getResources().getString(R.string.text_button_cancel),
                    new CallBackDismiss() {
                        @Override
                        public void callBackDismiss() {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_PATH_IMAGE, pathImageSelected);
                            intent.putExtra(EXTRA_NAME_IMAGE, nameImageSelected);
                            setResult(Activity.RESULT_OK, intent);
                            onBackPressed();
                        }
                    });
        }
    }
}
