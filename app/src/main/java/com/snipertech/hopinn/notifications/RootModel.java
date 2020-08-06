package com.snipertech.hopinn.notifications;

import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("to") //  "to" changed to token
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

    public RootModel(String token, NotificationModel notification) {
        this.token = token;
        this.notification = notification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }
}
