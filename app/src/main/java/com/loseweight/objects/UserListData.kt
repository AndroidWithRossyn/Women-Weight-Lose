package com.loseweight.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class  UserListData {

    @SerializedName("userList")
    @Expose
    var userList: List<UserList>? = null
    @SerializedName("operationResult")
    @Expose
    var operationResult: OperationResult? = null


    class UserList {
        @SerializedName("userId")
        @Expose
        var userId = 0
        @SerializedName("firstName")
        @Expose
        var firstName: String? = null
        @SerializedName("middleName")
        @Expose
        var middleName: String? = null
        @SerializedName("lastName")
        @Expose
        var lastName: String? = null
        @SerializedName("userName")
        @Expose
        var userName: String? = null
        @SerializedName("isActive")
        @Expose
        var isActive = false
        @SerializedName("email")
        @Expose
        var email: String? = null
        @SerializedName("mobile")
        @Expose
        var mobile: String? = null
        @SerializedName("initial")
        @Expose
        var initial: String? = null
        @SerializedName("displayName")
        @Expose
        var displayName: String? = null
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

        fun getName():String
        {
            return "$firstName, $lastName"
        }

        constructor(userId:Int,firstName:String?,middleName: String?,lastName: String?,userName: String?,isActive:Boolean,email: String?,mobile: String?,initial: String?,displayName: String?,userRoleId:Int,roleName:String?,facilityId:Int,facilityName: String?,facilityCode: String?)
        {
            this.userId = userId
            this.firstName = firstName
            this.middleName = middleName
            this.lastName = lastName
            this.userName = userName
            this.isActive = isActive
            this.email = email
            this.mobile = mobile
            this.initial = initial
            this.displayName = displayName
            this.userRoleId = userRoleId
            this.roleName = roleName
            this.facilityId = facilityId
            this.facilityName = facilityName
            this.facilityCode = facilityCode
        }
    }

}