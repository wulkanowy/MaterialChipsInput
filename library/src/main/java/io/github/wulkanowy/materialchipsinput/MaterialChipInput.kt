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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import androidx.core.view.setPadding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.wulkanowy.materialchipsinput.util.convertDpToPixels

class MaterialChipInput : ChipGroup {

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
        with(chipEditText) {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            minHeight = context.convertDpToPixels(32f).toInt()
            minWidth = context.convertDpToPixels(10f).toInt()
            hint = "Hint"
            imeOptions = IME_FLAG_NO_EXTRACT_UI
            privateImeOptions = "nm"
            inputType = TYPE_TEXT_VARIATION_FILTER or TYPE_TEXT_FLAG_NO_SUGGESTIONS or TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(0)
            setBackgroundResource(android.R.color.transparent)
            post { requestFocus() }

            setOnKeyListener { _, keyCode, event ->
                if (event.action == ACTION_DOWN && keyCode == KEYCODE_DEL && insertedChipList.isNotEmpty() && text?.toString().isNullOrBlank()) {
                    removeChipOnLastPosition()
                    true
                } else false
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    dropdownListView.processChangedText(text)
                }
            })

        }

        addView(chipEditText)
        setChipSpacing(context.convertDpToPixels(8f).toInt())
    }

    internal fun addChipOnLastPosition(chipItem: MaterialChipItem) {
        insertedChipList.add(chipItem)
        addView(Chip(context).apply { text = chipItem.title }, childCount - 1)
        dropdownListView.dropdownListViewAdapter.removeItem(chipItem)

        with(chipEditText) {
            hint = null
            text = null
        }
    }

    internal fun removeChipOnLastPosition() {
        val chipItem = insertedChipList.elementAt(insertedChipList.size - 1)

        insertedChipList.remove(chipItem)
        removeViewAt(childCount - 2)
        dropdownListView.dropdownListViewAdapter.addItem(chipItem)

        if (insertedChipList.isEmpty()) chipEditText.hint = "Hint"
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
        getHitRect(recyclerHitRect)

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
