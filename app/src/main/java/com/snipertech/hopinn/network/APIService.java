package com.snipertech.hopinn.network;

import com.snipertech.hopinn.notifications.MyResponse;
import com.snipertech.hopinn.notifications.Sender;
import com.snipertech.hopinn.model.Requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @GET("Api.php")
    Call<List<Requests>> getRequests();

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key= YOUR KEY HERE"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
