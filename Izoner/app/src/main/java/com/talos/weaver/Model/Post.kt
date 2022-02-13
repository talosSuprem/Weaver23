package com.talos.weaver.Model

class Post {
    var postid: String? = null
    var postimage: String? = null
    var description: String? = null
    var publisher: String? = null
    var dateofimage: String? = null

    constructor(postid: String?, postimage: String?, description: String?, publisher: String?, dateofimage: String?) {
        this.postid = postid
        this.postimage = postimage
        this.description = description
        this.publisher = publisher
        this.dateofimage = dateofimage
    }

    constructor() {}
}