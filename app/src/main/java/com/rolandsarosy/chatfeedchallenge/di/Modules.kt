package com.rolandsarosy.chatfeedchallenge.di

import com.rolandsarosy.chatfeedchallenge.BuildConfig
import com.rolandsarosy.chatfeedchallenge.features.chat.model.ChatModel
import com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel.ChatViewModel
import com.rolandsarosy.chatfeedchallenge.network.Endpoint
import com.rolandsarosy.chatfeedchallenge.network.responseadapter.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val TIME_OUT_IN_SEC = 12L
private const val OKHTTP_CONNECTION_KEEP_ALIVE_DURATION = 5L
private const val OK_HTTP = "okHttpInstance"
private const val RETROFIT = "retrofitInstance"

val networkModule = module {
    single { Moshi.Builder().build() }

    single(named(OK_HTTP)) { createOkHttpClient() }
    single(named(RETROFIT)) { createRetrofit(get(), get(named(RETROFIT))) }
    single { createEndpoint(get(named(RETROFIT))) }
}

val modelModule = module {
    single { ChatModel(get()) }
}

val viewModelModule = module {
    viewModel { ChatViewModel(get()) }
}

fun createOkHttpClient(): OkHttpClient {
    val builder = createOkHttpBuilder()
    // We can add interceptors here later.
    return builder.build()
}

fun createOkHttpBuilder(): OkHttpClient.Builder {
    val builder = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_IN_SEC, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_IN_SEC, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_IN_SEC, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, OKHTTP_CONNECTION_KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
        .protocols(listOf(Protocol.HTTP_1_1))
    if (BuildConfig.DEBUG) builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    return builder
}

fun createRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addCallAdapterFactory(NetworkResponseAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .build()

fun createEndpoint(retrofit: Retrofit): Endpoint = retrofit.create(Endpoint::class.java)
