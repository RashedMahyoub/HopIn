package com.snipertech.hopinn.viewModel;
import com.snipertech.hopinn.model.User;
import com.snipertech.hopinn.repository.ChatRepository;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatFragmentViewModel extends ViewModel {

    private MutableLiveData<List<User>> users;
    ChatRepository repository = ChatRepository.getInstance();


    public void init() {
        users = repository.getAllUsers();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void updateToken(String token){
        repository.updateToken(token);
    }
}
