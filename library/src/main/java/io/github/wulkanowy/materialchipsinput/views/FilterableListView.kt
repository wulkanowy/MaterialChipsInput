package io.github.wulkanowy.materialchipsinput.views


import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Rect
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import io.github.wulkanowy.materialchipsinput.MaterialChipsInput
import io.github.wulkanowy.materialchipsinput.R
import io.github.wulkanowy.materialchipsinput.adapter.FilterableAdapter
import io.github.wulkanowy.materialchipsinput.util.ViewUtil
import kotlinx.android.synthetic.main.list_dropdown.view.*

internal class FilterableListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : RelativeLayout(context, attrs, defStyle) {

    lateinit var filterableAdapter: FilterableAdapter

    private lateinit var chipsInput: MaterialChipsInput

    init {
        View.inflate(context, R.layout.list_dropdown, this)
        visibility = GONE
    }

    fun initialize(chipList: List<Chip>, chipsInput: MaterialChipsInput) {
        this.chipsInput = chipsInput

        filterableAdapter = FilterableAdapter(context, chipList, chipsInput)
        with(listDropdownRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = filterableAdapter
        }

        chipsInput.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                val layoutParams = LayoutParams(ViewUtil.getWindowWidth(context), MATCH_PARENT)
                        .apply {
                            addRule(ALIGN_PARENT_TOP)
                            addRule(ALIGN_PARENT_LEFT)

                            if (context.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                                bottomMargin = ViewUtil.getNavBarHeight(context)
                            }
                        }

                (chipsInput.rootView as ViewGroup).addView(this@FilterableListView, layoutParams)

                if (SDK_INT < JELLY_BEAN) {
                    @Suppress("DEPRECATION")
                    chipsInput.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    chipsInput.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }

    fun processText(text: CharSequence?) {
        if (text.isNullOrBlank()) {
            fadeOut()
            return
        }

        filterableAdapter.processText(text) {
            if (it > 0) fadeIn()
            else fadeOut()
        }
    }

    private fun fadeIn() {
        if (visibility == VISIBLE) return

        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)

        val coordinators = IntArray(2)
        chipsInput.getLocationInWindow(coordinators)

        val layoutParams = layoutParams as MarginLayoutParams
        layoutParams.topMargin = coordinators[1] + chipsInput.height

        layoutParams.bottomMargin = rootView.height - rect.bottom
        setLayoutParams(layoutParams)

        startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 200 })
        visibility = VISIBLE
    }

    private fun fadeOut() {
        if (visibility == GONE) return

        startAnimation(AlphaAnimation(1.0f, 0.0f).apply { duration = 200 })
        visibility = GONE
    }
}
