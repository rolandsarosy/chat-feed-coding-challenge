package com.rolandsarosy.chatfeedchallenge.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import java.io.IOException

sealed class ModelResponse<M : Any, E : Any> {
    data class Success<M : Any>(val data: M) : ModelResponse<M, Nothing>()
    data class ApiFailure<E : Any>(val error: E, val code: Int) : ModelResponse<Nothing, E>()
    data class UnknownFailure(val throwable: Throwable?) : ModelResponse<Nothing, Nothing>()
    data class NetworkFailure(val exception: IOException) : ModelResponse<Nothing, Nothing>()
}

suspend fun <M : Any, E : Any> Flow<ModelResponse<M, E>>.collectResponse(
    externalFlow: MutableSharedFlow<ModelResponse.Success<M>>? = null,
    success: (data: M) -> Unit,
    apiFailure: (error: E) -> Unit,
    unknownFailure: (throwable: Throwable?) -> Unit,
    networkFailure: (exception: IOException) -> Unit
) {
    this.onEach {
        when (it) {
            is ModelResponse.Success -> success(it.data)
            is ModelResponse.ApiFailure -> apiFailure(it.error)
            is ModelResponse.UnknownFailure -> unknownFailure(it.throwable)
            is ModelResponse.NetworkFailure -> networkFailure(it.exception)
        }
    }.filter { it is ModelResponse.Success }.collect { externalFlow?.tryEmit(it as ModelResponse.Success<M>) }
}
