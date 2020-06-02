package com.snipertech.hopinn.view.ui;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.RegisterFragmentBinding;

import java.util.HashMap;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private RegisterFragmentBinding fragmentBinding;
    private NavController navController;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentBinding = RegisterFragmentBinding.inflate(inflater, container, false);
        return fragmentBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        fragmentBinding.register.setOnClickListener(this);
        fragmentBinding.goToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                String name = fragmentBinding.username.getText().toString();
                String email = fragmentBinding.email.getText().toString();
                String password = fragmentBinding.password.getText().toString();
                register(name, email, password);
                break;
            case R.id.go_to_login:
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
                break;
        }
    }

    //register a new user
    private void register(String name, String email, String password) {
        if (name.equals("") || email.equals("") || password.equals("")) {
            Toast.makeText(requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (password.length() < 7) {
            Toast.makeText(requireContext(),
                    "Password must be more than 6 letters",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            fragmentBinding.registerProgress.setVisibility(View.VISIBLE);
            auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                    .Builder().setDisplayName(name).build();
                            user.updateProfile(profileUpdates);


                            databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", name);

                            databaseReference.setValue(hashMap).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                fragmentBinding.registerProgress.setVisibility(View.GONE);
                                                user.sendEmailVerification().addOnCompleteListener(task1 ->
                                                        Toast.makeText(requireContext(),
                                                                "A verification email is sent!",
                                                                Toast.LENGTH_SHORT
                                                        ).show());
                                            }
                                        }
                                    });
                        } else {
                            fragmentBinding.registerProgress.setVisibility(View.GONE);
                            Toast.makeText(requireContext(),
                                    "You can't register with this email or password",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        }

    }
}
