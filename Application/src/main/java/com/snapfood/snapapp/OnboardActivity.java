package com.snapfood.snapapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.snapfood.snapapp.fragment.ConfirmID;
import com.snapfood.snapapp.ocr.OcrCaptureActivity;

/**
 * Install/Setup activity
 */
public class OnboardActivity extends AppCompatActivity implements MenuAdapter.OnItemClickListener{
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

    //ripped from MenuActivity
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuTitle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        mStatusMessage = (TextView)findViewById(R.id.status);
        mCardNumber = (EditText) findViewById(R.id.card);
        mName = (EditText) findViewById(R.id.name);
        mDate = (EditText) findViewById(R.id.date);
        mPin = (EditText) findViewById(R.id.pin);

        //ripped from MenuActivity
        mTitle = mDrawerTitle = getString(R.string.app_name);
        menuTitle = getResources().getStringArray(R.array.menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MenuAdapter(menuTitle, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /* When using the ActionBarDrawerToggle, you must call it during
    * onPostCreate() and onConfigurationChanged()... */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_action, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.save_action).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        super.onBackPressed();
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
        new ConfirmID().show(getSupportFragmentManager(), ConfirmID.TAG);
    }

    @Override
    public void onClick(View view, int position) {
        //TODO
        // update the main content by replacing fragments
        Fragment fragment = MenuActivity.PlanetFragment.newInstance(position);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(menuTitle[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
