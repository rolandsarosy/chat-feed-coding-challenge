package com.rolandsarosy.chatfeedchallenge.features.chat.viewmodel

import com.rolandsarosy.chatfeedchallenge.R
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.data.domainobjects.ChatCommandData

class ChatCommandListItemViewModel(val data: ChatCommandData) : ListItemViewModel() {
    override fun getViewType() = R.layout.list_item_chat_command

    override fun areItemsTheSame(newItem: ListItemViewModel) = newItem is ChatCommandListItemViewModel

    override fun areContentsTheSame(newItem: ListItemViewModel): Boolean {
        return if (newItem is ChatCommandListItemViewModel) {
            newItem.data.id == data.id
        } else {
            false
        }
    }
}
