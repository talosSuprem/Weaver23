package com.talos.weaver.Model

class Notification {
    var userid: String? = null
    var text: String? = null
    var postid: String? = null
    var isIspost = false
        private set

    constructor(userid: String?, text: String?, postid: String?, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        isIspost = ispost
    }

    constructor() {}

    fun setIspost(ispost: Boolean) {
        isIspost = ispost
    }
}