package alarmclock.app.com.alarmclock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import alarmclock.app.com.alarmclock.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 7/2/2018.
 */

public class ActivityAlarmCalculator extends BaseActivity {

    @BindView(R.id.tvRandomText)
    TextView tvRandomText;

    @BindView(R.id.editResultRetype)
    EditText editResultRetype;

    @BindView(R.id.btnCheckResult)
    Button btnChecResult;

    @BindView(R.id.imgRefreshTextRandom)
    ImageView imgRefreshTextRandom;

    @OnClick({R.id.imgRefreshTextRandom, R.id.btnCheckResult})
    public void OnButtonClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imgRefreshTextRandom:
                strResultRandom = randomText(numberRandom);
                tvRandomText.setText(strResultRandom);
                break;
            case R.id.btnCheckResult:
                String s = editResultRetype.getText().toString();
                boolean result = compareTextRandom(s);
                if (!result) {
                    Toast.makeText(this, getResources().getString(R.string.text_input_incorreect), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private int numberRandom = 5;
    private String strResultRandom = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_retype);

        ButterKnife.bind(this);

        strResultRandom = randomText(numberRandom);

        tvRandomText.setText(strResultRandom);
    }

    /**
     * compare text random
     *
     * @param s: String
     * @return: true/false
     */
    private boolean compareTextRandom(String s) {

        String strRandom = tvRandomText.getText().toString();
        if (s.equals(strRandom)) {
            return true;
        }
        return false;
    }

    /**
     * Random text with lenght
     *
     * @param numberRandom: int
     * @return: String
     */
    private String randomText(int numberRandom) {
        String result = "";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(numberRandom);
        char tempChar;
        for (int i = 0; i < numberRandom; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        result = randomStringBuilder.toString();
        return result;
    }
}
