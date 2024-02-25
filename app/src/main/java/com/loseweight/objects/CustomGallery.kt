package com.loseweight.objects

class CustomGallery {


    lateinit var sdcardPath: String
    var isSeleted = false
    var isRes = true

    constructor(sdcardPath: String, isSeleted: Boolean, isRes: Boolean) {
        this.sdcardPath = sdcardPath
        this.isSeleted = isSeleted
        this.isRes = isRes
    }


    constructor() {}
}
