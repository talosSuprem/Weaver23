package com.talos.weaver.Model

class Comment {
    var comment: String? = null
    var publisher: String? = null
    var commentid: String? = null

    constructor(comment: String?, publisher: String?, commentid: String?) {
        this.comment = comment
        this.publisher = publisher
        this.commentid = commentid
    }

    constructor() {}
}