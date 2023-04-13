package com.rolandsarosy.chatfeedchallenge.common.recyclerview

import androidx.recyclerview.widget.DiffUtil

class ListItemViewModelDiffCallback(
    private val oldItems: List<ListItemViewModel>,
    private val newItems: List<ListItemViewModel>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition].areItemsTheSame(newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition].areContentsTheSame(newItems[newItemPosition])
}
