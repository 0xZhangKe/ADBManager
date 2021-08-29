package com.zhangke.adbmanager.util

/**
 * Created by ZhangKe on 2021/8/1.
 */
class ADBHelper {

    companion object {
        private const val TAG = "ADBHelper"
        private const val DEFAULT_PORT = 5555
        private const val NON_ADB_PORT = -1
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

    fun getRunningPort(): Int {
        return DeviceUtils.getProp(KEY_PROP_ADB_PORT).toIntOr(NON_ADB_PORT)
    }

    fun getDesiredPort(): Int {
        return ConfigManager.getInt(KEY_CONFIG_PORT, DEFAULT_PORT)
    }

    fun openADB(port: Int? = null) {
        if (adbIsOpen) {
            closeADB()
        }
        port?.let { setDesirePort(it) }
        val realPort = port ?: getDesiredPort()
        openADBInternal(realPort)
    }

    fun addListener(listener: ADBStateListener) {
        listener(adbIsOpen)
        listenerList += listener
    }

    fun removeListener(listener: ADBStateListener) {
        listenerList -= listener
    }

    fun closeADB() {
        Logger.i(TAG, "closing adb...")
        try {
            DeviceUtils.setProp(KEY_PROP_ADB_PORT, "$NON_ADB_PORT")
            DeviceUtils.runRootCommand("stop $ADB_PROCESS_NAME")
            Logger.i(TAG, "adb closed")
        } catch (e: Exception) {
            Logger.e(TAG, "closeADB()", e)
            Logger.i(TAG, "adb closing error:$e")
        }
        updateAdbState()
    }

    private fun openADBInternal(port: Int): Boolean {
        Logger.i(TAG, "open adb:$port")
        return try {
            DeviceUtils.setProp(KEY_PROP_ADB_PORT, port.toString())
            if (DeviceUtils.isProcessRunning(ADB_PROCESS_NAME)) {
                Logger.i(TAG, "killing process $ADB_PROCESS_NAME ...")
                val killed = DeviceUtils.runRootCommand("stop $ADB_PROCESS_NAME")
                if (killed) {
                    Logger.i(TAG, "Process killed")
                } else {
                    Logger.i(TAG, "Kill failed!")
                }
            }
            DeviceUtils.runRootCommand("start $ADB_PROCESS_NAME")
            Logger.i(TAG, "adb opened")
            true
        } catch (e: Exception) {
            Logger.e(TAG, "openADB()", e)
            false
        } finally {
            updateAdbState()
        }
    }

    private fun updateAdbState() {
        adbIsOpen = DeviceUtils.isProcessRunning(ADB_PROCESS_NAME)
    }

    private fun setDesirePort(port: Int) {
        ConfigManager.putInt(KEY_CONFIG_PORT, port)
    }
}

typealias ADBStateListener = ((Boolean) -> Unit)