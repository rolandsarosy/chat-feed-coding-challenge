package com.rolandsarosy.chatfeedchallenge.common.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rolandsarosy.chatfeedchallenge.BR

class RecyclerViewAdapter : RecyclerView.Adapter<BindableViewHolder>() {
    private var oldItems: ArrayList<ListItemViewModel> = ArrayList()

    var items: MutableList<ListItemViewModel> = ArrayList()
        set(value) {
            oldItems = ArrayList(items)
            this.items.clear()
            this.items.addAll(value)
            val diffResult = DiffUtil.calculateDiff(ListItemViewModelDiffCallback(oldItems, items))
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val dataBinding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        return BindableViewHolder(dataBinding)
    }

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        holder.getBinding().setVariable(BR.viewModel, items[position])
        holder.getBinding().executePendingBindings()
    }

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position)
    }
}
