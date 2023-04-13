package com.rolandsarosy.chatfeedchallenge.features.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rolandsarosy.chatfeedchallenge.common.base.BaseFragment
import com.rolandsarosy.chatfeedchallenge.common.base.ErrorObserver
import com.rolandsarosy.chatfeedchallenge.common.extensions.safeValue
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
        // TODO - TECH DEBT - This is a temporary solution to scroll to the bottom of the list.
        viewModel.onListItemAddedEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { isItemAdded ->
                if (isItemAdded) {
                    binding.chatList.smoothScrollToPosition(viewModel.listItems.safeValue(emptyList()).size - 1)
                }
            }
        }
    }
}
