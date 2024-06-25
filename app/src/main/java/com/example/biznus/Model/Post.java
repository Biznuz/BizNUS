package com.example.biznus.Model;

public class Post {
    private String listID;
    private String title;
    private String listImage;
    private String price;
    private String lister;
    private String condition;

    public Post(String listID, String title, String listImage, String price, String lister, String condition) {
        this.listID = listID;
        this.title = title;
        this.listImage = listImage;
        this.price = price;
        this.lister = lister;
        this.condition = condition;
    }

    public Post() {
    }

    public String getListID() { return listID; }

    public void setListID(String listID) { this.listID = listID; }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getListImage() {
        return listImage;
    }

    public void setListImage(String listImage) {
        this.listImage = listImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLister() {
        return this.lister;
    }

    public void setLister(String lister) {
        this.lister = lister;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
