package com.luckybirdt.mvvmdemo.network

import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.util.*

class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method
        val treeMap = TreeMap<String, Any>()
        if (method == "POST" || method == "PUT") {
            val body = request.body
            body?.let {
                it.contentType()?.let { type ->
                    val subtype: String = type.subtype
                    if (subtype.contains("json")) {
                        val customReq = bodyToString(body)
                        val obj = JSONObject(customReq)
                        val keys = obj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = obj.getString(key)
                            treeMap[key] = value
                        }
                        val newBuilder = request.newBuilder()
                            .url(request.url)
                            .method(request.method, body)
                            .build()
                        return chain.proceed(newBuilder)
                    } else if (subtype.contains("form")) {
                        val bodyBuilder = FormBody.Builder()
                        if (body is FormBody) {
                            if (body.size == 0) {
                                treeMap.clear()
                            } else {
                                for (index in 0 until body.size) {
                                    treeMap[body.name(index)] = body.value(index)
                                    bodyBuilder.add(body.name(index), body.value(index))
                                }
                                val newRequest = request.newBuilder()
                                    .url(request.url)
                                    .method(request.method, bodyBuilder.build())
                                    .build()
                                return chain.proceed(newRequest)
                            }
                        }
                    }
                }
            }
            return chain.proceed(request)
        }
        //将地址中的乱码过滤，多余的?页过滤掉
        val httpUrl = request.url.toString()
        val parse = httpUrl.toHttpUrlOrNull() ?: return chain.proceed(request)
        //其它方式处理 添加新的参数
        //解析URL中的key和value
        val encodedPathSegments = parse.queryParameterNames
        encodedPathSegments.forEach {
            treeMap[it] = parse.queryParameter(it) ?: ""
        }
        val authorizedUrlBuilder = parse
            .newBuilder()
            .scheme(parse.scheme)
            .host(parse.host)
        val requestBuilder = request.newBuilder()
            .method(request.method, request.body)
            .url(authorizedUrlBuilder.build())
        return chain.proceed(requestBuilder.build())
    }

    private fun bodyToString(request: RequestBody?): String {
        return try {
            val copy: RequestBody? = request
            val buffer = Buffer()
            if (copy != null) copy.writeTo(buffer) else return ""
            buffer.readUtf8()
        } catch (e: Exception) {
            "did not work"
        }
    }

}