package com.zhangke.adbmanager.util

import android.net.wifi.WifiManager
import java.io.BufferedReader
import java.io.DataOutputStream


/**
 * Created by ZhangKe on 2021/8/1.
 */

object DeviceUtils {

    private const val TAG = "DeviceUtils"

    fun setProp(property: String, value: String): Boolean {
        var successed = true
        suProcessOutputStream { process, out ->
            out.writeBytes("setprop $property $value\n")
            out.writeBytes("exit\n")
            out.flush()
            process.waitFor()
        }.error {
            Logger.e(TAG, "setProp($property, $value)", it)
            successed = false
        }
        return successed
    }

    fun getProp(property: String): String {
        var result = ""
        suProcessOutputStream { process, out ->
            val reader = BufferedReader(process.inputStream.reader())
            out.writeBytes("getprop $property\n")
            out.writeBytes("exit\n")
            out.flush()
            process.waitFor()
            result = reader.readLine()
            Logger.i(TAG, "getProp($property) -> $result")
        }
        return result
    }

    fun isProcessRunning(processName: String): Boolean {
        var running = false
        suProcessOutputStream { process, outputStream ->
            val reader = BufferedReader(process.inputStream.reader())
            outputStream.writeBytes("ps\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            process.waitFor()
            reader.forEachLine { line ->
                if (line.contains(processName)) {
                    running = true
                }
            }
        }.error {
            Logger.e(TAG, it.toString())
        }
        Logger.i(TAG, "isProcessRunning($processName) -> $running")
        return running
    }

    @JvmStatic
    fun hasRootPermission(): Boolean {
        var rooted = true
        suProcessOutputStream { process, out ->
            out.writeBytes("exit\n")
            out.flush()
            process.waitFor()
            if (process.exitValue() != 0) {
                rooted = false
            }
        }.error {
            Logger.e(TAG, "hasRootPermission()", it)
            rooted = false
        }
        return rooted
    }

    @JvmStatic
    fun runRootCommand(command: String): Boolean {
        var successed = true
        suProcessOutputStream { process, out ->
            out.writeBytes("$command\n")
            out.writeBytes("exit\n")
            out.flush()
            process.waitFor()
        }.error {
            Logger.e(TAG, "runRootCommand($command)", it)
            successed = false
        }
        return successed
    }

    @JvmStatic
    fun getIpAddress(wifiManager: WifiManager): String? {
        val ip = wifiManager.connectionInfo?.ipAddress ?: return null
        return ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "."
                + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF))
    }

    @JvmStatic
    fun wifiIsOpen(wifiManager: WifiManager): Boolean {
        return try {
            val wifiInfo = wifiManager.connectionInfo
            !(!wifiManager.isWifiEnabled || wifiInfo.ssid == null)
        } catch (e: Exception) {
            Logger.e(TAG, "wifiIsOpen()", e)
            false
        }
    }

    @JvmStatic
    private fun suProcessOutputStream(block: (Process, DataOutputStream) -> Unit): ErrorHandler {
        return suProcess().use { process ->
            DataOutputStream(process.outputStream)
                .use { block(process, it) }
        }
    }

    @JvmStatic
    private fun suProcess(): Process {
        return Runtime.getRuntime().exec("su")
    }

    @JvmStatic
    private inline fun Process.use(block: (Process) -> Unit): ErrorHandler {
        val errorHandler = ErrorHandler()
        try {
            block(this)
        } catch (e: Exception) {
            errorHandler.onError(e)
        } finally {
            destroy()
        }
        return errorHandler
    }
}