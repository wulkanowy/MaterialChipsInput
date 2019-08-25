package io.github.wulkanowy.materialchipsinput

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.text.InputType.TYPE_TEXT_VARIATION_FILTER
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent.KEYCODE_DEL
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.wulkanowy.materialchipsinput.util.dpToPx
import io.github.wulkanowy.materialchipsinput.util.getThemeAttrColor

class MaterialChipInput : LinearLayout {

    private val _addedChipItems = mutableListOf<ChipItem>()

    private val dropdownListViewAdapter = DropdownListViewAdapter(context)

    private val dropdownListView = DropdownListView(context)

    private val chipEditText = MaterialChipEditText(context)

    private val chipGroup = ChipGroup(context)

    private var chipEditTextTargeted = false

    var filterableChipItems: List<ChipItem> = emptyList()
        set(list) {
            field = list
            dropdownListViewAdapter.updateDataSet(list)
        }

    val addedChipItems: List<ChipItem> get() = _addedChipItems

    var onChipAddListener: (chip: ChipItem) -> Unit = {}

    var onChipRemoveListener: (chip: ChipItem) -> Unit = {}

    var onTextChangeListener: (text: String) -> Unit = {}

    val isDropdownListVisible get() = dropdownListView.visibility == VISIBLE

    var hint: String? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    init {
        initMaterialChipEditText()
        initChipGroup()
        initDropdownListView()

        orientation = VERTICAL
        addView(chipGroup, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        post {
            val listContainer = this.parent.parent as? FrameLayout ?: throw IllegalArgumentException("MaterialChipsInput must be a child of FrameLayout")
            listContainer.addView(dropdownListView, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }

    fun addChips(chipItems: List<ChipItem>) {
        chipItems.forEach { addChipOnLastPosition(it) }
    }

    fun hideDropdownList() {
        dropdownListView.fadeOut()
    }

    private fun processChangedText(text: CharSequence?) {
        onTextChangeListener(text?.toString().orEmpty())

        if (text.isNullOrBlank()) {
            dropdownListView.fadeOut()
            return
        }

        dropdownListViewAdapter.filterText(text) {
            with(dropdownListView) {
                if (it > 0) {
                    fadeIn(this@MaterialChipInput)
                    smoothScrollToPosition(0)
                } else fadeOut()
            }
        }
    }

    private fun addChipOnLastPosition(chipItem: ChipItem) {
        _addedChipItems.add(chipItem)
        chipGroup.addView(Chip(context).apply { text = chipItem.title }, chipGroup.childCount - 1)
        dropdownListViewAdapter.removeItemFromList(chipItem)
        onChipAddListener(chipItem)

        with(chipEditText) {
            hint = null
            text = null
        }
    }

    private fun removeChipOnLastPosition() {
        val chipItem = _addedChipItems.elementAt(_addedChipItems.size - 1)

        _addedChipItems.remove(chipItem)
        chipGroup.removeViewAt(chipGroup.childCount - 2)
        dropdownListViewAdapter.addItemToList(chipItem)
        onChipRemoveListener(chipItem)

        if (_addedChipItems.isEmpty()) chipEditText.hint = hint
    }

    private fun initChipGroup() {
        with(chipGroup) {
            addView(chipEditText, ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            setChipSpacing(context.dpToPx(8f).toInt())
        }
    }

    private fun initDropdownListView() {
        with(dropdownListView) {
            setBackgroundColor(context.getThemeAttrColor(R.attr.colorSurface))
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
            hint = this@MaterialChipInput.hint
            imeOptions = IME_FLAG_NO_EXTRACT_UI
            privateImeOptions = "nm"
            inputType = TYPE_TEXT_VARIATION_FILTER or TYPE_TEXT_FLAG_NO_SUGGESTIONS or TYPE_TEXT_FLAG_MULTI_LINE or TYPE_CLASS_TEXT
            setPadding(0)
            setBackgroundResource(android.R.color.transparent)
            doOnTextChanged { text, _, _, _ -> processChangedText(text) }

            setOnKeyListener { _, keyCode, event ->
                if (event.action == ACTION_DOWN && keyCode == KEYCODE_DEL && _addedChipItems.isNotEmpty() && text?.toString().isNullOrBlank()) {
                    removeChipOnLastPosition()
                    true
                } else false
            }
        }
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        return if (event.keyCode == KEYCODE_BACK && visibility == VISIBLE && isDropdownListVisible) {
            dropdownListView.fadeOut()
            true
        } else super.dispatchKeyEventPreIme(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val chipEditTextBounds = Rect().apply { this@MaterialChipInput.getLocalVisibleRect(this) }
        var sendToChipEditText = false

        when (event.actionMasked) {
            ACTION_DOWN -> {
                chipEditTextTargeted = chipEditTextBounds.contains(event.x.toInt(), event.y.toInt())
                sendToChipEditText = chipEditTextTargeted
            }
            ACTION_POINTER_DOWN, ACTION_POINTER_UP, ACTION_UP, ACTION_MOVE -> {
                sendToChipEditText = chipEditTextTargeted
            }
            ACTION_CANCEL -> {
                sendToChipEditText = chipEditTextTargeted
                chipEditTextTargeted = false
            }
        }

        event.setLocation(chipEditText.width - 1f, chipEditText.height / 2f)

        return if (sendToChipEditText) chipEditText.dispatchTouchEvent(event) else super.onTouchEvent(event)
    }
}
