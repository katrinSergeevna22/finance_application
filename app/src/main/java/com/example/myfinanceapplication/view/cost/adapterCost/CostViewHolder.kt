package com.example.myfinanceapplication.view.cost.adapterCost

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.CostItemBinding
import java.util.Locale

class CostViewHolder(
    private val binding: CostItemBinding,
    private val onInfoClicked: (Cost) -> Unit,

    ) : RecyclerView.ViewHolder(binding.root) {
    private var _cost: Cost? = null
    private val cost: Cost
        get() = _cost!!

    @SuppressLint("SetTextI18n")
    fun onBind(data: Cost) {
        _cost = data
        with(binding) {
            root.setOnClickListener {
                onInfoClicked(cost)
            }

            tvTitle.text = cost.titleOfCost
            val formattedSum = formatSum(cost.moneyCost)
            tvMoneyGoal.text = formattedSum
            if (!cost.isExpense) {
                tvMoneyGoal.setTextColor(ContextCompat.getColor(tvMoneyGoal.context, R.color.green))
                tvMoneyGoal.text = "+$formattedSum"
            } else if (cost.isExpense) {
                tvMoneyGoal.setTextColor(ContextCompat.getColor(tvMoneyGoal.context, R.color.red))
                tvMoneyGoal.text = "-$formattedSum"
            }

        }
    }

    private fun formatSum(value: Double): String {
        val h  = if (value == value.toInt().toDouble()) {
            // Если число целое - показываем без десятичной части
            value.toInt().toString()
        } else {
            // Если дробное - показываем 2 знака после запятой
            "%.2f".format(Locale.US, value).replace(",", ".")
        }
        Log.d("katrin_format_balance", h)
        return h
    }
}