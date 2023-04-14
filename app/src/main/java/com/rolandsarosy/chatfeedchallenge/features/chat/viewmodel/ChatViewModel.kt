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
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommandData
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatResponseData
import com.rolandsarosy.chatfeedchallenge.features.chat.ChatPollingEngine
import com.rolandsarosy.chatfeedchallenge.features.chat.PollingEngineMediator
import com.rolandsarosy.chatfeedchallenge.features.chat.model.ChatModel
import com.rolandsarosy.chatfeedchallenge.network.NetworkCallErrorResponse.NetworkCallError
import timber.log.Timber

class ChatViewModel(private val model: ChatModel) : BaseViewModel(), PollingEngineMediator {
    val listItems = MutableLiveData<List<ListItemViewModel>>().default(emptyList())
    val commandText = MutableLiveData<String>().default("")

    val onScrollToBottomEvent = MutableLiveData<Event<Boolean>>()
    val onHideKeyboardEvent = MutableLiveData<Event<Boolean>>()
    val shouldClearInputFocus = MutableLiveData<Boolean>().default(false)

    val onFocusChangeListener = View.OnFocusChangeListener { _, isFocused -> if (!isFocused) onHideKeyboardEvent.value = Event(true) }
    val editorActionListener = OnEditorActionListener { textView, _, _ ->
        onEnterCommand(textView)
        return@OnEditorActionListener true
    }

    private val pollingEngine: ChatPollingEngine by lazy { ChatPollingEngine(this) }

    override fun onPollingEngineRequestItem(skipTo: Int) = requestChatResponseItemFromNetwork(skipTo, false)

    override fun onPollingEngineRequestItemSilently(skipTo: Int) = requestChatResponseItemFromNetwork(skipTo, true)

    override fun onPollingEngineDisplayCommand(commandText: String) = addItemsToList(listOf(createChatCommandListItem(commandText)))

    override fun onPollingEngineInvalidCommand() {
        errorEvent.value = Event("Invalid command!")
    }

    override fun onPollingEngineDischargeItems(itemsToAdd: MutableList<ListItemViewModel>) = addItemsToList(itemsToAdd)

    fun onEnterCommand(view: View) = handleTextInput(commandText.safeValue(""))

    private fun requestChatResponseItemFromNetwork(skipTo: Int, isSilent: Boolean) {
        viewModelScope.launchOnIO(
            block = { model.getProducts(skipTo) },
            success = { handleChatResponseItemSuccess(it, isSilent) },
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

    private fun handleChatResponseItemSuccess(result: ChatResponseData, isSilent: Boolean) {
        Timber.d("Successfully received a chat response item from the network.")
        if (isSilent) {
            pollingEngine.storeListItem(createChatResponseListItem(result))
        } else {
            addItemsToList(listOf(createChatResponseListItem(result)))
        }
        pollingEngine.currentPage++
    }

    // Ideally, we'd handle errors gracefully here, such as having a "lives" system, where we would, let's say, reset the screen
    // After 3 consequitive failures. However, this was unspecified and frankly, outside of the scope.
    private fun handleChatResponseItemError(error: NetworkCallError) {
        Timber.e("There was an error while requesting a chat response item from the network:", error.message)
        errorEvent.value = Event(error.message)
    }

    private fun createChatResponseListItem(data: ChatResponseData): ChatResponseListItemViewModel {
        return ChatResponseListItemViewModel(data)
    }

    private fun createChatCommandListItem(commandText: String): ChatCommandListItemViewModel {
        return ChatCommandListItemViewModel(ChatCommandData(System.currentTimeMillis(), commandText))
    }

    private fun addItemsToList(itemsToAdd: List<ListItemViewModel>) {
        val currentListItems = listItems.safeValue(emptyList()).toMutableList()
        currentListItems.addAll(itemsToAdd)
        viewModelScope.launchOnMainThread { listItems.value = currentListItems }
        onScrollToBottomEvent.postValue(Event(true))
    }
}
