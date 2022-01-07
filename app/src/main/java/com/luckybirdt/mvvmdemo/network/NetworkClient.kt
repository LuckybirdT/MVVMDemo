package com.luckybirdt.mvvmdemo.network

import android.net.ParseException
import android.util.Log
import android.util.LruCache
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.luckybirdt.mvvmdemo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val DEFAULT_CONNECT_TIME = 10L // 连接超时时间
    private val DEFAULT_WRITE_TIME = 30L // 设置写操作超时时间
    private val DEFAULT_READ_TIME = 30L // 设置读操作超时时间
    private val SERVICE_CACHE_COUNT = 20 // 最多缓存的service数量
    private var serviceCache: LruCache<String, Any> = LruCache(SERVICE_CACHE_COUNT)
    private val okClient: OkHttpClient
    private val retrofitClient: Retrofit

    init {
        okClient = buildOkHttpClient()
        retrofitClient = buildRetrofit()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)// 连接超时时间
            .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)// 设置写操作超时时间
            .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)// 设置读操作超时时间
            .retryOnConnectionFailure(true)
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    private fun buildRetrofit(): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
        return builder.build()
    }

    private fun <T> getService(service: Class<T>): T {
        var retrofitService: T = serviceCache.get(service.canonicalName) as T
        if (retrofitService == null) {
            retrofitService = retrofitClient.create(service)
            serviceCache.put(service.canonicalName, retrofitService)
        }
        return retrofitService
    }

    suspend fun <T : Any, D : Any> request(apiInterface: Class<T>, call: suspend (service: T) -> BaseResponse<D>): ParseResult<D> {
        return try {
            val service = getService(apiInterface)
            val response = call(service)
            if (response.code == 0) {
                ParseResult.Success(response.result)
            } else {
                ParseResult.Failure(response.code, response.msg)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            ParseResult.ERROR(e, parseException(e))
        }
    }

    private fun parseException(e: Throwable): HttpError {
        return when (e) {
            is HttpException -> HttpError.BAD_NETWORK
            is ConnectException, is UnknownHostException -> HttpError.CONNECT_ERROR
            is InterruptedIOException -> HttpError.CONNECT_TIMEOUT
            is JsonParseException, is JSONException, is ParseException, is ClassCastException -> HttpError.PARSE_ERROR
            is CancellationException -> HttpError.CANCEL_REQUEST
            else -> HttpError.UNKNOWN
        }
    }

}