package com.luckybirdt.mvvmdemo.network

enum class HttpError(val code: Int, val msg: String) {
    //未知错误
    UNKNOWN(-1, "未知错误"),

    //网络连接错误
    CONNECT_ERROR(-2, "网络连接错误"),

    //连接超时
    CONNECT_TIMEOUT(-3, "连接超时"),

    //错误的请求
    BAD_NETWORK(-4, "错误的请求"),

    //数据解析异常
    PARSE_ERROR(-5, "数据解析异常"),

    //请求取消
    CANCEL_REQUEST(-6, "请求取消"),
}