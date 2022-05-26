package com.talos.weaver.Model;

public class ModelPost {


    String pId, pTitle, pDescr,  pImage, pTime, uid, uEmail, uDp, uName, type;
    int pScore, pLikes, pDislikes, pComments;



    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pDescr, String pImage, String pTime, String uid, String uEmail, String uDp, String uName, String type, int pScore, int pLikes, int pDislikes, int pComments) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.type = type;
        this.pScore = pScore;
        this.pLikes = pLikes;
        this.pDislikes = pDislikes;
        this.pComments = pComments;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
