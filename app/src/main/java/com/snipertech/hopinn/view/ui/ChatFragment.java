package com.snipertech.hopinn.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.iid.FirebaseInstanceId;
import com.snipertech.hopinn.databinding.ChatFragmentBinding;
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.view.adapter.UserAdapter;
import com.snipertech.hopinn.viewModel.ChatFragmentViewModel;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


public class ChatFragment extends Fragment {

    private ChatFragmentBinding chatFragmentBinding;
    private static final String USER_ID = "userId";
    private ChatFragmentViewModel chatFragmentViewModel;
    private UserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatFragmentBinding = ChatFragmentBinding.inflate(inflater, container, false);
        View view = chatFragmentBinding.getRoot();
        chatFragmentViewModel = new ViewModelProvider(this).get(ChatFragmentViewModel.class);
        initRecyclerView();
        chatFragmentViewModel.init();

        chatFragmentViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            userAdapter = new UserAdapter(requireContext(), users);
            chatFragmentBinding.usersList.setAdapter(userAdapter);

            userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
                @Override
                public void onUserClick(int position) {
                    User user = userAdapter.getUserAt(position);
                    String id = user.getId();
                    Intent intent = new Intent(requireActivity(), ChatRoomActivity.class);
                    intent.putExtra(USER_ID, id);
                    startActivity(intent);
                }
            });

            //refresh chat
            chatFragmentBinding.refreshChat.setOnRefreshListener(() -> {
                chatFragmentViewModel.init();
                userAdapter.notifyDataSetChanged();
                chatFragmentBinding.refreshChat.setRefreshing(false);
            });
        });

        updateToken();
        return view;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        chatFragmentBinding.usersList.setLayoutManager(layoutManager);
        chatFragmentBinding.usersList.setHasFixedSize(true);
    }

    //update token
    private void updateToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        String mToken = task.getResult().getToken();
                        chatFragmentViewModel.updateToken(mToken);
                    }
                });
    }
}
