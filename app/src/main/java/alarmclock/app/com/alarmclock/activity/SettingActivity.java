package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.UserSetting;
import alarmclock.app.com.alarmclock.util.Constant;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static alarmclock.app.com.alarmclock.util.SharePreferenceHelper.Key.KEY_PURCHASE;

/**
 * Created by Administrator on 5/28/2018.
 */

public class SettingActivity extends BaseActivity {

    private final static String tag = SettingActivity.class.getSimpleName();
    @BindView(R.id.spinnerNumberShake)
    Spinner spinnerNumberShake;

    @BindView(R.id.spinnerSpeekShake)
    Spinner spinnerSpeekShake;


    @BindView(R.id.layoutMain)
    View layoutMain;

    @BindView(R.id.layoutConsume)
    View layoutConsume;

    @BindView(R.id.tvPurchaseApp)
    TextView tvPurchaseApp;

    @BindView(R.id.tvRemovePurchase)
    TextView tvRemovePurchase;

    @BindView(R.id.tvCurrentVersion)
    TextView tvCurrentVersion;

    @BindView(R.id.imgStatusPurchaseApp)
    ImageView imgStatusPurchaseApp;

    @BindView(R.id.cbShowNotification)
    CheckBox cbShowNotification;

    @OnClick({R.id.tvPurchaseApp, R.id.tvRemovePurchase})
    public void OnButtonClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvPurchaseApp:
                if (checkServiceSupportIab()) {
                    if (!checkAppPurchased()) {
                        callFunctionBuy();
                    } else {
                        Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_status_purchased), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_you_need_login_gmail), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.tvRemovePurchase:
                callFunctionConsume();
                sharedPreferences.put(KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
                upDateStatusPurchase();
                break;
        }
    }

    private SharePreferenceHelper sharedPreferences;
    private boolean isFirstLoad = true;
    private boolean blnBind = false;
    private IInAppBillingService mService;
    private final static int VERSION_IAB = 3;
    private String deviceToken = "";
    private final String productID = "com.app.alarmclock.bhloc";
    private UserSetting mUserSetting;


    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharePreferences();

        setTitle(getResources().getString(R.string.text_title_setting));

        deviceToken = getSharePreferences().getString(SharePreferenceHelper.Key.KEY_TOKEN, "");
        mUserSetting = (UserSetting) getSharePreferences().getObject(SharePreferenceHelper.Key.KEY_USER_SETTING, UserSetting.class);
        initDataNumberShake();
        initDataSpeedShake();
        initDataCheckBoxShowNotification();
        initActionTextViewPurchase();
        setDataCurrentVersion();
    }

    private void setDataCurrentVersion() {
        tvCurrentVersion.setText(getVersionName());
    }

    @SuppressLint("ResourceType")
    private void initActionTextViewPurchase() {
        tvPurchaseApp.setTextColor(getResources().getColorStateList(R.drawable.selector_button));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Init spinner number shake
     */
    private void initDataNumberShake() {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i + 1);
        }

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numbers);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerNumberShake.setAdapter(dataAdapter);
        spinnerNumberShake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int number = i;
                if (mUserSetting == null) {
                    mUserSetting = new UserSetting();
                }
                mUserSetting.setNumberShake(number);
                sharedPreferences.putObject(SharePreferenceHelper.Key.KEY_USER_SETTING, mUserSetting);
                if (!isFirstLoad) {
                    Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_updated), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // int selected = sharedPreferences.getInt(SharePreferenceHelper.Key.NUMBERSHAKE, 0);
        int selected = mUserSetting != null ? mUserSetting.getNumberShake() : 0;
        spinnerNumberShake.setSelection(selected);
    }

    /**
     * Init data speed shake
     */
    private void initDataSpeedShake() {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            numbers.add(1000 + (i * 100));
        }

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numbers);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerSpeekShake.setAdapter(dataAdapter);
        spinnerSpeekShake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int number = i;
                if (mUserSetting == null) {
                    mUserSetting = new UserSetting();
                }
                mUserSetting.setSpeedShake(number);
                sharedPreferences.putObject(SharePreferenceHelper.Key.KEY_USER_SETTING, mUserSetting);
                if (!isFirstLoad) {
                    Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_updated), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                isFirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        int selected = mUserSetting != null ? mUserSetting.getSpeedShake() : 0;
        spinnerSpeekShake.setSelection(selected);
    }

    private void initDataCheckBoxShowNotification() {
        int selected = mUserSetting != null ? mUserSetting.getShowNotification() : 1;// default 'SHOW'
        cbShowNotification.setChecked(selected == 1 ? true : false);
        cbShowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int number = b ? 1 : 0;
                if (mUserSetting == null) {
                    mUserSetting = new UserSetting();
                }
                mUserSetting.setShowNotification(number);
                sharedPreferences.putObject(SharePreferenceHelper.Key.KEY_USER_SETTING, mUserSetting);
                if (!isFirstLoad) {
                    Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_updated), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                isFirstLoad = false;
            }
        });

    }

    /**
     * Function check server has support IAB
     *
     * @return true/ false
     */
    private boolean checkServiceSupportIab() {

        if (mService == null) {
            return false;
        }
        int response = 1;
        try {
            response = mService.isBillingSupported(VERSION_IAB, getPackageName(), "inapp");
            Log.d("response", response + "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (response > 0) {
            return false;
        }
        return true;
    }

    /**
     * Function check app purchased
     *
     * @return true/ false
     */
    private boolean checkAppPurchased() {

        Bundle ownedItems;
        try {
            ownedItems = mService.getPurchases(VERSION_IAB, getPackageName(), "inapp", deviceToken);
        } catch (RemoteException e) {
            return false;
        }

        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response != 0) return false;

        ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
        ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
        String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

        for (String sku : ownedSkus) {
            if (sku.equals(productID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function buy app
     */
    private void callFunctionBuy() {
        if (!blnBind) return;
        if (mService == null) return;

        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(productID);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        Bundle skuDetails;
        try {
            skuDetails = mService.getSkuDetails(VERSION_IAB, getPackageName(), "inapp", querySkus);
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }

        int response = skuDetails.getInt("RESPONSE_CODE");
        if (response != 0) return;

        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
        Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\" return " + responseList.toString());

        if (responseList.size() == 0) return;

        for (String thisResponse : responseList) {
            try {
                JSONObject object = new JSONObject(thisResponse);

                String sku = object.getString("productId");
                String title = object.getString("title");
                String price = object.getString("price");

                Log.d(tag, "getSkuDetails() - \"DETAILS_LIST\":\"productId\" return " + sku);
                Log.d(tag, "getSkuDetails() - \"DETAILS_LIST\":\"title\" return " + title);
                Log.d(tag, "getSkuDetails() - \"DETAILS_LIST\":\"price\" return " + price);

                if (!sku.equals(productID)) continue;

                Bundle buyIntentBundle = mService.getBuyIntent(VERSION_IAB, getPackageName(), sku, "inapp", deviceToken);

                response = buyIntentBundle.getInt("RESPONSE_CODE");

                if (response != 0) continue;

                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                //int requestCode = RequestCodeConstants.REQUEST_CODE_PURCHASE.ordinal();
//                if (callFrom == FROM_RESTORE) {
//                    requestCode = RequestCodeConstants.REQUEST_CODE_RESTORE.ordinal();
//                }
                try {
                    startIntentSenderForResult(pendingIntent.getIntentSender(), Constant.REQUEST_CODE_PURCHASE, new Intent(), 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Remove purchases
     */
    private void callFunctionConsume() {
        if (!blnBind) return;
        if (mService == null) return;

        int response;
        Bundle ownedItems;
        try {
            ownedItems = mService.getPurchases(VERSION_IAB, getPackageName(), "inapp", deviceToken);
            response = mService.consumePurchase(VERSION_IAB, getPackageName(), null);

            //Toast.makeText(SettingActivity.this, "consumePurchase() - success : return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
            Log.i(tag, "consumePurchase() - success : return " + String.valueOf(response));
        } catch (RemoteException e) {
            e.printStackTrace();

            Toast.makeText(SettingActivity.this, "consumePurchase() - fail!", Toast.LENGTH_SHORT).show();
            Log.w(tag, "consumePurchase() - fail!");
            return;
        }

        int responseCode = ownedItems.getInt("RESPONSE_CODE");
        //  if (response != 0) return;
        ArrayList<String> purchaseDataList =
                ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        if (purchaseDataList == null) {
            return;
        }
        for (String purchaseData : purchaseDataList) {
            JSONObject o = null;
            try {
                o = new JSONObject(purchaseData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String purchaseToken = o.optString("token", o.optString("purchaseToken"));
            // Consume purchaseToken, handling any errors
            try {
                mService.consumePurchase(3, SettingActivity.this.getPackageName(), purchaseToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void upDateStatusPurchase() {
        int result = sharedPreferences.getInt(KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
        int resId = R.drawable.ic_status_do_not_purchase;
        layoutConsume.setVisibility(View.GONE);
        if (result == Constant.PURCHASED) {
            resId = R.drawable.ic_status_purchased;
            layoutConsume.setVisibility(View.VISIBLE);
        }
        imgStatusPurchaseApp.setBackground(null);
        imgStatusPurchaseApp.setBackgroundResource(resId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        blnBind = bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
        upDateStatusPurchase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.REQUEST_CODE_PURCHASE == requestCode) {
            String msg = getResources().getString(R.string.text_purchase_success);
            if (RESULT_OK == resultCode) {
                msg = getResources().getString(R.string.text_purchase_success);
                sharedPreferences.put(KEY_PURCHASE, Constant.PURCHASED);
            } else {
                msg = getResources().getString(R.string.text_purchase_faild);
                sharedPreferences.put(KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
            }
            upDateStatusPurchase();
            Snackbar snackbar = Snackbar.make(layoutMain, msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
