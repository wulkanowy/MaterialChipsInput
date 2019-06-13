package com.pchmn.materialchips.util

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Paint.Align.CENTER
import android.graphics.Typeface.NORMAL
import android.text.TextPaint
import androidx.core.graphics.TypefaceCompat
import io.github.wulkanowy.materialchipsinput.util.convertDpToPixels

internal class LetterTileProvider(private val context: Context) {

    private val paint = TextPaint()

    private val bounds = Rect()

    private val firstChar = CharArray(1)

    private lateinit var colors: TypedArray

    private val titleLetterFontSize: Int = 0

    private lateinit var defaultBitmap: Bitmap

    private val width: Int = 0

    private val height: Int = 0

    init {
        with(paint) {
            typeface = TypefaceCompat.create(context, Typeface.create("sans-serif-light", NORMAL), NORMAL)
            color = WHITE
            textAlign = CENTER
            isAntiAlias = true
        }
    }

    fun getLetterTile(displayName: String?): Bitmap {
        val checkedName = if (displayName.isNullOrBlank()) "." else displayName

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas().apply {
            setBitmap(bitmap)
            drawColor(pickColor(checkedName))
        }

        if (checkedName[0].isLetterOrDigit()) {
            firstChar[0] = checkedName[0].toUpperCase()
            with(paint) {
                textSize = titleLetterFontSize.toFloat()
                getTextBounds(firstChar, 0, 1, bounds)
            }
            canvas.drawText(firstChar, 0, 1, (width / 2).toFloat(), (height / 2 + (bounds.bottom - bounds.top) / 2).toFloat(), paint)
        } else {
            val dimension = context.convertDpToPixels(4f)
            canvas.drawBitmap(defaultBitmap, dimension, dimension, null)
        }
        return bitmap
    }


    fun getCircularLetterTile(displayName: String?): Bitmap {
        val bitmap = getLetterTile(displayName)

        val output = if (bitmap.width > bitmap.height) {
            Bitmap.createBitmap(bitmap.height, bitmap.height, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)
        }

        val dimension = if (bitmap.width > bitmap.height) {
            (bitmap.height / 2).toFloat()
        } else {
            (bitmap.width / 2).toFloat()
        }

        val canvas = Canvas(output)
        val bitmapPaint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        with(bitmapPaint) {
            isAntiAlias = true
            color = -0xbdbdbe
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }

        with(canvas) {
            drawARGB(0, 0, 0, 0)
            drawCircle(dimension, dimension, dimension, bitmapPaint)
            drawBitmap(bitmap, rect, rect, bitmapPaint)
        }
        return output
    }

    private fun pickColor(key: String) = colors.getColor(Math.abs(key.hashCode()) % 8, BLACK)
}
