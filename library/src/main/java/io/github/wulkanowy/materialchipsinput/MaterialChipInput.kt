package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputType.*
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_DEL
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.widget.FrameLayout
import androidx.core.view.setPadding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.wulkanowy.materialchipsinput.util.convertDpToPixels
import kotlinx.android.synthetic.main.input_chips.view.*

class MaterialChipInput : FrameLayout {

    private val dropdownListView = DropdownListView(context)

    private var chipEditText = MaterialChipEditText(context)

    private val insertedChipList = mutableListOf<MaterialChipItem>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    var itemList: List<MaterialChipItem>? = null
        set(list) {
            field = list
            list?.let {
                dropdownListView.initialize(it, this)
            }
        }

    init {
        View.inflate(context, R.layout.input_chips, this)

        with(chipEditText) {
            layoutParams = ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            minHeight = context.convertDpToPixels(32f).toInt()
            minWidth = context.convertDpToPixels(10f).toInt()
            hint = "Hint"
            imeOptions = IME_FLAG_NO_EXTRACT_UI
            privateImeOptions = "nm"
            inputType = TYPE_TEXT_VARIATION_FILTER or TYPE_TEXT_FLAG_NO_SUGGESTIONS or TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(0)
            setBackgroundResource(android.R.color.transparent)

            setOnKeyListener { _, _, event ->
                if (event.action == ACTION_DOWN && event.keyCode == KEYCODE_DEL && insertedChipList.isNotEmpty() && text?.toString().isNullOrBlank()) {
                    this@MaterialChipInput.inputChipGroup.removeViewAt(this@MaterialChipInput.inputChipGroup.childCount - 2)

                    val chip = insertedChipList.elementAt(insertedChipList.size - 1)
                    insertedChipList.remove(chip)
                    if (insertedChipList.isEmpty()) {
                        chipEditText.hint = "Hint"
                    }
                    onChipRemoved(chip)
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onTextChanged(s)
                }
            })

        }

        inputChipGroup.addView(chipEditText)
        chipEditText.post { chipEditText.requestFocus() }
    }

    internal fun onItemInListSelected(chipItem: MaterialChipItem) {
        insertedChipList.add(chipItem)
        with(chipEditText) {
            hint = null
            text = null
        }
        inputChipGroup.addView(Chip(context).apply { text = chipItem.title }, inputChipGroup.childCount - 1)
        onChipAdded(chipItem)
    }

    private fun onChipAdded(chipItem: MaterialChipItem) {
        dropdownListView.dropdownListViewAdapter.removeItem(chipItem)
    }

    private fun onChipRemoved(chipItem: MaterialChipItem) {
        dropdownListView.dropdownListViewAdapter.addItem(chipItem)
    }

    private fun onTextChanged(text: CharSequence?) {
        dropdownListView.processChangedText(text)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        val isConsumed = dropdownListView.processKeyEvent(event)
        return if (isConsumed) true else super.dispatchKeyEventPreIme(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        var isHandled = false
        val editHitRect = Rect()
        chipEditText.getHitRect(editHitRect)

        val recyclerHitRect = Rect()
        inputChipGroup.getHitRect(recyclerHitRect)

        val extendedHitRect = Rect(editHitRect.right, editHitRect.top, recyclerHitRect.right, editHitRect.bottom)

        event?.let {
            if (extendedHitRect.contains(it.x.toInt(), it.y.toInt())) {
                chipEditText.apply {
                    isHandled = chipEditText.dispatchTouchEvent(it)
                }
            }
        }
        return if (isHandled) true else super.dispatchTouchEvent(event)
    }
}
