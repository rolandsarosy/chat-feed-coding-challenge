package com.rolandsarosy.chatfeedchallenge.network.responseadapter

import com.rolandsarosy.chatfeedchallenge.network.ModelResponse
import java.io.IOException

sealed class NetworkResponse<out N : Any, out E : Any> {
    data class Success<N : Any>(val data: N) : NetworkResponse<N, Nothing>()
    data class ApiError<E : Any>(val body: E, val code: Int) : NetworkResponse<Nothing, E>()
    data class NetworkError(val exception: IOException) : NetworkResponse<Nothing, Nothing>()
    data class UnknownError(val throwable: Throwable?) : NetworkResponse<Nothing, Nothing>()
}

@Suppress("UNCHECKED_CAST")
fun <N : Any, E : Any, M : Any> NetworkResponse<N, E>.mapToModelResponse(mapper: ((N) -> M)? = null): ModelResponse<M, E> {
    return when (this) {
        is NetworkResponse.Success -> ModelResponse.Success(mapper?.invoke(this.data) ?: this.data) as ModelResponse<M, E>
        is NetworkResponse.ApiError -> ModelResponse.ApiFailure(this.body, this.code) as ModelResponse<M, E>
        is NetworkResponse.NetworkError -> ModelResponse.NetworkFailure(this.exception) as ModelResponse<M, E>
        is NetworkResponse.UnknownError -> ModelResponse.UnknownFailure(this.throwable) as ModelResponse<M, E>
    }
}
