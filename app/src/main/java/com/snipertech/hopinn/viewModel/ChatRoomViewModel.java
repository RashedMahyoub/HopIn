package com.snipertech.hopinn.viewModel;
import com.snipertech.hopinn.model.Message;
import com.snipertech.hopinn.repository.ChatRepository;
import java.util.List;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ChatRoomViewModel extends ViewModel {
    private ChatRepository chatRepository;

    @ViewModelInject
    public ChatRoomViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void sendMessage(String receiver, String message){
        chatRepository.sendMessage(receiver, message);
    }

    public LiveData<List<Message>> readMessages(String userId){
        return chatRepository.readMessages(userId);
    }

    public void setStatus(String status){
        chatRepository.status(status);
    }

    public void setMessageSeen(String userId){
        chatRepository.seenMessage(userId);
    }

}
