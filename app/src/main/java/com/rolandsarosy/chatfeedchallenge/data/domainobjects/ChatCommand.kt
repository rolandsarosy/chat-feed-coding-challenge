package com.rolandsarosy.chatfeedchallenge.data.domainobjects

enum class ChatCommand(val text: String) {
    START("START"),
    STOP("STOP"),
    PAUSE("PAUSE"),
    RESUME("RESUME");

    companion object {
        @JvmStatic
        fun getFromValue(stringValue: String): ChatCommand? = values().find { value -> value.text == stringValue }
    }
}
