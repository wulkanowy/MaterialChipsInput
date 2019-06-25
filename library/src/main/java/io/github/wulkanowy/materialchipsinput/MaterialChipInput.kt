package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.graphics.Rect
import android.text.InputType.*
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.wulkanowy.materialchipsinput.util.dpToPx

class MaterialChipInput : LinearLayout {

    private val dropdownListViewAdapter = DropdownListViewAdapter(context)

    private val dropdownListView = DropdownListView(context)

    private val chipEditText = MaterialChipEditText(context)

    private val chipGroup = ChipGroup(context)

    private val insertedChipList = mutableListOf<MaterialChipItem>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    var itemList: List<MaterialChipItem> = emptyList()
        set(list) {
            field = list
            dropdownListViewAdapter.updateDataSet(list)
        }

    init {
        initMaterialChipEditText()
        initChipGroup()
        initDropdownListView()

        orientation = VERTICAL
        addView(chipGroup, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        addView(dropdownListView, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    private fun processChangedText(text: CharSequence?) {
        if (text.isNullOrBlank()) {
            dropdownListView.fadeOut()
            return
        }

        dropdownListViewAdapter.filterText(text) {
            with(dropdownListView) {
                if (it > 0) {
                    fadeIn()
                    smoothScrollToPosition(0)
                } else fadeOut()
            }
        }
    }

    private fun addChipOnLastPosition(chipItem: MaterialChipItem) {
        insertedChipList.add(chipItem)
        chipGroup.addView(Chip(context).apply { text = chipItem.title }, chipGroup.childCount - 1)
        dropdownListViewAdapter.removeItemFromSet(chipItem)

        with(chipEditText) {
            hint = null
            text = null
        }
    }

    private fun removeChipOnLastPosition() {
        val chipItem = insertedChipList.elementAt(insertedChipList.size - 1)

        insertedChipList.remove(chipItem)
        chipGroup.removeViewAt(chipGroup.childCount - 2)
        dropdownListViewAdapter.addItemToSet(chipItem)

        if (insertedChipList.isEmpty()) chipEditText.hint = "Hint"
    }

    private fun initChipGroup() {
        with(chipGroup) {
            addView(chipEditText, ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            setChipSpacing(context.dpToPx(8f).toInt())
        }
    }

    private fun initDropdownListView() {
        with(dropdownListView) {
            layoutManager = LinearLayoutManager(context)
            adapter = dropdownListViewAdapter
            visibility = GONE
            setHasFixedSize(true)
            dropdownListViewAdapter.onItemsSelectedListener = ::addChipOnLastPosition
        }
    }

    private fun initMaterialChipEditText() {
        with(chipEditText) {
            minHeight = context.dpToPx(32f).toInt()
            minWidth = context.dpToPx(10f).toInt()
            hint = "Hint"
            imeOptions = IME_FLAG_NO_EXTRACT_UI
            privateImeOptions = "nm"
            inputType = TYPE_TEXT_VARIATION_FILTER or TYPE_TEXT_FLAG_NO_SUGGESTIONS or TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(0)
            setBackgroundResource(android.R.color.transparent)
            doOnTextChanged { text, _, _, _ -> processChangedText(text) }

            setOnKeyListener { _, keyCode, event ->
                if (event.action == ACTION_DOWN && keyCode == KEYCODE_DEL && insertedChipList.isNotEmpty() && text?.toString().isNullOrBlank()) {
                    removeChipOnLastPosition()
                    true
                } else false
            }
        }
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        return if (event.keyCode == KEYCODE_BACK && visibility == VISIBLE) {
            dropdownListView.fadeOut()
            true
        } else super.dispatchKeyEventPreIme(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        var isHandled = false
        val editHitRect = Rect()
        chipEditText.getHitRect(editHitRect)

        val chipGroupHitRect = Rect()
        chipGroup.getHitRect(chipGroupHitRect)

        val extendedHitRect = Rect(editHitRect.right, editHitRect.top, chipGroupHitRect.right, editHitRect.bottom)

        event?.let {
            if (extendedHitRect.contains(it.x.toInt(), it.y.toInt())) {
                isHandled = chipEditText.dispatchTouchEvent(it)
            }
        }
        return if (isHandled) true else super.dispatchTouchEvent(event)
    }
}
