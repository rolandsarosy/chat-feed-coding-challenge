package com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel

import android.view.View
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rolandsarosy.chatfeedchallenge.common.base.BaseViewModel
import com.rolandsarosy.chatfeedchallenge.common.event.Event
import com.rolandsarosy.chatfeedchallenge.common.extensions.default
import com.rolandsarosy.chatfeedchallenge.common.extensions.safeValue
import com.rolandsarosy.chatfeedchallenge.common.extensions.waitAndRun
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommand
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommandData
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatResponseData
import com.rolandsarosy.chatfeedchallenge.features.chat.model.ChatModel
import com.rolandsarosy.chatfeedchallenge.network.NetworkCallErrorResponse.NetworkCallError
import timber.log.Timber

class ChatViewModel(private val model: ChatModel) : BaseViewModel() {
    val listItems = MutableLiveData<List<ListItemViewModel>>().default(emptyList())
    val commandText = MutableLiveData<String>().default("")

    val onListItemAddedEvent = MutableLiveData<Event<Boolean>>()
    val onHideKeyboardEvent = MutableLiveData<Event<Boolean>>()
    val shouldClearFocus = MutableLiveData<Boolean>().default(false)

    val onFocusChangeListener = View.OnFocusChangeListener { _, isFocused -> if (!isFocused) onHideKeyboardEvent.value = Event(true) }
    val editorActionListener = OnEditorActionListener { textView, _, _ ->
        handleTextInput(textView.text.toString().trim().uppercase())
        return@OnEditorActionListener true
    }

    private var previousSkipValue = POLL_STARTING_ITEM

    companion object {
        private const val POLL_STARTING_ITEM = 0
        private const val POLL_DELAY_IN_MILLIS = 5000L
    }

    fun onInputFieldEndIconClicked(view: View) = handleTextInput(commandText.safeValue("").trim().uppercase())

    private fun getChatItems(skipTo: Int = POLL_STARTING_ITEM) {
        previousSkipValue = skipTo
        viewModelScope.launchOnIO(
            block = { model.getProducts(skipTo) },
            success = { handleChatItemsSuccess(it) },
            apiFailure = { handleChatItemsError(it) }
        )
    }

    private fun handleTextInput(text: String) {
        commandText.value = ""
        shouldClearFocus.value = true
        when (ChatCommand.getFromValue(text)) {
            ChatCommand.START -> {
                Timber.d("START command received.")
                addItemToList(ChatCommandListItemViewModel(ChatCommandData(System.currentTimeMillis(), text)))
                getChatItems()
            }
            ChatCommand.STOP -> {
                Timber.d("STOP command received.")
                addItemToList(ChatCommandListItemViewModel(ChatCommandData(System.currentTimeMillis(), text)))
            }
            ChatCommand.PAUSE -> {
                Timber.d("PAUSE command received.")
                addItemToList(ChatCommandListItemViewModel(ChatCommandData(System.currentTimeMillis(), text)))
            }
            ChatCommand.RESUME -> {
                Timber.d("RESUME command received.")
                addItemToList(ChatCommandListItemViewModel(ChatCommandData(System.currentTimeMillis(), text)))
            }
            null -> {
                Timber.d("INVALID command received!")
                errorEvent.value = Event("Invalid command received!")
            }
        }
    }

    private fun addItemToList(item: ListItemViewModel) {
        val currentListItems = listItems.safeValue(emptyList()).toMutableList()
        currentListItems.add(item)
        listItems.postValue(currentListItems)
        onListItemAddedEvent.postValue(Event(true))
    }

    // TODO - CRITICAL - Handle cases where 100th item is reached, restarting the count and the list.
    private fun handleChatItemsSuccess(result: ChatResponseData) {
        Timber.d("Successfully retrieved chat items from the network.")
        addItemToList(ChatResponseListItemViewModel(result))
        waitAndRun(POLL_DELAY_IN_MILLIS, viewModelScope) { getChatItems(previousSkipValue + 1) }
    }

    private fun handleChatItemsError(error: NetworkCallError) {
        Timber.e("An error occurred while trying to retrieve chat items from the network.", error.message)
    }
}
