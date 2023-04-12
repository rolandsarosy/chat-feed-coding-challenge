package com.rolandsarosy.chatfeedchallenge.network.responseadapter

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

internal class NetworkResponseCall<S : Any, E : Any>(private val delegate: Call<S>, private val errorConverter: Converter<ResponseBody, E>) :
    Call<NetworkResponse<S, E>> {

    @Suppress("TooGenericExceptionCaught") // Suppressing this to catch all exceptions in case a not 2xx network response.
    override fun enqueue(callback: Callback<NetworkResponse<S, E>>) {
        return delegate.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()

                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.Success(body)))
                    } else {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.UnknownError(null)))
                    }
                } else {
                    val errorBody = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> try {
                            errorConverter.convert(error)
                        } catch (exception: Exception) {
                            Timber.e(exception, "Retrofit network response call has caught an unknown exception.")
                            null
                        }
                    }

                    if (errorBody != null) {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.ApiError(errorBody, code)))
                    } else {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.UnknownError(null)))
                    }
                }
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                val networkResponse = when (throwable) {
                    is UnknownHostException -> NetworkResponse.NetworkError(throwable)
                    is IOException -> NetworkResponse.UnknownError(throwable)
                    else -> NetworkResponse.UnknownError(throwable)
                }
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = NetworkResponseCall(delegate.clone(), errorConverter)

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun execute(): Response<NetworkResponse<S, E>> = throw UnsupportedOperationException("Execute is not supported in NetworkResponseCall.")
}
