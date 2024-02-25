package com.loseweight.objects

import com.bignerdranch.expandablerecyclerview.model.Parent
import com.loseweight.objects.HistoryDetailsClass

class HistoryWeekDataClass : Parent<HistoryDetailsClass> {

    var weekNumber = ""
    var weekStart = ""
    var weekEnd = ""
    var totTime = 0
    var totKcal = 0
    var totWorkout = 0

    var arrHistoryDetail: MutableList<HistoryDetailsClass> = mutableListOf<HistoryDetailsClass>()
    override fun getChildList(): MutableList<HistoryDetailsClass> {
        return arrHistoryDetail
    }

    override fun isInitiallyExpanded(): Boolean {
        return true
    }
}