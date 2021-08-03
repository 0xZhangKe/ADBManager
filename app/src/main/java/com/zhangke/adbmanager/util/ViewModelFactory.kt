package com.zhangke.adbmanager.util

import androidx.lifecycle.ViewModelProvider

private val appViewModelFactory: ViewModelProvider.Factory by lazy {
    ViewModelProvider.AndroidViewModelFactory(application)
}

@Suppress("FunctionName")
fun AppViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory
