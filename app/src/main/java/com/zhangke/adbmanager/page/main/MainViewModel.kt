package com.zhangke.adbmanager.page.main

import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
    val adbIsOpened = MutableLiveData<Boolean>()

    private val adbHelper = ADBHelper()
    private val logListener = object : LogListener {

        override fun onNewLog(log: String, level: Int) {
            logText.value = buildLogText(logText.value, log, level)
        }
    }

    private val adbListener: ADBStateListener = {
        adbIsOpened.value = it
    }

    fun init(lifecycleOwner: LifecycleOwner) {
        Logger.addLogListener(logListener)
        adbHelper.addListener(adbListener)
        adbPort.value = adbHelper.getDesiredPort().toString()
        if (!DeviceUtils.hasRootPermission()) {
            showToast(R.string.root_np_permission)
            return
        }
        requireWifiOpen()
        adbIsOpened.observe(lifecycleOwner, Observer {
            updateStatusLine(it)
        })
    }

    fun onAdbClicked() {
        if (adbIsOpened.value == true) {
            adbHelper.closeADB()
        } else {
            val port = adbPort.value?.toIntOrNull()
            if (port == null) {
                showToast(R.string.no_port)
                return
            }
            adbHelper.openADB(port)
        }
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

    private fun updateStatusLine(adbIsOpened: Boolean) {
        val ip =
            DeviceUtils.getIpAddress(appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
        val port = adbHelper.getRunningPort()
        val state = if (adbIsOpened) "OPENED" else "CLOSED"
        statusLine.value = "$state $ip:$port"
    }
}