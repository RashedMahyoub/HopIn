package com.snipertech.hopinn.viewModel;

import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.repository.HomeRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeFragmentViewModel extends ViewModel {
    private MutableLiveData<List<Requests>> requests;

    public void init(){
        HomeRepository repository = HomeRepository.getInstance();
        requests = repository.getAllRequests();
    }

    public LiveData<List<Requests>> getRequests(){
        return requests;
    }
}
