package io.github.wulkanowy.materialchipsinput.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.WHITE
import android.graphics.Paint.Align.CENTER
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.Typeface.NORMAL
import android.text.TextPaint
import androidx.core.graphics.TypefaceCompat
import androidx.core.graphics.applyCanvas
import kotlin.math.abs

private val materialColors = listOf(
        0xffe57373,
        0xfff06292,
        0xffba68c8,
        0xff9575cd,
        0xff7986cb,
        0xff64b5f6,
        0xff4fc3f7,
        0xff4dd0e1,
        0xff4db6ac,
        0xff81c784,
        0xffaed581,
        0xffff8a65,
        0xffd4e157,
        0xffffd54f,
        0xffffb74d,
        0xffa1887f,
        0xff90a4ae
)

fun createLetterBitmap(context: Context, text: String): Bitmap {
    val firstChar = (text.firstOrNull { it.isLetterOrDigit() } ?: "?").toString().toUpperCase()
    val bounds = Rect()
    val dimension = context.convertDpToPixels(32f).toInt()
    val paint = TextPaint().apply {
        typeface = TypefaceCompat.create(context, Typeface.create("sans-serif-light", NORMAL), NORMAL)
        color = WHITE
        textAlign = CENTER
        isAntiAlias = true
        textSize = context.convertDpToPixels(17f)
        getTextBounds(firstChar, 0, 1, bounds)
    }
    return Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888).applyCanvas {
        drawColor(materialColors[abs(text.hashCode()) % materialColors.size].toInt())
        drawText(firstChar, 0, 1, (dimension / 2).toFloat(), (dimension / 2 + (bounds.bottom - bounds.top) / 2).toFloat(), paint)
    }
}
