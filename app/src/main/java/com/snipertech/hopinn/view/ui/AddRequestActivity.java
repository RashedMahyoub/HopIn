package com.snipertech.hopinn.view.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.ActivityAddRequestBinding;
import com.snipertech.hopinn.util.ApplicationLanguageHelper;
import com.snipertech.hopinn.viewModel.AddRequestActivityViewModel;

import static com.snipertech.hopinn.util.Constants.LAUNCH_SECOND_ACTIVITY;
import static com.snipertech.hopinn.util.Constants.REGISTERED_LOCATION;
import static com.snipertech.hopinn.util.Constants.SHARED_LANG;
import static com.snipertech.hopinn.util.Constants.SHARED_PREF;

@AndroidEntryPoint
public class AddRequestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddRequestBinding binding;
    private AddRequestActivityViewModel viewModel;
    private FirebaseUser user;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        //initialize view model
        viewModel = new ViewModelProvider(this).get(AddRequestActivityViewModel.class);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //set on click listeners
        binding.confirmRequest.setOnClickListener(this);
        binding.location.setOnClickListener(this);

        //Observe for changes
        registerObservers();

        //get the toolbar
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_request:
                addRequest();
                break;
            case R.id.location:
                goToMaps();
                break;
        }
    }

    //go to Main Screen
    public void goToMain() {
        Intent intent = new Intent(AddRequestActivity.this, MainScreenActivity.class);
        startActivity(intent);
        finish();
    }

    //go to maps activity to choose location
    public void goToMaps() {
        Intent mIntent = new Intent(AddRequestActivity.this, GoogleMapActivity.class);
        startActivityForResult(mIntent, LAUNCH_SECOND_ACTIVITY);
    }

    //add request to database
    public void addRequest() {
        String message = binding.messageEditText.getText().toString();
        String city = binding.location.getText().toString();
        if (message.matches("")) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.enter_message),
                    Toast.LENGTH_LONG
            ).show();
        } else if (city.matches(getResources().getString(R.string.press_to_choose_the_city))) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.choose_city),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            viewModel.addRequest(user.getDisplayName(), message, user.getUid(), city);
            binding.confirmRequest.setVisibility(View.INVISIBLE);
            binding.requestProgress.setVisibility(View.VISIBLE);
        }
    }

    //Observe for data changes
    private void registerObservers() {
        viewModel.isSuccessful().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.request_successfully_added),
                        Toast.LENGTH_LONG
                ).show();
                goToMain();
            } else {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null) {
                        city = data.getStringExtra(REGISTERED_LOCATION);
                        binding.location.setText(city);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(
                            this,
                            getResources().getString(R.string.no_location),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
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
        binding = null;
        city = null;
        user = null;
        viewModel = null;
    }
}
