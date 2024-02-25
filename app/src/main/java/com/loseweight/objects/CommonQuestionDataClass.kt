package com.stretching.objects

import com.bignerdranch.expandablerecyclerview.model.Parent

class CommonQuestionDataClass : Parent<CommonQuestionClass> {

    var title = ""
    var isSelected = false

    var arrQuestionDetail: MutableList<CommonQuestionClass> = mutableListOf<CommonQuestionClass>()
    override fun getChildList(): MutableList<CommonQuestionClass> {
        return arrQuestionDetail
    }

    override fun isInitiallyExpanded(): Boolean {
        return true
    }
}