package com.example.biznus.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String userid;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;
    private String FCMtoken;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {}

    // Full constructor
    public User(String userid, String username, String fullname, String imageurl, String bio, String FCMtoken) {
        this.userid = userid;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.FCMtoken = FCMtoken;
    }

    // Getters and setters with default values
    public String getToken() {
        return FCMtoken != null ? FCMtoken : "";
    }

    public void setToken(String token) {
        this.FCMtoken = token;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String id) {
        this.userid = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname != null ? fullname : "";
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio != null ? bio : "";
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
