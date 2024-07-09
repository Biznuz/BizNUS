package com.example.biznus.Model;

import kotlin.text.UStringsKt;

public class Notification {
    private String userid;
    private String listid;
    private String notifs;
    private boolean islist;

    public Notification(String userid, String listid, String notifs, boolean islist) {
        this.userid = userid;
        this.listid = listid;
        this.notifs = notifs;
        this.islist = islist;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
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
