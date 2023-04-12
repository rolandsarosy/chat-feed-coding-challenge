package com.rolandsarosy.chatfeedchallenge.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkCallErrorResponse(
    @Json(name = "response") val response: NetworkCallError
) {
    @JsonClass(generateAdapter = true)
    data class NetworkCallError(
        @Json(name = "status") val status: String,
        @Json(name = "message") val message: String
    )
}
