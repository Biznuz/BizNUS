package com.example.biznus.Model;

public class Post {
    private String postTitle;
    private String postImage;
    private String postPrice;
    private String postUser;

    public Post(String postTitle, String postImage, String postPrice, String postUser) {
        this.postTitle = postTitle;
        this.postImage = postImage;
        this.postPrice = postPrice;
        this.postUser = postUser;
    }

    public Post() {
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(String postPrice) {
        this.postPrice = postPrice;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }
}
