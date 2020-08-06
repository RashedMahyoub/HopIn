package com.snipertech.hopinn.view.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.RegisterFragmentBinding;
import com.snipertech.hopinn.viewModel.AuthViewModel;
import java.util.regex.Pattern;

@AndroidEntryPoint
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private AuthViewModel viewModel;
    private RegisterFragmentBinding fragmentBinding;
    private NavController navController;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentBinding = RegisterFragmentBinding.inflate(inflater, container, false);
        return fragmentBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        navController = Navigation.findNavController(view);

        fragmentBinding.register.setOnClickListener(this);
        fragmentBinding.goToLogin.setOnClickListener(this);
        textInputWatchers();
        registerObserver();
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
                    getResources().getString(R.string.fill_in_all_fields),
                    Toast.LENGTH_SHORT
            ).show();
        } else if (password.length() < 7) {
            Toast.makeText(requireContext(),
                    getResources().getString(R.string.password_too_short),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            fragmentBinding.registerProgress.setVisibility(View.VISIBLE);
            fragmentBinding.register.setVisibility(View.GONE);
            viewModel.registerUser(name, email, password);
        }
    }

    //Add text watchers for email and password validations
    private void textInputWatchers(){
        fragmentBinding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidEmailId(editable.toString())){
                    fragmentBinding.emailContainer.setError(null);
                }else {
                    fragmentBinding.emailContainer.setError(
                            getResources().getString(R.string.email_not_right)
                    );
                }
            }
        });

        fragmentBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() < 7) {
                    fragmentBinding.passwordContainer.setError(
                            getResources().getString(R.string.password_too_short)
                    );
                } else {
                    fragmentBinding.passwordContainer.setError(null);
                }
            }
        });
    }

    //Observe for data changes
    private void registerObserver() {
        viewModel.successLiveData().observe(getViewLifecycleOwner(), isSuccess -> {
            fragmentBinding.registerProgress.setVisibility(View.GONE);
            fragmentBinding.register.setVisibility(View.VISIBLE);
            if (isSuccess) {
                Toast.makeText(requireContext(),
                        getResources().getString(R.string.verification_sent),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(requireContext(),
                        getResources().getString(R.string.register_error),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
        fragmentBinding = null;
        navController = null;
    }
}
