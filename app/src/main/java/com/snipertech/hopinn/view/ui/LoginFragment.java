package com.snipertech.hopinn.view.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private LoginFragmentBinding loginFragmentBinding;
    private NavController navController;
    private FirebaseAuth auth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loginFragmentBinding = LoginFragmentBinding.inflate(inflater, container, false);
        return loginFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();
        loginFragmentBinding.login.setOnClickListener(this);
        loginFragmentBinding.goToRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String email = loginFragmentBinding.email.getText().toString();
                String password = loginFragmentBinding.password.getText().toString();
                login(email, password);
                break;
            case R.id.go_to_register:
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;
        }
    }

    //login
    private void login(String email, String password) {
        if (email.equals("")
                || password.equals("")) {
            Toast.makeText(requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            loginFragmentBinding.loginProgress.setVisibility(View.VISIBLE);
            auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            loginFragmentBinding.loginProgress.setVisibility(View.GONE);
                            //Check if email is verified!
                            if (auth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(requireActivity(),
                                        MainScreenActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            }else {
                                Toast.makeText(requireContext(),
                                        "Please verify you're email first",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            loginFragmentBinding.loginProgress.setVisibility(View.GONE);
                            Toast.makeText(requireContext(),
                                    "Authentication failed!, please try again",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });


        }
    }
}
