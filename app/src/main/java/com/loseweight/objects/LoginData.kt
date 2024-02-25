package com.loseweight.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginData {

    @SerializedName("tokenID")
    @Expose
    var tokenID: String? = null
    @SerializedName("applicationUser")
    @Expose
    var applicationUser: ApplicationUser? = null
    @SerializedName("operationResult")
    @Expose
    var operationResult: OperationResult? = null


    class ApplicationUser {
        @SerializedName("userId")
        @Expose
        var userId = 0
        @SerializedName("userName")
        @Expose
        var userName: String? = null
        @SerializedName("firstName")
        @Expose
        var firstName: String? = null
        @SerializedName("middleName")
        @Expose
        var middleName: String? = null
        @SerializedName("lastName")
        @Expose
        var lastName: String? = null
        @SerializedName("emailId")
        @Expose
        var emailId: String? = null
        @SerializedName("welcomeName")
        @Expose
        var welcomeName: String? = null
        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber: Any? = null
        @SerializedName("userRoleId")
        @Expose
        var userRoleId = 0
        @SerializedName("roleName")
        @Expose
        var roleName: String? = null
        @SerializedName("facilityId")
        @Expose
        var facilityId = 0
        @SerializedName("facilityName")
        @Expose
        var facilityName: String? = null
        @SerializedName("facilityCode")
        @Expose
        var facilityCode: String? = null
        @SerializedName("customerId")
        @Expose
        var customerId = 0
        @SerializedName("customerName")
        @Expose
        var customerName: String? = null
        @SerializedName("ipAddress")
        @Expose
        var ipAddress: Any? = null
        @SerializedName("isKeyTrackingEnabled")
        @Expose
        var isKeyTrackingEnabled = false
        @SerializedName("isLoggingEnabled")
        @Expose
        var isLoggingEnabled = false
        @SerializedName("isPasswordResetRequired")
        @Expose
        var isPasswordResetRequired = false
        @SerializedName("isFirstTimeLogin")
        @Expose
        var isFirstTimeLogin = false
    }

}