package alarmclock.app.com.alarmclock.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;

/**
 * Created by Administrator on 5/9/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    private Context mContext;
    // Logcat Tag
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 14;

    // Database Name
    private static final String DATABASE_NAME = "AlarmClock";

    //Table Name
    private static final String TABLE_ALARM = "alarm";

    // Common column name
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "create_at";
    private static final String KEY_UPDATE_AT = "update_at";

    // Island Table - column names
    private static final String KEY_ID_ALARM = "KEY_ID_ALARM";
    private static final String KEY_HOUR = "KEY_HOUR";
    private static final String KEY_MINUTE = "KEY_MINUTE";
    private static final String KEY_FORMAT = "KEY_FORMAT";
    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_STATUS = "KEY_STATUS1";

    private static final String KEY_REPEAT_MO = "KEY_REPEAT_MO";
    private static final String KEY_REPEAT_TU = "KEY_REPEAT_TU";
    private static final String KEY_REPEAT_WE = "KEY_REPEAT_WE";
    private static final String KEY_REPEAT_TH = "KEY_REPEAT_TH";
    private static final String KEY_REPEAT_FR = "KEY_REPEAT_FR";
    private static final String KEY_REPEAT_SA = "KEY_REPEAT_SA";
    private static final String KEY_REPEAT_SU = "KEY_REPEAT_SU";

    private static final String KEY_DAY_CREATE = "KEY_DAY_CREATE";
    private static final String KEY_MONTH_CREATE = "KEY_MONTH_CREATE";
    private static final String KEY_URI_TONE = "KEY_URI_TONE";
    private static final String KEY_NAME_TONE = "KEY_NAME_TONE";
    private static final String KEY_MINISECOND = "KEY_MINISECOND";
    private static final String KEY_PATH_IMAGE = "KEY_PATH_IMAGE";
    private static final String KEY_NAME_IMAGE = "KEY_NAME_IMAGE";
    private static final String KEY_VOLUME = "KEY_VOLUME";
    private static final String KEY_NAME_CONTACT = "KEY_NAME_CONTACT";
    private static final String KEY_NUMBER_CONTACT = "KEY_NUMBER_CONTACT";

    // Island table create statement
    private static final String CREATE_island_TABLE = "CREATE TABLE " + TABLE_ALARM + "("
            + KEY_ID_ALARM + " INTEGER PRIMARY KEY,"
            + KEY_STATUS + " TEXT,"
            + KEY_HOUR + " TEXT,"
            + KEY_MINUTE + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_FORMAT + " TEXT,"
            + KEY_REPEAT_MO + " INTEGER,"
            + KEY_REPEAT_TU + " INTEGER,"
            + KEY_REPEAT_WE + " INTEGER,"
            + KEY_REPEAT_TH + " INTEGER,"
            + KEY_REPEAT_FR + " INTEGER,"
            + KEY_REPEAT_SA + " INTEGER,"
            + KEY_VOLUME + " INTEGER,"
            + KEY_REPEAT_SU + " INTEGER,"
            + KEY_DAY_CREATE + " INTEGER,"
            + KEY_MONTH_CREATE + " INTEGER,"
            + KEY_URI_TONE + " TEXT,"
            + KEY_NAME_TONE + " TEXT,"
            + KEY_MINISECOND + " TEXT,"
            + KEY_PATH_IMAGE + " TEXT,"
            + KEY_NAME_IMAGE + " TEXT,"
            + KEY_NAME_CONTACT + " TEXT,"
            + KEY_NUMBER_CONTACT + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";


    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_island_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);

        // create new tables
        onCreate(db);
    }

    //Select all islands
    public List<ItemAlarm> getAllAlarms() {
        List<ItemAlarm> alarms = new ArrayList<>();

        String alarms_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_ALARM);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(alarms_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ItemAlarm alarm = new ItemAlarm();

                    alarm.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_ALARM)));
                    alarm.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                    alarm.setHour(cursor.getString(cursor.getColumnIndex(KEY_HOUR)));
                    alarm.setMinute(cursor.getString(cursor.getColumnIndex(KEY_MINUTE)));
                    alarm.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    alarm.setFormat(cursor.getString(cursor.getColumnIndex(KEY_FORMAT)));

                    alarm.setRepeatMo(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_MO)));
                    alarm.setRepeatTu(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_TU)));
                    alarm.setRepeatWe(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_WE)));
                    alarm.setRepeatTh(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_TH)));
                    alarm.setRepeatFr(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_FR)));
                    alarm.setRepeatSa(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_SA)));
                    alarm.setRepeatSu(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_SU)));
                    alarm.setNameTone(cursor.getString(cursor.getColumnIndex(KEY_NAME_TONE)));
                    alarm.setPathImageWallPaper(cursor.getString(cursor.getColumnIndex(KEY_PATH_IMAGE)));

                    alarm.setNameContact(cursor.getString(cursor.getColumnIndex(KEY_NAME_CONTACT)));
                    alarm.setNumberContact(cursor.getString(cursor.getColumnIndex(KEY_NUMBER_CONTACT)));

                    alarm.setNameImageWallPaper(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGE)));
                    alarm.setVolume(cursor.getInt(cursor.getColumnIndex(KEY_VOLUME)));

                    String miliseconds = (cursor.getString(cursor.getColumnIndex(KEY_MINISECOND)));
                    alarm.setMilisecod(Long.parseLong(miliseconds));

                    String uri = cursor.getString(cursor.getColumnIndex(KEY_URI_TONE));
                    if (!TextUtils.isEmpty(uri)) {
                        alarm.setUriCustom(Uri.parse(uri));
                    }

                    alarm.setDayCreate(cursor.getInt(cursor.getColumnIndex(KEY_DAY_CREATE)));
                    alarm.setMonthCreate(cursor.getInt(cursor.getColumnIndex(KEY_MONTH_CREATE)));
                    alarms.add(alarm);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get islands from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return alarms;
    }

    public ItemAlarm getAlarmById(int id) {
        List<ItemAlarm> alarms = new ArrayList<>();

        String alarms_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE KEY_ID_ALARM = " + id, TABLE_ALARM);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(alarms_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ItemAlarm alarm = new ItemAlarm();

                    alarm.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_ALARM)));
                    alarm.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                    alarm.setHour(cursor.getString(cursor.getColumnIndex(KEY_HOUR)));
                    alarm.setMinute(cursor.getString(cursor.getColumnIndex(KEY_MINUTE)));
                    alarm.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    alarm.setFormat(cursor.getString(cursor.getColumnIndex(KEY_FORMAT)));

                    alarm.setRepeatMo(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_MO)));
                    alarm.setRepeatTu(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_TU)));
                    alarm.setRepeatWe(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_WE)));
                    alarm.setRepeatTh(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_TH)));
                    alarm.setRepeatFr(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_FR)));
                    alarm.setRepeatSa(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_SA)));
                    alarm.setRepeatSu(cursor.getInt(cursor.getColumnIndex(KEY_REPEAT_SU)));
                    alarm.setNameTone(cursor.getString(cursor.getColumnIndex(KEY_NAME_TONE)));
                    alarm.setPathImageWallPaper(cursor.getString(cursor.getColumnIndex(KEY_PATH_IMAGE)));


                    alarm.setNameContact(cursor.getString(cursor.getColumnIndex(KEY_NAME_CONTACT)));
                    alarm.setNumberContact(cursor.getString(cursor.getColumnIndex(KEY_NUMBER_CONTACT)));

                    alarm.setVolume(cursor.getInt(cursor.getColumnIndex(KEY_VOLUME)));
                    alarm.setNameImageWallPaper(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGE)));
                    String miliseconds = (cursor.getString(cursor.getColumnIndex(KEY_MINISECOND)));
                    alarm.setMilisecod(Long.parseLong(miliseconds));

                    String uri = cursor.getString(cursor.getColumnIndex(KEY_URI_TONE));
                    if (!TextUtils.isEmpty(uri)) {
                        alarm.setUriCustom(Uri.parse(uri));
                    }

                    alarm.setDayCreate(cursor.getInt(cursor.getColumnIndex(KEY_DAY_CREATE)));
                    alarm.setMonthCreate(cursor.getInt(cursor.getColumnIndex(KEY_MONTH_CREATE)));
                    return alarm;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get islands from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    // Insert an Island into the database
    public void addAlarm(ItemAlarm alarm) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_STATUS, alarm.getStatus());
            values.put(KEY_HOUR, alarm.getHour());
            values.put(KEY_MINUTE, alarm.getMinute());
            values.put(KEY_TITLE, alarm.getTitle());
            values.put(KEY_FORMAT, alarm.getFormat());

            values.put(KEY_REPEAT_MO, alarm.getRepeatMo());
            values.put(KEY_REPEAT_TU, alarm.getRepeatTu());
            values.put(KEY_REPEAT_WE, alarm.getRepeatWe());
            values.put(KEY_REPEAT_TH, alarm.getRepeatTh());
            values.put(KEY_REPEAT_FR, alarm.getRepeatFr());
            values.put(KEY_REPEAT_SA, alarm.getRepeatSa());
            values.put(KEY_REPEAT_SU, alarm.getRepeatSu());
            values.put(KEY_MINISECOND, String.valueOf(alarm.getMilisecod()));

            values.put(KEY_NAME_CONTACT, alarm.getNameContact());
            values.put(KEY_NUMBER_CONTACT, alarm.getNumberContact());

            values.put(KEY_DAY_CREATE, alarm.getDayCreate());
            values.put(KEY_MONTH_CREATE, alarm.getMonthCreate());

            values.put(KEY_NAME_IMAGE, alarm.getNameImageWallPaper());
            values.put(KEY_PATH_IMAGE, alarm.getPathImageWallPaper());
            values.put(KEY_VOLUME, alarm.getVolume());

            String nameTone = alarm.getNameTone();
            values.put(KEY_NAME_TONE, nameTone);
            if (!nameTone.toLowerCase().equals(mContext.getResources().getString(R.string.text_none).toLowerCase())) {
                values.put(KEY_URI_TONE, alarm.getUriCustom().toString());
            } else {
                values.put(KEY_URI_TONE, "");
            }

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_ALARM, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add island to database");
        } finally {
            db.endTransaction();
        }
    }


    public void addListAlarm(List<ItemAlarm> itemAlarms) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        //db.beginTransaction();

        try {
            for (ItemAlarm alarm : itemAlarms) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID_ALARM, alarm.getId());
                values.put(KEY_HOUR, alarm.getHour());
                values.put(KEY_STATUS, alarm.getStatus());
                values.put(KEY_MINUTE, alarm.getMinute());
                values.put(KEY_TITLE, alarm.getTitle());
                values.put(KEY_FORMAT, alarm.getFormat());

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_ALARM, null, values);

            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add island to database");
        } finally {
            // db.setTransactionSuccessful();
            //db.endTransaction();
        }
    }

    // Insert or update a island in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    public long addOrUpdateAlarm(ItemAlarm alarm) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long islandId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_STATUS, alarm.getStatus());
            values.put(KEY_HOUR, alarm.getHour());
            values.put(KEY_MINUTE, alarm.getMinute());
            values.put(KEY_TITLE, alarm.getTitle());
            values.put(KEY_FORMAT, alarm.getFormat());

            values.put(KEY_REPEAT_MO, alarm.getRepeatMo());
            values.put(KEY_REPEAT_TU, alarm.getRepeatTu());
            values.put(KEY_REPEAT_WE, alarm.getRepeatWe());
            values.put(KEY_REPEAT_TH, alarm.getRepeatTh());
            values.put(KEY_REPEAT_FR, alarm.getRepeatFr());
            values.put(KEY_REPEAT_SA, alarm.getRepeatSa());
            values.put(KEY_REPEAT_SU, alarm.getRepeatSu());


            values.put(KEY_NAME_CONTACT, alarm.getNameContact());
            values.put(KEY_NUMBER_CONTACT, alarm.getNumberContact());

            values.put(KEY_DAY_CREATE, alarm.getDayCreate());
            values.put(KEY_MONTH_CREATE, alarm.getMonthCreate());
            values.put(KEY_MINISECOND, String.valueOf(alarm.getMilisecod()));
            values.put(KEY_VOLUME, alarm.getVolume());
            values.put(KEY_NAME_IMAGE, alarm.getNameImageWallPaper());
            values.put(KEY_PATH_IMAGE, alarm.getPathImageWallPaper());

            String nameTone = alarm.getNameTone();
            values.put(KEY_NAME_TONE, nameTone);
            if (!nameTone.toLowerCase().equals(mContext.getResources().getString(R.string.text_none).toLowerCase())) {
                values.put(KEY_URI_TONE, alarm.getUriCustom().toString());
            } else {
                values.put(KEY_URI_TONE, "");
            }


            // First try to update the island in case the island already exists in the database
            // This assumes islandNames are unique
            islandId = db.update(TABLE_ALARM, values, KEY_ID_ALARM + "=" + alarm.getId(), null);
            db.setTransactionSuccessful();
            // Check if update succeeded

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return islandId;
    }


    //Delete all island
    public void deleteAllAlarm() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ALARM, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all islands");
        } finally {
            db.endTransaction();
        }
    }

    public Integer deleteAlarmById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ALARM,
                KEY_ID_ALARM + " = ? ",
                new String[]{id + ""});
    }

}
