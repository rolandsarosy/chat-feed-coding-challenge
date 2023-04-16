package com.rolandsarosy.chatfeedchallenge.common.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.rolandsarosy.chatfeedchallenge.network.NetworkCallErrorResponse.NetworkCallError
import com.rolandsarosy.chatfeedchallenge.network.responseadapter.NetworkResponse

typealias GenericNetworkResponse<S> = NetworkResponse<S, NetworkCallError>

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

fun <T> MutableLiveData<T>.safeValue(default: T): T = value ?: default
