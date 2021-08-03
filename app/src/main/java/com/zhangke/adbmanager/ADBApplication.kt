package com.zhangke.adbmanager

import android.app.Application
import com.zhangke.adbmanager.util.initApplication

/**
 * Created by ZhangKe on 2021/8/3.
 */
class ADBApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }
}