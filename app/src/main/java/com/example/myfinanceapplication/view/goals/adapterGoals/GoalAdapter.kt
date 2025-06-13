package com.example.myfinanceapplication.view.goals.adapterGoals

//import com.example.myapplication.databinding.GoalItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.myfinanceapplication.databinding.GoalItemBinding
import com.example.myfinanceapplication.model.Goal

class GoalAdapter(
    private val onInfoClicked: (Goal) -> Unit,
) : ListAdapter<Goal, GoalViewHolder>(GoalDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GoalItemBinding.inflate(inflater, parent, false)

        return GoalViewHolder(binding, onInfoClicked)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}