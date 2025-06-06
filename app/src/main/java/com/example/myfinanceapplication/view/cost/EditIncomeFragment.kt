package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentEditIncomeBinding
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.CostViewModel
import com.example.myfinanceapplication.viewModel.EditCostViewModel

class EditIncomeFragment : Fragment() {
    lateinit var binding: FragmentEditIncomeBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var editViewModel: EditCostViewModel
    private lateinit var selectIncome: Cost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun setupUI() {
        binding.apply {
            ibSave.setOnClickListener {
                editIncome()
            }

            ibClose.setOnClickListener {
                (activity as IncomeActivity).closeFragments()
            }

            tvBtnCategory?.text = "Выберите категорию"
            ibtnCategory.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment())
                    .addToBackStack(null)
                    .commit()

                val categoriesFragment = CategoriesFragmentForIncome()
                categoriesFragment.setTargetFragment(this@EditIncomeFragment, 1)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.place_holder_addIncomeFragment, categoriesFragment)
                    .addToBackStack(null)
                    .commit()

            }
        }
    }

    var selectingCategory = ""
    private fun observeSelectCost() {
        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            binding.apply {
                etTitle.setText(cost.titleOfCost.toString())
                etSum.setText(cost.moneyCost.toString())
                etMultyLineComment.setText(cost.comment)
                selectIncome = cost
                editViewModel.selectCost = cost
                selectingCategory = cost.category ?: ""
                tvBtnCategory?.text = if (selectingCategory != "")
                    "Выбрана: $selectingCategory"
                else
                    "Выберите категорию"
            }
        }
    }

    private fun editIncome() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            //val category = spinnerCategory?.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()

            val resultChecking = editViewModel.checkIncomeData(
                title,
                formattedSum(sum),
                selectingCategory,
                comment,
            )
            if (resultChecking) {

                //viewModel.editIncomeToBase(newIncome, selectIncome!!)
                viewModel.setSelectedCost(editViewModel.selectCost)
                (activity as IncomeActivity).closeFragments()
                //fragmentManager?.popBackStack()
            } else {
                Toast.makeText(
                    (activity as IncomeActivity),
                    editViewModel.answerException,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun formattedSum(sum: String): String{
        val cleanString = sum
            .replace(",", ".")
            .replace(Regex("[^\\d.]"), "") // Удаляем все, кроме цифр и точек

        // Обрабатываем ввод
        val formattedValue = when {
            cleanString.contains(".") -> {
                // Если есть точка, ограничиваем до 2 знаков после запятой
                val parts = cleanString.split(".")
                when {
                    parts.size > 2 -> parts[0] + "." + parts[1].take(2) // Если несколько точек
                    parts[1].length > 2 -> parts[0] + "." + parts[1].take(2) // Если больше 2 знаков
                    else -> cleanString
                }
            }
            else -> cleanString
        }

        return formattedValue
    }

    fun receiveData(data: String) {
        selectingCategory = data
        binding.apply {
            tvBtnCategory?.text = if (data != "")
                "Выбрана: $data"
            else
                "Выберите категорию"

        }
    }
}
