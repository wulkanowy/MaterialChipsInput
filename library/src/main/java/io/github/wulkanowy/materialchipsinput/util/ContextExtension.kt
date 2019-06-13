package io.github.wulkanowy.materialchipsinput.util

import android.content.Context
import android.content.res.Configuration.*
import android.util.DisplayMetrics.DENSITY_DEFAULT
import android.view.KeyCharacterMap
import android.view.KeyEvent.KEYCODE_BACK
import android.view.ViewConfiguration

fun Context.convertDpToPixels(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT

val Context.navBarHeight: Int
    get() {
        if (!ViewConfiguration.get(this).hasPermanentMenuKey() && !KeyCharacterMap.deviceHasKey(KEYCODE_BACK)) {
            with(resources) {
                val name = when {
                    configuration.orientation == ORIENTATION_PORTRAIT -> "navigation_bar_height"
                    configuration.screenLayout and SCREENLAYOUT_SIZE_MASK >= SCREENLAYOUT_SIZE_LARGE -> "navigation_bar_height_landscape"
                    else -> "navigation_bar_width"
                }

                val resourcesId = getIdentifier(name, "dimen", "android")
                if (resourcesId > 0) return resources.getDimensionPixelSize(resourcesId)
            }
        }
        return 0
    }
