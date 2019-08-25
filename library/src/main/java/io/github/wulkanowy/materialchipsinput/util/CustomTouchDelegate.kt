package io.github.wulkanowy.materialchipsinput.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.TouchDelegate
import android.view.View

class CustomTouchDelegate(private val bounds: Rect, private val delegateView: View) :
    TouchDelegate(bounds, delegateView) {

    private var delegateTargeted = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var sendToDelegate = false

        when (event.actionMasked) {
            ACTION_DOWN -> {
                delegateTargeted = bounds.contains(x, y)
                sendToDelegate = delegateTargeted
            }
            ACTION_POINTER_DOWN, ACTION_POINTER_UP, ACTION_UP, ACTION_MOVE -> {
                sendToDelegate = delegateTargeted
            }
            ACTION_CANCEL -> {
                sendToDelegate = delegateTargeted
                delegateTargeted = false
            }
        }

        return if (sendToDelegate) delegateView.dispatchTouchEvent(event) else false
    }
}