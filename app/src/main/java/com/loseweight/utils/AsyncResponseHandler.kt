package com.loseweight.utils

import okhttp3.Call
import okhttp3.Callback
import java.io.IOException


abstract class AsyncResponseHandler () : Callback {


    abstract fun onStart()

    abstract fun onSuccess(content: String)

    abstract fun onFinish()

    //
    abstract fun onFailure(e: Throwable, content: String)

    override fun onFailure(call: Call, e: IOException) {
        onFailure(e, "")
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: okhttp3.Response) {
        onFinish()

        if (response.code != 200) {
            Debug.e("onResponse", "" + response.code + " " + response.message)
        }

        if (response.code == 401) {

            val sb = StringBuilder()
//            val br = BufferedReader(InputStreamReader(response.body()!!.source().inputStream()))
            val inputAsString =
                response.body!!.source().inputStream().bufferedReader().use { it.readText() }
//            var read: String

//            while ((read = br.readLine()) != null) {
//                sb.append(read)
//            }
            sb.append(inputAsString)
//            br.close()

            try {
                if (!StringUtils.isEmpty(sb.toString())) {
                    onFailure(Throwable(""), "" + sb.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

//            Utils.clearLoginCredetials(context)
//
//            LocalBroadcastManager.getInstance(context)
//                .sendBroadcast(
//                    Intent(Constant.FINISH_ACTIVITY)
//                )

//            val intent = Intent(
//                context,
//                LoginActivity::class.java
//            )
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            context.startActivity(intent)
            return
        }

        if (response.isSuccessful) {

//            val sb = StringBuilder()
//            val br = BufferedReader(InputStreamReader(response.body()!!.source().inputStream()))
//            var read: String
            val sb = StringBuilder()
//            val br = BufferedReader(InputStreamReader(response.body()!!.source().inputStream()))
            val inputAsString =
                response.body!!.source().inputStream().bufferedReader().use { it.readText() }
            sb.append(inputAsString)
//            while ((read = br.readLine()) != null) {
//                sb.append(read)
//            }

//            br.close()

            //            if (response.code() == 401) {
            //
            //                Utils.clearLoginCredetials(context);
            //
            //                LocalBroadcastManager.getInstance(context)
            //                        .sendBroadcast(
            //                                new Intent(Constant.FINISH_ACTIVITY));
            //
            //                Intent intent = new Intent(context,
            //                        LoginActivity.class);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            //                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //                context.startActivity(intent);
            //                return;
            //            }

            try {
                onSuccess(sb.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

//            val sb = StringBuilder()
//            val br = BufferedReader(InputStreamReader(response.body()!!.source().inputStream()))
//            var read: String
//
//            while ((read = br.readLine()) != null) {
//                sb.append(read)
//            }
//
//            br.close()

            val sb = StringBuilder()
            val inputAsString =
                response.body!!.source().inputStream().bufferedReader().use { it.readText() }
            sb.append(inputAsString)
            onFailure(Throwable(""), "" + sb.toString())
        }
    }
}
