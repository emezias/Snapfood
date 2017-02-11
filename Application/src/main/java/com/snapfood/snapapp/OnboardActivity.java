package com.snapfood.snapapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

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
    EditText mCardNumber, mName, mDate, mPin;
    SharedPreferences mPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        mStatusMessage = (TextView)findViewById(R.id.status);
        mCardNumber = (EditText) findViewById(R.id.card);
        mName = (EditText) findViewById(R.id.name);
        mDate = (EditText) findViewById(R.id.date);
        mPin = (EditText) findViewById(R.id.pin);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CardIOActivity.canReadCardWithCamera()) {
            mStatusMessage.setText(R.string.intro_message);
            findViewById(R.id.scanButton).setEnabled(true);
        } else {
            mStatusMessage.setText(R.string.intro2_message);
            findViewById(R.id.scanButton).setEnabled(false);
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mPreferences.contains(CARD_NUMBER)) {
            mCardNumber.setText(mPreferences.getString(CARD_NUMBER, ""));
        }
        if (mPreferences.contains(NAME)) {
            mName.setText(mPreferences.getString(NAME, ""));
        }
        if (mPreferences.contains(B_DATE)) {
            mDate.setText(mPreferences.getString(B_DATE, ""));
        }
        if (mPreferences.contains(PIN)) {
            mPin.setText(mPreferences.getString(PIN, ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.save_action:
                // create intent to perform web search for this planet
                saveData(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_ACTIVITY) {
            //handle with and without data returned from the scan
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                String resultStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                ed.putString(OnboardActivity.CARD_NUMBER, scanResult.getRedactedCardNumber());
                /*if (scanResult.isExpiryValid()) {
                    resultStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }*/

                if (scanResult.cardholderName != null) {
                    resultStr += "Cardholder Name : " + scanResult.cardholderName + "\n";
                    ed.putString(OnboardActivity.NAME, scanResult.cardholderName);
                }
                ed.apply();
                mStatusMessage.setText(resultStr);
            } else {
                mStatusMessage.setText(getString(R.string.cancel_scan));
            }
        }
    }

    public void scanCard(View btn) {
        //set in xml to launch OCR activity
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        /*scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false); // default: false*/
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false); // default: false

        // hides the manual entry button
        // if set, developers should provide their own manual entry mechanism in the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

        // matches the theme of your application
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, SCAN_ACTIVITY);
    }

    public void saveCardData( ) {
        startActivity(new Intent(this, MenuActivity.class));
        //TODO error check values in fields
        final SharedPreferences.Editor ed = mPreferences.edit();
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

    public void saveData(View btn) {
        //set in xml to move to next
        //new ConfirmID().show(getSupportFragmentManager(), ConfirmID.TAG);
        startActivity(new Intent(this, MenuActivity.class));
    }
}
