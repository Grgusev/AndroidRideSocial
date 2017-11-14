package com.pjtech.android.ridesocial.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.firebase.FirebaseManager;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.ui.dialog.MyProgressDialog;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;
import com.pjtech.android.ridesocial.ui.fragments.RideHistoryFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView imgProfile;
    private Toolbar toolbar;

    public static HomeActivity mInstance = null;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_RIDEHISTORY = "photos";

    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    protected void setStatusBarTranslucent(boolean makeTranslucent) {

        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void startLogoActivity()
    {
        Intent intent = new Intent(HomeActivity.this, LogoActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mInstance = this;

        FirebaseManager.loginInAnonymously(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                GlobalData.mFirebaseManager = new FirebaseManager(HomeActivity.this);
            }
        });

        if (GlobalData.userInfo == null)
        {
            startLogoActivity();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //actionbar translucent
        setStatusBarTranslucent(true);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (CircleImageView) navHeader.findViewById(R.id.userimage);

        Picasso.with(this).load(ApiClient.BASE_URL + GlobalData.userInfo.photoUrl)
                .placeholder(R.drawable.profile).fit().centerCrop().into(imgNavHeaderBg);

        imgProfile = (TextView) navHeader.findViewById(R.id.username);
        imgProfile.setText(GlobalData.userInfo.userName);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void loadNavHeader() {

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (CURRENT_TAG.equals(TAG_HOME)) return;

            if (shouldLoadHomeFragOnBackPress) {
                // checking if user is on other navigation menu
                // rather than home
                if (navItemIndex != 0) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment();
                    return;
                }
            }

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                RideHistoryFragment rideFragment = new RideHistoryFragment();
                return rideFragment;
            default:
                return new HomeFragment();
        }
    }

    public void setToolbarTitleText(String titleText)
    {
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(titleText);
    }

    private void setToolbarTitle() {
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;

        } else if (id == R.id.nav_ride_history) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_RIDEHISTORY;
        } else if (id == R.id.nav_payment_setup) {
            Intent intent = new Intent(HomeActivity.this, PaymentUserSetupActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_add_cab_service) {
            Intent intent = new Intent(HomeActivity.this, UserCabServiceActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_link_with_facebook) {
            Intent intent = new Intent(HomeActivity.this, UserFaceBookActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_invite_friends) {
            Intent intent = new Intent(HomeActivity.this, InviteFriendActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
            startActivity(intent);
            return false;
        }

        //Checking if the item is in checked state or not, if not make it in checked state
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);

        drawer.closeDrawer(GravityCompat.START);

        loadHomeFragment();

        return true;
    }
}
