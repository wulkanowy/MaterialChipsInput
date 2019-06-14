package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager.HORIZONTAL
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
            layoutManager = ChipsLayoutManager.newBuilder(context)
                    .setOrientation(HORIZONTAL)
                    .build()
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
        dropdownListView.processText(text)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        val isConsumed = dropdownListView.processKeyEvent(event)
        return if (isConsumed) true else super.dispatchKeyEventPreIme(event)
    }
}
