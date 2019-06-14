package io.github.wulkanowy.materialchipsinput


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

internal class DropdownListViewAdapter(
        originalChipList: List<MaterialChipItem>,
        private val context: Context,
        private val chipInput: MaterialChipInput)
    : RecyclerView.Adapter<DropdownListViewAdapter.ItemViewHolder>() {

    private val currentChipList = originalChipList.toMutableList()

    private val filteredChipList = originalChipList.toMutableList()

    private var chipFilter: ChipFilter

    init {
        chipFilter = ChipFilter(this)
    }

    override fun getItemCount() = filteredChipList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dropdown_list, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder) {
            itemView.setOnClickListener {
                filteredChipList.getOrNull(position)?.let {
                    chipInput.onItemInListSelected(it)
                }
            }
        }
    }

    fun processText(text: CharSequence, onComplete: (filteredCount: Int) -> Unit) {
        chipFilter.filter(text, onComplete)
    }

    fun removeItem(chipItem: MaterialChipItem) {
        currentChipList.remove(chipItem)
        with(filteredChipList) {
            clear()
            addAll(currentChipList)
        }
        notifyDataSetChanged()
    }

    fun addItem(chipItem: MaterialChipItem) {
        currentChipList.add(chipItem)
        with(filteredChipList) {
            clear()
            addAll(currentChipList)
        }
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView get() = itemView
    }

    class ChipFilter(private val adapter: DropdownListViewAdapter) : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val originalList = adapter.currentChipList
            val filteredList = mutableListOf<MaterialChipItem>()

            if (constraint.isBlank()) {
                filteredList.addAll(originalList)
            } else {
                val pattern = constraint.toString().toLowerCase().trim()
                originalList.forEach {
                    if (it.title.toLowerCase().contains(pattern)) {
                        filteredList.add(it)
                    }
                }
            }

            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            with(adapter) {
                filteredChipList.clear()
                filteredChipList.addAll(results.values as List<MaterialChipItem>)
                notifyDataSetChanged()
            }
        }
    }
}
