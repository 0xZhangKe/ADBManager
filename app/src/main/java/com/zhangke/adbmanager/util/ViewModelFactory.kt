package com.zhangke.adbmanager.util

import android.app.Application
import androidx.lifecycle.ViewModelProvider

private val appViewModelFactory: ViewModelProvider.Factory by lazy {
    ViewModelProvider.AndroidViewModelFactory(appContext as Application)
}

@Suppress("FunctionName")
fun AppViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory
