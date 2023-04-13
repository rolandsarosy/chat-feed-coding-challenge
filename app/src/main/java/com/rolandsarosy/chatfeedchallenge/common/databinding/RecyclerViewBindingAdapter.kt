package com.rolandsarosy.chatfeedchallenge.common.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.ListItemViewModel
import com.rolandsarosy.chatfeedchallenge.common.recyclerview.RecyclerViewAdapter

object RecyclerViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("source")
    fun setupRecyclerView(view: RecyclerView, source: List<ListItemViewModel>) {
        val currentAdapter = view.adapter as? RecyclerViewAdapter
        if (currentAdapter != null && currentAdapter.itemCount != 0) {
            dispatchSourceToAdapter(currentAdapter, source)
        } else {
            createAdapter(view, source)
        }
    }

    private fun dispatchSourceToAdapter(adapter: RecyclerViewAdapter, source: List<ListItemViewModel>) {
        adapter.items = source.toMutableList()
    }

    private fun createAdapter(view: RecyclerView, source: List<ListItemViewModel>) {
        with(RecyclerViewAdapter()) {
            this.items = source.toMutableList()
            view.adapter = this
        }
    }
}
