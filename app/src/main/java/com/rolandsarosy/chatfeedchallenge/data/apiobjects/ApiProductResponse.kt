package com.rolandsarosy.chatfeedchallenge.data.apiobjects

import com.rolandsarosy.chatfeedchallenge.common.utils.TimeUtils
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatResponseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiProductResponse(@Json(name = "products") val products: List<ApiProduct>) {
    @JsonClass(generateAdapter = true)
    data class ApiProduct(
        @Json(name = "id") val id: Long,
        @Json(name = "description") val description: String
    )
}

// We can assume that the first item is the one we need from the "list", since we're always requesting just the one.
fun ApiProductResponse.asChatResponseData() = ChatResponseData(
    id = products[0].id,
    text = products[0].description,
    arrivedAtTime = TimeUtils.getTimeOfDay(System.currentTimeMillis())
)
