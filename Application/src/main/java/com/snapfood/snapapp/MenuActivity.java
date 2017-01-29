package com.snapfood.snapapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/**
 * This activity hosts the fragments that are available after onboarding is completed
 * Begin with the Account Details
 * Also host Confirm ID scan and Confirm ID result
 */
public class MenuActivity extends AppCompatActivity implements MenuAdapter.OnItemClickListener {
    public static final String TAG = "MenuActivity";
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    public ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setMenu();
        mTitle = mDrawerTitle = getTitle();
    }

    public void setMenu() {
        menuListArray = getResources().getStringArray(R.array.menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MenuAdapter(menuListArray, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

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
    protected void onResume() {
        super.onResume();
        final FragmentManager mgr = getSupportFragmentManager();
        final FragmentTransaction tx = mgr.beginTransaction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shop_action, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.shop_action).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments, as needed
        mDrawerLayout.closeDrawer(mDrawerList);
        switch (position) {
            case 0:
                //set card data
                if (!TAG.equals(OnboardActivity.TAG))
                startActivity(new Intent(this, OnboardActivity.class));
                finish();
                return;
            case 1:
                //check id
                Toast.makeText(this, "Check ID", Toast.LENGTH_SHORT).show();
                if (!TAG.equals(CheckIDActivity.TAG)) {
                    //skip, fragment is loaded
                    startActivity(new Intent(this, CheckIDActivity.class));
                    finish();
                }
                return;
            case 2:
                //balances
                Toast.makeText(this, "To do", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "To do", Toast.LENGTH_SHORT).show();
                //shopping, map TODO
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

}
