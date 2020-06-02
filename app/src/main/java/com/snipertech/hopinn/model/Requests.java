package com.snipertech.hopinn.model;


public class Requests {
    private String name;
    private String message;
    private String userId;

    public Requests(String name, String message, String userId) {
        this.name = name;
        this.message = message;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
