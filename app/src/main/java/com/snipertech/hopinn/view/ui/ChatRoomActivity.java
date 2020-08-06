package com.snipertech.hopinn.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.util.ApplicationLanguageHelper;
import com.snipertech.hopinn.view.adapter.ChatAdapter;
import com.snipertech.hopinn.viewModel.ChatRoomViewModel;

import static com.snipertech.hopinn.util.Constants.SHARED_LANG;
import static com.snipertech.hopinn.util.Constants.SHARED_PREF;
import static com.snipertech.hopinn.util.Constants.USER_ID;

@AndroidEntryPoint
public class ChatRoomActivity extends AppCompatActivity {

    private ActivityChatRoomBinding binding;
    private String receiverId;
    private ChatRoomViewModel chatRoomViewModel;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        registerObserver();

        binding.sendMessage.setOnClickListener(view -> sendMessage(receiverId));
        seenMessage(receiverId);
    }

    //initialize everything
    private void init(){
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        //get the user to chat with
        receiverId = getIntent().getStringExtra(USER_ID);

        initActionBar();
        initUsersAndChat(receiverId);
        keyboardEnterKeySendMessage();
        initRecycler();
        changeIconColorOnTextChanged();
        setContentView(view);
    }

    //Change icon tint color when writing message
    private void changeIconColorOnTextChanged() {
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    binding.sendMessage.setIconTint(ColorStateList.valueOf(Color.WHITE));
                } else {
                    binding.sendMessage.setIconTint(ColorStateList.valueOf(Color.GRAY));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    binding.sendMessage.setIconTint(ColorStateList.valueOf(Color.WHITE));
                } else {
                    binding.sendMessage.setIconTint(ColorStateList.valueOf(Color.GRAY));
                }
            }
        });
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

    //Send message when enter button is pressed
    private void keyboardEnterKeySendMessage(){
        binding.inputMessage.setOnEditorActionListener((textView, i, keyEvent) -> {
            sendMessage(receiverId);
            return true;
        });
    }

    //initialize the actionbar with toolbar
    public void initActionBar(){
        //get the toolbar
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    //set up addValueEventLister for the database
    public void initUsersAndChat(String id){
        //get the id of the desired user to chat with and the user object from the database
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null && user.getUsername() != null){
                    binding.toolbarTitle.setText(user.getUsername());
                    Glide.with(getApplicationContext())
                            .applyDefaultRequestOptions(
                                    new RequestOptions()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round)
                            )
                            .load(user.getProfileUri())
                            .centerCrop()
                            .into(binding.profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //initialize recycler view
    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.chatroomList.setHasFixedSize(true);
        binding.chatroomList.setLayoutManager(linearLayoutManager);
    }

    //Observe data
    private void registerObserver(){
        //get all messages
        chatRoomViewModel.readMessages(receiverId).observe(
                this, messageList -> {
                    chatAdapter = new ChatAdapter(getApplicationContext(), messageList);
                    binding.chatroomList.setAdapter(chatAdapter);
                });
    }

    //if the user is active set the status to online
    @Override
    protected void onResume() {
        super.onResume();
        chatRoomViewModel.setStatus("online");
    }

    //if the user isn't active set the status to offline
    @Override
    protected void onPause() {
        super.onPause();
        chatRoomViewModel.setStatus("offline");
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
        chatAdapter = null;
        chatRoomViewModel = null;
        receiverId = null;
    }
}

