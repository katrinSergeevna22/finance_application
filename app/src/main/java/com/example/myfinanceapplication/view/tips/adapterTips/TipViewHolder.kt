package com.example.myfinanceapplication.view.tips.adapterTips

import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.databinding.TipItemBinding
import com.example.myfinanceapplication.model.Tip

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

        }
    }
}