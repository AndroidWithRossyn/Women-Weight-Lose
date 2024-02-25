package com.loseweight.utils

import android.content.Context
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject



internal object RequestParamsUtils {
    val AUTHORIZATION = "Authorization"

    val UID = "uid"
    val FACILITYCODE = "facilityCode"
    val USER_IDS = "userIds"
    val MOBILE_NUMBER = "mobno"
    val DRAFTED = "drafted"
    val CATALOGUE_ID = "catalogue_id"
    val CATALOGUE_IDS = "catalogueIds"
    val CATALOGUE_REF_ID = "catalogue_ref_id"
    val FROM_ID = "from"
    val STATUS = "status"
    val TO_ID = "to"
    val IS_CONNECTED = "connected"
    val DATE = "date"
    val UPDATE_DATE = "updateDate"
    val TITLE = "title"
    val TOKEN = "token"
    val PRODUCT_ID = "product_id"
    val EMPLOYEEID = "employeeID"
    val EMPLOYEE_ID = "EmployeeID"
    val EMPLOYEENUMBER = "employeeNumber"
    val NAME = "name"
    val BEACONMAJORID = "beaconMajorID"
    val BEACONMINORID = "beaconMinorIDs"
    val SEARCHTEXT = "searchText"
    val PIN = "pin"
    val FILES = "Files"
    val CLOCKINDATETIME = "ClockInDateTime"
    val CLOCKEDOUTDATETIME = "ClockedOutDateTime"
    const val CONTENT_TYPE = "Content-Type"

    const val DEMOCODE = "democode"
    const val VISITORINFO = "visitorinfo"
    const val VISITORNAME = "visitorname"
    const val EMAILID = "eMailid"
    const val PHONE = "phone"
    const val ORGANIZATION = "organization"
    const val IMG = "img"
    const val PHOTO = "photo"
    const val VISITORID = "visitorid"

    const val DOCUMENT_TYPE = "DocumentType"


    //    public static RequestParams newRequestParams(Context c) {
    //        RequestParams params = new RequestParams();
    //        params.put(UID, "" + Utils.getPref(c, RequestParamsUtils.UID, ""));
    //        params.put(SESSION_ID, "" + Utils.getPref(c, RequestParamsUtils.SESSION_ID, ""));
    ////        params.put(APIKEY, Constant.API_KEY);
    //
    //        return params;
    //    }
    fun newRequestFormBody(c: Context): FormBody.Builder {

        return FormBody.Builder()
    }

    fun newRequestMultiFormBodyBuilder(c: Context): MultipartBody.Builder {

        val builder = MultipartBody.Builder()
        /* if (!Utils.isUserLoggedIn(c)) {
             Debug.e("Device Id -->", Utils.getDeviceId(c))
             builder.addFormDataPart(DEVICE_ID, Utils.getDeviceId(c))
         }*/
        try {
//            val appConfig = Utils.getAppConfig(c)
//            if (appConfig != null)
//                builder.addFormDataPart(DEMOCODE, appConfig.visitorAppConfig!!.demoCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return builder
    }

    fun newRequestFormBody(param: JSONObject): RequestBody {

        return RequestBody.create(("application/json".toMediaTypeOrNull()), param.toString())

    }

    fun newRequestFormBody(param: JSONObject, context: Context): RequestBody {
        try {
//            val appConfig = Utils.getAppConfig(context)
//            if (appConfig != null)
//                param.put(DEMOCODE, appConfig.visitorAppConfig!!.demoCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return RequestBody.create(("application/json".toMediaTypeOrNull()), param.toString())

    }
}