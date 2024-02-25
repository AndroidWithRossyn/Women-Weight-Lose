package com.loseweight.objects

import java.util.*

class UserModel {
    var uid: String? = null
    var name: String? = null
    var token: String? = null
    var utype: String? = null
    var mobno: String? = null
    var pic: String? = null
    var desc: String? = null
    var iscustomer: Boolean? = null
    var isvendor : Boolean? = null
    var plan: MutableMap<String, Any>? = null


    constructor() {}


    // signup constructor
    public constructor(
        ID: String?,
        name: String?,
        token: String?,
        user_type: String?,
        map: HashMap<String, Boolean>,
        isCustomer: Boolean?,
        isVendor: Boolean?,
        devicetoken : String?
    ) {
        this.uid = ID
        this.name = name
        this.token = token
        this.utype = user_type
        this.iscustomer= isCustomer
        this.isvendor = isVendor
    }

    constructor(
        ID: String?,
        name: String?,
        token: String?,
        user_type: String?,
        mobile_number: String,
        map: HashMap<String, Boolean>,
        isCustomer: Boolean?,
        isVendor: Boolean?
    ) {
        this.uid = ID
        this.name = name
        this.token = token
        this.utype = user_type
        this.mobno = mobile_number
        this.iscustomer = isCustomer
        this.isvendor = isVendor
    }

    constructor(
        ID: String?,
        name: String?,
        token: String?,
        user_type: String?,
        mobile_number: String,
        map: HashMap<String, Boolean>,
        description: String,
        isCustomer: Boolean?,
        isVendor: Boolean?
    ) {
        this.uid = ID
        this.name = name
        this.token = token
        this.utype = user_type
        this.mobno = mobile_number
        this.desc = description
        this.iscustomer = isCustomer
        this.isvendor = isVendor
    }

    constructor(
        user_id: String?,
        name: String?,
        mobile_number: String?,
        map: HashMap<String, Boolean>,
        user_type: String?,
        token: String?
    ) {
        this.uid = user_id
        this.name = name
        this.mobno = mobile_number
        this.token = token
        this.utype = user_type
    }

}