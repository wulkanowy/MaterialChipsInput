package io.github.wulkanowy.materialchipsinput

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.*
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText

class MaterialChipEditText : AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val connection = super.onCreateInputConnection(outAttrs)

        with(outAttrs) {
            val currentAction = imeOptions and IME_MASK_ACTION
            if (currentAction and IME_ACTION_DONE != 0) {
                imeOptions = imeOptions xor currentAction
                imeOptions = imeOptions or IME_ACTION_DONE
            }

            if (imeOptions and IME_FLAG_NO_ENTER_ACTION != 0) {
                imeOptions = imeOptions and IME_FLAG_NO_ENTER_ACTION.inv()
            }
        }
        return connection
    }
}
