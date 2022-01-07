package com.luckybirdt.mvvmdemo.network

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder().apply {
            //添加请求头
//            addHeader()
        }
        return chain.proceed(requestBuilder.build())
    }

}