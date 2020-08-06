package com.snipertech.hopinn.model;

import java.util.List;

public class User {
    private String id;
    private String username;
    private String status;
    private String profileUri;
    private String lastSpoken;

    public User(String id, String username, String status, String profileUri, String lastSpoken) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.profileUri = profileUri;
        this.lastSpoken = lastSpoken;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getLastSpoken() {
        return lastSpoken;
    }

    public void setLastSpoken(String lastSpoken) {
        this.lastSpoken = lastSpoken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
