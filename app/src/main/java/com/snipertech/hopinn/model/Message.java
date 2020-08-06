package com.snipertech.hopinn.model;

public class Message {
    private String sender;
    private String message;
    private String receiver;
    private String isSeen;
    private String timeSent;

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public Message(String sender, String message, String receiver, String isSeen, String timeSent) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.isSeen = isSeen;
        this.timeSent = timeSent;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
