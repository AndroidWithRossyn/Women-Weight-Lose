package com.loseweight.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OperationResult {

    @SerializedName("isSuccess")
    @Expose
    var isSuccess = false
    @SerializedName("responseMessage")
    @Expose
    var responseMessage: String? = null
    @SerializedName("errorCode")
    @Expose
    var errorCode: Any? = null
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String? = null
}