package com.example.myapplication.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.Goal
import com.example.myfinanceapplication.Tip
import com.example.myfinanceapplication.databinding.GoalItemBinding
import com.example.myfinanceapplication.databinding.TipItemBinding

//import com.squareup.picasso.Picasso

class TipViewHolder(
    private val binding: TipItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

    private var _tip: Tip? = null
    private val tip: Tip
        get() = _tip!!

    fun onBind(data: Tip) {
        _tip = data
        with(binding) {
            tvTitle.text = tip.title
            tvAdvice.text = tip.text

            /*Picasso.with(root.context)
                .load(goal.posterUrl)
                .into(ivPoster)*/
        }
    }

    fun parse(input: String): String {
        val pattern = Regex("genre=([a-zA-Zа-яА-Я]+)")
        val matches = pattern.findAll(input)

        val genresList = mutableListOf<String>()

        for (match in matches) {
            genresList.add(match.groupValues[1])
        }
        return genresList[0]
    }
}