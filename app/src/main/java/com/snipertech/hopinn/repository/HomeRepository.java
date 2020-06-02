package com.snipertech.hopinn.repository;

import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.network.RetrofitClient;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private RetrofitClient client =  new RetrofitClient();
    private static HomeRepository homeRepository;

    public static HomeRepository getInstance(){
        if (homeRepository == null){
            homeRepository = new HomeRepository();
        }
        return homeRepository;
    }

    public MutableLiveData<List<Requests>> getAllRequests(){
        MutableLiveData<List<Requests>> requests = new MutableLiveData<>();
        client.getAllRequests().enqueue(new Callback<List<Requests>>() {
            @Override
            public void onResponse(@NonNull Call<List<Requests>> call,
                                   @NonNull Response<List<Requests>> response) {
                if(response.isSuccessful()){
                    requests.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Requests>> call, @NonNull Throwable t) {
                requests.setValue(null);
            }
        });
        return requests;
    }

}
