package io.github.wulkanowy.materialchipsinput.util

import android.content.Context
import android.util.DisplayMetrics.DENSITY_DEFAULT
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

internal fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT

@ColorInt
internal fun Context.getThemeAttrColor(@AttrRes colorAttr: Int): Int {
    val array = obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}
