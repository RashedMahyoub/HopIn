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
                    "Authorization: key=AAAAfIwx72Y:APA91bGleWHi6opjFZvOivlcj-zjrq4JqNRmNINt8Wc1Xbsfe-m4h9FeyKYEsRcVOfWjkbxMsZC9OLDptbgymAX9XTjBy4GGH-fU37kUB2nMVHgY5LFHuJ_I1EtylRdTpeHqQ5FjID3W"
            }
    )

    @POST("fcm/send")
    Single<ResponseBody> sendNotification(@Body RootModel root);
}
