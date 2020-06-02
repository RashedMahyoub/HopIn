package com.snipertech.hopinn.network;

import com.snipertech.hopinn.model.Requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.18/Example/";

    /**
     * Create an instance of Retrofit object
     * */

    public static Retrofit getClient(String url){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public Call<List<Requests>> getAllRequests(){
        return getClient(BASE_URL).create(APIService.class).getRequests();
    }
}
