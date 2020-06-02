package com.snipertech.hopinn.model;

public class Message {
    private String sender;
    private String message;
    private String receiver;
    private String isSeen;

    public Message(String message, String receiver, String sender, String isSeen) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.isSeen = isSeen;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
