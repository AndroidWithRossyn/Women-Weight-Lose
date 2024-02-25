package com.loseweight.utils

import android.os.Environment
import java.io.File


internal object Constant {
    val FINISH_ACTIVITY = "finish_activity"
    val FOLDER_NAME = "Stretching Exercises"
    val CACHE_DIR = ".StretchingExercises/Cache"
    val TMP_DIR = (Environment
        .getExternalStorageDirectory().absolutePath
            + File.separator
            + FOLDER_NAME + "/tmp")
    val PATH = Environment.getExternalStorageDirectory()
        .absolutePath + File.separator + "" + FOLDER_NAME
    val FOLDER_RIDEINN_PATH = (Environment
        .getExternalStorageDirectory().absolutePath
            + File.separator
            + ".StretchingExercises")
    val USER_LATITUDE = "lat"
    const val APP_JSON = "application/json"
    val USER_LONGITUDE = "longi"
    val LOGIN_INFO = "login_info"
    val CONFIG_INFO = "config_info"
    val THEME_COLOR = "#1C2166"
    val ARROW = "=>"
    val ERROR_CODE = -1
    val STATUS_ERROR_CODE = 5001
    val STATUS_SUCCESS_CODE = 5002
    val STATUS_SUCCESS_EXISTS_CODE = 5003
    val STATUS_SUCCESS_NOT_EXISTS_CODE = 5004
    val STATUS_SUCCESS_EMPTY_LIST_CODE = 5005

    val CONNECTIVITY_CHANGE = "CONNECTIVITY_CHANGE"

    const val WORKOUT_TIME_FORMAT = "mm:ss"
    const val SEC_DURATION_CAL = 0.08
    const val DEFAULT_REST_TIME = 15L
    const val DEFAULT_READY_TO_GO_TIME = 15L
    const val CapDateFormatDisplay = "yyyy-MM-dd HH:mm:ss"

    const val PREF_FIRST_DAY_OF_WEEK = "PREF_FIRST_DAY_OF_WEEK"
    val PREF_IS_INSTRUCTION_SOUND_ON ="pref_is_instruction_sound_on"
    val PREF_IS_COACH_SOUND_ON ="pref_is_coach_sound_on"
    val PREF_IS_SOUND_MUTE ="pref_is_sound_mute"
    val PREF_IS_FIRST_TIME ="pref_is_first_time"
    val PREF_IS_WATER_TRACKER_ON ="pref_is_water_tracker_on"
    val PREF_READY_TO_GO_TIME ="pref_ready_to_go_time"
    val PREF_REST_TIME ="pref_rest_time"
    val PREF_IS_REMINDER_SET ="pref_is_reminder_set"

    const val PREF_GOAL = "PREF_GOAL"
    const val PREF_WEIGHT_UNIT = "PREF_WEIGHT_UNIT"
    const val PREF_HEIGHT_UNIT = "PREF_HEIGHT_UNIT"
    const val PREF_TARGET_WEIGHT = "PREF_TARGET_WEIGHT"
    const val PREF_LAST_INPUT_WEIGHT = "PREF_LAST_INPUT_WEIGHT"
    const val PREF_LAST_INPUT_FOOT = "PREF_LAST_INPUT_FOOT"
    const val PREF_LAST_INPUT_INCH = "PREF_LAST_INPUT_INCH"
    const val PREF_DOB = "PREF_DOB"
    const val PREF_GENDER = "PREF_GENDER"

    val PREF_LANGUAGE= "pref_language"
    val PREF_LANGUAGE_NAME= "pref_language_name"

    val PREF_WATER_TRACKER_DATE ="pref_water_tracker_date"
    val PREF_WATER_TRACKER_GLASS ="pref_water_tracker_glass"

    const val PREF_KEY_PURCHASE_STATUS = "KeyPurchaseStatus"

    const val DEF_KG = "KG"
    const val DEF_LB = "LB"
    const val DEF_IN = "IN"
    const val DEF_FT = "FT"
    const val DEF_CM = "CM"

    const val MALE = "Male"
    const val FEMALE = "Female"

    const val extraReminderId = "extraReminderId"

    const val WEIGHT_TABLE_DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_TIME_24_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT = "yyyy-MM-dd"

    const val extra_day_id = "extra_day_id"
    const val workout_type_step = "s"

    const val PlanDaysYes = "YES"
    const val PlanDaysNo = "NO"

    const val PlanLvlTitle = "Title"
    const val PlanLvlBeginner = "Beginner"
    const val PlanLvlIntermediate = "Intermediate"
    const val PlanLvlAdvanced = "Advanced"

    const val PlanTypeMainGoals = "MainGoals"
    const val PlanTypeFastWorkoutFatBurning = "FastWorkoutFatBurning"
    const val PlanTypeFastWorkoutTrainingGoal = "FastWorkoutTrainingGoal"
    const val PlanTypeFastWorkoutRandom = "FastWorkoutRandom"
    const val PlanTypeBodyFocus = "BodyFocus"
    const val PlanTypeSubPlan = "SubPlan"


    const val FROM_FAST_WORKOUT = "FastWorkOut"
    const val FROM_DRINK_NOTIFICATION = "from_drink_notification"

    const val EXTRA_REMINDER_ID = "Reminder_ID"

    /*KG*/
    const val MIN_KG = 20
    const val MAX_KG = 997

    /*LB*/
    const val MIN_LB = 44
    const val MAX_LB = 2200

    /*FT*/
    const val MIN_FT = 0
    const val MAX_FT = 13

    /*IN*/
    const val MIN_IN = 0
    const val MAX_IN = 11

    /*CM*/
    const val MIN_CM = 20
    const val MAX_CM = 400

    const val MONTHLY_SKU = "monthly_sub"
    const val YEARLY_SKU = "yearly_sub"

    var FB_BANNER_TYPE_AD = "FB_BANNER_TYPE_AD"
    var GOOGLE_BANNER_TYPE_AD = "GOOGLE_BANNER_TYPE_AD"
    var GOOGLE_BANNER = "GOOGLE_BANNER"
    var GOOGLE_INTERSTITIAL = "GOOGLE_INTERSTITIAL"
    var GOOGLE_REWARDED = "GOOGLE_REWARDED"
    var FB_BANNER = "FB_BANNER"
    var FB_INTERSTITIAL = "FB_INTERSTITIAL"
    var FB_REWARDED = "FB_REWARDED"
    var AD_TYPE_FB_GOOGLE = "AD_TYPE_FB_GOOGLE"
    var STATUS_ENABLE_DISABLE = "STATUS_ENABLE_DISABLE"

    var GOOGLE_ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val GOOGLE_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val GOOGLE_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    const val FB_BANNER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
    const val FB_INTERSTITIAL_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"

    var AD_FACEBOOK = "facebook"
    var AD_GOOGLE = "google"
    val AD_TYPE_FACEBOOK_GOOGLE = AD_GOOGLE

    var ENABLE = "Enable"
    var DISABLE = "Disable"
    val ENABLE_DISABLE = ENABLE

    var START_BTN_COUNT = "start_btn_count"
    var EXIT_BTN_COUNT = "exit_btn_count"
}