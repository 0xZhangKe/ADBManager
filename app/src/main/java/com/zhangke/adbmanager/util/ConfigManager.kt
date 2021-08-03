package com.zhangke.adbmanager.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by ZhangKe on 2021/8/3.
 */
object ConfigManager{

    private const val CONFIG_FILE_NAME = "AdbManagerConfig"

    fun getInt(key: String, default: Int): Int{
        return getSP().getInt(key, default)
    }

    fun putInt(key: String, value: Int){
        getSP().edit().putInt(key, value).apply()
    }

    private fun getSP(): SharedPreferences{
        return appContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE)
    }
}