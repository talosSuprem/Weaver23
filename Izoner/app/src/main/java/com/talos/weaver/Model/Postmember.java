package com.talos.weaver.Model;

public class Postmember {
    String name, url, postUri, time, uid, type, desc , title;
    int pScore, pLikes, pDislikes, pComments;

    public Postmember() {
    }

    public Postmember(String name, String url, String postUri, String time, String uid, String type, String desc, String title, int pScore, int pLikes, int pDislikes, int pComments) {
        this.name = name;
        this.url = url;
        this.postUri = postUri;
        this.time = time;
        this.uid = uid;
        this.type = type;
        this.desc = desc;
        this.title = title;
        this.pScore = pScore;
        this.pLikes = pLikes;
        this.pDislikes = pDislikes;
        this.pComments = pComments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getpScore() {
        return pScore;
    }

    public void setpScore(int pScore) {
        this.pScore = pScore;
    }

    public int getpLikes() {
        return pLikes;
    }

    public void setpLikes(int pLikes) {
        this.pLikes = pLikes;
    }

    public int getpDislikes() {
        return pDislikes;
    }

    public void setpDislikes(int pDislikes) {
        this.pDislikes = pDislikes;
    }

    public int getpComments() {
        return pComments;
    }

    public void setpComments(int pComments) {
        this.pComments = pComments;
    }
}
