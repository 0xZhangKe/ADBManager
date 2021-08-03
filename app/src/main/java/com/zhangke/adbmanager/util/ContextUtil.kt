package com.zhangke.adbmanager.util

import android.app.Application
import android.content.Context
import com.google.android.material.internal.ContextUtils

/**
 * Created by ZhangKe on 2020/10/26.
 */

@Volatile
lateinit var appContext: Context
    private set

@Volatile
lateinit var application: Application
    private set

fun initApplication(app: Application) {
    application = app
    appContext = application
}
