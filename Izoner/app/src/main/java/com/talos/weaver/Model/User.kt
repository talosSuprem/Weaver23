package com.talos.weaver.Model

class User {
    var id: String? = null
    var username: String? = null
    var fullname: String? = null
    var imageurl: String? = null
    var bio: String? = null
    var coinUser: Int? = null
    var status: String? = null
    var search: String? = null
    var coinCount: Int? = null
    var crypter: String? = null
    var strikes: Int? = null
    var email: String? = null
    var cover: String? = null
    var onlineStatus: String? = null
    var typingTo: String? = null;


    constructor()
    constructor(
        id: String?,
        username: String?,
        fullname: String?,
        imageurl: String?,
        bio: String?,
        coinUser: Int?,
        status: String?,
        search: String?,
        coinCount: Int?,
        crypter: String?,
        strikes: Int?,
        email: String?,
        cover: String?,
        onlineStatus: String?,
        typingTo: String?
    ) {
        this.id = id
        this.username = username
        this.fullname = fullname
        this.imageurl = imageurl
        this.bio = bio
        this.coinUser = coinUser
        this.status = status
        this.search = search
        this.coinCount = coinCount
        this.crypter = crypter
        this.strikes = strikes
        this.email = email
        this.cover = cover
        this.onlineStatus = onlineStatus
        this.typingTo = typingTo
    }




}