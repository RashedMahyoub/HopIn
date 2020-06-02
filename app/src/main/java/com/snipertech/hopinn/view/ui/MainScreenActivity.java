package com.snipertech.hopinn.view.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.ActivityMainScreenBinding;
import com.snipertech.hopinn.view.adapter.SectionsPagerAdapter;

public class MainScreenActivity extends AppCompatActivity {

    ActivityMainScreenBinding mainScreenBinding;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainScreenBinding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        View view = mainScreenBinding.getRoot();
        setContentView(view);
        currentUser();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = mainScreenBinding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = mainScreenBinding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = mainScreenBinding.fab;

        //get the toolbar
        MaterialToolbar toolbar = mainScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        //go to add request activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                //go to the add request activity
                startActivity(new Intent(
                        MainScreenActivity.this,
                        AddRequestActivity.class
                ));
            }
        });
    }

    //get the current user
    private void currentUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
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
        if (item.getItemId() == R.id.log_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
        super.onBackPressed();
        return;
    }
}