package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.google.android.flexbox.FlexWrap
import kotlinx.android.synthetic.main.input_chips.view.*

class MaterialChipInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private var materialChipInputAdapter: MaterialChipInputAdapter

    private var dropdownListView: DropdownListView

    var itemList: List<MaterialChipItem>? = null
        set(list) {
            field = list
            list?.let {
                dropdownListView.initialize(it, this)
            }
        }

    val selectedChipList get() = materialChipInputAdapter.chipList

    init {
        View.inflate(context, R.layout.input_chips, this)
        materialChipInputAdapter = MaterialChipInputAdapter(context, this, inputChipsRecycler)
        dropdownListView = DropdownListView(context)

        with(inputChipsRecycler) {
            isNestedScrollingEnabled = false
            adapter = materialChipInputAdapter
            layoutManager = FixedFlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
            }
        }
    }

    internal fun onItemInListSelected(chipItem: MaterialChipItem) {
        materialChipInputAdapter.addChip(chipItem)
    }

    internal fun onChipAdded(chipItem: MaterialChipItem) {
        dropdownListView.dropdownListViewAdapter.removeItem(chipItem)
    }

    internal fun onChipRemoved(chipItem: MaterialChipItem) {
        dropdownListView.dropdownListViewAdapter.addItem(chipItem)
    }

    internal fun onTextChanged(text: CharSequence?) {
        dropdownListView.processChangedText(text)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        val isConsumed = dropdownListView.processKeyEvent(event)
        return if (isConsumed) true else super.dispatchKeyEventPreIme(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        var isHandled = false
        val editHitRect = Rect()
        materialChipInputAdapter.chipEditText.getHitRect(editHitRect)

        val recyclerHitRect = Rect()
        inputChipsRecycler.getHitRect(recyclerHitRect)

        val extendedHitRect = Rect(editHitRect.right, editHitRect.top, recyclerHitRect.right, editHitRect.bottom)

        event?.let {
            if (extendedHitRect.contains(it.x.toInt(), it.y.toInt())) {
                materialChipInputAdapter.chipEditText.apply {
                    isHandled = materialChipInputAdapter.chipEditText.dispatchTouchEvent(it)
                }
            }
        }

        return if (isHandled) true else super.dispatchTouchEvent(event)
    }
}
