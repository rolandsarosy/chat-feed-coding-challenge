package com.rolandsarosy.chatfeedchallenge.features.chat.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.rolandsarosy.chatfeedchallenge.common.base.BaseFragment
import com.rolandsarosy.chatfeedchallenge.common.base.ErrorObserver
import com.rolandsarosy.chatfeedchallenge.databinding.FragmentChatBinding
import com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate), ErrorObserver {
    private val viewModel: ChatViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.errorEvent.observe(viewLifecycleOwner, defaultErrorObserver(requireContext()))
        viewModel.onHideKeyboardEvent.observe(viewLifecycleOwner) { event -> event.getContentIfNotHandled()?.let { if (it) hideSoftKeyboard() } }
    }

    private fun hideSoftKeyboard() {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
