

package com.snapfood.snapapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.snapfood.snapapp.ocr.OcrCaptureActivity;

/**
 * Install/Setup activity
 */
public class OnboardActivity extends MenuActivity {
    public static final String TAG = "OnboardActivity";
    public static final int SCAN_ACTIVITY = 9;
    //Preference constants
    public static final String NAME = "name";
    public static final String CARD_NUMBER = "number";
    public static final String B_DATE = "date";
    public static final String PIN = "pin";

    TextView mStatusMessage;
    EditText mCardNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        mStatusMessage = (TextView)findViewById(R.id.status);
        mCardNumber = (EditText) findViewById(R.id.card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).contains(CARD_NUMBER)) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_ACTIVITY) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    mStatusMessage.setText(R.string.ocr_success);
                    mCardNumber.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    mStatusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                mStatusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
    }

    public void scanCard(View btn) {
        //set in xml to launch OCR activity
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, true);
        //TODO add as options in to layout
        startActivityForResult(intent, SCAN_ACTIVITY);
    }

    public void saveData(View btn) {
        //set in xml to move to next
        startActivity(new Intent(this, MenuActivity.class));
        //TODO error check values in fields
        final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        String tmp = mCardNumber.getText().toString();
        if (!TextUtils.isEmpty(tmp)) {
            ed.putString(CARD_NUMBER, tmp);
        }

        tmp = ((TextView)findViewById(R.id.name)).getText().toString();
        if (!TextUtils.isEmpty(tmp)) {
            ed.putString(NAME, tmp);
        }

        tmp = ((TextView)findViewById(R.id.date)).getText().toString();
        if (!TextUtils.isEmpty(tmp)) {
            ed.putString(B_DATE, tmp);
        }

        tmp = ((TextView)findViewById(R.id.pin)).getText().toString();
        if (!TextUtils.isEmpty(tmp)) {
            ed.putString(PIN, tmp);
        }
        ed.apply();
        finish();
    }

}
