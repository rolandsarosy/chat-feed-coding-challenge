package com.rolandsarosy.chatfeedchallenge.features.chat

import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommand
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatPollingState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn

class ChatPollingEngine(private val engineMediator: PollingEngineMediator) {
    var currentPage = STARTING_POLLING_PAGE
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

    fun storeListItem(listItem: ListItemViewModel) = storedListItems.add(listItem)

    fun destroyFlow() {
        stopTicker()
        job = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startTicker() {
        job = GlobalScope.launch { tickerFlow.launchIn(this) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stopTicker() = GlobalScope.launch { job?.cancel() }

    private fun requestNewAction() {
        when (state) {
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineRequestItem(currentPage)
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineRequestItemSilently(currentPage)
            ChatPollingState.STOPPED -> throw IllegalStateException("Ticking should not have happened in the 'STOPPED' state of the engine.")
        }
    }

    private fun stopAndReset() {
        stopTicker()
        state = ChatPollingState.STOPPED
        engineMediator.onPollingEngineDisplayCommand(ChatCommand.STOP.text)
        storedListItems = mutableListOf()
        currentPage = STARTING_POLLING_PAGE
    }

    private fun handleStartByState() {
        when (state) {
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.STOPPED -> {
                state = ChatPollingState.FETCHING
                engineMediator.onPollingEngineDisplayCommand(ChatCommand.START.text)
                startTicker()
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
                engineMediator.onPollingEngineDisplayCommand(ChatCommand.PAUSE.text)
            }
            ChatPollingState.STOPPED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.PAUSED -> engineMediator.onPollingEngineInvalidCommand()
        }
    }

    private fun handleResumeByState() {
        when (state) {
            ChatPollingState.PAUSED -> {
                state = ChatPollingState.FETCHING
                engineMediator.onPollingEngineDisplayCommand(ChatCommand.RESUME.text)
                engineMediator.onPollingEngineDischargeItems(storedListItems)
                storedListItems = mutableListOf()
            }
            ChatPollingState.STOPPED -> engineMediator.onPollingEngineInvalidCommand()
            ChatPollingState.FETCHING -> engineMediator.onPollingEngineInvalidCommand()
        }
    }
}

interface PollingEngineMediator {
    fun onPollingEngineRequestItem(skipTo: Int)
    fun onPollingEngineRequestItemSilently(skipTo: Int)
    fun onPollingEngineDisplayCommand(commandText: String)
    fun onPollingEngineInvalidCommand()
    fun onPollingEngineDischargeItems(itemsToAdd: MutableList<ListItemViewModel>)
}
