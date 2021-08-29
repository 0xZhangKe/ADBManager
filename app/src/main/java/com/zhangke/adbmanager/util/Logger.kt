package com.zhangke.adbmanager.util

import android.util.Log
import androidx.annotation.IntDef
import java.lang.Exception

/**
 * Created by ZhangKe on 2021/8/1.
 */
object Logger {

    private val listenerList = mutableListOf<LogListener>()

    fun addLogListener(listener: LogListener) {
        listenerList += listener
    }

    fun removeListener(listener: LogListener) {
        listenerList -= listener
    }

    fun i(tag: String, text: String) {
        Log.i(tag, text)
        listenerList.forEach { it.onNewLog(text, LogLevel.INFO) }
    }

    fun d(tag: String, text: String) {
        Log.d(tag, text)
        listenerList.forEach { it.onNewLog(text, LogLevel.DEBUG) }
    }

    fun e(tag: String, message: String, e: Exception) {
        Log.e(tag, message, e)
        listenerList.forEach { it.onNewLog(buildErrorLogText(message, e), LogLevel.ERROR) }
    }

    fun e(tag: String, message: String) {
        Log.e(tag, message)
        listenerList.forEach { it.onNewLog(buildErrorLogText(message), LogLevel.ERROR) }
    }

    private fun buildErrorLogText(message: String? = null, e: Exception? = null): String {
        if (message == null && e == null) return "empty error log!"
        if (message == null) return e.toString()
        if (e == null) return message
        return "message: $message, error:$e"
    }
}

fun
interface LogListener {

    fun onNewLog(log: String, @LogLevelRange level: Int)
}

object LogLevel {
    const val INFO = 0
    const val DEBUG = 1
    const val ERROR = 2

    fun logTypeName(@LogLevelRange level: Int): String {
        return when (level) {
            INFO -> "INFO"
            DEBUG -> "DEBUG"
            else -> "ERROR"
        }
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@IntDef(value = [LogLevel.INFO, LogLevel.DEBUG, LogLevel.ERROR])
@Retention(AnnotationRetention.SOURCE)
annotation class LogLevelRange