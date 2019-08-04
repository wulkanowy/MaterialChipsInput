package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.materialchipsinput.util.dpToPx

internal class DropdownListView : RecyclerView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    fun fadeIn(chipInput: MaterialChipInput) {
        if (visibility == VISIBLE) return

        val visibleRect = Rect().apply {
            rootView.getWindowVisibleDisplayFrame(this)
            top = 0
        }

        val coordinators = IntArray(2).apply {
            chipInput.getLocationOnScreen(this)
        }

        updateLayoutParams<FrameLayout.LayoutParams> {
            val defaultHeight = context.dpToPx(144f).toInt()
            val calculatedHeight = visibleRect.height() - (coordinators[1] + chipInput.height)

            height = if (calculatedHeight < defaultHeight) defaultHeight else calculatedHeight
            topMargin = chipInput.height + chipInput.top
        }

        visibility = VISIBLE
        startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 200 })
    }

    fun fadeOut() {
        if (visibility == GONE) return

        startAnimation(AlphaAnimation(1.0f, 0.0f).apply { duration = 200 })
        visibility = GONE
    }
}
