package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager


class FixedFlexboxLayoutManager : FlexboxLayoutManager {

    constructor(context: Context) : super(context)

    constructor(context: Context, flexDirection: Int) : super(context, flexDirection)

    constructor(context: Context, flexDirection: Int, flexWrap: Int) : super(context, flexDirection, flexWrap)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return when (lp) {
            is RecyclerView.LayoutParams -> LayoutParams(lp)
            is ViewGroup.MarginLayoutParams -> LayoutParams(lp)
            else -> LayoutParams(lp)
        }
    }
}
