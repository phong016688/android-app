package com.datn.cookingguideapp.data.remote.interceptor

import android.util.Log
import com.datn.cookingguideapp.data.cache.AppPrefKey
import com.datn.cookingguideapp.data.cache.AppPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.HttpURLConnection
import java.util.*

class InterceptorImpl(
    private val preferences: AppPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = initializeRequestWithHeaders(chain.request())
        val response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            preferences.clearKey(AppPrefKey.Auth.ACCESS_TOKEN)
        }
        val responseBody = response.body
        val responseBodyString = response.body?.string()
        Log.d("====>log_response ${request.url}", responseBodyString ?: "")
        return createNewResponse(response, responseBody, responseBodyString)
    }

    private fun initializeRequestWithHeaders(request: Request): Request {
        val accessToken = preferences.get(AppPrefKey.Auth.ACCESS_TOKEN, String::class.java)
        Log.d("====>log_request ${request.url}", request.body.toString())
        return request.newBuilder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Cache-Control", "no-store")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept-Language", Locale.getDefault().language)
            .method(request.method, request.body)
            .build()
    }

    private fun createNewResponse(
        response: Response,
        responseBody: ResponseBody?,
        responseBodyString: String?,
    ): Response {
        val contentType = responseBody?.contentType()
        return response.newBuilder()
            .body((responseBodyString ?: "").toResponseBody(contentType))
            .build()
    }
}
