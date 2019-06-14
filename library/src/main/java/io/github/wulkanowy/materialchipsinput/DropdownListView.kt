package io.github.wulkanowy.materialchipsinput


import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Rect
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import androidx.core.view.marginLeft
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.materialchipsinput.util.navBarHeight
import kotlinx.android.synthetic.main.list_dropdown.view.*

internal class DropdownListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : RelativeLayout(context, attrs, defStyle) {

    lateinit var dropdownListViewAdapter: DropdownListViewAdapter

    private lateinit var chipInput: MaterialChipInput

    init {
        View.inflate(context, R.layout.list_dropdown, this)
        visibility = GONE
    }

    fun initialize(chipList: List<MaterialChipItem>, chipInput: MaterialChipInput) {
        this.chipInput = chipInput

        dropdownListViewAdapter = DropdownListViewAdapter(chipList, context, chipInput)
        with(listDropdownRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = dropdownListViewAdapter
        }

        chipInput.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                val layoutParams = LayoutParams(context.resources.displayMetrics.widthPixels, MATCH_PARENT)
                        .apply {
                            addRule(ALIGN_PARENT_TOP)
                            addRule(ALIGN_PARENT_LEFT)

                            if (context.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                                bottomMargin = context.navBarHeight
                            }
                        }

                (chipInput.rootView as ViewGroup).addView(this@DropdownListView, layoutParams)

                if (SDK_INT < JELLY_BEAN) {
                    @Suppress("DEPRECATION")
                    chipInput.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    chipInput.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }

    fun processChangedText(text: CharSequence?) {
        if (text.isNullOrBlank()) {
            fadeOut()
            return
        }

        dropdownListViewAdapter.filterText(text) {
            if (it > 0) {
                fadeIn()
                listDropdownRecycler.smoothScrollToPosition(0)
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

        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)

        val coordinators = IntArray(2)
        chipInput.getLocationInWindow(coordinators)

        (layoutParams as MarginLayoutParams).updateMargins(
                top = coordinators[1] + chipInput.height,
                bottom = rootView.height - rect.bottom,
                left = if (rect.left > 0) rect.left else marginLeft
        )
        requestLayout()
        startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 200 })
        visibility = VISIBLE
    }

    private fun fadeOut() {
        if (visibility == GONE) return

        startAnimation(AlphaAnimation(1.0f, 0.0f).apply { duration = 200 })
        visibility = GONE
    }
}
