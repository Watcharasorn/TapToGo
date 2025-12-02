package com.reader.client

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.common.apiutil.ResultCode
import com.common.apiutil.pos.CommonUtil

/**
 * Simple helper around Telpo's color LED API.
 * Provides three calls: yellow (default), green, red.
 * Green/Red automatically turn off after a short delay.
 */
class ColorLedController(context: Context) {

    private val commonUtil = CommonUtil(context)
    private val handler = Handler(Looper.getMainLooper())

    private val ledTypes: IntArray = lookupConstants(
        LED_TYPE_CLASS,
        listOf("COLOR_LED_1", "COLOR_LED_2", "COLOR_LED_3", "COLOR_LED_4")
    )

    private val yellowColor = constantInt(LED_COLOR_CLASS, "YELLOW_LED")
    private val greenColor = constantInt(LED_COLOR_CLASS, "GREEN_LED")
    private val redColor = constantInt(LED_COLOR_CLASS, "RED_LED")
    private val whiteColor = constantInt(LED_COLOR_CLASS, "WHITE_LED")
    private val defaultColor = listOf(yellowColor, whiteColor, greenColor, redColor).firstOrNull { it >= 0 } ?: 0
    private var currentColor = defaultColor

    private val resetRunnable = Runnable { turnOffAll() }

    fun showYellow(holdMs: Long = DEFAULT_HOLD_MS) {
        handler.removeCallbacks(resetRunnable)
        applyColor(yellowColor, DEFAULT_BRIGHTNESS)
        scheduleReset(holdMs)
    }

    fun showGreen(holdMs: Long = DEFAULT_HOLD_MS) {
        handler.removeCallbacks(resetRunnable)
        applyColor(greenColor, DEFAULT_BRIGHTNESS)
        scheduleReset(holdMs)
    }

    fun showRed(holdMs: Long = DEFAULT_HOLD_MS) {
        handler.removeCallbacks(resetRunnable)
        applyColor(redColor, DEFAULT_BRIGHTNESS)
        scheduleReset(holdMs)
    }

    fun shutdown() {
        handler.removeCallbacks(resetRunnable)
        turnOffAll()
    }

    private fun scheduleReset(delayMs: Long) {
        if (delayMs <= 0) {
            turnOffAll()
        } else {
            handler.postDelayed(resetRunnable, delayMs)
        }
    }

    private fun applyColor(color: Int, brightness: Int) {
        val effectiveColor = if (color >= 0) color else defaultColor
        if (effectiveColor < 0) {
            Log.w(TAG, "Skip LED update: no valid color constant available")
            return
        }
        if (ledTypes.isEmpty()) {
            Log.w(TAG, "Skip LED update: led type constants unavailable")
            return
        }

        val action = Runnable {
            ledTypes.forEach { ledType ->
                val result = runCatching { commonUtil.setColorLed(ledType, effectiveColor, brightness) }.getOrElse {
                    Log.e(TAG, "setColorLed failed for type $ledType", it)
                    ResultCode.ERR_SYS_UNEXPECT
                }
                if (result != ResultCode.SUCCESS) {
                    Log.w(TAG, "setColorLed returned code $result for type $ledType")
                }
            }
            if (effectiveColor >= 0) {
                currentColor = effectiveColor
            }
        }

        if (Looper.myLooper() == handler.looper) {
            action.run()
        } else {
            handler.post(action)
        }
    }

    private fun turnOffAll() {
        val color = if (currentColor >= 0) currentColor else defaultColor
        handler.removeCallbacks(resetRunnable)
        applyColor(color, 0)
    }

    private fun lookupConstants(className: String, fieldNames: List<String>): IntArray {
        return fieldNames.mapNotNull { name ->
            val value = constantInt(className, name, fallback = Int.MIN_VALUE)
            if (value == Int.MIN_VALUE) null else value
        }.toIntArray().also {
            if (it.isEmpty()) {
                logAvailableFields(className)
            }
        }
    }

    private fun constantInt(className: String, fieldName: String, fallback: Int = -1): Int {
        return try {
            val clazz = Class.forName(className)
            val field = clazz.fields.firstOrNull { it.name == fieldName }
                ?: clazz.declaredFields.firstOrNull { it.name == fieldName }
            if (field != null) {
                field.isAccessible = true
                field.getInt(null)
            } else {
                logAvailableFields(className)
                fallback
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Unable to load $fieldName from $className", ex)
            fallback
        }
    }

    private fun logAvailableFields(className: String) {
        runCatching { Class.forName(className) }.onSuccess { clazz ->
            val names = clazz.fields.map { it.name } + clazz.declaredFields.map { it.name }
            Log.w(TAG, "Available fields for $className => $names")
        }.onFailure {
            Log.e(TAG, "Cannot enumerate fields for $className", it)
        }
    }

    companion object {
        private const val TAG = "ColorLedController"
        private const val DEFAULT_HOLD_MS = 2000L
        private const val DEFAULT_BRIGHTNESS = 255
        private const val LED_TYPE_CLASS = "com.common.CommonConstants\$LedType"
        private const val LED_COLOR_CLASS = "com.common.CommonConstants\$LedColor"
    }
}
