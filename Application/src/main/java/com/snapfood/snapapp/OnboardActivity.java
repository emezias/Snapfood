

package com.snapfood.snapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.snapfood.navigationdrawer.R;
import com.snapfood.snapapp.ocr.OcrCaptureActivity;

/**
 * Install/Setup activity
 */
public class OnboardActivity extends Activity {
    public static final String TAG = "OnboardActivity";
    public static final int SCAN_ACTIVITY = 9;
    TextView mStatusMessage;
    EditText mCardNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        mStatusMessage = (TextView)findViewById(R.id.status);
        mCardNumber = (EditText) findViewById(R.id.card);
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
}
