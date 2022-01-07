package com.luckybirdt.mvvmdemo.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModel : ViewModel(), ViewModelLifecycle {

    private lateinit var lifecycleOwner: LifecycleOwner

    companion object {
        @JvmStatic
        fun <T : BaseViewModel> createViewModelFactory(viewModel: T): ViewModelProvider.Factory {
            return ViewModelFactory(viewModel)
        }
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        this.lifecycleOwner = owner
    }

    override fun onCreate() {

    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }

}

/**
 * 创建ViewModel的工厂，以此方法创建的ViewModel，可在构造函数中传参
 */
class ViewModelFactory(private val viewModel: BaseViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel as T
    }

}