package com.example.myfinanceapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.myfinanceapplication.databinding.FragmentInfoExpenseBinding
import com.example.myfinanceapplication.databinding.FragmentInfoIncomeBinding
import kotlinx.coroutines.launch

class InfoExpenseFragment : Fragment() {
    lateinit var binding: FragmentInfoExpenseBinding
    lateinit var viewModel: CostViewModel
    private val dataRepository = DataRepository()
    var goalMutableList = mutableListOf<Goal>()
    val newBackgroundFragment = BackgroundFragment.newInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoExpenseBinding.inflate(inflater)

        Log.d("Fragment", "InfoIncome")
        viewModel = ViewModelProvider(requireActivity()).get(CostViewModel::class.java)

        viewModel.selectedCost.observe(viewLifecycleOwner, Observer { cost ->
            binding.apply {
                tvTitleInfo.text = cost.titleOfCost
                tvMoneyExpenseInfo.text = cost.moneyCost.toString()
                tvDateExpenseInfo.text = cost.date
                tvExpenseCategory.text = cost.category
                if (cost.category == "Цель") {
                    tvCategoryGoal.visibility = View.VISIBLE
                    tvExpenseCategoryGoal.visibility = View.VISIBLE
                    tvExpenseCategoryGoal.text = cost.goal
                }
                else {
                    tvCategoryGoal.visibility = View.GONE
                    tvExpenseCategoryGoal.visibility = View.GONE
                }

                tvExpenseComment.text = cost.comment

            }
        })

        val goalLiveData = dataRepository.getGoals()
        goalLiveData.observe((activity as ExpensesActivity), Observer { goalsItem ->
            goalMutableList.clear()
            goalMutableList.addAll(goalsItem)
            Log.d("DeleteInfoObserveItem", goalsItem.toString())
            Log.d("DeleteInfoObserve", goalMutableList.toString())
        })

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
            Log.d("DeleteInfo", selectExpense.goal)
            if (selectExpense.category == "Цель"){
                val goal =
                    goalMutableList.filter { it.titleOfGoal == selectExpense.goal }[0]
                viewModel.minusProgressGoal(
                    goal,
                    selectExpense.moneyCost
                )
            }
            viewModel.deleteExpense()
            (activity as ExpensesActivity).closeFragments()
        }

        binding.ibClose.setOnClickListener{
            (activity as ExpensesActivity).closeFragments()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoExpenseFragment()
    }
}