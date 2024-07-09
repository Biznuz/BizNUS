package com.example.biznus.Model;

import java.util.Date;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private String timeSent;

    private Date date;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, String timeSent, Date date) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timeSent = timeSent;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
