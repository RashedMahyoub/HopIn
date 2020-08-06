package com.snipertech.hopinn.repository;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.snipertech.hopinn.notifications.NotificationModel;
import com.snipertech.hopinn.notifications.RootModel;
import com.snipertech.hopinn.notifications.Token;
import com.snipertech.hopinn.model.Message;
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.network.APIService;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatRepository {
    public MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference root;
    private final APIService apiService;
    boolean notify = false;

    @Inject
    public ChatRepository(APIService retrofit) {
        apiService = retrofit;
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
                    if(message != null) {
                        if (firebaseUser.getUid().equals(message.getSender())) {
                            if (!users.contains(message.getReceiver())) {
                                users.add(message.getReceiver());
                            }
                        }
                        if (firebaseUser.getUid().equals(message.getReceiver())) {
                            if (!users.contains(message.getSender())) {
                                users.add(message.getSender());
                            }
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
                    if(user != null) {
                        for (String id : users) {
                            if (id.equals(user.getId()) && !user.getId().equals(firebaseUser.getUid())) {
                                if (usersList.size() != 0) {
                                    for (User user1 : usersList) {
                                        if (!user1.getId().equals(user.getId())) {
                                            usersList.add(user);
                                            break;
                                        }
                                    }
                                } else {
                                    usersList.add(user);
                                }
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

    @SuppressLint("SimpleDateFormat")
    //send a message
    public void sendMessage(String receiver, String message) {
        notify = true;
        if (firebaseUser != null) {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("hh:mm a, dd/MM/yyyy", Locale.ENGLISH);
            String timeSent = dateFormat.format(new Date());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", firebaseUser.getUid());
            hashMap.put("message", message);
            hashMap.put("receiver", receiver);
            hashMap.put("isSeen", "false");
            hashMap.put("timeSent", timeSent);

            root = FirebaseDatabase.getInstance().getReference("Messages");
            root.push().setValue(hashMap);

            root = FirebaseDatabase.getInstance().getReference("Users").child(receiver);
            root.child("lastSpoken").setValue(timeSent);
        }

        root = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify && user != null) {
                    sendNotification(receiver, user.getUsername(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, String sender, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    if(token != null) {
                        RootModel rootModel = new RootModel(
                                token.getToken(),
                                new NotificationModel(sender, msg)
                        );

                    Single<ResponseBody> observer = apiService.sendNotification(rootModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());

                    observer.subscribe(result ->
                            Log.d("TAG", "Successfully notification send by using retrofit."),
                            error -> Log.d("TAG", "Error" + error.getMessage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //get all the messages for the chat
    public MutableLiveData<List<Message>> readMessages(String receiver) {
        List<Message> messages = new ArrayList<>();
        MutableLiveData<List<Message>> allMessages = new MutableLiveData<>();
        root = FirebaseDatabase.getInstance().getReference("Messages");

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if(message != null) {
                        if (receiver.equals(message.getReceiver())
                                && firebaseUser.getUid().equals(message.getSender())
                                || receiver.equals(message.getSender())
                                && firebaseUser.getUid().equals(message.getReceiver())) {
                            messages.add(message);
                        }
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
    public void status(String status) {
        root = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        root.updateChildren(hashMap);
    }


    //set the message to seen
    public void seenMessage(String receiver) {
        root = FirebaseDatabase.getInstance().getReference("Messages");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if(message != null) {
                        if (firebaseUser.getUid().equals(message.getReceiver())
                                && receiver.equals(message.getSender())){

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isSeen", "true");
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    //Update user information
    public void updateUser(Bitmap imageBitmap, String username){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        //Upload image to firebase storage
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(firebaseUser.getUid() + ".jpeg");
        //delete current image
        reference.delete()
                .addOnFailureListener(error ->Log.d("TAG", "Delete" + error.getMessage()));

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadUri(reference, username))
                .addOnFailureListener(error ->Log.d("TAG", "Upload" + error.getMessage()));
    }


    private void getDownloadUri(StorageReference reference, String username){
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> updateProfile(uri, username))
                .addOnFailureListener(error ->Log.d("TAG", "Error" + error.getMessage()));
    }

    //Update profile
    private void updateProfile(Uri image, String username){
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(image)
                .build();
        firebaseUser.updateProfile(changeRequest)
                .addOnSuccessListener(success -> isSuccessful.postValue(true))
                .addOnFailureListener(failure -> isSuccessful.postValue(false));

        root = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> data = new HashMap<>();
        data.put("profileUri", image.toString());
        data.put("username", username);
        root.updateChildren(data);
    }
}
