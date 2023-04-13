package com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.rolandsarosy.chatfeedchallenge.common.base.BaseViewModel
import com.rolandsarosy.chatfeedchallenge.common.extensions.waitAndRun
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatData
import com.rolandsarosy.chatfeedchallenge.features.chat.model.ChatModel
import com.rolandsarosy.chatfeedchallenge.network.NetworkCallErrorResponse.NetworkCallError
import timber.log.Timber

class ChatViewModel(private val model: ChatModel) : BaseViewModel() {
    private var previousSkipValue = POLL_STARTING_ITEM

    companion object {
        private const val POLL_STARTING_ITEM = 0
        private const val POLL_DELAY_IN_MILLIS = 5000L
    }

    fun getChatItems(skipTo: Int = POLL_STARTING_ITEM) {
        previousSkipValue = skipTo
        viewModelScope.launchOnIO(
            block = { model.getProducts(skipTo) },
            success = { handleChatItemsSuccess(it) },
            apiFailure = { handleChatItemsError(it) }
        )
    }

    // TODO - Handle cases where 100th item is reached, restarting the count and the list.
    private fun handleChatItemsSuccess(result: ChatData) {
        Timber.d("Successfully retrieved chat items from the network.")
        Timber.d("Retrieved item is: ${result.id}, ${result.description}")
        waitAndRun(POLL_DELAY_IN_MILLIS, viewModelScope) { getChatItems(previousSkipValue + 1) }
    }

    private fun handleChatItemsError(error: NetworkCallError) {
        Timber.e("An error occurred while trying to retrieve chat items from the network.", error.message)
    }
}
