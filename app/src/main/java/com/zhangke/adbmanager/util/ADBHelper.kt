package com.zhangke.adbmanager.util

/**
 * Created by ZhangKe on 2021/8/1.
 */
class ADBHelper {

    companion object {
        private const val DEFAULT_PORT = 5555
        private const val KEY_CONFIG_PORT = "adb_port"
        private const val ADB_PROCESS_NAME = "adbd"
        private const val KEY_PROP_ADB_PORT = "service.adb.tcp.port"
    }

    var adbIsOpen = false
        private set(value) {
            if (field == value) return
            field = value
            listenerList.forEach { it(value) }
        }
        get() {
            updateAdbState()
            return field
        }

    private val listenerList = mutableListOf<ADBStateListener>()

    init {
        updateAdbState()
    }

    fun getDefaultPort(): Int {
        return ConfigManager.getInt(KEY_CONFIG_PORT, DEFAULT_PORT)
    }

    fun setPort(port: Int) {
        ConfigManager.putInt(KEY_CONFIG_PORT, port)
    }

    fun addListener(listener: ADBStateListener) {
        listener(adbIsOpen)
        listenerList += listener
    }

    fun removeListener(listener: ADBStateListener) {
        listenerList -= listener
    }

    fun toggleADB() {
        if (adbIsOpen) {
            closeADB()
        } else {
            openADB()
        }
    }

    private fun openADB() {

    }

    private fun closeADB() {

    }

    private fun updateAdbState() {
        adbIsOpen = DeviceUtils.isProcessRunning(ADB_PROCESS_NAME)
    }
}

typealias ADBStateListener = ((Boolean) -> Unit)