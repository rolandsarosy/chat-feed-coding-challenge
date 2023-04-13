package com.rolandsarosy.chatfeedchallenge.features.chat.view

import android.os.Bundle
import android.view.View
import com.rolandsarosy.chatfeedchallenge.common.base.BaseFragment
import com.rolandsarosy.chatfeedchallenge.databinding.FragmentChatBinding
import com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {
    private val viewModel: ChatViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChatItems()
    }
}
