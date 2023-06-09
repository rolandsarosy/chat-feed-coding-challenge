package com.rolandsarosy.chatfeedchallenge.features.chat

import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommand
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommandData
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatPollingState
import com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel.ChatCommandListItemViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn

class ChatPollingEngine(private val engineMediator: PollingEngineMediator) {
    private var currentPage = STARTING_POLLING_PAGE
        set(value) {
            field = if (value >= MAX_POLLING_PAGE) {
                STARTING_POLLING_PAGE
            } else value
        }
    private var state = ChatPollingState.STOPPED
    private var storedListItems: MutableList<ListItemViewModel> = mutableListOf()
    private var job: Job? = null

    // As the specification was unclear, the timer runs independently of the polling's result, with no regard to its network or processing delay.
    // It simply requests a new action at every tick. Changing this behaviour would be simple, but the specification leaned towards this one more.
    private val tickerFlow = flow {
        while (true) {
            emit(requestNewAction())
            delay(TICKER_DELAY)
        }
    }

    companion object {
        private const val STARTING_POLLING_PAGE = 0
        private const val MAX_POLLING_PAGE = 100
        private const val TICKER_DELAY = 5000L
    }

    fun handleCommand(command: ChatCommand) {
        when (command) {
            ChatCommand.START -> handleStartByState()
            ChatCommand.STOP -> handleStopByState()
            ChatCommand.PAUSE -> handlePauseByState()
            ChatCommand.RESUME -> handleResumeByState()
        }
    }

    fun handleItemSuccess(listItem: ListItemViewModel) {
        when (state) {
            ChatPollingState.FETCHING -> {
                engineMediator.onPollingEngineDischargeItems(listOf(listItem))
                currentPage++
            }
            ChatPollingState.PAUSED -> {
                storedListItems.add(listItem)
                currentPage++
            }
            ChatPollingState.STOPPED -> Unit
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun destroyTicker() = GlobalScope.launch {
        job?.cancel()
        job = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun launchTicker() {
        job = GlobalScope.launch { tickerFlow.launchIn(this) }
    }

    private fun requestNewAction() {
        when (state) {
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineRequestItem(currentPage)
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineRequestItem(currentPage)
            ChatPollingState.STOPPED -> throw IllegalStateException("Ticking should not have happened in the 'STOPPED' state of the engine.")
        }
    }

    private fun stopAndReset() {
        destroyTicker()
        state = ChatPollingState.STOPPED
        engineMediator.onPollingEngineDischargeItems(listOf(ChatCommandListItemViewModel(ChatCommandData(text = ChatCommand.STOP.text))))
        storedListItems.clear()
        currentPage = STARTING_POLLING_PAGE
    }

    private fun handleStartByState() {
        when (state) {
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.STOPPED -> {
                state = ChatPollingState.FETCHING
                engineMediator.onPollingEngineDischargeItems(listOf(ChatCommandListItemViewModel(ChatCommandData(text = ChatCommand.START.text))))
                launchTicker()
            }
        }
    }

    private fun handleStopByState() {
        when (state) {
            ChatPollingState.FETCHING -> stopAndReset()
            ChatPollingState.PAUSED -> stopAndReset()
            ChatPollingState.STOPPED -> engineMediator.onPollingEngineInvalidCommand()
        }
    }

    private fun handlePauseByState() {
        when (state) {
            ChatPollingState.FETCHING -> {
                state = ChatPollingState.PAUSED
                engineMediator.onPollingEngineDischargeItems(listOf(ChatCommandListItemViewModel(ChatCommandData(text = ChatCommand.PAUSE.text))))
            }
            ChatPollingState.STOPPED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineInvalidCommand()
        }
    }

    private fun handleResumeByState() {
        when (state) {
            ChatPollingState.PAUSED -> {
                state = ChatPollingState.FETCHING
                storedListItems.add(0, ChatCommandListItemViewModel(ChatCommandData(text = ChatCommand.RESUME.text)))
                engineMediator.onPollingEngineDischargeItems(storedListItems)
                storedListItems.clear()
            }
            ChatPollingState.STOPPED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineInvalidCommand()
        }
    }
}

interface PollingEngineMediator {
    fun onPollingEngineRequestItem(skipTo: Int)
    fun onPollingEngineInvalidCommand()
    fun onPollingEngineDischargeItems(itemsToAdd: List<ListItemViewModel>)
}
