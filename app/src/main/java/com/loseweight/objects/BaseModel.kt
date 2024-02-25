package com.loseweight.objects

open class BaseModel {

    var code: Int = 0
    var id: String? = null;

    constructor()
    constructor(code: Int) {
        this.code = code
    }

    constructor(code: Int, id: String?) {
        this.code = code
        this.id = id
    }


}