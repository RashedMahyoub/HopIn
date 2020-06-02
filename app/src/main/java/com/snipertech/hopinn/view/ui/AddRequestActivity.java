package com.snipertech.hopinn.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.databinding.ActivityAddRequestBinding;
import com.snipertech.hopinn.repository.BackgroundTask;

public class AddRequestActivity extends AppCompatActivity {
    private ActivityAddRequestBinding binding;
    private FirebaseUser user;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addRequest();
            }
        });

        setContentView(view);
    }

    //go to Main Screen
    public void goToMain(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
    }

    //add request to database
    public void addRequest(){
        String message = binding.messageEditText.getText().toString();
        String name = binding.inputName.getText().toString();
        String userId = user.getUid();
        if(name.matches("")) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter Name please ",
                    Toast.LENGTH_LONG
            ).show();
        } else if(message.matches("")) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter a message please ",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            new BackgroundTask(AddRequestActivity.this).execute(name, message, userId);
            finish();
            goToMain();
            Toast.makeText(
                    getApplicationContext(),
                    "Successfully added ",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
