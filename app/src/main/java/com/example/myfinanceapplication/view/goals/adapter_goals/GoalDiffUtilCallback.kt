package com.example.myapplication.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinanceapplication.model.Goal

class GoalDiffUtilCallback : DiffUtil.ItemCallback<Goal>() {
    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean =
        oldItem.goalId == newItem.goalId

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean =
        oldItem == newItem
}