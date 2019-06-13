package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager.HORIZONTAL
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.input_chips.view.*

class MaterialChipsInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private var materialChipsInputAdapter: MaterialChipsInputAdapter

    private var dropdownListView: DropdownListView

    var itemList: List<Chip>? = null
        set(list) {
            field = list
            list?.let {
                dropdownListView.initialize(it, this)
            }
        }

    val selectedChipList get() = materialChipsInputAdapter.chipList

    init {
        View.inflate(context, R.layout.input_chips, this)
        materialChipsInputAdapter = MaterialChipsInputAdapter(context, this, inputChipsRecycler)
        dropdownListView = DropdownListView(context)

        with(inputChipsRecycler) {
            isNestedScrollingEnabled = false
            adapter = materialChipsInputAdapter
            layoutManager = ChipsLayoutManager.newBuilder(context)
                    .setOrientation(HORIZONTAL)
                    .build()
        }
    }

    internal fun onItemSelectedInternal(chip: Chip) {
        materialChipsInputAdapter.addChip(chip)
    }

    internal fun onChipAddedInternal(chip: Chip) {
        dropdownListView.dropdownListViewAdapter.removeItem(chip)
    }

    internal fun onChipRemovedInternal(chip: Chip) {
        dropdownListView.dropdownListViewAdapter.addItem(chip)
    }

    internal fun onTextChangedInternal(text: CharSequence?) {
        dropdownListView.processText(text)
    }
}
