package com.rolandsarosy.chatfeedchallenge

import android.view.View
import android.widget.TextView
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
import com.rolandsarosy.chatfeedchallenge.common.databinding.TextInputEditTextBindingAdapter.addDataBindingOnFocusChangeListener
import com.rolandsarosy.chatfeedchallenge.common.databinding.TextInputEditTextBindingAdapter.addEditorActionCompleteListener
import com.rolandsarosy.chatfeedchallenge.common.databinding.TextInputEditTextBindingAdapter.clearFocus
import com.rolandsarosy.chatfeedchallenge.common.databinding.TextInputEditTextBindingAdapter.getDataBindingText
import com.rolandsarosy.chatfeedchallenge.common.databinding.TextInputEditTextBindingAdapter.setDataBindingText
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class TextInputEditTextBindingAdapterTest {
    private lateinit var editText: TextInputEditText
    private lateinit var listener: InverseBindingListener

    @Before
    fun setUp() {
        editText = mockk(relaxed = true)
        listener = mockk(relaxed = true)
    }

    @Test
    fun `setDataBindingText sets text when text is different`() {
        every { editText.text.toString() } returns "old text"
        val newText = "new text"
        editText.setDataBindingText(newText)
        verify(exactly = 1) { editText.setText(newText) }
    }

    @Test
    fun `setDataBindingText does not set text when text is the same`() {
        val text = "same text"
        every { editText.text.toString() } returns text
        editText.setDataBindingText(text)
        verify(exactly = 0) { editText.setText(text) }
    }

    @Test
    fun `getDataBindingText returns EditText text`() {
        val text = "some text"
        every { editText.text.toString() } returns text
        val result = editText.getDataBindingText()
        assertEquals(text, result)
    }

    @Test
    fun `addEditorActionCompleteListener sets listener on EditText`() {
        val listener = mockk<TextView.OnEditorActionListener>(relaxed = true)
        editText.addEditorActionCompleteListener(listener)
        verify(exactly = 1) { editText.setOnEditorActionListener(listener) }
    }

    @Test
    fun `clearFocus clears focus on EditText when shouldClearFocus is true`() {
        every { editText.clearFocus() } returns Unit
        editText.clearFocus(true)
        verify(exactly = 1) { editText.clearFocus() }
    }

    @Test
    fun `clearFocus does not clear focus on EditText when shouldClearFocus is false`() {
        editText.clearFocus(false)
        verify(exactly = 0) { editText.clearFocus() }
    }

    @Test
    fun `addDataBindingOnFocusChangeListener adds listener to EditText`() {
        val listener = mockk<View.OnFocusChangeListener>(relaxed = true)
        editText.addDataBindingOnFocusChangeListener(listener)
        val view = mockk<View>()
        val shouldHaveFocus = true
        editText.onFocusChangeListener.onFocusChange(view, shouldHaveFocus)
        listener.onFocusChange(view, shouldHaveFocus)
        verify { listener.onFocusChange(view, shouldHaveFocus) }
    }
}
