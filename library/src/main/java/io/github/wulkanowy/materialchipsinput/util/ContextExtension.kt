package io.github.wulkanowy.materialchipsinput.util

import android.content.Context
import android.util.DisplayMetrics.DENSITY_DEFAULT

internal fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT
