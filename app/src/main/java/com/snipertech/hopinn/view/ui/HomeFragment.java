package com.snipertech.hopinn.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.HomeFragmentBinding;
import com.snipertech.hopinn.view.adapter.RequestAdapter;
import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.view.dialog.MessageDialog;
import com.snipertech.hopinn.viewModel.HomeFragmentViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;

import static com.snipertech.hopinn.util.Constants.LAUNCH_SECOND_ACTIVITY;
import static com.snipertech.hopinn.util.Constants.REGISTERED_LOCATION;
import static com.snipertech.hopinn.util.Constants.SHARED_CITY;
import static com.snipertech.hopinn.util.Constants.SHARED_PREF;
import static com.snipertech.hopinn.util.Constants.USER_ID;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private HomeFragmentBinding homeFragmentBinding;
    private HomeFragmentViewModel homeFragmentViewModel;
    private RequestAdapter requestAdapter;
    private SharedPreferences preferences;
    private String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeFragmentBinding =  HomeFragmentBinding.inflate(inflater, container, false);
        View view = homeFragmentBinding.getRoot();
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerObserver();
    }

    private void init(){
        homeFragmentViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        initRecycler();

        homeFragmentBinding.location.setOnClickListener(onClick -> {
            Intent intent = new Intent(requireActivity(), GoogleMapActivity.class);
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
        });

        preferences = requireActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        updateCityText();
    }

    private void registerObserver(){
        homeFragmentViewModel.getRequests().observe(getViewLifecycleOwner(),
                requests -> {
                    if(requests.size() == 0){
                        homeFragmentBinding.noItems.setVisibility(View.VISIBLE);
                        homeFragmentBinding.noItemsText.setVisibility(View.VISIBLE);
                        homeFragmentBinding.recyclerview.setVisibility(View.INVISIBLE);
                    } else {
                        homeFragmentBinding.noItems.setVisibility(View.GONE);
                        homeFragmentBinding.noItemsText.setVisibility(View.GONE);
                        homeFragmentBinding.recyclerview.setVisibility(View.VISIBLE);

                        requestAdapter = new RequestAdapter(getContext(), requests);
                        homeFragmentBinding.recyclerview.setAdapter(requestAdapter);

                        requestAdapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
                            //on click for the single item
                            @Override
                            public void onItemClick(int position) {
                                Requests requests = requestAdapter.getMessageAt(position);
                                new MessageDialog().initDialog(requireActivity(),
                                        requests.getName(),
                                        requests.getMessage()
                                );
                            }

                            //on click for chat button in the single item
                            @Override
                            public void onRequestClick(int position) {
                                Requests requests = requestAdapter.getMessageAt(position);
                                String id = requests.getUserId();
                                Intent intent = new Intent(requireActivity(), ChatRoomActivity.class);
                                intent.putExtra(USER_ID, id);
                                startActivity(intent);
                            }
                        });
                        refreshList();
                    }
                });
    }

    private void refreshList(){
        //Refresh requests
        homeFragmentBinding.refreshRequests.setOnRefreshListener(() -> {
            homeFragmentViewModel.init(city);
            requestAdapter.notifyDataSetChanged();
            homeFragmentBinding.refreshRequests.setRefreshing(false);
        });
    }

    //Update city text
    private void updateCityText(){
        city = preferences.getString(SHARED_CITY, getResources().getString(R.string.press_to_choose_the_city));
        homeFragmentBinding.location.setText(city);
        homeFragmentViewModel.init(city);
    }

    //Initialize recycler view
    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        homeFragmentBinding.recyclerview.setLayoutManager(linearLayoutManager);
        homeFragmentBinding.recyclerview.setHasFixedSize(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LAUNCH_SECOND_ACTIVITY){
            switch (resultCode){
                case Activity.RESULT_OK:
                    if(data != null){
                       SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SHARED_CITY, data.getStringExtra(REGISTERED_LOCATION));
                        editor.apply();
                        updateCityText();
                        break;
                    }
                case Activity.RESULT_CANCELED:
                    Toast.makeText(
                            requireContext(),
                            getResources().getString(R.string.no_location),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
        }
    }
}
