package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager.HORIZONTAL
import com.google.android.material.chip.Chip
import io.github.wulkanowy.materialchipsinput.adapter.ChipsAdapter
import io.github.wulkanowy.materialchipsinput.views.FilterableListView
import kotlinx.android.synthetic.main.input_chips.view.*

class MaterialChipsInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private var chipsAdapter: ChipsAdapter

    private var filterableListView: FilterableListView

    var itemList: List<Chip>? = null
        set(list) {
            field = list
            list?.let {
                filterableListView.initialize(it, this)
            }
        }

    val selectedChipList get() = chipsAdapter.chipList

    init {
        View.inflate(context, R.layout.input_chips, this)
        chipsAdapter = ChipsAdapter(context, this, inputChipsRecycler)
        filterableListView = FilterableListView(context)

        with(inputChipsRecycler) {
            isNestedScrollingEnabled = false
            adapter = chipsAdapter
            layoutManager = ChipsLayoutManager.newBuilder(context)
                    .setOrientation(HORIZONTAL)
                    .build()
        }
    }

    internal fun onItemSelectedInternal(chip: Chip) {
        chipsAdapter.addChip(chip)
    }

    internal fun onChipAddedInternal(chip: Chip) {
        filterableListView.filterableAdapter.removeItem(chip)
    }

    internal fun onChipRemovedInternal(chip: Chip) {
        filterableListView.filterableAdapter.addItem(chip)
    }

    internal fun onTextChangedInternal(text: CharSequence?) {
        filterableListView.processText(text)
    }
}
