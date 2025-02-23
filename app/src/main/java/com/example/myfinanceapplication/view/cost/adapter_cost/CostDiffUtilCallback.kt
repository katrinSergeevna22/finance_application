package com.example.myapplication.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinanceapplication.model.Cost

class CostDiffUtilCallback : DiffUtil.ItemCallback<Cost>() {
    override fun areItemsTheSame(oldItem: Cost, newItem: Cost): Boolean =
        oldItem.costId == newItem.costId

    override fun areContentsTheSame(oldItem: Cost, newItem: Cost): Boolean =
        oldItem == newItem
}