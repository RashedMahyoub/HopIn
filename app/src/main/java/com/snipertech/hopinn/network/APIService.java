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
                    "Authorization:key=AAAAfIwx72Y:APA91bGleWHi6opjFZvOivlcj-zjrq4JqNRmNINt8Wc1Xbsfe-m4h9FeyKYEsRcVOfWjkbxMsZC9OLDptbgymAX9XTjBy4GGH-fU37kUB2nMVHgY5LFHuJ_I1EtylRdTpeHqQ5FjID3W"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
