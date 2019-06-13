package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.text.Editable
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.text.InputType.TYPE_TEXT_VARIATION_FILTER
import android.text.TextWatcher
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_DEL
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.github.wulkanowy.materialchipsinput.util.convertDpToPixels


class MaterialChipsInputAdapter(
        private val context: Context,
        private val chipsInput: MaterialChipsInput,
        private val recycler: RecyclerView)
    : RecyclerView.Adapter<MaterialChipsInputAdapter.ItemViewHolder>() {

    val chipList = mutableListOf<Chip>()

    private val chipEditTextHint: String = "Hint"

    private val chipEditText: AppCompatEditText = AppCompatEditText(context)

    companion object {

        private const val TYPE_CHIP = 0

        private const val TYPE_EDIT_TEXT = 1
    }

    init {
        with(chipEditText) {
            layoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            hint = chipEditTextHint
            setBackgroundResource(android.R.color.transparent)
            imeOptions = IME_FLAG_NO_EXTRACT_UI
            privateImeOptions = "nm"
            inputType = TYPE_TEXT_VARIATION_FILTER or TYPE_TEXT_FLAG_NO_SUGGESTIONS

            setOnKeyListener { _, _, event ->
                if (event.action == ACTION_DOWN && event.keyCode == KEYCODE_DEL) {
                    removeChip(chipList.size - 1)
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    chipsInput.onTextChangedInternal(s)
                }
            })

        }
    }

    override fun getItemCount() = chipList.size + 1

    override fun getItemViewType(position: Int) = if (position == chipList.size) TYPE_EDIT_TEXT else TYPE_CHIP

    override fun getItemId(position: Int) = chipList[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(if (viewType == TYPE_CHIP) Chip(context) else chipEditText)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_EDIT_TEXT) {
            with(holder.itemView as AppCompatEditText) {
                if (chipList.isEmpty()) hint = chipEditTextHint
                layoutParams = layoutParams.apply { width = context.convertDpToPixels(50f).toInt() }

                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        layoutParams = layoutParams.apply {
                            width = recycler.right - left - context.convertDpToPixels(8f).toInt()
                        }
                        requestFocus()

                        if (SDK_INT < JELLY_BEAN) {
                            @Suppress("DEPRECATION")
                            viewTreeObserver.removeGlobalOnLayoutListener(this)
                        } else {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })

            }
        } else if (itemCount > 1) {
            with(holder.itemView as Chip) {
                text = position.toString()
            }
        }
    }

    fun addChip(chip: Chip) {
        chipList.add(chip)
        with(chipEditText) {
            hint = null
            text = null
        }
        notifyItemInserted(chipList.size)
        recycler.smoothScrollToPosition(itemCount)
        chipsInput.onChipAddedInternal(chip)
    }

    private fun removeChip(position: Int) {
        val chip = chipList[position]
        chipList.remove(chip)

        if (chipList.isEmpty()) {
            chipEditText.hint = chipEditTextHint
        }
        notifyDataSetChanged()
        chipsInput.onChipRemovedInternal(chip)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
