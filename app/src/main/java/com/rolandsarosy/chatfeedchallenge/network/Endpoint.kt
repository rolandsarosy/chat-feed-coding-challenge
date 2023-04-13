package com.rolandsarosy.chatfeedchallenge.network

import com.rolandsarosy.chatfeedchallenge.common.extensions.GenericNetworkResponse
import com.rolandsarosy.chatfeedchallenge.data.apiobjects.ApiProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoint {

    companion object {
        private const val REQUEST_ITEM_LIMIT = 1
    }

    @GET("/products")
    suspend fun getProducts(
        @Query("limit") limit: Int = REQUEST_ITEM_LIMIT,
        @Query("skip") skipTo: Int
    ): GenericNetworkResponse<ApiProductResponse>
}
