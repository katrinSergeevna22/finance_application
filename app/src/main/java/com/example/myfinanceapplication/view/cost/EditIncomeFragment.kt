package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentEditIncomeBinding
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.view_model.CostViewModel
import com.example.myfinanceapplication.view_model.EditCostViewModel

class EditIncomeFragment : Fragment() {
    lateinit var binding: FragmentEditIncomeBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var editViewModel: EditCostViewModel
    lateinit var selectIncome: Cost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditIncomeBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(CostViewModel::class.java)
        editViewModel = ViewModelProvider(requireActivity()).get(EditCostViewModel::class.java)
        observeSelectCost()
        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditIncomeFragment()
    }
    private fun setupUI(){
        binding.apply {
            binding.ibSave.setOnClickListener {
                editIncome()
            }

            binding.ibClose.setOnClickListener{
                parentFragmentManager.popBackStack()
            }
        }
    }
    fun observeSelectCost(){
        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            binding.apply {
                etTitle.setText(cost.titleOfCost.toString())
                etSum.setText(cost.moneyCost.toString())
                etMultyLineComment.setText(cost.comment)
                selectIncome = cost
                editViewModel.selectCost = cost
                val categoriesArray = resources.getStringArray(R.array.categoriesIncome)
                val categoryToSet = selectIncome.category

                val categoryIndex = categoriesArray.indexOf(categoryToSet)
                if (categoryIndex != -1) {
                    spinnerCategory?.setSelection(categoryIndex)
                }
            }
        }
    }
    fun editIncome() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            val category = spinnerCategory?.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()

            val resultChecking = editViewModel.checkIncomeData(title, sum, category, comment, viewModel.getBalanceNow())
            if (resultChecking){

                //viewModel.editIncomeToBase(newIncome, selectIncome!!)
                viewModel.setSelectedCost(editViewModel.selectCost)
                (activity as IncomeActivity).closeFragments()
                //fragmentManager?.popBackStack()
            }
            else{
                Toast.makeText((activity as IncomeActivity), editViewModel.answerException, Toast.LENGTH_SHORT).show()
            }

        }
    }
}
