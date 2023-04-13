package com.rolandsarosy.chatfeedchallenge.common.databinding

import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

object TextInputEditTextBindingAdapter {
    @JvmStatic
    @BindingAdapter("text")
    fun TextInputEditText.setDataBindingText(text: String) = if (this.text.toString() != text) this.setText(text) else Unit

    @JvmStatic
    @InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
    fun TextInputEditText.getDataBindingText() = this.text.toString()

    @JvmStatic
    @BindingAdapter("textAttrChanged")
    fun TextInputEditText.setupListener(listener: InverseBindingListener) = this.doAfterTextChanged { listener.onChange() }

    @JvmStatic
    @BindingAdapter("onEditorActionCompleteListener")
    fun addEditorActionCompleteListener(editText: TextInputEditText, listener: OnEditorActionListener) {
        editText.setOnEditorActionListener(listener)
    }
}
