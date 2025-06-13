package com.example.myfinanceapplication.view.goals.adapterGoals

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.databinding.GoalItemBinding
import com.example.myfinanceapplication.model.Goal
import java.util.Locale

class GoalViewHolder(
    private val binding: GoalItemBinding,
    private val onInfoClicked: (Goal) -> Unit,

    ) : RecyclerView.ViewHolder(binding.root) {
    private var _film: Goal? = null
    private val goal: Goal
        get() = _film!!

    @SuppressLint("SetTextI18n")
    fun onBind(data: Goal) {
        _film = data
        with(binding) {

            root.setOnClickListener {
                onInfoClicked(goal)
            }

            tvTitle.text = goal.titleOfGoal
            tvMoneyGoal.text = formatSum(goal.moneyGoal)
            tvProgressMoney.text =
                formatSum(goal.progressOfMoneyGoal) + " из " + formatSum(goal.moneyGoal)
            tvDate.text = goal.date
            val totalAmountToSave = goal.moneyGoal// Общая сумма для накопления
            val currentAmountSaved =
                goal.progressOfMoneyGoal // Текущая сумма, которая уже накоплена

            val progress =
                (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
            progressBar.progress = progress

        }
    }

    private fun formatSum(value: Double): String {
        return if (value == value.toInt().toDouble()) {
            // Если число целое - показываем без десятичной части
            value.toInt().toString()
        } else {
            // Если дробное - показываем 2 знака после запятой
            "%.2f".format(Locale.US, value).replace(",", ".")
        }
    }
}