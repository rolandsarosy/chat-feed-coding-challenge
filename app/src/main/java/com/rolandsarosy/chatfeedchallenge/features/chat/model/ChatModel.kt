package com.rolandsarosy.chatfeedchallenge.features.chat.model

import com.rolandsarosy.chatfeedchallenge.data.apiobjects.asChatResponseData
import com.rolandsarosy.chatfeedchallenge.network.Endpoint
import com.rolandsarosy.chatfeedchallenge.network.responseadapter.mapToModelResponse
import kotlinx.coroutines.flow.flow

class ChatModel(private val endpoint: Endpoint) {

    fun getProducts(skipTo: Int) = flow { emit(endpoint.getProducts(skipTo = skipTo).mapToModelResponse { it.asChatResponseData() }) }
}
