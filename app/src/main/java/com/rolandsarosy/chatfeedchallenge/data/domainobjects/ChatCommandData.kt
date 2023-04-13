package com.rolandsarosy.chatfeedchallenge.data.domainobjects

class ChatCommandData(
    // Ideally, we'd use something like a rolling set of IDs for locally-generated IDs but that is outside of the scope of this project.
    // Using something like the current system time in milliseconds is the next best thing.
    val id: Long,
    val text: String
)
