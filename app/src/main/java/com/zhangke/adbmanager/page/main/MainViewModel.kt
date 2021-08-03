package com.zhangke.adbmanager.page.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhangke.adbmanager.R
import com.zhangke.adbmanager.util.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ZhangKe on 2021/8/1.
 */
class MainViewModel : ViewModel() {

    val adbPort = MutableLiveData<String>()
    val statusLine = MutableLiveData<String>()
    val logText = MutableLiveData<CharSequence>()

    private val adbHelper = ADBHelper()
    private val logListener = object : LogListener {

        override fun onNewLog(log: String, level: Int) {
            logText.value = buildLogText(logText.value, log, level)
        }
    }

    private val adbListener: ADBStateListener = {
        if (it) {

        } else {

        }
    }

    fun init() {
        Logger.addLogListener(logListener)
        adbHelper.addListener(adbListener)
        adbPort.value = adbHelper.getDefaultPort().toString()
        if (!DeviceUtils.hasRootPermission()) {
            showToast(R.string.root_np_permission)
            return
        }
        requireWifiOpen()
    }

    fun onAdbClicked() {
        adbHelper.toggleADB()
    }

    override fun onCleared() {
        super.onCleared()
        Logger.removeListener(logListener)
        adbHelper.removeListener(adbListener)
    }

    private fun requireWifiOpen(): Boolean {
        val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!DeviceUtils.wifiIsOpen(wifiManager)) {
            showToast(R.string.wifi_is_not_open)
            return false
        }
        return true
    }

    private fun showToast(@StringRes resId: Int) {
        Toast.makeText(appContext, resId, Toast.LENGTH_SHORT).show()
    }

    private fun buildLogText(
        oldText: CharSequence?,
        newLog: String,
        @LogLevelRange level: Int
    ): CharSequence {
        val builder = StringBuilder()
        builder.append(oldText)
        builder.append("\n")
        builder.append(getCurrentTime())
        builder.append(" ")
        builder.append(LogLevel.logTypeName(level))
        builder.append(" ")
        builder.append(newLog)
        return builder.toString()
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm:ss:SSS", Locale.ROOT)
        return format.format(Date())
    }
}