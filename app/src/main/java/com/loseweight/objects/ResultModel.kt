package com.loseweight.objects

class ResultModel {

    var code: Int = 0
    var totalCustomers : Int = 0
    var totalUpdates : Int = 0
    var totalCatalogueViews : Int = 0
    var id: String? = null;
    var userModel: UserModel? = null
    var userModelList : MutableList<UserModel>? = null

    constructor()
    constructor(code: Int) {
        this.code = code
    }


    constructor(code: Int,userModel: UserModel){
        this.code = code
        this.userModel = userModel
    }


    constructor(code: Int,userModelList: MutableList<UserModel>){
        this.code = code
        this.userModelList = userModelList
    }

    constructor(code: Int, id: String?) {
        this.code = code
        this.id = id
    }

    constructor(code: Int, totalCustomers: Int) {
        this.code = code
        this.totalCustomers = totalCustomers
    }




}