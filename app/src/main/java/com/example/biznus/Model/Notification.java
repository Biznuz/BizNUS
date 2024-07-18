package com.example.biznus.Model;

import kotlin.text.UStringsKt;

public class Notification {
    private String userid;
    private String listID;
    private String notifs;
    private boolean islist;

    public Notification() {
    }

    public Notification(String userid, String listID, String notifs, boolean islist) {
        this.userid = userid;
        this.listID = listID;
        this.notifs = notifs;
        this.islist = islist;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getNotifs() {
        return notifs;
    }

    public void setNotifs(String notifs) {
        this.notifs = notifs;
    }

    public boolean isIslist() {
        return islist;
    }

    public void setIslist(boolean islist) {
        this.islist = islist;
    }
}
