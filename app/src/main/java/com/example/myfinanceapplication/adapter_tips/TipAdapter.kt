package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
//import com.example.myapplication.databinding.GoalItemBinding
import com.example.myfinanceapplication.Goal
import com.example.myfinanceapplication.Tip
import com.example.myfinanceapplication.databinding.GoalItemBinding
import com.example.myfinanceapplication.databinding.TipItemBinding

class TipAdapter() : ListAdapter<Tip, TipViewHolder>(TipDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TipItemBinding.inflate(inflater, parent, false)

        return TipViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.onBind(getItem(position))

    }

}