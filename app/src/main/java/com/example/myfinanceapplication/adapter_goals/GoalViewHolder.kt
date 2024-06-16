package com.example.myapplication.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapplication.Goal
import com.example.myfinanceapplication.databinding.GoalItemBinding

class GoalViewHolder(
    private val binding: GoalItemBinding,
    private val onInfoClicked: (Goal) -> Unit,

) : RecyclerView.ViewHolder(binding.root) {
    private var _film: Goal? = null
    private val goal: Goal
        get() = _film!!

    fun onBind(data: Goal) {
        _film = data
        with(binding) {

            root.setOnClickListener {
                Log.d("My log: click", goal.goalId.toString())
                onInfoClicked(goal)
            }

            /*root.setOnLongClickListener{
                Log.d("My log: long click", goal.goalId.toString())
                onLongClicked(goal)
                true
            }*/


            tvTitle.text = goal.titleOfGoal
            tvMoneyGoal.text = goal.moneyGoal.toString()
            tvProgressMoney.text = goal.progressOfMoneyGoal.toString() + " из " + goal.moneyGoal.toString()
            tvDate.text = goal.date
            val totalAmountToSave =  goal.moneyGoal// Общая сумма для накопления
            val currentAmountSaved = goal.progressOfMoneyGoal // Текущая сумма, которая уже накоплена

            val progress = (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
            progressBar.progress = progress
            //progressBar.max = goal.moneyGoal.toString().toInt()
            //progressBar.progress = goal.progressOfMoneyGoal.toString().toInt()

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