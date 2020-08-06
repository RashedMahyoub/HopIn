package com.snipertech.hopinn.model;


public class Requests {
    private String name;
    private String message;
    private String imageUrl;
    private String userId;
    private String city;
    private String requestTime;

    public Requests(){

    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public Requests(String name, String message, String imageUrl, String userId, String city, String requestTime) {
        this.name = name;
        this.message = message;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.city = city;
        this.requestTime = requestTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getUserId() { return userId; }

    public void setName(String name) { this.name = name; }

    public void setMessage(String message) { this.message = message; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }

    public String getMessage() { return message; }
}
