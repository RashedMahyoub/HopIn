package com.snipertech.hopinn.viewModel;

import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.repository.RequestRepository;

import java.util.List;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeFragmentViewModel extends ViewModel {
    private MutableLiveData<List<Requests>> requests = new MutableLiveData<>();
    RequestRepository repository;

    @ViewModelInject
    public HomeFragmentViewModel(RequestRepository requestRepository){
        repository = requestRepository;
    }

    public void init(String city){
        requests = repository.getAllRequests(city);
    }
    public LiveData<List<Requests>> getRequests(){
        return requests;
    }
}
