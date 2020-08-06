package com.snipertech.hopinn.view.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.ActivityMainScreenBinding;
import com.snipertech.hopinn.util.ApplicationLanguageHelper;
import com.snipertech.hopinn.view.adapter.SectionsPagerAdapter;

import static com.snipertech.hopinn.util.Constants.SHARED_LANG;
import static com.snipertech.hopinn.util.Constants.SHARED_PREF;

@AndroidEntryPoint
public class MainScreenActivity extends AppCompatActivity {

    ActivityMainScreenBinding mainScreenBinding;
    private long mLastClickTime = 0;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainScreenBinding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        View view = mainScreenBinding.getRoot();
        currentUser();
        setUpPageAdapter();
        fabFunction();

        //get the toolbar
        MaterialToolbar toolbar = mainScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(view);
        MobileAds.initialize(this, initializationStatus -> { });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    //Floating action button function
    private void fabFunction() {
        //go to add request activity
        mainScreenBinding.fab.setOnClickListener(view1 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            //go to the add request activity
            startActivity(new Intent(
                    MainScreenActivity.this,
                    AddRequestActivity.class
            ));
        });
    }

    //setup Page Adapter
    private void setUpPageAdapter() {
        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = mainScreenBinding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = mainScreenBinding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

    //get the current user
    private void currentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
        assert firebaseUser != null;
        editor.putString("currentUser", firebaseUser.getUid());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        });
                break;
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (mLastClickTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            finish();
            moveTaskToBack(true);
            super.onBackPressed();

        } else {
            backToast =
                    Toast.makeText(
                            getBaseContext(),
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                    );
            backToast.show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        ApplicationLanguageHelper applicationLanguageHelper = new ApplicationLanguageHelper();
        SharedPreferences sharedPreferences = base.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if(sharedPreferences != null) {
            super.attachBaseContext(
                    applicationLanguageHelper.setAppLocale(
                            base, sharedPreferences.getString(SHARED_LANG, "")
                    )
            );
        } else {
            super.attachBaseContext(base);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backToast = null;
        mainScreenBinding = null;
    }
}