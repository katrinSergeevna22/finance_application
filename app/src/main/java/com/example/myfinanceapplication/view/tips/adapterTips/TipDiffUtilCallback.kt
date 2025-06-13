package com.example.myfinanceapplication.view.tips.adapterTips

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinanceapplication.model.Tip

class TipDiffUtilCallback : DiffUtil.ItemCallback<Tip>() {
    override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean =
        oldItem.tipId == newItem.tipId

    override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean =
        oldItem == newItem
}