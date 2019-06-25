package io.github.wulkanowy.materialchipsinput


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.materialchipsinput.util.createLetterBitmap
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_dropdown_list.*

internal class DropdownListViewAdapter(private val context: Context)
    : RecyclerView.Adapter<DropdownListViewAdapter.ItemViewHolder>() {

    var onItemsSelectedListener: (item: MaterialChipItem) -> Unit = {}

    private val originalItemList = mutableListOf<MaterialChipItem>()

    private val currentItemList = mutableListOf<MaterialChipItem>()

    private val filteredItemList = mutableListOf<MaterialChipItem>()

    private var chipFilter = ItemFilter(this)

    override fun getItemCount() = filteredItemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dropdown_list, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder) {
            filteredItemList.getOrNull(position)?.let { materialChipItem ->
                itemDropdownAvatar.setImageBitmap(createLetterBitmap(context, materialChipItem.title))
                itemDropdownTitle.text = materialChipItem.title
                itemDropdownSummary.text = materialChipItem.summary
                itemView.setOnClickListener { onItemsSelectedListener(materialChipItem) }
            }
        }
    }

    fun updateDataSet(items: List<MaterialChipItem>) {
        with(originalItemList) {
            clear()
            addAll(items)
        }

        with(currentItemList) {
            clear()
            addAll(items)
        }
    }

    fun filterText(text: CharSequence, onComplete: (filteredCount: Int) -> Unit) {
        chipFilter.filter(text, onComplete)
    }

    fun removeItemFromSet(chipItem: MaterialChipItem) {
        currentItemList.remove(chipItem)
    }

    fun addItemToSet(chipItem: MaterialChipItem) {
        currentItemList.add(chipItem)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView get() = itemView
    }

    class ItemFilter(private val adapter: DropdownListViewAdapter) : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val originalList = adapter.currentItemList
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
                filteredItemList.clear()
                filteredItemList.addAll(results.values as List<MaterialChipItem>)
                notifyDataSetChanged()
            }
        }
    }
}
