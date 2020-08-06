package com.snipertech.hopinn.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.ActivityMainBinding;
import com.snipertech.hopinn.util.ApplicationLanguageHelper;

import static com.snipertech.hopinn.util.Constants.SHARED_LANG;
import static com.snipertech.hopinn.util.Constants.SHARED_PREF;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user != null){
            startActivity(new Intent(this, MainScreenActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user = null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        ApplicationLanguageHelper applicationLanguageHelper = new ApplicationLanguageHelper();
        SharedPreferences sharedPreferences = newBase.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if(sharedPreferences != null) {
            super.attachBaseContext(
                    applicationLanguageHelper.setAppLocale(
                            newBase, sharedPreferences.getString(SHARED_LANG, "")
                    )
            );
        } else {
            super.attachBaseContext(newBase);
        }
    }
}
