package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentInfoExpenseBinding
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.CostViewModel

class InfoExpenseFragment : Fragment() {
    lateinit var binding: FragmentInfoExpenseBinding
    lateinit var viewModel: CostViewModel
    private val dataRepository = DataRepository()
    private var goalMutableList = mutableListOf<Goal>()
    private val newBackgroundFragment = BackgroundFragment.newInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoExpenseBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireActivity())[CostViewModel::class.java]

        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            binding.apply {
                tvTitleInfo.text = cost.titleOfCost
                tvMoneyExpenseInfo.text = cost.moneyCost.toString()
                tvDateExpenseInfo.text = cost.date
                tvExpenseCategory.text = cost.category
                if (cost.category == "Цель") {
                    tvCategoryGoal.visibility = View.VISIBLE
                    tvExpenseCategoryGoal.visibility = View.VISIBLE
                    tvExpenseCategoryGoal.text = cost.goal
                } else {
                    tvCategoryGoal.visibility = View.GONE
                    tvExpenseCategoryGoal.visibility = View.GONE
                }

                tvExpenseComment.text = cost.comment

            }
        }

        val goalLiveData = dataRepository.getGoals()
        goalLiveData.observe((activity as ExpensesActivity)) { goalsItem ->
            goalMutableList.clear()
            goalMutableList.addAll(goalsItem)
        }

        binding.ibEdit.setOnClickListener {
            requireFragmentManager().beginTransaction()
                .replace(R.id.backgroundFragment, newBackgroundFragment)
                .addToBackStack(null)
                .commit()
            requireFragmentManager().beginTransaction()
                .replace(R.id.place_holder_addExpenseFragment, EditExpenseFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        binding.ibDelete.setOnClickListener {
            val selectExpense = viewModel.selectedCost.value!!
            if (selectExpense.category == "Цель") {
                val goalList =
                    goalMutableList.filter { it.titleOfGoal == selectExpense.goal }
                if (goalList.isNotEmpty()) {
                    val goal = goalList[0]
                    viewModel.minusProgressGoal(
                        goal,
                        selectExpense.moneyCost
                    )
                }
            }
            viewModel.deleteExpense()
            (activity as ExpensesActivity).closeFragments()
        }

        binding.ibClose.setOnClickListener {
            (activity as ExpensesActivity).closeFragments()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoExpenseFragment()
    }
}