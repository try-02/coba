package com.pos.offline.util

import android.os.Looper
import android.util.Log
import com.pos.offline.BuildConfig

object AppLogger {

    const val TAG_LIFECYCLE = "POS_LIFECYCLE"
    const val TAG_VM = "POS_VM"
    const val TAG_STATE = "POS_STATE"
    const val TAG_DB = "POS_DB"
    const val TAG_THREAD = "POS_THREAD"
    const val TAG_COROUTINE = "POS_COROUTINE"
    const val TAG_IO = "POS_IO"

    private const val SLOW_THRESHOLD_MS = 150L

    inline fun d(
        tag: String,
        message: () -> String,
    ) {
        if (BuildConfig.DEBUG) {
            val threadName = Thread.currentThread().name
            val isMain = Looper.myLooper() == Looper.getMainLooper()
            val threadIndicator =
                if (isMain) {
                    "[MAIN]"
                } else {
                    "[$threadName]"
                }

            Log.d(tag, "$threadIndicator ${message()}")
        }
    }

    inline fun e(
        tag: String,
        throwable: Throwable? = null,
        message: () -> String,
    ) {
        if (BuildConfig.DEBUG) {
            val threadName = Thread.currentThread().name

            Log.e(
                tag,
                "[$threadName] ❌ ${message()}",
                throwable,
            )
        }
    }

    fun warnIfMainThread(operationName: String) {
        if (
            BuildConfig.DEBUG &&
            Looper.myLooper() == Looper.getMainLooper()
        ) {
            Log.e(
                TAG_THREAD,
                "⚠️ '$operationName' sedang berjalan pada MAIN thread",
            )
        }
    }

    fun <T> measure(
        tag: String,
        operationName: String,
        block: () -> T,
    ): T {
        if (!BuildConfig.DEBUG) {
            return block()
        }

        val start = System.nanoTime()
        var completed = false

        try {
            val result = block()
            completed = true
            return result
        } finally {
            val durationMs =
                (System.nanoTime() - start) / 1_000_000L

            val flag =
                if (durationMs > SLOW_THRESHOLD_MS) {
                    "SLOW (>150ms)"
                } else {
                    "OK"
                }

            val status =
                if (completed) {
                    "SUCCESS"
                } else {
                    "TERMINATED/EXCEPTION"
                }

            d(tag) {
                "$operationName [$status] selesai dalam ${durationMs}ms [$flag]"
            }
        }
    }

suspend fun <T> measureSuspend(
    tag: String,
    operationName: String,
    block: suspend () -> T,
): T {
        if (!BuildConfig.DEBUG) {
            return block()
        }

        val start = System.nanoTime()
        var completed = false

        try {
            val result = block()
            completed = true
            return result
        } finally {
            val durationMs =
                (System.nanoTime() - start) / 1_000_000L

            val flag =
                if (durationMs > SLOW_THRESHOLD_MS) {
                    "SLOW (>150ms)"
                } else {
                    "OK"
                }

            val status =
                if (completed) {
                    "SUCCESS"
                } else {
                    "TERMINATED/CANCELLED"
                }

            d(tag) {
                "$operationName [$status] selesai dalam ${durationMs}ms [$flag]"
            }
        }
    }
}