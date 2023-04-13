package com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel

import com.rolandsarosy.chatfeedchallenge.R
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatResponseData

class ChatResponseListItemViewModel(val data: ChatResponseData) : ListItemViewModel() {
    override fun getViewType() = R.layout.list_item_chat_response

    override fun areItemsTheSame(newItem: ListItemViewModel) = newItem is ChatResponseListItemViewModel

    override fun areContentsTheSame(newItem: ListItemViewModel): Boolean {
        return if (newItem is ChatResponseListItemViewModel) {
            newItem.data.id == data.id
        } else {
            false
        }
    }
}
