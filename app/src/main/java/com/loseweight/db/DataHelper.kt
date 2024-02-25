package com.utillity.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.loseweight.objects.*
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

// Database nameC
private const val DBName = "LoseWeight.db"

// Home Plan table
private const val PlanTable = "HomePlanTable"
private const val PlanId = "PlanId"
private const val PlanName = "PlanName"
private const val PlanProgress = "PlanProgress"
private const val PlanText = "PlanText"
private const val PlanLvl = "PlanLvl"
private const val PlanImage = "PlanImage"
private const val PlanDays = "PlanDays"
private const val DayCount = "Days"
private const val PlanType = "PlanType"
private const val ShortDes = "ShortDes"
private const val Introduction = "Introduction"
private const val PlanWorkouts = "PlanWorkouts"
private const val PlanMinutes = "PlanMinutes"
private const val IsPro = "IsPro"
private const val HasSubPlan = "HasSubPlan"
private const val TestDes = "TestDes"
private const val PlanThumbnail = "PlanThumbnail"
private const val PlanTypeImage = "PlanTypeImage"
private const val ParentPlanId = "ParentPlanId"
private const val PlanSort = "sort"
private const val DefaultSort = "DefaultSort"


// Plan days table getting by planId
private const val PlanDaysTable = "PlanDaysTable"
private const val DayId = "DayId"
private const val DayName = "DayName"
private const val WeekName = "WeekName"
private const val IsCompleted = "IsCompleted"
private const val DayProgress = "DayProgress"

// DayExTable table getting by DayId
private const val DayExTable = "DayExTable"
private const val HomeExSingleTable = "HomeExSingleTable"
private const val DayExId = "Id"
private const val ExId = "ExId"
private const val ExTime = "ExTime"
private const val UpdatedExTime = "UpdatedExTime"

private const val ReplaceExId = "ReplaceExId"
private const val IsDeleted = "IsDeleted"

// ExerciseTable getting
private const val ExerciseTable = "ExerciseTable"
private const val ExName = "ExName"
private const val ExUnit = "ExUnit"
private const val ExPath = "ExPath"
private const val ExDescription = "ExDescription"
private const val ExVideo = "ExVideo"
private const val ExReplaceTime = "ReplaceTime"

// ReminderTable getting
private const val ReminderTable = "ReminderTable"
private const val RId = "RId"
private const val RemindTime = "RemindTime"
private const val Days = "Days"
private const val IsActive = "IsActive"

// WeightTable getting
private const val WeightTable = "WeightTable"
private const val WeightId = "WeightId"
private const val WeightKg = "WeightKg"
private const val WeightLb = "WeightLb"
private const val WeightDate = "WeightDate"
private const val CurrentTimeStamp = "CurrentTimeStamp"

// HistoryTable getting
private const val HistoryTable = "HistoryTable"
private const val HId = "HId"
private const val HPlanName = "HPlanName"
private const val HPlanId = "HPlanId"
private const val HDayName = "HDayName"
private const val HDayId = "HDayId"
private const val HBurnKcal = "HBurnKcal"
private const val HTotalEx = "HTotalEx"
private const val HKg = "HKg"
private const val HFeet = "HFeet"
private const val HInch = "HInch"
private const val HFeelRate = "HFeelRate"
private const val HCompletionTime = "HCompletionTime"
private const val HDateTime = "HDateTime"


class DataHelper(private val mContext: Context) {

    fun checkDBExist(): Boolean {
        var isExist = false
        val dbFile = mContext.getDatabasePath(DBName)

        if (!dbFile.exists()) {
            try {
                if (copyDatabase(dbFile)) {
                    if (dbFile.exists()) {
                        isExist = true
                    }
                } else if (dbFile.delete()) {
                    if (copyDatabase(dbFile)) {
                        if (dbFile.exists()) {
                            isExist = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return isExist
    }

    private fun getReadWriteDB(): SQLiteDatabase {
        val dbFile = mContext.getDatabasePath(DBName)
        if (!dbFile.exists()) {
            try {
                val checkDB = mContext.openOrCreateDatabase(DBName, Context.MODE_PRIVATE, null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: Exception) {
                throw RuntimeException("Error creating source database", e)
            }
        }
//        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private fun copyDatabase(dbFile: File): Boolean {
        var isSuccess = false
        var ins: InputStream? = null
        var os: FileOutputStream? = null

        try {
            ins = mContext.assets.open(DBName)
            os = FileOutputStream(dbFile)

            val buffer = ByteArray(1024)
            while (ins.read(buffer) > 0) {
                os.write(buffer)
            }

            isSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (os != null) {
                os.flush()
                os.close()
            }
            if (ins != null) {
                ins.close()
            }
        }

        return isSuccess
    }

    // Todo Home Plan table Process
    fun getHomePlanList(strPlanType: String): ArrayList<HomePlanTableClass> {

        val arrPlan: ArrayList<HomePlanTableClass> = ArrayList()
        var lang = Utils.getSelectedLanguage(mContext)

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query =
                "Select * From $PlanTable where $PlanType = '$strPlanType' order by $PlanSort"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = HomePlanTableClass()
                    aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                    aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                    //aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName+"_$lang"))
                    aClass.planProgress =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                    aClass.planText = cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                    aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                    aClass.planImage = cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                    aClass.planDays = cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                    aClass.days = cursor.getString(cursor.getColumnIndexOrThrow(DayCount))
                    aClass.planType = cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                    aClass.planWorkouts =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                    aClass.planMinutes = cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                    aClass.planTestDes = cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                    aClass.parentPlanId =
                        cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))
                    aClass.planThumbnail =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                    aClass.planTypeImage =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                    aClass.shortDes = cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                    aClass.introduction =
                        cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                    aClass.isPro =
                        cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                    aClass.hasSubPlan =
                        cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan)).equals("true")
                    arrPlan.add(aClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }
        return arrPlan
    }

    fun getHomeSubPlanList(parentPlanId: String): ArrayList<HomePlanTableClass> {

        val arrPlan: ArrayList<HomePlanTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            //val query = "Select * From $PlanTable where FIND_IN_SET $ParentPlanId = '$parentPlanId' order by $PlanSort"
            //val query = "SELECT * FROM $PlanTable WHERE $ParentPlanId  LIKE '%$parentPlanId,%' or $ParentPlanId  LIKE '%,$parentPlanId%' or $ParentPlanId  LIKE '$parentPlanId' order by $PlanSort"
            val query = "SELECT * FROM $PlanTable order by $PlanSort"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    var parentPlanIdsStr =
                        cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))

                    if (parentPlanIdsStr.isNullOrEmpty().not() && parentPlanIdsStr.equals("0")
                            .not()
                    ) {
                        var parentIdList = parentPlanIdsStr.split(",")

                        if (!parentIdList.isNullOrEmpty() && parentIdList.contains(parentPlanId)) {

                            val aClass = HomePlanTableClass()
                            aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                            aClass.planName =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                            aClass.planProgress =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                            aClass.planText =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                            aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                            aClass.planImage =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                            aClass.planDays =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                            aClass.days = cursor.getString(cursor.getColumnIndexOrThrow(DayCount))
                            aClass.planType =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                            aClass.planWorkouts =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                            aClass.planMinutes =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                            aClass.planTestDes =
                                cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                            aClass.parentPlanId = parentPlanId
                            aClass.planThumbnail =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                            aClass.planTypeImage =
                                cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                            aClass.shortDes =
                                cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                            aClass.introduction =
                                cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                            aClass.isPro =
                                cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                            aClass.hasSubPlan =
                                cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan))
                                    .equals("true")
                            arrPlan.add(aClass)

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }
        return arrPlan
    }

    fun getPlanByPlanId(planId: Int): HomePlanTableClass? {
        var aClass: HomePlanTableClass? = null
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()
            val query =
                "Select * From $PlanTable where $PlanId = '$planId'"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {

                cursor.moveToFirst()
                aClass = HomePlanTableClass()
                aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                aClass.planProgress =
                    cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                aClass.planText = cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                aClass.planImage = cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                aClass.planDays = cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                aClass.days = cursor.getString(cursor.getColumnIndexOrThrow(DayCount))
                aClass.planType = cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                aClass.planWorkouts =
                    cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                aClass.planMinutes = cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                aClass.planTestDes = cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                aClass.parentPlanId = cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))
                aClass.planThumbnail =
                    cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                aClass.planTypeImage =
                    cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                aClass.shortDes = cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                aClass.introduction =
                    cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                aClass.isPro =
                    cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                aClass.hasSubPlan =
                    cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan)).equals("true")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return aClass
    }

    // Todo reset Plan Day
    fun resetPlanDay(ExcList: ArrayList<HomeExTableClass>) {

        var db: SQLiteDatabase? = null

        val contentValues = ContentValues()
        contentValues.put(UpdatedExTime, "")
        contentValues.put(ReplaceExId, "")
        contentValues.put(IsCompleted, "0")
        contentValues.put(IsDeleted, "0")

        try {
            db = getReadWriteDB()
            for (item in ExcList) {
                val contentValues = ContentValues()
                contentValues.put(UpdatedExTime, "")
                contentValues.put(ReplaceExId, "")
                contentValues.put(PlanSort, item.defaultPlanSort)
                db.update(DayExTable, contentValues, "$DayExId = ${item.dayExId}", null)
            }
            // Todo clear  full body workout progress
            //db.update(DayExTable, contentValues, "$DayId = $strDayId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

    }

    fun reStartPlanDay(strDayId: String) {

        var db: SQLiteDatabase? = null

        val contentValues = ContentValues()
        contentValues.put(UpdatedExTime, "")
        //contentValues.put(ReplaceExId, "")
        contentValues.put(IsCompleted, "0")

        try {
            db = getReadWriteDB()
            // Todo clear  full body workout progress
            db.update(DayExTable, contentValues, "$DayId = $strDayId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

    }

    fun resetPlanExc(ExcList: ArrayList<HomeExTableClass>) {

        var db: SQLiteDatabase? = null

        val contentValues = ContentValues()
        contentValues.put(UpdatedExTime, "")
        contentValues.put(ReplaceExId, "")

        try {
            db = getReadWriteDB()
            for (item in ExcList) {
                val contentValues = ContentValues()
                contentValues.put(UpdatedExTime, "")
                contentValues.put(ReplaceExId, "")
                contentValues.put(PlanSort, item.defaultPlanSort)
                db.update(HomeExSingleTable, contentValues, "$DayExId = ${item.dayExId}", null)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

    }

    // Todo Edit exercise list
    fun getReplaceExList(strExId: String): ArrayList<ExTableClass> {

        val arrExTableClass: ArrayList<ExTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query =
                "SELECT * FROM $ExerciseTable WHERE $ExId != $strExId"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = ExTableClass()
                    aClass.exId = cursor.getString(cursor.getColumnIndexOrThrow(ExId))
                    aClass.exName = cursor.getString(cursor.getColumnIndexOrThrow(ExName))
                    aClass.exUnit = cursor.getString(cursor.getColumnIndexOrThrow(ExUnit))
                    aClass.exPath = cursor.getString(cursor.getColumnIndexOrThrow(ExPath))
                    aClass.exDescription =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExDescription))
                    aClass.exVideo = cursor.getString(cursor.getColumnIndexOrThrow(ExVideo))
                    aClass.exReplaceTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExReplaceTime))
                    aClass.exTime = cursor.getString(cursor.getColumnIndexOrThrow(ExTime))
                    arrExTableClass.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrExTableClass
    }

    fun getOriginalPlanExTime(dayExId: Int): String? {

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var exTime: String? = null

        val contentValues = ContentValues()
        contentValues.put(UpdatedExTime, "")

        try {
            db = getReadWriteDB()
            // Todo clear  full body workout progress
            val query = "Select $ExTime From $HomeExSingleTable WHERE $DayExId = $dayExId"
            cursor = db.rawQuery(query, null)
            cursor.moveToFirst()
            exTime = cursor.getString(cursor.getColumnIndexOrThrow(ExTime))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return exTime
    }

    fun getOriginalPlanExID(dayExId: Int): String? {

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var exTime: String? = null

        val contentValues = ContentValues()
        contentValues.put(UpdatedExTime, "")

        try {
            db = getReadWriteDB()
            // Todo clear  full body workout progress
            val query = "Select $ExId From $HomeExSingleTable WHERE $DayExId = $dayExId"
            cursor = db.rawQuery(query, null)
            cursor.moveToFirst()
            exTime = cursor.getString(cursor.getColumnIndexOrThrow(ExId))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return exTime
    }

    fun updatePlanExTime(dayExId: Int, strExTime: String): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        cv.put(UpdatedExTime, strExTime)

        try {
            db = getReadWriteDB()

            count = db.update(HomeExSingleTable, cv, "$DayExId = $dayExId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count > 0
    }

    fun updatePlanEx(item: HomeExTableClass): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        cv.put(PlanSort, item.planSort!!.toInt())
        cv.put(UpdatedExTime, item.exTime!!.toInt())
        cv.put(ReplaceExId, item.replaceExId!!.toInt())

        try {
            db = getReadWriteDB()

            count = db.update(HomeExSingleTable, cv, "$DayExId = ${item.dayExId}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count > 0
    }

    fun updateDayPlanEx(item: HomeExTableClass): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        if (item.planSort.isNullOrEmpty().not())
            cv.put(PlanSort, item.planSort!!.toInt())
        cv.put(UpdatedExTime, item.exTime!!.toInt())
        if(item.isDeleted.isNullOrEmpty())
            item.isDeleted = "0"
        cv.put(IsDeleted, item.isDeleted)
        if (item.replaceExId.isNullOrEmpty().not())
            cv.put(ReplaceExId, item.replaceExId!!.toInt())

        try {
            db = getReadWriteDB()

            count = db.update(DayExTable, cv, "$DayExId = ${item.dayExId}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count > 0
    }

    fun replaceExercise(strDayExId: String, strExId: String, strExTime: String): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        cv.put(ReplaceExId, strExId)
        cv.put(UpdatedExTime, strExTime)
        cv.put(ExTime, strExTime)

        try {
            db = getReadWriteDB()

            count = db.update(HomeExSingleTable, cv, "$DayExId = ?", arrayOf(strDayExId))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count > 0
    }

    // Todo Plan table Process
    fun getCompleteDayCountByPlanId(strId: String): Int {

        var completedCount = 0

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query =
                "Select $DayId From $PlanDaysTable where $PlanId = $strId And $IsCompleted = '1'"
            cursor = db.rawQuery(query, null)
            if (cursor != null) {
                completedCount = cursor.count
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return completedCount
    }

    // Todo Plan table Process
    fun getPlanNameByPlanId(strId: String): String {

        var planName = ""

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select $PlanName From $PlanTable where $PlanId = $strId"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }
        return planName
    }

    // Todo Getting Home Detail exercise data
    fun getHomeDetailExList(strPlanId: String): ArrayList<HomeExTableClass> {

        val arrDayExClass: ArrayList<HomeExTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "SELECT DX.$DayExId, DX.$PlanId," +
                    "       DX.$DayId," +
                    "       DX.$IsCompleted," +
                    "       DX.$UpdatedExTime," +
                    "       DX.$ReplaceExId," +
                    "       DX.$PlanSort," +
                    "       DX.$DefaultSort," +
                    "       CASE WHEN DX.$UpdatedExTime != ''" +
                    "       THEN DX.$UpdatedExTime" +
                    "       ELSE DX.$ExTime" +
                    "       END as $ExTime, " +
                    "       CASE WHEN DX.$ReplaceExId != ''" +
                    "       THEN DX.$ReplaceExId" +
                    "       ELSE DX.$ExId" +
                    "       END as $ExId, " +
                    "EX.$ExDescription, EX.$ExVideo,EX.$ExPath,EX.$ExName,Ex.$ExUnit FROM $HomeExSingleTable as DX " +
                    "INNER JOIN $ExerciseTable as EX ON " +
                    "(CASE WHEN DX.$ReplaceExId != ''" +
                    "       THEN DX.$ReplaceExId" +
                    "       ELSE DX.$ExId" +
                    "       END)" +
                    "= EX.$ExId WHERE DX.$PlanId = $strPlanId ORDER BY DX.$PlanSort"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = HomeExTableClass()
                    aClass.dayExId = cursor.getString(cursor.getColumnIndexOrThrow(DayExId))
                    aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                    aClass.dayId = cursor.getString(cursor.getColumnIndexOrThrow(DayId))
                    aClass.exId = cursor.getString(cursor.getColumnIndexOrThrow(ExId))
                    aClass.exTime = cursor.getString(cursor.getColumnIndexOrThrow(ExTime))
                    aClass.isCompleted = cursor.getString(cursor.getColumnIndexOrThrow(IsCompleted))
                    aClass.updatedExTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(UpdatedExTime))
                    aClass.replaceExId = cursor.getString(cursor.getColumnIndexOrThrow(ReplaceExId))
                    aClass.exName = cursor.getString(cursor.getColumnIndexOrThrow(ExName))
                    aClass.exUnit = cursor.getString(cursor.getColumnIndexOrThrow(ExUnit))
                    aClass.exPath = cursor.getString(cursor.getColumnIndexOrThrow(ExPath))
                    aClass.exDescription =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExDescription))
                    aClass.exVideo = cursor.getString(cursor.getColumnIndexOrThrow(ExVideo))
                    aClass.planSort = cursor.getString(cursor.getColumnIndexOrThrow(PlanSort))
                    aClass.defaultPlanSort =
                        cursor.getString(cursor.getColumnIndexOrThrow(DefaultSort))
                    arrDayExClass.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrDayExClass
    }

    fun updateCompleteHomeExByDayExId(strDayExId: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(IsCompleted, "1")

        try {
            db = getReadWriteDB()
            mCount = db.update(HomeExSingleTable, contentValues, "$DayExId = $strDayExId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    fun updateCompleteExByDayExId(strDayExId: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(IsCompleted, "1")

        try {
            db = getReadWriteDB()
            mCount = db.update(DayExTable, contentValues, "$DayExId = $strDayExId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    // Todo Reminder Table methods
    fun getRemindersList(): ArrayList<ReminderTableClass> {
        val arrReminder = ArrayList<ReminderTableClass>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From $ReminderTable order by $RId DESC"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val reminderClass = ReminderTableClass()
                    reminderClass.rId = cursor.getString(cursor.getColumnIndexOrThrow(RId))
                    reminderClass.remindTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(RemindTime))
                    reminderClass.days = cursor.getString(cursor.getColumnIndexOrThrow(Days))
                    reminderClass.isActive =
                        cursor.getString(cursor.getColumnIndexOrThrow(IsActive))
                    arrReminder.add(reminderClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrReminder
    }

    fun getRemindersListString(): String {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var reminders = ""
        try {
            db = getReadWriteDB()
            val query = "Select * From $ReminderTable order by $RId DESC"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    if (reminders.isNullOrEmpty()) {
                        reminders = cursor.getString(cursor.getColumnIndexOrThrow(RemindTime))
                    } else {
                        reminders = "$reminders, " + cursor.getString(
                            cursor.getColumnIndexOrThrow(RemindTime)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return reminders
    }

    fun getReminderById(mid: String): ReminderTableClass {
        val reminderClass = ReminderTableClass()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From $ReminderTable where $RId=$mid"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    reminderClass.rId = cursor.getString(cursor.getColumnIndexOrThrow(RId))
                    reminderClass.remindTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(RemindTime))
                    reminderClass.days = cursor.getString(cursor.getColumnIndexOrThrow(Days))
                    reminderClass.isActive =
                        cursor.getString(cursor.getColumnIndexOrThrow(IsActive))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return reminderClass
    }

    fun addReminder(reminderClass: ReminderTableClass): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(RemindTime, reminderClass.remindTime)
        contentValues.put(Days, reminderClass.days)
        contentValues.put(IsActive, reminderClass.isActive)

        try {
            db = getReadWriteDB()
            mCount = db.insert(ReminderTable, null, contentValues).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    fun deleteReminder(id: String): Boolean {
        var isSuccess = false
        var db: SQLiteDatabase? = null

        try {
            db = getReadWriteDB()
            val mCount = db.delete(ReminderTable, "$RId=?", arrayOf(id))
            if (mCount > 0) {
                isSuccess = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return isSuccess
    }

    fun updateReminder(strReminderId: String, strIsActive: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(IsActive, strIsActive)

        try {
            db = getReadWriteDB()
            mCount = db.update(ReminderTable, contentValues, "$RId = $strReminderId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    fun updateReminderDays(strReminderId: String, strDays: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(Days, strDays)

        try {
            db = getReadWriteDB()
            mCount = db.update(ReminderTable, contentValues, "$RId = $strReminderId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    fun updateReminderTimes(strReminderId: String, strTime: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(RemindTime, strTime)

        try {
            db = getReadWriteDB()
            mCount = db.update(ReminderTable, contentValues, "$RId = $strReminderId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    // Todo Weight related data
    fun getWeightList(): ArrayList<WeightTableClass> {

        val arrWeightTableClass: ArrayList<WeightTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()
            val query = "Select * From $WeightTable"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = WeightTableClass()
                    aClass.weightId = cursor.getString(cursor.getColumnIndexOrThrow(WeightId))
                    aClass.weightKg = cursor.getString(cursor.getColumnIndexOrThrow(WeightKg))
                    aClass.weightLb = cursor.getString(cursor.getColumnIndexOrThrow(WeightLb))
                    aClass.weightDate = cursor.getString(cursor.getColumnIndexOrThrow(WeightDate))
                    aClass.currentTimeStamp =
                        cursor.getString(cursor.getColumnIndexOrThrow(CurrentTimeStamp))
                    arrWeightTableClass.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrWeightTableClass
    }

    // Todo get Max Weight
    fun getMaxWeight(): String {

        var strMaxWeight = "0"
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val maxkg = "maxkg"
        try {
            db = getReadWriteDB()

            val query = "SELECT MAX(CAST($WeightKg as INTEGER)) as $maxkg from $WeightTable"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strMaxWeight = cursor.getString(cursor.getColumnIndex(maxkg))

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return strMaxWeight
    }

    // Todo get Min Weight
    fun getMinWeight(): String {

        var strMinWeight = "0"
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val minkg = "minkg"
        try {
            db = getReadWriteDB()

            val query = "SELECT MIN(CAST($WeightKg as INTEGER)) as $minkg from $WeightTable"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strMinWeight = cursor.getString(cursor.getColumnIndex(minkg))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return strMinWeight
    }

    // get weight for graph
    fun getUserWeightData(): ArrayList<HashMap<String, String>> {

        //val arrKg = ArrayList<String>()
        val arrDateChange = ArrayList<HashMap<String, String>>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

            val query =
                "Select * from $WeightTable where $WeightKg != '0' group by $WeightDate order by $WeightDate"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //                    if (!arrKg.contains(cursor.getString(cursor.getColumnIndexOrThrow(WeightKG)))) {
                    //arrKg.add(cursor.getString(cursor.getColumnIndexOrThrow(WeightKG)))
                    val hashMap = HashMap<String, String>()
                    hashMap.put("KG", cursor.getString(cursor.getColumnIndexOrThrow(WeightKg)))
                    hashMap.put(
                        "DT",
                        cursor.getString(cursor.getColumnIndexOrThrow(WeightDate))
                    )
                    arrDateChange.add(hashMap)
                    //                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrDateChange
    }

    //* Todo Update weight if weight exist *//*
    fun updateWeight(strDate: String, strWeightKG: String, strWeightLB: String): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        cv.put(WeightKg, strWeightKG)
        cv.put(WeightLb, strWeightLB)

        try {
            db = getReadWriteDB()

            count = db.update(WeightTable, cv, "$WeightDate = ?", arrayOf(strDate))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count > 0
    }

    //* Todo add user Weight query *//*
    fun addUserWeight(strWeightKG: String, strDate: String, strweightLB: String): Int {
        var count = 0
        var db: SQLiteDatabase? = null

        val row = ContentValues()
//        row.put(Id,"")
        row.put(WeightKg, strWeightKG)
        row.put(WeightDate, strDate)
        row.put(WeightLb, strweightLB)
        row.put(CurrentTimeStamp, Utils.parseTime(Date().time, "yyyy-MM-dd HH:mm:ss"))

        try {
            db = getReadWriteDB()
            count = db.insert(WeightTable, null, row).toInt()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count
    }

    //* Todo Check weight record is exist or not *//*
    fun weightExistOrNot(strDate: String): Boolean {

        var boolResult = false
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()

            val query = "Select * From $WeightTable Where $WeightDate = '$strDate'"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                boolResult = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }

            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return boolResult
    }

    fun getWeightForDate(strDate: String): String {

        var strMinWeight = "0"
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()

            val query = "Select * From $WeightTable Where $WeightDate = '$strDate'"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                strMinWeight = cursor.getString(cursor.getColumnIndexOrThrow(WeightKg))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }

            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return strMinWeight
    }

    //* Todo History *//*
    fun getHistoryList(): ArrayList<HistoryTableClass> {

        val arrWeightTableClass: ArrayList<HistoryTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()
            val query = "Select * From $HistoryTable"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = HistoryTableClass()
                    aClass.hId = cursor.getString(cursor.getColumnIndexOrThrow(HId))
                    aClass.hPlanName = cursor.getString(cursor.getColumnIndexOrThrow(HPlanName))
                    aClass.hPlanId = cursor.getString(cursor.getColumnIndexOrThrow(HPlanId))
                    aClass.hDayName = cursor.getString(cursor.getColumnIndexOrThrow(HDayName))
                    aClass.hBurnKcal = cursor.getString(cursor.getColumnIndexOrThrow(HBurnKcal))
                    aClass.hTotalEx = cursor.getString(cursor.getColumnIndexOrThrow(HTotalEx))
                    aClass.hKg = cursor.getString(cursor.getColumnIndexOrThrow(HKg))
                    aClass.hFeet = cursor.getString(cursor.getColumnIndexOrThrow(HFeet))
                    aClass.hInch = cursor.getString(cursor.getColumnIndexOrThrow(HInch))
                    aClass.hFeelRate = cursor.getString(cursor.getColumnIndexOrThrow(HFeelRate))
                    aClass.hCompletionTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(HCompletionTime))
                    aClass.hDateTime = cursor.getString(cursor.getColumnIndexOrThrow(HDateTime))
                    arrWeightTableClass.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrWeightTableClass
    }

    fun getHistoryTotalMinutes(): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totMinutesSum = 0

        try {
            db = getReadWriteDB()
            val query =
                "SELECT SUM(CAST($HCompletionTime as INTEGER)) as $HCompletionTime FROM $HistoryTable"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totMinutesSum = cursor.getInt(cursor.getColumnIndex(HCompletionTime))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return totMinutesSum
    }

    fun getHistoryTotalWorkout(): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val totWorkout = "totCompletionTime"
        var totWorkoutSum = 0

        try {
            db = getReadWriteDB()
            val query = "SELECT SUM(CAST($HTotalEx as INTEGER)) as $totWorkout FROM $HistoryTable"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totWorkoutSum = cursor.getInt(cursor.getColumnIndex(totWorkout))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return totWorkoutSum
    }

    fun getHistoryTotalKCal(): Float {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totKcalSum = 0f

        try {
            db = getReadWriteDB()
            val query = "SELECT SUM(CAST($HBurnKcal as Float)) as $HBurnKcal FROM $HistoryTable"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totKcalSum = cursor.getFloat(cursor.getColumnIndex(HBurnKcal))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return totKcalSum
    }

    fun getCompleteExerciseDate(): ArrayList<String> {

        val arrDt = ArrayList<String>()
        val arrDtTemp = ArrayList<String>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

            val query = "Select * from $HistoryTable"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (!arrDtTemp.contains
                            (
                            Utils.parseTime(
                                cursor.getString(cursor.getColumnIndexOrThrow(HDateTime)),
                                Constant.DATE_TIME_24_FORMAT,
                                Constant.DATE_FORMAT
                            )
                        )
                    ) {
                        arrDtTemp.add(
                            Utils.parseTime(
                                cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                        HDateTime
                                    )
                                ), Constant.DATE_TIME_24_FORMAT, Constant.DATE_FORMAT
                            )
                        )
                        arrDt.add(cursor.getString(cursor.getColumnIndexOrThrow(HDateTime)))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrDt
    }

    // Todo Get Weekly history data
    fun getWeekDayOfHistory(): ArrayList<HistoryWeekDataClass> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val weekStart = "WeekStart"
        val WeekEnd = "WeekEnd"
        val WeekNumber = "WeekNumber"
        val arrHistoryData = ArrayList<HistoryWeekDataClass>()

        try {
            db = getReadWriteDB()

            val query = "select strftime('%W', ${HDateTime}) $WeekNumber," +
                    "    max(date($HDateTime, 'weekday 0' ,'-6 day')) $weekStart," +
                    "    max(date($HDateTime, 'weekday 0', '-0 day')) $WeekEnd " +
                    "from $HistoryTable " +
                    "group by $WeekNumber"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    val historyWeekDataClass = HistoryWeekDataClass()

                    historyWeekDataClass.weekNumber =
                        cursor.getString(cursor.getColumnIndexOrThrow(WeekNumber))
                    historyWeekDataClass.weekStart =
                        cursor.getString(cursor.getColumnIndexOrThrow(weekStart))
                    historyWeekDataClass.weekEnd =
                        cursor.getString(cursor.getColumnIndexOrThrow(WeekEnd))
                    historyWeekDataClass.totKcal = getTotBurnWeekKcal(
                        historyWeekDataClass.weekStart,
                        historyWeekDataClass.weekEnd
                    )
                    historyWeekDataClass.totTime = getTotWeekWorkoutTime(
                        historyWeekDataClass.weekStart,
                        historyWeekDataClass.weekEnd
                    )

                    historyWeekDataClass.arrHistoryDetail = getWeekHistoryData(
                        historyWeekDataClass.weekStart,
                        historyWeekDataClass.weekEnd
                    )

                    historyWeekDataClass.totWorkout = historyWeekDataClass.arrHistoryDetail.size
                    arrHistoryData.add(historyWeekDataClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrHistoryData
    }

    fun getTotBurnWeekKcal(strWeekStart: String, strWeekEnd: String): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totKcal = 0
        try {
            db = getReadWriteDB()

            val query =
                "SELECT sum($HBurnKcal) as ${HBurnKcal} from $HistoryTable WHERE date('$strWeekStart') <= date($HDateTime) AND date('$strWeekEnd') >= date($HDateTime)"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()
                totKcal = cursor.getFloat(cursor.getColumnIndexOrThrow(HBurnKcal)).roundToInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return totKcal
    }

    fun getTotWeekWorkoutTime(strWeekStart: String, strWeekEnd: String): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totCompletionTime = 0
        try {
            db = getReadWriteDB()

            val query =
                "SELECT sum($HCompletionTime) as $HCompletionTime from $HistoryTable WHERE date('$strWeekStart') <= date($HDateTime) AND date('$strWeekEnd') >= date($HDateTime)"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()
                totCompletionTime = cursor.getInt(cursor.getColumnIndexOrThrow(HCompletionTime))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return totCompletionTime
    }

    fun getWeekHistoryData(
        strWeekStart: String,
        strWeekEnd: String
    ): ArrayList<HistoryDetailsClass> {

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val arrHistoryWeekDetails = ArrayList<HistoryDetailsClass>()
        try {
            db = getReadWriteDB()
//            val query = "SELECT * FROM tbl_history WHERE date('$strWeekStart') <= date(datetime) AND date('$strWeekEnd') >= date(datetime)"
            val query =
                "SELECT * FROM $HistoryTable WHERE date('$strWeekStart') <= date(${HDateTime}) AND date('$strWeekEnd') >= date(${HDateTime}) Order by $HId Desc "

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val historyDetailsClass = HistoryDetailsClass()
                    historyDetailsClass.HID =
                        cursor.getString(cursor.getColumnIndexOrThrow(HId))
                    historyDetailsClass.PlanId =
                        cursor.getString(cursor.getColumnIndexOrThrow(HPlanId))
                    historyDetailsClass.PlanName =
                        cursor.getString(cursor.getColumnIndexOrThrow(HPlanName))
                    historyDetailsClass.DateTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDateTime))
                    historyDetailsClass.CompletionTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(HCompletionTime))
                    historyDetailsClass.BurnKcal =
                        cursor.getString(cursor.getColumnIndexOrThrow(HBurnKcal))
                    historyDetailsClass.TotalWorkout =
                        cursor.getString(cursor.getColumnIndexOrThrow(HTotalEx))
                    historyDetailsClass.Kg = cursor.getString(cursor.getColumnIndexOrThrow(HKg))
                    historyDetailsClass.Feet = cursor.getString(cursor.getColumnIndexOrThrow(HFeet))
                    historyDetailsClass.Inch = cursor.getString(cursor.getColumnIndexOrThrow(HInch))
                    historyDetailsClass.FeelRate =
                        cursor.getString(cursor.getColumnIndexOrThrow(HFeelRate))
                    historyDetailsClass.DayName =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDayName))
                    historyDetailsClass.DayId =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDayId))
                    historyDetailsClass.planDetail = getPlanByPlanId(
                        cursor.getString(cursor.getColumnIndexOrThrow(HPlanId)).toInt()
                    )
//                    historyDetailsClass.WeekName = cursor.getString(cursor.getColumnIndexOrThrow(Week_name))

                    arrHistoryWeekDetails.add(historyDetailsClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrHistoryWeekDetails
    }

    // Todo Check User history available or not
    fun isHistoryAvailable(strDate: String): Boolean {
        var dtIsAvailable = false
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

//            val query = "Select * from $CompleteWorkoutTable"
            val query = "Select $HId From $HistoryTable " +
                    "Where " +
                    "DateTime(strftime('%Y-%m-%d', DateTime($HDateTime)))" +
                    "= " +
                    "DateTime(strftime('%Y-%m-%d', DateTime('$strDate')));"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                dtIsAvailable = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return dtIsAvailable
    }

    //* Todo History Methods  *//*
    fun addHistory(
        strPlanId: String,
        strPlanName: String,
        strDateTime: String,
        strCompletionTime: String,
        strBurnKcal: String,
        strTotalWorkout: String,
        strKg: String,
        strFeet: String,
        strInch: String,
        strFeelRate: String,
        strDayName: String,
        dayId: String
    ): Int {

        var db: SQLiteDatabase? = null
        var count = 0

        val cv = ContentValues()
        cv.put(HPlanId, strPlanId)
        cv.put(HPlanName, strPlanName)
        cv.put(HDateTime, strDateTime)
        cv.put(HCompletionTime, strCompletionTime)
        cv.put(HBurnKcal, strBurnKcal)
        cv.put(HTotalEx, strTotalWorkout)
        cv.put(HKg, strKg)
        cv.put(HFeet, strFeet)
        cv.put(HInch, strInch)
        cv.put(HFeelRate, strFeelRate)
        cv.put(HDayName, strDayName)
        cv.put(HDayId, dayId)

        try {
            db = getReadWriteDB()
            count = db.insert(HistoryTable, null, cv).toInt()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count
    }

    fun deleteHistory(historyId: String): Boolean {
        var isSuccess = false
        var db: SQLiteDatabase? = null

        try {
            db = getReadWriteDB()
            val mCount = db.delete(HistoryTable, "$HId=?", arrayOf(historyId))
            if (mCount > 0) {
                isSuccess = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return isSuccess
    }

    fun getPlanDayNameByDayId(strId: String): String {

        var dayName = ""

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select $DayName,$WeekName From $PlanDaysTable where $DayId = $strId"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                val weekName = cursor.getString(cursor.getColumnIndexOrThrow(WeekName))
                dayName = cursor.getString(cursor.getColumnIndexOrThrow(DayName))
                /*if (weekName.toInt() > 1) {
                    dayName = (cursor.getString(cursor.getColumnIndexOrThrow(DayName))
                        .toInt() + ((weekName.toInt() - 1) * 7)).toString()
                } else {
                    dayName = cursor.getString(cursor.getColumnIndexOrThrow(DayName))
                }*/
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return dayName
    }

    fun updatePlanDayCompleteByDayId(strDayId: String): Int {
        var mCount = 0
        var db: SQLiteDatabase? = null
        val contentValues = ContentValues()
        contentValues.put(IsCompleted, "1")

        try {
            db = getReadWriteDB()
            mCount = db.update(PlanDaysTable, contentValues, "$DayId = $strDayId", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return mCount
    }

    fun getRecentHistory(): HistoryDetailsClass? {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var historyDetailsClass: HistoryDetailsClass? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From $HistoryTable order by $HId DESC"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                historyDetailsClass = HistoryDetailsClass()
                historyDetailsClass.HID =
                    cursor.getString(cursor.getColumnIndexOrThrow(HId))
                historyDetailsClass.PlanId =
                    cursor.getString(cursor.getColumnIndexOrThrow(HPlanId))
                historyDetailsClass.PlanName =
                    cursor.getString(cursor.getColumnIndexOrThrow(HPlanName))
                historyDetailsClass.DateTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(HDateTime))
                historyDetailsClass.CompletionTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(HCompletionTime))
                historyDetailsClass.BurnKcal =
                    cursor.getString(cursor.getColumnIndexOrThrow(HBurnKcal))
                historyDetailsClass.TotalWorkout =
                    cursor.getString(cursor.getColumnIndexOrThrow(HTotalEx))
                historyDetailsClass.Kg = cursor.getString(cursor.getColumnIndexOrThrow(HKg))
                historyDetailsClass.Feet = cursor.getString(cursor.getColumnIndexOrThrow(HFeet))
                historyDetailsClass.Inch = cursor.getString(cursor.getColumnIndexOrThrow(HInch))
                historyDetailsClass.FeelRate =
                    cursor.getString(cursor.getColumnIndexOrThrow(HFeelRate))
                historyDetailsClass.DayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(HDayName))
                historyDetailsClass.DayId =
                    cursor.getString(cursor.getColumnIndexOrThrow(HDayId))
                historyDetailsClass.planDetail =
                    getPlanByPlanId(cursor.getString(cursor.getColumnIndexOrThrow(HPlanId)).toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return historyDetailsClass
    }

    fun getRecentHistoryList(): ArrayList<HistoryDetailsClass> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val arrRecent: ArrayList<HistoryDetailsClass> = ArrayList()
        try {
            db = getReadWriteDB()
            val query = "SELECT *" +
                    "  FROM HistoryTable AS HI," +
                    "       HomePlanTable AS HP" +
                    " WHERE HI.HPlanId = HP.PlanId" +
                    " GROUP BY HI.HPlanId" +
                    " ORDER BY HI.HId DESC"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val historyDetailsClass = HistoryDetailsClass()
                    historyDetailsClass.HID =
                        cursor.getString(cursor.getColumnIndexOrThrow(HId))
                    historyDetailsClass.PlanId =
                        cursor.getString(cursor.getColumnIndexOrThrow(HPlanId))
                    historyDetailsClass.PlanName =
                        cursor.getString(cursor.getColumnIndexOrThrow(HPlanName))
                    historyDetailsClass.DateTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDateTime))
                    historyDetailsClass.CompletionTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(HCompletionTime))
                    historyDetailsClass.BurnKcal =
                        cursor.getString(cursor.getColumnIndexOrThrow(HBurnKcal))
                    historyDetailsClass.TotalWorkout =
                        cursor.getString(cursor.getColumnIndexOrThrow(HTotalEx))
                    historyDetailsClass.Kg = cursor.getString(cursor.getColumnIndexOrThrow(HKg))
                    historyDetailsClass.Feet = cursor.getString(cursor.getColumnIndexOrThrow(HFeet))
                    historyDetailsClass.Inch = cursor.getString(cursor.getColumnIndexOrThrow(HInch))
                    historyDetailsClass.FeelRate =
                        cursor.getString(cursor.getColumnIndexOrThrow(HFeelRate))
                    historyDetailsClass.DayName =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDayName))
                    historyDetailsClass.DayId =
                        cursor.getString(cursor.getColumnIndexOrThrow(HDayId))

                    val aClass = HomePlanTableClass()
                    aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                    aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                    aClass.planProgress =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                    aClass.planText = cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                    aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                    aClass.planImage = cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                    aClass.planDays = cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                    aClass.days = cursor.getString(cursor.getColumnIndexOrThrow(DayCount))
                    aClass.planType = cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                    aClass.planWorkouts =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                    aClass.planMinutes = cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                    aClass.planTestDes = cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                    aClass.parentPlanId =
                        cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))
                    aClass.planThumbnail =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                    aClass.planTypeImage =
                        cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                    aClass.shortDes = cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                    aClass.introduction =
                        cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                    aClass.isPro =
                        cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                    aClass.hasSubPlan =
                        cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan)).equals("true")

                    historyDetailsClass.planDetail = aClass

                    arrRecent.add(historyDetailsClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrRecent
    }

    // restart progress
    fun restartProgress() {

        var db: SQLiteDatabase? = null

        val contentValues = ContentValues()
        contentValues.put(IsCompleted, "0")

        try {
            db = getReadWriteDB()
            // Todo clear ex plan multiple day table workout progress
            db.update(HomeExSingleTable, contentValues, null, null)

            // Todo clear ex single day table workout progress
            db.update(DayExTable, contentValues, null, null)

            // Todo clear lower body workout progress
            db.update(PlanDaysTable, contentValues, null, null)

            db.delete(HistoryTable, null, null)
            db.delete(WeightTable, null, null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

    }

    fun restartDayPlan(planId:String) {

        var db: SQLiteDatabase? = null

        val contentValues = ContentValues()
        contentValues.put(IsCompleted, "0")

        try {
            db = getReadWriteDB()

            // Todo clear ex single day table workout progress
            db.update(DayExTable, contentValues, "$PlanId = ${planId}", null)

            // Todo clear lower body workout progress
            db.update(PlanDaysTable, contentValues, "$PlanId = ${planId}", null)


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

    }

    // Todo Get Weekly Day wise Data
    fun getWorkoutWeeklyData(strCategoryName: String, planId: String): ArrayList<PWeeklyDayData> {

        val arrPWeeklyDayData = ArrayList<PWeeklyDayData>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            var query = ""

            //query = "SELECT $PlanId, group_concat(DISTINCT(CAST($DayName as INTEGER))) as $DayName, $WeekName, $DayId, $IsCompleted from $PlanDaysTable WHERE $PlanId = '$planId'  GROUP BY CAST($WeekName as INTEGER)"
            query = "SELECT  max($DayId) as DayId, $PlanId, group_concat(DISTINCT(CAST($DayName as INTEGER))) as $DayName, $WeekName, $IsCompleted from $PlanDaysTable where $PlanId = $planId GROUP BY CAST($WeekName as INTEGER)"


            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = PWeeklyDayData()
                    aClass.Workout_id = cursor.getString(cursor.getColumnIndex(PlanId))
                    aClass.dayId = cursor.getString(cursor.getColumnIndex(DayId))
                    aClass.Day_name = cursor.getString(cursor.getColumnIndex(DayName))
                    aClass.Week_name = cursor.getString(cursor.getColumnIndex(WeekName))
                    aClass.Is_completed = cursor.getString(cursor.getColumnIndex(IsCompleted))
                    aClass.categoryName = strCategoryName

                    aClass.arrWeekDayData = getWeekDaysData(aClass.Week_name, planId)

                    /*val aClass1 = PWeekDayData()
                    aClass1.Day_name = "Cup"

                    if (aClass.Is_completed == "1") {
                        aClass1.Is_completed = "1"
                    } else {
                        aClass1.Is_completed = "0"
                    }

                    aClass.arrWeekDayData.add(aClass1)*/
                    arrPWeeklyDayData.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
            }
        }
        return arrPWeeklyDayData
    }

    private fun getWeekDaysData(strWeekName: String, planId: String): ArrayList<PWeekDayData> {

        val arrWeekDayData = ArrayList<PWeekDayData>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {

            db = getReadWriteDB()
            var query = ""

            /*query = "select $DayName, $DayId ,$IsCompleted,$PlanMinutes,$PlanWorkouts FROM $PlanDaysTable " +
                        "WHERE $DayName IN ('1','2','3','4','5','6','7') " +
                        "AND $WeekName = '$strWeekName' GROUP by $DayName"*/
            query =
                "select $DayName, $DayId ,$IsCompleted,$PlanMinutes,$PlanWorkouts FROM $PlanDaysTable " +
                        "WHERE $WeekName = '$strWeekName' AND $PlanId = '$planId'"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = PWeekDayData()
                    aClass.Day_id = cursor.getString(cursor.getColumnIndex(DayId))
                    aClass.Day_name = cursor.getString(cursor.getColumnIndex(DayName))
                    aClass.Workouts = cursor.getString(cursor.getColumnIndex(PlanWorkouts))
                    aClass.Minutes = cursor.getString(cursor.getColumnIndex(PlanMinutes))
                    aClass.Is_completed = cursor.getString(cursor.getColumnIndex(IsCompleted))
                    arrWeekDayData.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
            }
        }

        return arrWeekDayData
    }

    fun getDaysPlanData(strDayId: String): PWeekDayData? {

        var pWeekDayData: PWeekDayData? = null
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {

            db = getReadWriteDB()
            var query = ""

            query = "select * FROM $PlanDaysTable " +
                    "WHERE $DayId = '$strDayId'"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()

                pWeekDayData = PWeekDayData()
                pWeekDayData.Day_id = cursor.getString(cursor.getColumnIndex(DayId))
                pWeekDayData.Day_name = cursor.getString(cursor.getColumnIndex(DayName))
                pWeekDayData.Workouts = cursor.getString(cursor.getColumnIndex(PlanWorkouts))
                pWeekDayData.Minutes = cursor.getString(cursor.getColumnIndex(PlanMinutes))
                pWeekDayData.Is_completed = cursor.getString(cursor.getColumnIndex(IsCompleted))

            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
            }
        }

        return pWeekDayData
    }

    // Todo Getting day exercise data
    fun getDayExList(strDayId: String): ArrayList<HomeExTableClass> {

        val arrDayExClass: ArrayList<HomeExTableClass> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "SELECT DX.$DayExId, DX.$PlanId," +
                    "       DX.$DayId," +
                    "       DX.$IsCompleted," +
                    "       DX.$UpdatedExTime," +
                    "       DX.$ReplaceExId," +
                    "       DX.$IsDeleted," +
                    "       DX.$PlanSort," +
                    "       DX.$DefaultSort," +
                    "       CASE WHEN DX.$UpdatedExTime != ''" +
                    "       THEN DX.$UpdatedExTime" +
                    "       ELSE DX.$ExTime" +
                    "       END as $ExTime, " +
                    "       CASE WHEN DX.$ReplaceExId != ''" +
                    "       THEN DX.$ReplaceExId" +
                    "       ELSE DX.$ExId" +
                    "       END as $ExId, " +
                    "EX.$ExDescription, EX.$ExVideo,EX.$ExPath,EX.$ExName,Ex.$ExUnit FROM $DayExTable as DX " +
                    "INNER JOIN $ExerciseTable as EX ON " +
                    "(CASE WHEN DX.$ReplaceExId != ''" +
                    "       THEN DX.$ReplaceExId" +
                    "       ELSE DX.$ExId" +
                    "       END)" +
                    "= EX.$ExId WHERE DX.$DayId = $strDayId AND DX.$IsDeleted = '0' ORDER BY DX.$PlanSort"

            //            val query =
            //                "SELECT DX.*, EX.$ExDescription, EX.$ExVideo,EX.$ExPath,EX.$ExName,Ex.$ExUnit FROM $DayExTable as DX INNER JOIN $ExerciseTable as EX ON DX.$ExId = EX.$ExId WHERE $DayId = $strDayId"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = HomeExTableClass()
                    aClass.dayExId = cursor.getString(cursor.getColumnIndexOrThrow(DayExId))
                    aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                    aClass.dayId = cursor.getString(cursor.getColumnIndexOrThrow(DayId))
                    aClass.exId = cursor.getString(cursor.getColumnIndexOrThrow(ExId))
                    aClass.exTime = cursor.getString(cursor.getColumnIndexOrThrow(ExTime))
                    aClass.isCompleted = cursor.getString(cursor.getColumnIndexOrThrow(IsCompleted))
                    aClass.isDeleted = cursor.getString(cursor.getColumnIndexOrThrow(IsDeleted))
                    aClass.updatedExTime =
                        cursor.getString(cursor.getColumnIndexOrThrow(UpdatedExTime))
                    aClass.replaceExId = cursor.getString(cursor.getColumnIndexOrThrow(ReplaceExId))
                    aClass.exName = cursor.getString(cursor.getColumnIndexOrThrow(ExName))
                    aClass.exUnit = cursor.getString(cursor.getColumnIndexOrThrow(ExUnit))
                    aClass.exPath = cursor.getString(cursor.getColumnIndexOrThrow(ExPath))
                    aClass.planSort = cursor.getString(cursor.getColumnIndexOrThrow(PlanSort))
                    aClass.defaultPlanSort =
                        cursor.getString(cursor.getColumnIndexOrThrow(DefaultSort))
                    aClass.exDescription =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExDescription))
                    aClass.exVideo = cursor.getString(cursor.getColumnIndexOrThrow(ExVideo))
                    arrDayExClass.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return arrDayExClass
    }


    fun getCompleteDayExList(strDayId: String): Int {

        var count = 0
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query =
                "Select $DayExId From $DayExTable where $DayId = $strDayId AND $IsCompleted = 1 AND $IsDeleted = '0'"

            cursor = db.rawQuery(query, null)
            count = cursor.count

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && db.isOpen) {
                db.close()
                db.releaseReference()
            }
        }

        return count
    }


    /* fun getDiscoverPlanList(catName: String): ArrayList<HomePlanTableClass> {

         val arrPlan: ArrayList<HomePlanTableClass> = ArrayList()

         var db: SQLiteDatabase? = null
         var cursor: Cursor? = null
         try {
             db = getReadWriteDB()
             val query =
                 "Select * From $PlanTable where $DiscoverCatName = '$catName' order by $PlanSort"
             cursor = db.rawQuery(query, null)
             if (cursor != null && cursor.count > 0) {
                 while (cursor.moveToNext()) {
                     val aClass = HomePlanTableClass()
                     aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                     aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                     aClass.planProgress =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                     aClass.planText = cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                     aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                     aClass.planImage = cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                     aClass.planDays = cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                     aClass.planType = cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                     aClass.planWorkouts =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                     aClass.planMinutes = cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                     aClass.planTestDes = cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                     aClass.parentPlanId = cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))
                     aClass.planThumbnail =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                     aClass.planTypeImage =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                     aClass.shortDes = cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                     aClass.introduction =
                         cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                     aClass.discoverIcon =
                         cursor.getString(cursor.getColumnIndexOrThrow(DiscoverIcon))
                     aClass.isPro =
                         cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                     aClass.hasSubPlan =
                         cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan)).equals("true")
                     arrPlan.add(aClass)
                 }
             }
         } catch (e: Exception) {
             e.printStackTrace()
         } finally {
             if (cursor != null && !cursor.isClosed) {
                 cursor.close()
             }
             if (db != null && db.isOpen) {
                 db.close()
                 db.releaseReference()
             }
         }
         return arrPlan
     }


     fun getRandomDiscoverPlan(): HomePlanTableClass {

         val aClass = HomePlanTableClass()

         var db: SQLiteDatabase? = null
         var cursor: Cursor? = null
         try {
             db = getReadWriteDB()
             val query = "SELECT * FROM  $PlanTable WHERE $DiscoverCatName != '' AND $HasSubPlan = 'false' ORDER BY RANDOM() LIMIT 1;"


             cursor = db.rawQuery(query, null)
             if (cursor != null && cursor.count > 0) {
                 while (cursor.moveToNext()) {

                     aClass.planId = cursor.getString(cursor.getColumnIndexOrThrow(PlanId))
                     aClass.planName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                     aClass.planProgress =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanProgress))
                     aClass.planText = cursor.getString(cursor.getColumnIndexOrThrow(PlanText))
                     aClass.planLvl = cursor.getString(cursor.getColumnIndexOrThrow(PlanLvl))
                     aClass.planImage = cursor.getString(cursor.getColumnIndexOrThrow(PlanImage))
                     aClass.planDays = cursor.getString(cursor.getColumnIndexOrThrow(PlanDays))
                     aClass.planType = cursor.getString(cursor.getColumnIndexOrThrow(PlanType))
                     aClass.planWorkouts =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanWorkouts))
                     aClass.planMinutes = cursor.getString(cursor.getColumnIndexOrThrow(PlanMinutes))
                     aClass.planTestDes = cursor.getString(cursor.getColumnIndexOrThrow(TestDes))
                     aClass.parentPlanId = cursor.getString(cursor.getColumnIndexOrThrow(ParentPlanId))
                     aClass.planThumbnail =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanThumbnail))
                     aClass.planTypeImage =
                         cursor.getString(cursor.getColumnIndexOrThrow(PlanTypeImage))
                     aClass.shortDes = cursor.getString(cursor.getColumnIndexOrThrow(ShortDes))
                     aClass.introduction =
                         cursor.getString(cursor.getColumnIndexOrThrow(Introduction))
                     aClass.discoverIcon =
                         cursor.getString(cursor.getColumnIndexOrThrow(DiscoverIcon))
                     aClass.isPro =
                         cursor.getString(cursor.getColumnIndexOrThrow(IsPro)).equals("true")
                     aClass.hasSubPlan =
                         cursor.getString(cursor.getColumnIndexOrThrow(HasSubPlan)).equals("true")
                 }
             }
         } catch (e: Exception) {
             e.printStackTrace()
         } finally {
             if (cursor != null && !cursor.isClosed) {
                 cursor.close()
             }
             if (db != null && db.isOpen) {
                 db.close()
                 db.releaseReference()
             }
         }
         return aClass
     }*/


}
