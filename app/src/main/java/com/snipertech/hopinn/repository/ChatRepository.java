package com.snipertech.hopinn.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snipertech.hopinn.notifications.Data;
import com.snipertech.hopinn.notifications.MyResponse;
import com.snipertech.hopinn.notifications.Sender;
import com.snipertech.hopinn.notifications.Token;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.model.Message;
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.network.APIService;
import com.snipertech.hopinn.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static ChatRepository chatRepository;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference root;
    APIService apiService;
    boolean notify = false;


    public static ChatRepository getInstance() {
        if (chatRepository == null) {
            chatRepository = new ChatRepository();
        }
        return chatRepository;
    }

    //list of all users with whom you have messages
    public MutableLiveData<List<User>> getAllUsers() {
        root = FirebaseDatabase.getInstance().getReference("Messages");
        List<String> users = new ArrayList<>();
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);

                    if (firebaseUser.getUid().equals(message.getSender())) {
                        if(!users.contains(message.getReceiver())){
                            users.add(message.getReceiver());
                        }
                    }
                    if (firebaseUser.getUid().equals(message.getReceiver())) {
                        if(!users.contains(message.getSender())){
                            users.add(message.getSender());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return readChats(users);
    }


    private MutableLiveData<List<User>> readChats(List<String> users) {
        List<User> usersList = new ArrayList<>();
        MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
        root = FirebaseDatabase.getInstance().getReference("Users");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : users) {
                        if (id.equals(user.getId())) {
                            if (usersList.size() != 0) {
                                for (User user1 : usersList) {
                                    if (!user1.getId().equals(user.getId())) {
                                        usersList.add(user);
                                        break;
                                    }
                                }
                            }else {
                                usersList.add(user);
                            }
                        }
                    }
                }
                allUsers.setValue(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return allUsers;
    }


    //send a message
    public void sendMessage(String receiver, String message) {
        notify = true;
        if (firebaseUser != null) {
            String sender = firebaseUser.getUid();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("isSeen", "false");

            root = FirebaseDatabase.getInstance().getReference("Messages");
            root.push().setValue(hashMap);
        }

        final String msg = message;

        root = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        apiService = RetrofitClient.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher,
                            username+": "+msg, "New Message", receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call,
                                                       @NonNull Response<MyResponse> response) {
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call,
                                                      @NonNull Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //get all the messages for the chat
    public MutableLiveData<List<Message>> readMessages(String myId, String userId) {
        List<Message> messages = new ArrayList<>();
        MutableLiveData<List<Message>> allMessages = new MutableLiveData<>();
        root = FirebaseDatabase.getInstance().getReference("Messages");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (userId.equals(message.getReceiver()) && myId.equals(message.getSender())
                            || userId.equals(message.getSender()) && myId.equals(message.getReceiver())) {
                        messages.add(message);
                    }
                }
                allMessages.setValue(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return allMessages;
    }


    //set the status of the user
    public void status(String status){
        root = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        root.updateChildren(hashMap);
    }


    //set the message to seen
    public void seenMessage(final String userId){
        root = FirebaseDatabase.getInstance().getReference("Messages");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message chat = snapshot.getValue(Message.class);
                    if (firebaseUser.getUid().equals(chat.getReceiver())
                            && userId.equals(chat.getSender())){

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }
}
