package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.myfinanceapplication.Cost
import com.example.myfinanceapplication.Goal
import com.example.myfinanceapplication.databinding.CostItemBinding

class CostAdapter(
    private val onInfoClicked: (Cost) -> Unit,
) : ListAdapter<Cost, CostViewHolder>(CostDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CostItemBinding.inflate(inflater, parent, false)

        return CostViewHolder(binding, onInfoClicked)
    }


    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        holder.onBind(getItem(position))

    }

}