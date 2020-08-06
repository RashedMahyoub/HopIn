package com.snipertech.hopinn.view.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.LoginFragmentBinding;
import com.snipertech.hopinn.viewModel.AuthViewModel;

import static com.snipertech.hopinn.util.Constants.EMAIL_NOT_VERIFIED;
import static com.snipertech.hopinn.util.Constants.EMAIL_VERIFIED;
import static com.snipertech.hopinn.util.Constants.USER_NOT_FOUND;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements View.OnClickListener {
    private AuthViewModel viewModel;
    private LoginFragmentBinding loginFragmentBinding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loginFragmentBinding = LoginFragmentBinding.inflate(inflater, container, false);
        return loginFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        navController = Navigation.findNavController(view);

        loginFragmentBinding.login.setOnClickListener(this);
        loginFragmentBinding.goToRegister.setOnClickListener(this);
        loginFragmentBinding.restPassword.setOnClickListener(this);

        registerObserver();
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
            case R.id.rest_password:
                navController.navigate(R.id.action_loginFragment_to_changePasswordFragment);
                break;
        }
    }

    //login
    private void login(String email, String password) {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(requireContext(),
                    getResources().getString(R.string.fill_in_all_fields),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            loginFragmentBinding.loginProgress.setVisibility(View.VISIBLE);
            loginFragmentBinding.login.setVisibility(View.GONE);
            viewModel.loginUser(email, password);
        }
    }


    //Observe for success or not
    private void registerObserver() {
        viewModel.loginLiveData().observe(getViewLifecycleOwner(), isSuccess -> {
            loginFragmentBinding.loginProgress.setVisibility(View.GONE);
            loginFragmentBinding.login.setVisibility(View.VISIBLE);
            switch (isSuccess) {
                case EMAIL_VERIFIED:
                    Intent intent = new Intent(requireActivity(),
                            MainScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    break;
                case EMAIL_NOT_VERIFIED:
                    Toast.makeText(requireContext(),
                            getResources().getString(R.string.email_not_verified),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                case USER_NOT_FOUND:
                    Toast.makeText(requireContext(),
                            getResources().getString(R.string.user_not_found),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
        });
    }
}
