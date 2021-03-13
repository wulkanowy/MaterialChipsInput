package io.github.wulkanowy.materialchipsinput

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.materialchipsinput.databinding.ItemDropdownListBinding
import io.github.wulkanowy.materialchipsinput.util.createLetterBitmap
import me.xdrop.fuzzywuzzy.FuzzySearch

internal class DropdownListViewAdapter(private val context: Context) :
    RecyclerView.Adapter<DropdownListViewAdapter.ItemViewHolder>() {

    var onItemsSelectedListener: (item: ChipItem) -> Unit = {}

    private val originalItemList = mutableListOf<ChipItem>()

    private val currentItemList = mutableListOf<ChipItem>()

    private val filteredItemList = mutableListOf<ChipItem>()

    private var chipFilter = ItemFilter(this)

    override fun getItemCount() = filteredItemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDropdownListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredItemList.getOrNull(position) ?: return

        with(holder.binding) {
            itemDropdownAvatar.setImageBitmap(createLetterBitmap(context, item.title))
            itemDropdownTitle.text = item.title
            itemDropdownSummary.text = item.summary

            root.setOnClickListener { onItemsSelectedListener(item) }
        }
    }

    fun updateDataSet(items: List<ChipItem>) {
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

    fun removeItemFromList(chipItem: ChipItem) {
        currentItemList.remove(chipItem)
    }

    fun addItemToList(chipItem: ChipItem) {
        currentItemList.add(chipItem)
    }

    class ItemViewHolder(val binding: ItemDropdownListBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemFilter(private val adapter: DropdownListViewAdapter) :
        Filter() {

        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val originalList = adapter.currentItemList
            val filteredList = mutableListOf<ChipItem>()

            if (constraint.isBlank()) {
                filteredList.addAll(originalList)
            } else {
                filteredList.addAll(originalList
                    .map {
                        val ratio = FuzzySearch.tokenSortPartialRatio(
                            constraint.toString().toLowerCase().trim(),
                            it.title.toLowerCase()
                        )

                        Pair(it, ratio)
                    }
                    .sortedByDescending { (_, ratio) -> ratio }
                    .filter { (_, ratio) -> ratio > 50 }
                    .map { (it, _) -> it })
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
                filteredItemList.addAll((results.values as List<ChipItem>?).orEmpty())
                notifyDataSetChanged()
            }
        }
    }
}
