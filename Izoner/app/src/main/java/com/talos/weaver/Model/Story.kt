package com.talos.weaver.Model

class Story {
    var imageurl: String? = null
    var timestart: Long = 0
    var timeend: Long = 0
    var storyid: String? = null
    var userid: String? = null

    constructor(
        imageurl: String?,
        timestart: Long,
        timeend: Long,
        storyid: String?,
        userid: String?
    ) {
        this.imageurl = imageurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }

    constructor() {}
}