package com.snipertech.hopinn.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class HomeFragment extends Fragment {

    private HomeFragmentBinding homeFragmentBinding;
    private static final String USER_ID = "userId";
    private HomeFragmentViewModel homeFragmentViewModel;
    private RequestAdapter requestAdapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeFragmentBinding =  HomeFragmentBinding.inflate(inflater, container, false);
        View view = homeFragmentBinding.getRoot();
        homeFragmentViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        initRecycler();

        homeFragmentViewModel.init();
        homeFragmentViewModel.getRequests().observe(getViewLifecycleOwner(),
                requests -> {
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

                    //refresh requests
                    homeFragmentBinding.refreshRequests.setOnRefreshListener(() -> {
                        requestAdapter.notifyDataSetChanged();
                        homeFragmentBinding.refreshRequests.setRefreshing(false);
                    });
                });
        displayUsername();
        return view;
    }


    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        homeFragmentBinding.recyclerview.setLayoutManager(linearLayoutManager);
        homeFragmentBinding.recyclerview.setHasFixedSize(true);
    }

    //Display the username
    private void displayUsername(){
        if(user.getDisplayName() != null){
            homeFragmentBinding.profile.setText(user.getDisplayName());
        }
    }

}
