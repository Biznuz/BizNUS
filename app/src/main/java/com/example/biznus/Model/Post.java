package com.example.biznus.Model;

public class Post {
    private String title;
    private String listImage;
    private String price;
    private String lister;

    public Post(String title, String listImage, String price, String lister) {
        this.title = title;
        this.listImage = listImage;
        this.price = price;
        this.lister = lister;
    }

    public Post() {
    }

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
}
