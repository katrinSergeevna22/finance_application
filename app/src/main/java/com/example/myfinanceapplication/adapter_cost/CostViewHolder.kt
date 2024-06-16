package com.example.myapplication.adapter

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.Cost
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.CostItemBinding

//import com.squareup.picasso.Picasso

class CostViewHolder(
    private val binding: CostItemBinding,
    private val onInfoClicked: (Cost) -> Unit,

) : RecyclerView.ViewHolder(binding.root) {
    private var _cost: Cost? = null
    private val cost: Cost
        get() = _cost!!

    fun onBind(data: Cost) {
        _cost = data
        with(binding) {
            root.setOnClickListener {
                Log.d("My log: click Income", cost.costId.toString())
                onInfoClicked(cost)
            }


            tvTitle.text = cost.titleOfCost
            tvMoneyGoal.text = cost.moneyCost.toString()
            if (!cost.isExpense){
                tvMoneyGoal.setTextColor(ContextCompat.getColor(tvMoneyGoal.context, R.color.green))
                tvMoneyGoal.text = "+" + cost.moneyCost.toString()
            }
            else if(cost.isExpense){
                tvMoneyGoal.setTextColor(ContextCompat.getColor(tvMoneyGoal.context, R.color.red))
                tvMoneyGoal.text = "-" + cost.moneyCost.toString()
            }


            /*Picasso.with(root.context)
                .load(goal.posterUrl)
                .into(ivPoster)*/
        }
    }

}