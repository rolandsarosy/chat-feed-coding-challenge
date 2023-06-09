package com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel

import android.view.View
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rolandsarosy.chatfeedchallenge.common.base.BaseViewModel
import com.rolandsarosy.chatfeedchallenge.common.event.Event
import com.rolandsarosy.chatfeedchallenge.common.extensions.default
import com.rolandsarosy.chatfeedchallenge.common.extensions.safeValue
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommand
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatResponseData
import com.rolandsarosy.chatfeedchallenge.features.chat.ChatPollingEngine
import com.rolandsarosy.chatfeedchallenge.features.chat.PollingEngineMediator
import com.rolandsarosy.chatfeedchallenge.features.chat.model.ChatModel
import com.rolandsarosy.chatfeedchallenge.network.NetworkCallErrorResponse.NetworkCallError
import timber.log.Timber

class ChatViewModel(private val model: ChatModel) : BaseViewModel(), PollingEngineMediator {
    val listItems = MutableLiveData<List<ListItemViewModel>>().default(emptyList())
    val commandText = MutableLiveData<String>().default("")

    val onHideKeyboardEvent = MutableLiveData<Event<Boolean>>()
    val shouldClearInputFocus = MutableLiveData<Boolean>().default(false)

    val onFocusChangeListener = View.OnFocusChangeListener { _, isFocused -> if (!isFocused) onHideKeyboardEvent.value = Event(true) }
    val editorActionListener = OnEditorActionListener { textView, _, _ ->
        onEnterCommand(textView)
        return@OnEditorActionListener true
    }

    private val pollingEngine: ChatPollingEngine by lazy { ChatPollingEngine(this) }

    override fun onPollingEngineRequestItem(skipTo: Int) = requestChatResponseItemFromNetwork(skipTo)

    override fun onPollingEngineInvalidCommand() = errorEvent.postValue(Event("Invalid command!"))

    override fun onPollingEngineDischargeItems(itemsToAdd: List<ListItemViewModel>) = addItemsToList(itemsToAdd)

    override fun onCleared() {
        super.onCleared()
        pollingEngine.destroyTicker()
    }

    fun onEnterCommand(view: View) = handleTextInput(commandText.safeValue(""))

    private fun requestChatResponseItemFromNetwork(skipTo: Int) {
        viewModelScope.launchOnIO(
            block = { model.getProducts(skipTo) },
            success = { handleChatResponseItemSuccess(it) },
            apiFailure = { handleChatResponseItemError(it) }
        )
    }

    private fun handleTextInput(text: String) {
        commandText.value = ""
        shouldClearInputFocus.value = true
        val command = ChatCommand.getFromValue(text.trim().uppercase())
        if (command != null) {
            pollingEngine.handleCommand(command)
        } else {
            onPollingEngineInvalidCommand()
        }
    }

    private fun handleChatResponseItemSuccess(result: ChatResponseData) {
        Timber.d("Successfully received a chat response item from the network.")
        pollingEngine.handleItemSuccess(createChatResponseListItem(result))
    }

    // Ideally, we'd handle errors gracefully here, such as having a "lives" system, where we would, let's say, reset the screen
    // After 3 consecutive failures. However, this was unspecified and frankly, outside of the scope.
    private fun handleChatResponseItemError(error: NetworkCallError) {
        Timber.e("There was an error while requesting a chat response item from the network:", error.message)
        errorEvent.value = Event(error.message)
    }

    private fun createChatResponseListItem(data: ChatResponseData) = ChatResponseListItemViewModel(data)

    private fun addItemsToList(itemsToAdd: List<ListItemViewModel>) {
        val currentListItems = listItems.safeValue(emptyList()).toMutableList()
        currentListItems.addAll(itemsToAdd)
        viewModelScope.launchOnMainThread { listItems.value = currentListItems }
    }
}
