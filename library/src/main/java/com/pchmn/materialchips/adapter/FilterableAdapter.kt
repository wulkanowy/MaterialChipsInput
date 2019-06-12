package com.pchmn.materialchips.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.pchmn.materialchips.MaterialChipsInput
import com.pchmn.materialchips.R
import kotlinx.android.extensions.LayoutContainer

internal class FilterableAdapter(
        private val context: Context,
        originalChipList: List<Chip>,
        private val chipsInput: MaterialChipsInput)
    : RecyclerView.Adapter<FilterableAdapter.ItemViewHolder>() {

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
                chipsInput.onItemSelectedInternal(filteredChipList[position])
            }
        }
    }

    fun processText(text: CharSequence, onComplete: (filteredCount: Int) -> Unit) {
        chipFilter.filter(text, onComplete)
    }

    fun removeItem(chip: Chip) {
        currentChipList.remove(chip)
        with(filteredChipList) {
            clear()
            addAll(currentChipList)
        }
        notifyDataSetChanged()
    }

    fun addItem(chip: Chip) {
        currentChipList.add(chip)
        with(filteredChipList) {
            clear()
            addAll(currentChipList)
        }
        notifyDataSetChanged()
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView get() = itemView
    }

    class ChipFilter(private val adapter: FilterableAdapter) : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val originalList = adapter.currentChipList
            val filteredList = mutableListOf<Chip>()

            if (constraint.isBlank()) {
                filteredList.addAll(originalList)
            } else {
                val pattern = constraint.toString().toLowerCase().trim()
                originalList.forEach {
                    if (it.text.toString().toLowerCase().contains(pattern)) {
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
                filteredChipList.addAll(results.values as List<Chip>)
                notifyDataSetChanged()
            }
        }
    }
}
