package com.loseweight.utils

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import java.io.IOException
import java.util.concurrent.TimeUnit

internal object AsyncHttpRequest : OkHttpClient() {

    fun newRequestPost(context: Context, body: RequestBody, url: String): Call {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(50, TimeUnit.SECONDS)
        builder.writeTimeout(3, TimeUnit.MINUTES)
        val client = builder.build()
        val request = Request.Builder()
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .url(url)
            .post(body)
        return client.newCall(request.build())
    }

    fun newRequestPut(context: Context, body: RequestBody, url: String): Call {
        val builder = OkHttpClient.Builder()
        val client = builder.build()
        val request = Request.Builder()
            .url(url)
            .put(body)
        return client.newCall(request.build())
    }

    fun newRequestPatch(context: Context, body: RequestBody, url: String): Call {
        val builder = OkHttpClient.Builder()
        val client = builder.build()
        val request = Request.Builder()
            .url(url)
            .patch(body)
        return client.newCall(request.build())
    }

    fun newRequestDelete(context: Context, body: RequestBody, url: String): Call {
        val builder = OkHttpClient.Builder()
        val client = builder.build()
        val request = Request.Builder()
            .url(url)
            .delete(body)
        return client.newCall(request.build())
    }

    @Throws(IOException::class)
    fun newRequestGet(context: Context, url: String): Call {
        val builder = OkHttpClient.Builder()

        val client = builder.build()
        val request = Request.Builder()
            .addHeader(
                RequestParamsUtils.AUTHORIZATION, Utils.getUserToken(
                    context
                )!!)
            .url(url)
        return client.newCall(request.build())
    }

    fun newRequestPostJson(url: String, json: String): Call {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()
        val body = RequestBody.create(JSON, json)
        val request = okhttp3.Request.Builder()
            .addHeader(
                RequestParamsUtils.AUTHORIZATION,
                "key=" + "AIzaSyDZo25hGs9WQnl_bcgY5-eOXAbPJiLV-AA"
            ) // Change a
            .addHeader("Content-Type", "application/json")
            .url(url)
            .post(body)
            .build()

        return client.newCall(request)
    }
}
