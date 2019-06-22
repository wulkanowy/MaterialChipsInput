package io.github.wulkanowy.materialchipsinput


import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.materialchipsinput.util.dpToPx

internal class DropdownListView : RecyclerView {

    lateinit var dropdownListViewAdapter: DropdownListViewAdapter

    private lateinit var chipInput: MaterialChipInput

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    init {
        visibility = GONE
    }

    fun initialize(chipList: List<MaterialChipItem>, chipInput: MaterialChipInput) {
        this.chipInput = chipInput

        dropdownListViewAdapter = DropdownListViewAdapter(chipList, context, chipInput)
        layoutManager = LinearLayoutManager(context)
        adapter = dropdownListViewAdapter

        chipInput.post {
            chipInput.addView(this, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }

    fun processChangedText(text: CharSequence?) {
        if (text.isNullOrBlank()) {
            fadeOut()
            return
        }

        dropdownListViewAdapter.filterText(text) {
            if (it > 0) {
                fadeIn()
                smoothScrollToPosition(0)
            } else fadeOut()
        }
    }

    fun processKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KEYCODE_BACK && visibility == VISIBLE) {
            fadeOut()
            true
        } else false
    }

    private fun fadeIn() {
        if (visibility == VISIBLE) return

        val visibleRect = Rect().apply {
            rootView.getWindowVisibleDisplayFrame(this)
            top = 0
        }

        val coordinators = IntArray(2).apply {
            chipInput.getLocationOnScreen(this)
        }

        updateLayoutParams<LinearLayout.LayoutParams> {
            val defaultHeight = context.dpToPx(72f).toInt()
            val calculatedHeight = visibleRect.height() - (coordinators[1] + chipInput.height)

            height = if (calculatedHeight < defaultHeight) defaultHeight else calculatedHeight
        }

        visibility = VISIBLE
        startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 200 })
    }

    private fun fadeOut() {
        if (visibility == GONE) return

        startAnimation(AlphaAnimation(1.0f, 0.0f).apply { duration = 200 })
        visibility = GONE
    }
}
