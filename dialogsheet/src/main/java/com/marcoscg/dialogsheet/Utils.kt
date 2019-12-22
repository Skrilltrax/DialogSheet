package com.marcoscg.dialogsheet

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import androidx.annotation.ColorInt
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * Created by @MarcosCGdev on 01/12/2017.
 */
internal object Utils {

    @JvmStatic
    fun isColorLight(@ColorInt color: Int): Boolean {
        return when(color) {
            Color.BLACK -> false
            Color.WHITE or Color.TRANSPARENT -> true
            else -> getDarkness(color) < 0.4
        }
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    @ColorInt
    @JvmStatic
    fun getTextColor(color: Int): Int {
        return if (isColorLight(color)) Color.parseColor("#DE000000") else Color.parseColor("#FFFFFFFF")
    }

    @ColorInt
    @JvmStatic
    fun getTextColorSec(color: Int): Int {
        return if (isColorLight(color)) Color.parseColor("#8A000000") else Color.parseColor("#B3FFFFFF")
    }

    @ColorInt
    @JvmStatic
    fun getAttrColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
        val color = a.getColor(0, -1)
        a.recycle()
        return color
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun getDarkness(color: Int): Double =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
}