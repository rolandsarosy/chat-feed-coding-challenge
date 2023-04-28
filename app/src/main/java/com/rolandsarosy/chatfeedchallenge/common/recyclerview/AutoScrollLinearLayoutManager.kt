package com.rolandsarosy.chatfeedchallenge.common.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoScrollLinearLayoutManager(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    LinearLayoutManager(context, attrs, defStyleAttr, defStyleRes) {

    override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsAdded(recyclerView, positionStart, itemCount)
        recyclerView.scrollToPosition(positionStart + itemCount - 1)
    }
}
