package com.snipertech.hopinn.network;

import com.snipertech.hopinn.notifications.RootModel;
import com.squareup.okhttp.ResponseBody;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=KEY_HERE"
            }
    )

    @POST("fcm/send")
    Single<ResponseBody> sendNotification(@Body RootModel root);
}
