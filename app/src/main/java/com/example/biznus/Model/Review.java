package com.example.biznus.Model;

public class Review {

    private String userid;
    private String lister;
    private String review;
    private float rating;

    public Review() {
    }

    public Review(String userid, String lister, String review, float rating) {
        this.userid = userid;
        this.lister = lister;
        this.review = review;
        this.rating = rating;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLister() {
        return lister;
    }

    public void setLister(String lister) {
        this.lister = lister;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
