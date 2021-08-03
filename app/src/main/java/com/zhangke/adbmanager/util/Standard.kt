package com.zhangke.adbmanager.util

import java.lang.Exception

/**
 * Created by ZhangKe on 2021/8/1.
 */

inline fun <T> T?.isNull(block: () -> Unit): T? {
    if (this == null) {
        block()
    }
    return this
}

open class ErrorHandler {

    private var errorBlock: ((Exception) -> Unit)? = null

    fun onError(exception: Exception) {
        errorBlock.isNull { Logger.e("ErrorHandler", "error not implement:$exception") }
            ?.invoke(exception)
    }

    fun error(block: (Exception) -> Unit) {
        errorBlock = block
    }
}