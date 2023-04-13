package com.rolandsarosy.chatfeedchallenge.common.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

object TextInputLayoutBindingAdapter {

    @JvmStatic
    @BindingAdapter("onEndIconClickListener")
    fun TextInputLayout.addEndIconOnClickListener(listener: View.OnClickListener) = this.setEndIconOnClickListener(listener)
}
