package com.rolandsarosy.chatfeedchallenge.common.databinding

import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object EditTextBindingAdapter {
    @JvmStatic
    @BindingAdapter("text")
    fun EditText.setDataBindingText(text: String) = if (this.text.toString() != text) this.setText(text) else Unit

    @JvmStatic
    @InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
    fun EditText.getDataBindingText() = this.text.toString()

    @JvmStatic
    @BindingAdapter("textAttrChanged")
    fun EditText.setupListener(listener: InverseBindingListener) = this.doAfterTextChanged { listener.onChange() }

    @JvmStatic
    @BindingAdapter("onEditorActionCompleteListener")
    fun addEditorActionCompleteListener(editText: EditText, listener: OnEditorActionListener) {
        editText.setOnEditorActionListener(listener)
    }
}
