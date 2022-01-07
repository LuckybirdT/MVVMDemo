package com.luckybirdt.mvvmdemo.network

open class BaseResponse<T>(val code: Int, val msg: String, var result: T)