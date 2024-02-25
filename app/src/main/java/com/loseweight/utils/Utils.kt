package com.loseweight.utils

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
//import com.loseweight.BuildConfig
import com.loseweight.R
import com.loseweight.adapter.SpinnerSelAdapter
import com.loseweight.interfaces.SpinnerCallback
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.objects.LoginData
import com.loseweight.objects.Spinner
import com.loseweight.utils.reminder.AlarmReceiver
import com.utillity.db.DataHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt

internal object Utils {
    val TAG = Utils::class.java.name + Constant.ARROW
    fun setPref(c: Context, pref: String, `val`: String) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putString(pref, `val`)
        e.apply()
    }

    fun getPref(c: Context, pref: String, `val`: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(
            pref,
            `val`
        )
    }

    fun setPref(c: Context, pref: String, `val`: Boolean) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putBoolean(pref, `val`)
        e.apply()
    }

    fun getPref(c: Context, pref: String, `val`: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
            pref, `val`
        )
    }

    fun delPref(c: Context, pref: String) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.remove(pref)
        e.apply()
    }

    fun setPref(c: Context, pref: String, `val`: Int) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putInt(pref, `val`)
        e.apply()

    }

    fun getPref(c: Context, pref: String, `val`: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(
            pref,
            `val`
        )
    }

    fun setPref(c: Context, pref: String, `val`: Long) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putLong(pref, `val`)
        e.apply()
    }

    fun getPref(c: Context, pref: String, `val`: Long): Long {
        return PreferenceManager.getDefaultSharedPreferences(c).getLong(
            pref,
            `val`
        )
    }

    fun setPref(c: Context, pref: String, `val`: Float) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putFloat(pref, `val`)
        e.apply()

    }

    fun getPref(c: Context, pref: String, `val`: Float): Float {
        return PreferenceManager.getDefaultSharedPreferences(c).getFloat(
            pref,
            `val`
        )
    }


    fun setPref(c: Context, file: String, pref: String, `val`: String) {
        val settings = c.getSharedPreferences(
            file,
            Context.MODE_PRIVATE
        )
        val e = settings.edit()
        e.putString(pref, `val`)
        e.apply()

    }

    fun getPref(c: Context, file: String, pref: String, `val`: String): String? {
        return c.getSharedPreferences(file, Context.MODE_PRIVATE).getString(
            pref, `val`
        )
    }

    fun validateEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches()
        }
    }

    fun validate(target: String, pattern: String): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        } else {
            val r = Pattern.compile(pattern)
            return r.matcher(target).matches()
        }
    }

    fun isAlphaNumeric(target: String): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        } else {
            val r = Pattern.compile("^[a-zA-Z0-9]*$")
            return r.matcher(target)
                .matches()
        }
    }

    fun isNumeric(target: String): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        } else {
            val r = Pattern.compile("^[0-9]*$")
            return r.matcher(target)
                .matches()
        }
    }

    fun getDeviceWidth(context: Context): Int {
        try {
            val metrics = context.resources.displayMetrics
            return metrics.widthPixels
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return 480
    }

    fun getDeviceHeight(context: Context): Int {
        try {
            val metrics = context.resources.displayMetrics
            return metrics.heightPixels
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return 800
    }

    fun isInternetConnected(mContext: Context?): Boolean {
        var outcome = false

        try {
            if (mContext != null) {
                val cm = mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfos = cm.allNetworkInfo

                for (tempNetworkInfo in networkInfos) {
                    if (tempNetworkInfo.isConnected) {
                        outcome = true
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outcome
    }

    fun getDeviceId(c: Context): String {
        var aid: String?
        try {
            aid = ""
            aid = Settings.Secure.getString(
                c.contentResolver,
                "android_id"
            )
            if (aid == null) {
                aid = "No DeviceId"
            } else if (aid.length <= 0) {
                aid = "No DeviceId"
            }
        } catch (e: Exception) {
            sendExceptionReport(e)
            aid = "No DeviceId"
        }
        return aid!!
    }

    fun random(min: Float, max: Float): Float {
        return (min + Math.random() * (max - min + 1)).toFloat()
    }

    fun random(min: Int, max: Int): Int {
        return Math.round((min + Math.random() * (max - min + 1)).toFloat())
    }

    fun hasFlashFeature(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_FLASH
        )
    }

    fun hasCameraFeature(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA
        )
    }

    fun hideKeyBoard(c: Context, v: View) {
        val imm = c
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }


    fun getBold(c: Context): Typeface? {
        try {
            return Typeface.createFromAsset(
                c.assets,
                "roboto-bold.ttf"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getMedium(c: Context): Typeface? {
        try {
            return Typeface.createFromAsset(
                c.assets,
                "roboto-medium.ttf"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getNormal(c: Context): Typeface? {
        try {
            return Typeface.createFromAsset(
                c.assets,
                "roboto-regular.ttf"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun passwordValidator(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        //        String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
        val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{6,15}$"


        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun formatNo(str: String): String? {
        val number = removeComma(nullSafe(str))
        return if (!StringUtils.isEmpty(number)) {

            //            if (!finalStr.startsWith("$"))
            //                finalStr = "$" + finalStr;
            formatToComma(number)
        } else number

    }

    fun `formatNo$`(str: String): String {
        val number = removeComma(nullSafe(str))
        if (!StringUtils.isEmpty(number)) {

            var finalStr = formatToComma(number)

            if (!finalStr!!.startsWith("$"))
                finalStr = "$$finalStr"
            return finalStr
        }

        return number

    }

    fun formatToComma(str: String): String? {
        var number: String? =
            removeComma(nullSafe(str))
        if (!StringUtils.isEmpty(number)) {

            var finalStr: String
            if (number!!.contains(".")) {
                number = truncateUptoTwoDecimal(number)
                val decimalFormat = DecimalFormat("#.##")
                finalStr = decimalFormat.format(BigDecimal(number!!))
            } else {
                finalStr = number
            }

            finalStr =
                NumberFormat.getNumberInstance(Locale.US).format(java.lang.Double.valueOf(finalStr))
            return finalStr
        }

        return number
    }

    fun truncateUptoTwoDecimal(doubleValue: String): String? {
        if (doubleValue != null) {
            var result: String = doubleValue
            val decimalIndex = result.indexOf(".")
            if (decimalIndex != -1) {
                val decimalString = result.substring(decimalIndex + 1)
                if (decimalString.length > 2) {
                    result = doubleValue.substring(0, decimalIndex + 3)
                } else if (decimalString.length == 1) {
                    //                    result = String.format(Locale.ENGLISH, "%.2f",
                    //                            Double.parseDouble(value));
                }
            }
            return result
        }

        return doubleValue
    }

    fun removeComma(str: String): String {
        var str = str
        try {
            if (!StringUtils.isEmpty(str)) {
                str = str.replace(",".toRegex(), "")
                try {
                    val format = NumberFormat.getCurrencyInstance()
                    val number = format.parse(str)
                    return number.toString()
                } catch (e: ParseException) {
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Debug.e("removeComma", "" + str)
        return str
    }

    fun getRowFadeSpeedAnimation(c: Context): LayoutAnimationController {
        val anim = AnimationUtils.loadAnimation(
            c,
            R.anim.raw_fade
        ) as AlphaAnimation
        return LayoutAnimationController(
            anim, 0.3f
        )
    }


    fun sendExceptionReport(e: Exception) {
        e.printStackTrace()

        try {
            // Writer result = new StringWriter();
            // PrintWriter printWriter = new PrintWriter(result);
            // e.printStackTrace(printWriter);
            // String stacktrace = result.toString();
            // new CustomExceptionHandler(c, URLs.URL_STACKTRACE)
            // .sendToServer(stacktrace);
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

    }


    fun getAndroidId(c: Context): String {
        var aid: String?
        try {
            aid = ""
            aid = Settings.Secure.getString(
                c.contentResolver,
                "android_id"
            )

            if (aid == null) {
                aid = "No DeviceId"
            } else if (aid.length <= 0) {
                aid = "No DeviceId"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            aid = "No DeviceId"
        }

        Debug.e("", "aid : " + aid!!)

        return aid

    }

    fun getAppVersionCode(c: Context): Int {
        try {
            return c.packageManager.getPackageInfo(c.packageName, 0).versionCode
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return 0

    }

    fun getPhoneModel(c: Context): String {

        try {
            return Build.MODEL
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return ""
    }

    fun getPhoneBrand(c: Context): String {

        try {
            return Build.BRAND
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return ""
    }

    fun getOsVersion(c: Context): String {

        try {
            return Build.VERSION.RELEASE
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return ""
    }

    fun getOsAPILevel(c: Context): Int {

        try {
            return Build.VERSION.SDK_INT
        } catch (e: Exception) {
            sendExceptionReport(e)
        }

        return 0
    }

    fun parseCalendarFormat(c: Calendar, pattern: String): String {
        val sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        return sdf.format(c.time)
    }

    fun parseTime(time: Long, pattern: String): String {
        val sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        return sdf.format(Date(time))
    }

    fun parseTime(time: String, pattern: String): Date {
        val sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        try {
            return sdf.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Date()
    }

    fun parseTimeForDate(time: Long, pattern: String): Date {
        Debug.e("timezone", TimeZone.getDefault().id)
        val sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )

        try {
            return sdf.parse(sdf.format(Date(time)))

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Date()
    }


    fun parseTime(
        time: String, fromPattern: String,
        toPattern: String
    ): String {
        var sdf = SimpleDateFormat(
            fromPattern,
            Locale.getDefault()
        )
        try {
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(toPattern, Locale.getDefault())
            return sdf.format(d)
        } catch (e: Exception) {
            Debug.i("parseTime", "" + e.message)
        }

        return ""
    }

    fun parseTimeUTCtoDefault(time: String, pattern: String): Date {

        var sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.parse(sdf.format(d))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Date()
    }

    fun parseTimeUTCtoDefault(time: Long): Date {

        try {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.timeInMillis = time
            val d = cal.time
            val sdf = SimpleDateFormat()
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.parse(sdf.format(d))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Date()
    }

    fun parseTimeUTCtoDefault(time: String, fromPattern: String, toPattern: String): String {
        var sdf = SimpleDateFormat(
            fromPattern,
            Locale.getDefault()
        )
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(toPattern, Locale.getDefault())
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun parseTimeUTCtoDefaultGerman(time: String, fromPattern: String, toPattern: String): String {
        var sdf = SimpleDateFormat(
            fromPattern,
            Locale.getDefault()
        )
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(toPattern, Locale.GERMAN)
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun parseTimeUTCtoDefaultEnglish(time: String, fromPattern: String, toPattern: String): String {

        var sdf = SimpleDateFormat(
            fromPattern,
            Locale.getDefault()
        )
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(toPattern, Locale.ENGLISH)
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun parseTimeUTCtoDefaultTurkey(time: String, fromPattern: String, toPattern: String): String {
        var sdf = SimpleDateFormat(
            fromPattern,
            Locale.getDefault()
        )
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val d = sdf.parse(time)
            sdf = SimpleDateFormat(toPattern, Locale("tr", "TR"))
            sdf.timeZone = Calendar.getInstance().timeZone
            return sdf.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertToTimeZone(
        date: String,
        frompattern: String,
        topattern: String,
        defaultzoneid: String,
        convertzoneid: String
    ): String {
        //Debug.e("TAG", "convertToTimeZone() called with: date = [" + date + "], frompattern = [" + frompattern + "], topattern = [" + topattern + "], defaultzoneid = [" + defaultzoneid + "], convertzoneid = [" + convertzoneid + "]");
        var returnDate = date
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(convertzoneid))
            //Debug.e("Convert time zone", TimeZone.getDefault().getID());
            val inputDate = SimpleDateFormat(frompattern).parse(date)
            //Debug.e("Input Time", inputDate.toString());
            TimeZone.setDefault(TimeZone.getTimeZone(defaultzoneid))
            //Debug.e("Output time zone", TimeZone.getDefault().getID());
            val dateFormatGmt = SimpleDateFormat(topattern)
            dateFormatGmt.timeZone = TimeZone.getTimeZone(defaultzoneid)
            val dateFormatDefautl = SimpleDateFormat(topattern)
            val date1 = dateFormatDefautl.parse(dateFormatGmt.format(inputDate))
            val dateFormat = SimpleDateFormat(topattern)
            returnDate = dateFormat.format(date1)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return returnDate
        }
    }

    fun daysBetween(startDate: Date, endDate: Date): Long {
        val sDate = getDatePart(startDate)
        val eDate = getDatePart(endDate)
        var daysBetween: Long = 0
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }

    fun getFullDayName(day: Int): String {
        val c = Calendar.getInstance()
        // date doesn't matter - it has to be a Monday
        // I new that first August 2011 is one ;-)
        c.set(2011, 7, 1, 0, 0, 0)
        c.add(Calendar.DAY_OF_MONTH, day)
        return String.format("%tA", c)
    }

    fun getDatePart(date: Date): Calendar {
        val cal = Calendar.getInstance()       // get calendar instance
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)            // set hour to midnight
        cal.set(Calendar.MINUTE, 0)                 // set minute in hour
        cal.set(Calendar.SECOND, 0)                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0)            // set millisecond in second
        return cal                                  // return the date part
    }

    fun nullSafe(content: String?): String {
        if (content == null) {
            return ""
        }
        return if (content.equals("null", ignoreCase = true)) {
            ""
        } else content
    }

    fun nullSafe(content: String, defaultStr: String): String {
        if (StringUtils.isEmpty(content)) {
            return defaultStr
        }
        return if (content.equals("null", ignoreCase = true)) {
            defaultStr
        } else content

    }

    fun nullSafeDash(content: String): String {
        if (StringUtils.isEmpty(content)) {
            return "-"
        }
        return if (content.equals("null", ignoreCase = true)) {
            "-"
        } else content
    }

    fun nullSafe(content: Int, defaultStr: String): String {
        return if (content == 0) {
            defaultStr
        } else "" + content
    }


    fun isSDcardMounted(): Boolean {
        val state = Environment.getExternalStorageState()
        return if (state == Environment.MEDIA_MOUNTED && state != Environment.MEDIA_MOUNTED_READ_ONLY) {
            true
        } else false
    }

    fun isGPSProviderEnabled(context: Context): Boolean {
        val locationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun isNetworkProviderEnabled(context: Context): Boolean {
        val locationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isLocationProviderEnabled(context: Context): Boolean {
        return isGPSProviderEnabled(context) || isNetworkProviderEnabled(
            context
        )
    }

    fun isLocationProviderRequired(context: Context): Boolean {
        val lang =
            getPref(context, Constant.USER_LONGITUDE, "")
        val lat =
            getPref(context, Constant.USER_LATITUDE, "")
        if (lat != null) {
            if (lang != null) return !(lat.length > 0 && lang.length > 0)
        }
        return false
    }

    fun isUserLoggedIn(c: Context): Boolean {
        return getUserToken(c)!!.isNotEmpty()
    }

    fun getUid(c: Context): String? {
        return getPref(c, RequestParamsUtils.UID, "")!!
    }

//    fun getLoginDetails(c: Context): LogIn? {
//        val response = Utils.getPref(c, Constant.LOGIN_INFO, "")
//
//        if (response != null && response.length > 0) {
//
//            //            LogIn login = new Gson().fromJson(
//            //                    response, new TypeToken<LogIn>() {
//            //                    }.getType());
//            var login: LogIn? = null
//            try {
//                login = Gson().fromJson<LogIn>(JSONObject(response).toString(), object : TypeToken<LogIn>() {
//
//                }.type)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            if (login!!.responseCode === 200) {
//                return login
//            }
//
//        }
//        return null
//    }

//    fun getCartData(c: Context): CartData? {
//        val response = Utils.getPref(c, Constant.CART_DATA, "")
//
//        if (response != null && response.length > 0) {
//
//            //            LogIn login = new Gson().fromJson(
//            //                    response, new TypeToken<LogIn>() {
//            //                    }.getType());
//            var cartData: CartData? = null
//            try {
//                cartData = Gson().fromJson<CartData>(JSONObject(response).toString(), object : TypeToken<CartData>() {
//
//                }.type)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            if (cartData!!.responseCode === 200) {
//                return cartData
//            }
//
//        }
//        return null
//    }

//    fun getProfileDetails(c: Context): MyProfile? {
//        val response = Utils.getPref(c, Constant.PROFILE_INFO, "")
//
//        if (response != null && response.length > 0) {
//
//            //            LogIn login = new Gson().fromJson(
//            //                    response, new TypeToken<LogIn>() {
//            //                    }.getType());
//            var login: MyProfile? = null
//            try {
//                login = Gson().fromJson<MyProfile>(JSONObject(response).toString(), object : TypeToken<MyProfile>() {
//
//                }.type)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            if (login!!.responseCode === 200) {
//                return login
//            }
//
//        }
//        return null
//    }


    fun clearLoginCredetials(c: Context) {
//        Utils.delPref(c, RequestParamsUtils.UID)
//        Utils.delPref(c, RequestParamsUtils.SESSION_ID)
        delPref(c, Constant.LOGIN_INFO)
        delPref(c, RequestParamsUtils.UID)
        delPref(c, RequestParamsUtils.TOKEN)
//        Utils.delPref(c, Constant.USER_LATITUDE)
//        Utils.delPref(c, Constant.USER_LONGITUDE)
//        Utils.delPref(c, RequestParamsUtils.TOKEN)
//        Utils.delPref(c, Constant.PROFILE_INFO)
//        Utils.delPref(c, Constant.COUNTRY_CODE)
//        Utils.delPref(c, Constant.CART_DATA)
//        Utils.delPref(c, Constant.ZIP_CODE)
//        Utils.delPref(c, Constant.IS_EMAIL_CONFIRM)
//        Utils.delPref(c, Constant.IS_ZIP_AVAILABLE)
//        Utils.delPref(c, Constant.ADDRESS_PRESELECT)
        val nMgr = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
    }



    fun asList(str: String): ArrayList<String?> {
        return ArrayList<String?>(
            Arrays.asList(
                *str
                    .split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        )
    }

    fun implode(data: ArrayList<String>): String {
        try {
            var devices = ""
            for (iterable_element in data) {
                devices = "$devices,$iterable_element"
            }
            if (devices.length > 0 && devices.startsWith(",")) {
                devices = devices.substring(1)
            }
            return devices
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Create a File for saving an image or video
     *
     * @return Returns file reference
     */
    /*fun getOutputMediaFile(): File? {
        try {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            val mediaStorageDir: File
            if (isSDcardMounted()) {
                mediaStorageDir = File(Constant.FOLDER_RIDEINN_PATH)
            } else {
                mediaStorageDir = File(
                    Environment.getRootDirectory(),
                    Constant.FOLDER_NAME
                )
            }

            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            // Create a media file name
            val mediaFile = File(
                mediaStorageDir.path
                        + File.separator + Date().time + ".jpg"
            )
            mediaFile.createNewFile()
            return mediaFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }*/

    fun getOutputMediaFile(context:Context): File? {
        try {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            val mediaStorageDir: File
            if (isSDcardMounted()) {

                mediaStorageDir = File(context.getExternalFilesDir(Constant.FOLDER_NAME)?.absolutePath)
            } else {
                mediaStorageDir = File(
                    Environment.getRootDirectory(),
                    Constant.FOLDER_NAME
                )
            }

            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            // Create a media file name
            val mediaFile = File(
                mediaStorageDir.path
                        + File.separator + Date().time + ".jpg"
            )
            mediaFile.createNewFile()
            return mediaFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun scanMediaForFile(context: Context, filePath: String) {
        resetExternalStorageMedia(context, filePath)
        notifyMediaScannerService(context, filePath)
    }

    fun resetExternalStorageMedia(context: Context, filePath: String): Boolean {
        try {
            if (Environment.isExternalStorageEmulated())
                return false
            val uri = Uri.parse("file://" + File(filePath))
            val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, uri)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun notifyMediaScannerService(context: Context, filePath: String) {
        MediaScannerConnection.scanFile(
            context, arrayOf(filePath),
            null
        ) { path, uri ->
            Debug.i("ExternalStorage", "Scanned $path:")
            Debug.i("ExternalStorage", "-> uri=$uri")
        }
    }

    fun cancellAllNotication(context: Context) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun toInitCap(param: String?): String? {
        try {
            if (param != null && param.length > 0) {
                val charArray = param.toCharArray() // convert into char
                // array
                charArray[0] = Character.toUpperCase(charArray[0]) // set
                // capital
                // letter to
                // first
                // position
                return String(charArray) // return desired output
            } else {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return param
    }

    fun encodeTobase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)

        Debug.e("LOOK", imageEncoded)
        return imageEncoded
    }

    fun decodeBase64(input: String): Bitmap {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun getExtenstion(urlPath: String): String {
        if (urlPath.contains(".")) {
            val extension = urlPath.substring(urlPath.lastIndexOf(".") + 1)
            return urlPath.substring(urlPath.lastIndexOf(".") + 1)
        }
        return ""
    }

    fun getFileName(urlPath: String): String {
        return if (urlPath.contains(".")) {
            urlPath.substring(urlPath.lastIndexOf("/") + 1)
        } else ""
    }

    fun dpToPx(context: Context, `val`: Int): Float {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            `val`.toFloat(),
            r.displayMetrics
        )
    }

    fun spToPx(context: Context, `val`: Int): Float {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            `val`.toFloat(),
            r.displayMetrics
        )
    }

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        Debug.e("type", "" + type!!)
        return type
    }

    fun isJPEGorPNG(url: String): Boolean {
        try {
            val type = getMimeType(url)
            val ext = type!!.substring(type.lastIndexOf("/") + 1)
            if (ext.equals("jpeg", ignoreCase = true) || ext.equals(
                    "jpg",
                    ignoreCase = true
                ) || ext.equals(
                    "png",
                    ignoreCase = true
                )
            ) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return false
    }

    fun getFileSize(url: String): Double {
        val file = File(url)
        // Get length of file in bytes
        val fileSizeInBytes = file.length().toDouble()
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val fileSizeInKB = fileSizeInBytes / 1024
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val fileSizeInMB = fileSizeInKB / 1024

        Debug.e("fileSizeInMB", "" + fileSizeInMB)
        return fileSizeInMB
    }

    fun getAsteriskName(str: String): String? {
        var str = str
        val n = 4
        str = nullSafe(str)
        val fStr = StringBuilder()
        if (!TextUtils.isEmpty(str)) {
            if (str.length > n) {
                fStr.append(str.substring(0, n))
                for (i in 0 until str.length - n) {
                    fStr.append("*")
                }

                return fStr.toString()
            } else {
                fStr.append(str.substring(0, str.length - 1))
            }
        }
        return str
    }

    fun getUserToken(c: Context): String? {
        return getPref(c, RequestParamsUtils.TOKEN, "")
    }

    internal lateinit var dialog: android.app.Dialog

    fun twoDecimal(rate: String): String {
        if (!StringUtils.isEmpty(rate)) {
            val df = DecimalFormat("0.00", DecimalFormatSymbols(Locale.ENGLISH))
            return "CHF " + df.format(java.lang.Float.parseFloat(rate).toDouble())
        }
        return ""
    }

    fun twoDecimalWithoutChf(rate: String): String {
        if (!StringUtils.isEmpty(rate)) {
            val df = DecimalFormat("0.00", DecimalFormatSymbols(Locale.ENGLISH))
            return df.format(java.lang.Float.parseFloat(rate).toDouble())
        }
        return ""
    }

    fun toRequestBody(value: String): RequestBody {
        val body = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), value)
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), value)
    }


    fun getHashKey(c: Context) {
        // Add code to print out the key hash
        try {
            val info = c.packageManager.getPackageInfo(
                c.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Debug.e(
                    "KeyHash:",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            }
        } catch (e: NoSuchAlgorithmException) {

        }
    }

    fun getUserCountryCode(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tm != null) {
                val simCountry = tm.simCountryIso
                // String networkOperator = tm.getNetworkOperator();

                if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                    /* if (isEuropeCountry(simCountry.toLowerCase(Locale.getDefault()), context)) {
                        Log.e("trueeeeee ", "trueeeee     &#8364");
                        return "&#8364";
                    } else {*/
                    return simCountry.toLowerCase(Locale.getDefault())
                    //   }
                } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                    val networkCountry = tm.networkCountryIso
                    if (networkCountry != null && networkCountry.length == 2) { // network country code is available

                        /*if (isEuropeCountry(networkCountry.toLowerCase(Locale.getDefault()), context)) {
                            Log.e("trueeeeee ", "trueeeee     &#8364");
                            return "&#8364";
                        } else {*/
                        return networkCountry.toLowerCase(Locale.getDefault())
                        //  }
                        //  return networkCountry.toLowerCase(Locale.getDefault());
                    }
                }
            } else {
                return Locale.getDefault().country
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    object UtilCurrency {
        var currencyLocaleMap: SortedMap<Currency, Locale>

        init {
            currencyLocaleMap =
                TreeMap(Comparator { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) })
            for (locale in Locale.getAvailableLocales()) {
                try {
                    val currency = Currency.getInstance(locale)
                    currencyLocaleMap[currency] = locale
                } catch (e: Exception) {
                }

            }
        }

        fun getCurrencySymbol(currencyCode: String): String {
            val currency = Currency.getInstance(currencyCode)
            var symbol = ""
            symbol = currency.getSymbol(currencyLocaleMap[currency])
            if (symbol.contains("$")) {
                symbol = "$"
            }
            //return currency.getSymbol(currencyLocaleMap.get(currency));
            return symbol
        }
    }

    fun getCurrencyCode(countryCode: String): String {
        return Currency.getInstance(Locale("", countryCode)).currencyCode
    }

    fun isToday(timeInMillis: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(timeInMillis)
        return date == dateFormat.format(System.currentTimeMillis())
    }


    fun isYesterDay(time: Long): Boolean {

//        Debug.e("Time adapter ", String.valueOf(Utils.parseTime(time, "dd MMM")));
//        Debug.e("tODAY LONG ", String.valueOf(( parteTime(new Date(), "yyyy-MM-dd"))));
//        Debug.e("1 Day MiLLISECONDS ", String.valueOf(((TimeUnit.DAYS.toMillis(1)))));
//        Debug.e("1 Day Minus ", String.valueOf(( parteTime(new Date(), "yyyy-MM-dd") - (TimeUnit.DAYS.toMillis(1)))));
//        Debug.e("Long time", String.valueOf(time));

        if (time >= (parseTime_(Date(), "yyyy-MM-dd") - (TimeUnit.DAYS.toMillis(1)))) {
            return true;
        }
        return false;

    }

//    fun isToday( time :Long) :Boolean {
//       return (time >= parseTime( Date(), "yyyy-MM-dd"));
//   }

    fun formatDate(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /* public static boolean compareDate(Date date1, Date date2) {

//        if (date1.compareTo(date2) > 0) {
//            System.out.println("Date1 is after Date2");
//        } else if (date1.compareTo(date2) < 0) {
//            System.out.println("Date1 is before Date2");
//        } else if (date1.compareTo(date2) == 0) {
//            System.out.println("Date1 is equal to Date2");
//        } else {
//            System.out.println("How to get here?");
//        }
        return date1.compareTo(date2) == 0;
    }*/


    fun compareDate(date1: Date, date2: Date): Boolean {
        var date1 = date1
        var date2 = date2

        //        if (date1.compareTo(date2) > 0) {
        //            System.out.println("Date1 is after Date2");
        //        } else if (date1.compareTo(date2) < 0) {
        //            System.out.println("Date1 is before Date2");
        //        } else if (date1.compareTo(date2) == 0) {
        //            System.out.println("Date1 is equal to Date2");
        //        } else {
        //            System.out.println("How to get here?");

        //        }

        date1 = parseTime(date1, "yyyy-MM-dd")
        Debug.e("date 1", "" + date1.time)
        date2 = parseTime(date2, "yyyy-MM-dd")
        Debug.e("date 2", "" + date2.time)

        return date1.compareTo(date2) == 0
    }

    fun compareDateTommorrow(date1: Date): Boolean {
        //        if (date1.compareTo(date2) > 0) {
        //            System.out.println("Date1 is after Date2");
        //        } else if (date1.compareTo(date2) < 0) {
        //            System.out.println("Date1 is before Date2");
        //        } else if (date1.compareTo(date2) == 0) {
        //            System.out.println("Date1 is equal to Date2");
        //        } else {
        //            System.out.println("How to get here?");

        //        }
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        Debug.e("tomorrow", tomorrow.toString())
        return compareDate(date1, tomorrow)
    }

    fun parseTime(time: Date, pattern: String): Date {
        val sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        try {
            return sdf.parse(sdf.format(time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun parseTime_(date: Date, pattern: String): Long {
        var sdf = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        );
        try {
            var sdf1 = SimpleDateFormat(
                pattern,
                Locale.getDefault()
            );
            var format = sdf.format(date);
            Debug.e("Format", format);
            return sdf1.parse(sdf.format(date)).getTime();
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        return 0L;
    }

    /*fun getUriForShare(context: Context, file: File): Uri? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file
                )
            } else {
                return Uri.fromFile(file)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return null;
        }
    }*/

    fun getUserId(c: Context): String? {
        return getPref(c, RequestParamsUtils.UID, "");
    }

    fun getUserData(c: Context): LoginData.ApplicationUser? {
        try {
            val response =
                getPref(c, Constant.LOGIN_INFO, "")
            if (response != null && response.isNotEmpty()) {
                Debug.e(TAG, response)
                val data = Gson().fromJson<LoginData.ApplicationUser>(
                    response, object : TypeToken<LoginData.ApplicationUser>() {}.type
                )
                if (data != null) {
                    return data
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun checkTimeRes(time: Long): String {
        if (isToday(time)) {
            return "Today"
        } else if (isYesterDay(time)) {
            return "Yesterday";
        } else {
            return parseTime(time, "dd MMM");
        }
//        if (isToday(time)) {
//            return "Today";
//        } else if (isYesterDay(time)) {
//            return "Yesterday";
//        } else {
//            return parseTime(time, "dd MMM");
//        }

    }

    private val suffixes = TreeMap<Long, String>()

    init {
        suffixes[1_000L] = "K";
        suffixes[1_000_000L] = "M";
        suffixes[1_000_000_000L] = "G";
        suffixes[1_000_000_000_000L] = "T";
        suffixes[1_000_000_000_000_000L] = "P";
        suffixes[1_000_000_000_000_000_000L] = "E";
    }

    fun format(value: Long): String {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == java.lang.Long.MIN_VALUE) return format(java.lang.Long.MIN_VALUE + 1)
        if (value < 0) return "-" + format(-value)
        if (value < 1000) return java.lang.Long.toString(value) //deal with easy case

        val e = suffixes.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value

        val truncated = value / (divideBy!! / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }


    fun getImageToDownload(
        imgArray: MutableList<String>,
        needDownloaded: Boolean,
        dirPath: String
    ): List<File> {
        var filesToDownload = mutableListOf<File>()
        try {
            for (url in imgArray) {
                var fileName = getFileName(url);
                var rootDir: File? = null;
                if (isSDcardMounted()) {
                    rootDir = File(dirPath)
                } else {
                    rootDir = File(dirPath);
                }

                var mediaFile = File(rootDir.getPath() + File.separator + fileName);

                if (needDownloaded) {
                    if (mediaFile.exists()) {
                        filesToDownload.add(mediaFile);
                    }
                } else {
                    if (!mediaFile.exists()) {
                        filesToDownload.add(mediaFile);
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }

        return filesToDownload;
    }

    fun scanMediaForMultiFile(context: Context, filePath: Array<String>) {
        resetExternalStorageMedia(context, filePath)
        notifyMediaScannerService(context, filePath)
    }

    fun resetExternalStorageMedia(
        context: Context,
        filePath: Array<String>
    ): Boolean {
        try {
            if (Environment.isExternalStorageEmulated())
                return (false)

            for (i in filePath.indices) {
                val uri = Uri.parse("file://" + File(filePath[i]))
                val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, uri)
                context.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return (false)
        }

        return (true)
    }

    fun notifyMediaScannerService(
        context: Context,
        filePath: Array<String>
    ) {

        MediaScannerConnection.scanFile(
            context, filePath,
            null
        ) { path, uri ->
            Debug.i("ExternalStorage", "Scanned $path:")
            Debug.i("ExternalStorage", "-> uri=$uri")
        }
    }

//    fun getFilePaths(downloadedFiles:MutableList<File>) : Array<String>{
//       var customArray = String[downloadedFiles.size()];
//       for (int i = 0; i < downloadedFiles.size(); i++) {
//           customArray[i] = downloadedFiles.get(i).getAbsolutePath();
//       }
//
//       return customArray;
//   }

    fun isImageDownloaded(url: String, dirPath: String): File? {
        try {
            val fileName = getFileName(url)
            var rootDir: File? = null
            if (isSDcardMounted()) {
                rootDir = File(dirPath)
            } else {
                rootDir = File(dirPath)
            }

            val mediaFile = File(rootDir.path + File.separator + fileName)

            if (mediaFile.exists()) {
                return mediaFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getOutputMediaFileImage(name: String, dirPath: String): File? {
        try {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            val mediaStorageDir: File
            if (isSDcardMounted()) {
                mediaStorageDir = File(dirPath)
            } else {
                mediaStorageDir = File(dirPath)
            }

            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }

            // Create a media file name
            //            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            val mediaFile: File
            //            if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.path + File.separator + name)
            //            } else {
            //                mediaFile = new File(mediaStorageDir.getPath()
            //                        + File.separator + new Date().getTime() + ".jpg");
            //                mediaFile.createNewFile();
            //            }

            return mediaFile
        } catch (e: Exception) {
            return null
        }

    }


    fun isPackageInstalled(packagename: String, context: Context): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: NameNotFoundException) {
            return false
        }

    }

    fun getDisplayTime(timestamp: Long): String {
        return android.text.format.DateUtils.getRelativeTimeSpanString(
            timestamp, Date().getTime(), 0,
            android.text.format.DateUtils.FORMAT_ABBREV_MONTH
        ).toString();
//        return DateUtils
//                .getRelativeTimeSpanString(timestamp, new Date().getTime(), 0,
//                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }


    fun createFolder(foldername: String): Boolean {
        Debug.e(
            "createFolder() called with: foldername = [",
            Environment.getExternalStorageDirectory().toString() + File.separator + foldername + "]"
        )
        val file =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + foldername)
        if (file.isDirectory && file.exists()) {
            return true
        } else if (file.mkdirs()) {
            return true
        }
        return false
    }

    fun saveBitmap(file: File, bitmap: Bitmap): File? {
        var out: FileOutputStream? = null
        try {

            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                out?.close()

                return file
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    fun getRelativeLeft(myView: View): Int {
        return if (myView.parent === myView.rootView)
            myView.left
        else
            myView.left + getRelativeLeft(myView.parent as View)
    }

    fun getRelativeTop(myView: View): Int {
        return if (myView.parent === myView.rootView)
            myView.top
        else
            myView.top + getRelativeTop(myView.parent as View)
    }

    /*fun getDisplayTime(timestamp: Long): String {
        return DateUtils
            .getRelativeTimeSpanString(
                timestamp, Date().time, 0,
                DateUtils.FORMAT_ABBREV_MONTH
            ).toString()
        //        return DateUtils
        //                .getRelativeTimeSpanString(timestamp, new Date().getTime(), 0,
        //                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }*/

    fun compressImage(
        file: File, maxImageSize: Float,
        filter: Boolean
    ): Bitmap {

        val realImage = BitmapFactory.decodeFile(file.absolutePath)

        val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    }

    fun loadImage(img: ImageView, url: String, context: Context, resId: Int) {
        run {
            val options: RequestOptions = RequestOptions()
                .placeholder(resId)
                .error(resId)
            Glide.with(context).load(url)
                .apply(options).into(img)
        }

    }

    fun loadLocalImage(img: ImageView, file: File, context: Context, resId: Int) {
        run {
            val options: RequestOptions = RequestOptions()
                .placeholder(resId)
                .error(resId)
            Glide.with(context).load(file).apply(options).into(img)
        }
    }

    /*fun mapToTimeStamp(date: LinkedTreeMap<String, Any>): Timestamp {

        val nanosec = date.get("nanoseconds") as Double
        val seconds = date.get("seconds") as Double
        return Timestamp(seconds.toLong(), nanosec.toInt())
    }*/

    fun getUserAuthToken(c: Context): String? {
        return getPref(c, RequestParamsUtils.TOKEN, "")
    }

    fun lbToKg(weightValue: Double): Double {
        return weightValue / 2.2046226218488
    }

    fun kgToLb(weightValue: Double): Double {
        return weightValue * 2.2046226218488
    }

    fun cmToInch(heightValue: Double): Double {
        Debug.d("<><><>Cm to Inch", (heightValue / 2.54).toString())
        return heightValue / 2.54
    }

    fun inchToCm(heightValue: Double): Double {
        Debug.d("<><><>inch to cm", (heightValue * 2.54).toString())
        return heightValue * 2.54
    }

    fun ftInToInch(ft: Int, `in`: Double): Double {
        return (ft * 12).toDouble() + `in`
    }

    fun calcInchToFeet(inch: Double): Int {
        return (inch / 12.0).toInt()
    }

    fun calcInFromInch(inch: Double): Double {
        return BigDecimal(inch % 12.0).setScale(1, 6).toDouble()
    }

    fun getMeter(inch: Double): Double {
        return inch * 0.0254
    }

    fun getBmiCalculation(kg: Float, foot: Int, inch: Int): Double {
        val bmi = kg / getMeter(ftInToInch(foot, inch.toDouble())).pow(2.0)
        return bmi
    }

    fun calculationForBmiGraph(point: Float): Float {
        /*
           Calculation formula
           pos = ((point - view starting point) * view_Weight) / (view ending - view starting point)
           pos += before view current value
           pos -= 0.08 (For center margin remove from calculation)
        * */
        var pos = 0f
        try {

            if (point < 15) {
                return 0f
            } else if (point > 15 && point <= 16) {
                //pos = (0.25f * point) / 15.5f

                pos = ((point - 15f) * 0.5f) / 1f
                pos -= 0.05f

            } else if (point > 16 && point <= 18.5) {

                pos = ((point - 16f) * 1.5f) / 2.5f
                pos += 0.5f
                pos -= 0.05f

//            pos = (0.75f * point) / 15.5f
//            pos += 0.5f

            } else if (point > 18.5 && point <= 25) {

                pos = ((point - 18.5f) * 2f) / 6.5f
                pos += 0.5f + 1.5f
                pos -= 0.05f

//            pos = (1.0f * point) / 15.5f
//            pos += 0.5f + 1.5f

            } else if (point > 25 && point <= 30) {

                pos = ((point - 25f) * 1f) / 5f
                pos += 0.5f + 1.5f + 2f
                pos -= 0.05f

//            pos = (0.50f * point) / 15.5f
//            pos += 0.5f + 1.5f + 2f

            } else if (point > 30 && point <= 35) {

                pos = ((point - 30f) * 1f) / 5f
                pos += 0.5f + 1.5f + 2f + 1f
                pos -= 0.05f

//            pos = (0.50f * point) / 15.5f
//            pos += 0.5f + 1.5f + 2f + 1f

            } else if (point > 35 && point <= 40) {

                pos = ((point - 35f) * 1f) / 5f
                pos += 0.5f + 1.5f + 2f + 1f + 1f
                pos -= 0.05f

//            pos = (0.50f * point) / 15.5f
//            pos += 0.5f + 1.5f + 2f + 1f + 1f

            } else if (point > 40) {
                return 6.90f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pos
    }

    fun bmiWeightString(point: Float): String {

        try {
            if (point < 15) {
                return "Severely underweight"
            } else if (point > 15 && point < 16) {
                return "Very underweight"
            } else if (point > 16 && point < 18.5) {
                return "Underweight"
            } else if (point > 18.5 && point < 25) {
                return "Healthy Weight"
            } else if (point > 25 && point < 30) {
                return "Overweight"
            } else if (point > 30 && point < 35) {
                return "Moderately obese"
            } else if (point > 35 && point < 40) {
                return "Very obese"
            } else if (point > 40) {
                return "Severely obese"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun bmiWeightTextColor(context: Context, point: Float): Int {

        try {
            if (point < 15) {
                return ContextCompat.getColor(context, R.color.black)
            } else if (point > 15 && point < 16) {
                return ContextCompat.getColor(context, R.color.colorFirst)
            } else if (point > 16 && point < 18.5) {
                return ContextCompat.getColor(context, R.color.colorSecond)
            } else if (point > 18.5 && point < 25) {
                return ContextCompat.getColor(context, R.color.colorThird)
            } else if (point > 25 && point < 30) {
                return ContextCompat.getColor(context, R.color.colorFour)
            } else if (point > 30 && point < 35) {
                return ContextCompat.getColor(context, R.color.colorFive)
            } else if (point > 35 && point < 40) {
                return ContextCompat.getColor(context, R.color.colorSix)
            } else if (point > 40) {
                return ContextCompat.getColor(context, R.color.black)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ContextCompat.getColor(context, R.color.black)
    }

    fun getCurrentWeek(): java.util.ArrayList<String> {
        val currentWeekArrayList = java.util.ArrayList<String>()
//        currentWeekArrayList.add("Temp Data")
        try {
            val format = SimpleDateFormat(Constant.CapDateFormatDisplay, Locale.ENGLISH)
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            for (i in 1..7) {
                try {
                    val data = format.format(calendar.time)
                    currentWeekArrayList.add(data)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentWeekArrayList
    }

    fun getCurrentWeekByFirstDay(context: Context): java.util.ArrayList<String> {
        val currentWeekArrayList = java.util.ArrayList<String>()
//        currentWeekArrayList.add("Temp Data")
        try {
            val format = SimpleDateFormat(Constant.CapDateFormatDisplay, Locale.ENGLISH)
            val calendar = Calendar.getInstance(Locale.ENGLISH)

            when (getFirstWeekDayNameByDayNo(getPref(context,Constant.PREF_FIRST_DAY_OF_WEEK,1))) {
                "Sunday" -> {
                    calendar.firstDayOfWeek = Calendar.SUNDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
                "Monday" -> {
                    calendar.firstDayOfWeek = Calendar.MONDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }
                "Saturday" -> {
                    calendar.firstDayOfWeek = Calendar.SATURDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                }
            }

            for (i in 1..7) {
                try {
                    val data = format.format(calendar.time)
                    currentWeekArrayList.add(data)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentWeekArrayList
    }

    fun getFirstWeekDayNameByDayNo(dayNo: Int): String {
        var dayNumber = ""
        when (dayNo) {
            1 -> dayNumber = "Sunday"
            2 -> dayNumber = "Monday"
            3 -> dayNumber = "Saturday"
        }
        return dayNumber
    }

    fun getFirstWeekDayNoByDayName(dayName: String): Int {
        var dayNumber = 1
        when (dayName) {
            "Sunday" -> dayNumber = 1
            "Monday" -> dayNumber = 2
            "Saturday" -> dayNumber = 3
        }
        return dayNumber
    }

    fun getAssetItems(
        mContext: Context,
        categoryName: String
    ): java.util.ArrayList<String>? {
        val arrayList = java.util.ArrayList<String>()
        val imgPath: Array<String>
        val assetManager = mContext.assets
        try {
            imgPath = assetManager.list(categoryName)!!
            if (imgPath != null) {
                for (anImgPath in imgPath) {
                    arrayList.add("///android_asset/$categoryName/$anImgPath")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return arrayList
    }

    fun ReplaceSpacialCharacters(string: String): String? {
        return string.replace(" ", "").replace("&", "").replace("-", "").replace("'", "")
    }

    fun secToString(sec: Int, pattern: String): String {
        var formatter = DecimalFormat("00")
        var p1 = sec % 60
        var p2: Int = sec / 60
        val p3 = p2 % 60
        p2 /= 60

        if(p2 > 0)
        {
            return  formatter.format(p2)+":"+formatter.format(p3)+":"+formatter.format(p1)

        }
        return formatter.format(p3)+":"+formatter.format(p1)
    }

    fun getDrawableId(name: String?, context: Context): Int {
        return context.resources.getIdentifier(
            name,
            "drawable",
            context.packageName
        )
    }

    fun getCalorieFromSec(second:Long): Double {

        return second * Constant.SEC_DURATION_CAL

    }

    fun isPurchased(context: Context):Boolean{
        if(Debug.DEBUG_IS_PURCHASE)
            return true

        return getPref(context, Constant.PREF_KEY_PURCHASE_STATUS, false)
    }

    fun rateUs(context: Context) {
        val appPackageName = context.getPackageName()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun contactUs(content: Context) {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("Enter Your Email"))
            sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, content.resources.getString(R.string.app_name))
            content.startActivity(sendIntentGmail)
        } catch (e: Exception) {
            val sendIntentIfGmailFail = Intent(Intent.ACTION_SEND)
            sendIntentIfGmailFail.type = "*/*"
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_EMAIL, arrayOf("Enter Your Email"))
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_SUBJECT, content.resources.getString(R.string.app_name))
            if (sendIntentIfGmailFail.resolveActivity(content.packageManager) != null) {
                content.startActivity(sendIntentIfGmailFail)
            }
        }
    }

    fun openUrl(content: Context, strUrl: String) {
        val appPackageName = content.getPackageName() // getPackageName() from Context or Activity object
        try {
            content.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)))
        } catch (e: android.content.ActivityNotFoundException) {
            content.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)))
        }
    }

    fun shareStringLink(content: Context, strSubject: String, strText: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, strText)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, strSubject)
        shareIntent.type = "text/plain"
        content.startActivity(Intent.createChooser(shareIntent, content.resources.getString(R.string.app_name)))
    }

    fun DownloadTTSEngine(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://search?q=text to speech&c=apps")
                )
            )
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/search?q=text to speech&c=apps")
                )
            )
        }
    }

    fun startAlarm(reminderId: Int,hourOfDay:Int,minute:Int,context: Context) {

        try {
            val alarmStartTime = Calendar.getInstance()
            alarmStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmStartTime.set(Calendar.MINUTE, minute)
            alarmStartTime.set(Calendar.SECOND, 0)

            val hourDt12 = parseTime(alarmStartTime.timeInMillis, "yyyy-MM-dd hh:mm")

            val format = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
            val alarmDt = format.parse(hourDt12)

            if(hourOfDay <= alarmStartTime!!.get(Calendar.HOUR_OF_DAY) && minute < alarmStartTime!!.get(Calendar.MINUTE)){
               // mDay += 1
                alarmStartTime.add(Calendar.DAY_OF_YEAR,1)
            }

            AlarmReceiver().setAlarm(context,alarmStartTime,reminderId)

            /*val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(Constant.extraReminderId, "" + mCount)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, mCount, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmDt!!.time, AlarmManager.INTERVAL_DAY, pendingIntent)*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getShortDayName(dayNo: String): String {
        var dayName = ""
        when (dayNo) {
            "1" -> dayName = "Sun"
            "2" -> dayName = "Mon"
            "3" -> dayName = "Tue"
            "4" -> dayName = "Wed"
            "5" -> dayName = "Thu"
            "6" -> dayName = "Fri"
            "7" -> dayName = "Sat"

        }
        return dayName
    }

    // Todo Set Day Progress in text and progress bar
    fun setDayProgressData(context: Context, planData: HomePlanTableClass, txtDayLeft: TextView, txtDayPer: TextView?, pbDay: ProgressBar,btnDay:Button? = null):Int {
        try {
            val dbHelper = DataHelper(context)

            // Week day progress data
            val compDay = dbHelper.getCompleteDayCountByPlanId(planData.planId!!)

            //val proPercentage = String.format("%.1f", (((compDay).toFloat()) * 100) / 28)
            val proPercentage = String.format("%.2f", (((((compDay).toFloat()) * 100) / planData.days!!.toInt()).toDouble())).replace(",", ".")
            if((planData.days!!.toInt() - compDay) > 0)
            txtDayLeft.text = (planData.days!!.toInt() - compDay).toString().plus(" Days left")
            else
                txtDayLeft.text = context.getString(R.string.well_done)
            if (txtDayPer != null) {
                txtDayPer.text = proPercentage.toDouble().roundToInt().toString().plus("%")
            }

            pbDay.progress = proPercentage.toFloat().toInt()

            if(btnDay != null ){if((planData.days!!.toInt() - compDay) > 0){
                btnDay.text = " Day " + (compDay + 1)
            } else
                btnDay.text = context.getString(R.string.finished)
            }

            return compDay
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    fun showSpinner(
        mContext: Context?,
        title: String,
        tv: TextView?,
        data: java.util.ArrayList<Spinner>,
        isFilterable: Boolean,
        isShowTitle:Boolean,
        callback: SpinnerCallback?
    ) {
        val a = Dialog(mContext!!)
        val w = a.window
        a.requestWindowFeature(Window.FEATURE_NO_TITLE)
        a.setContentView(R.layout.spinner)
        a.setCanceledOnTouchOutside(false)
        a.setCancelable(false)
        w!!.setBackgroundDrawableResource(android.R.color.transparent)

        val llTitle = w.findViewById<View>(R.id.llTitle) as LinearLayout
        if(isShowTitle)
            llTitle.visibility = View.VISIBLE
        else
            llTitle.visibility = View.GONE
        val lblselect = w.findViewById<View>(R.id.dialogtitle) as TextView
        lblselect.text = title.replace("*", "").trim { it <= ' ' }

        //        TextView dialogClear = (TextView) w.findViewById(R.id.dialogClear);
        //        dialogClear.setVisibility(View.VISIBLE);
        //        dialogClear.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                tv.setText("");
        //                tv.setTag(null);
        //
        //                a.dismiss();
        //            }
        //        });

        val editSearch = w.findViewById<View>(R.id.editSearch) as EditText
        if (isFilterable) {
            editSearch.visibility = View.VISIBLE
        } else {
            editSearch.visibility = View.GONE
        }

        val adapter = SpinnerSelAdapter(mContext!!)
        adapter.setFilterable(isFilterable)
        val lv = w.findViewById<View>(R.id.lvSpinner) as ListView
        lv.adapter = adapter
        adapter.addAll(data)

        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterview, view, position, l ->

            adapter.changeSelection(position, false)
            val selList = java.util.ArrayList<Spinner>()
            selList.addAll(adapter.selectedAll)

            tv?.text = selList[0].title
            tv?.tag = selList[0].ID

            callback?.onDone(selList)

            a.dismiss()
        }

        editSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.length >= 1) {
                    adapter.filter!!.filter(editable.toString().trim { it <= ' ' })
                } else {
                    adapter.filter!!.filter("")
                }

            }
        })

        a.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        val window: Window = a.getWindow()!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        a.show()
    }

    fun getSelectedLanguage(mContext: Context): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Utils.getPref(mContext, Constant.PREF_LANGUAGE, Locale.getDefault().language) ?: Locale.getDefault().language
        } else {
            val lan = Resources.getSystem().getConfiguration().locale.language
            Utils.getPref(mContext, Constant.PREF_LANGUAGE, lan) ?: lan
        }
    }

    fun md5(s: String): String? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }
}
