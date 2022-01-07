package com.luckybirdt.mvvmdemo.module

import android.os.Bundle
import com.luckybirdt.mvvmdemo.R
import com.luckybirdt.mvvmdemo.base.BaseActivity
import com.luckybirdt.mvvmdemo.databinding.ActivityMainBinding
import com.luckybirdt.mvvmdemo.model.MainModel

class MainActivity : BaseActivity<ActivityMainBinding, MainModel>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun createViewModel(): MainModel = MainModel()

    override fun initialize(savedInstanceState: Bundle?) {

    }

}