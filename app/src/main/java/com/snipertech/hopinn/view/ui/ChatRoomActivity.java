package com.snipertech.hopinn.view.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.databinding.ActivityChatRoomBinding;
import com.snipertech.hopinn.model.Message;
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.view.adapter.ChatAdapter;
import com.snipertech.hopinn.viewModel.ChatRoomViewModel;
import java.util.HashMap;

public class ChatRoomActivity extends AppCompatActivity {

    private ActivityChatRoomBinding binding;
    private ChatRoomViewModel chatRoomViewModel;
    private ChatAdapter chatAdapter;
    private DatabaseReference databaseReference;
    private static final String USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        //get the user to chat with
        String id = getIntent().getStringExtra(USER_ID);
        assert id != null;
        initActionBar();
        initUsersAndChat(id);
        initRecycler();

        //get all messages
        chatRoomViewModel.readMessages(getCurrentUser(), id).observe(
                this, messageList -> {
                    chatAdapter = new ChatAdapter(getApplicationContext(), messageList);
                    binding.chatroomList.setAdapter(chatAdapter);
                });

        //send message when enter button is pressed
        binding.inputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage(id);
                return true;
            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(id);
            }
        });
        seenMessage(id);
    }


    //setSeenMessage
    private void seenMessage(String id) {
        chatRoomViewModel.setMessageSeen(id);
    }

    //send message method
    private void sendMessage(String id){
        String message = binding.inputMessage.getText().toString();
        if(!message.equals("")){
            chatRoomViewModel.sendMessage(id, message);
            binding.inputMessage.setText("");
        }
    }

    //initialize the actionbar with toolbar
    public void initActionBar(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //get the current user
    private String getCurrentUser(){
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        return preferences.getString("currentUser", "none");
    }


    //set up addValueEventLister for the database
    public void initUsersAndChat(String id){
        //get the id of the desired user to chat with and the user object from the database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if(user.getUsername() != null){
                    binding.toolbarTitle.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //initialize recycler view
    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.chatroomList.setLayoutManager(linearLayoutManager);
        binding.chatroomList.setHasFixedSize(true);
    }


    //set the current user
    private void setCurrentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser", userid);
        editor.apply();
    }

    //if the user is active set the status to online
    @Override
    protected void onResume() {
        super.onResume();
        chatRoomViewModel.setStatus("online");
        setCurrentUser(getCurrentUser());
    }

    //if the user isn't active set the status to offline
    @Override
    protected void onPause() {
        super.onPause();
        chatRoomViewModel.setStatus("offline");
        setCurrentUser("none");
    }
}

