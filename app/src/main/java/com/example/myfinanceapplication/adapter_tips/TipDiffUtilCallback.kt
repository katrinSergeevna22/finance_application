package com.example.myapplication.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinanceapplication.Goal
import com.example.myfinanceapplication.Tip

class TipDiffUtilCallback : DiffUtil.ItemCallback<Tip>() {
    override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean =
        oldItem.tipId == newItem.tipId

    override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean =
        oldItem == newItem
}