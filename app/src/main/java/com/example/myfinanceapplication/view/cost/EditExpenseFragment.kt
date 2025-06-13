package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentEditExpenseBinding
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.CostViewModel
import com.example.myfinanceapplication.viewModel.EditCostViewModel

class EditExpenseFragment : Fragment() {
    lateinit var binding: FragmentEditExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var editViewModel: EditCostViewModel
    private lateinit var selectExpense: Cost
    private var goalForSpinner = mutableListOf<String>()
    var goalList = mutableListOf<Goal>()
    var selectGoal = Goal()
    var titleOfGoal = ""
    var category = ""

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditExpenseBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(CostViewModel::class.java)
        editViewModel = ViewModelProvider(requireActivity()).get(EditCostViewModel::class.java)
        viewModel.loadGoals()

        observeViewModel()
        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditExpenseFragment()
    }

    fun setupUI() {
        binding.apply {
            ibSave.setOnClickListener {
                editGoal()
            }
            ibClose.setOnClickListener {
                (activity as ExpensesActivity).closeFragments()
            }

            spinnerGoal.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectGoal = goalList[position]
                        titleOfGoal = selectGoal.titleOfGoal.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(
                            (activity as ExpensesActivity),
                            "Выберите цель",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            tvGoalExpense.visibility = View.GONE
            spinnerGoal.visibility = View.GONE
            tvBtnCategory?.text = "Выберите категорию"
            ibtnCategory.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment())
                    .addToBackStack(null)
                    .commit()

                val categoriesFragment = CategoriesFragmentForExpense()
                categoriesFragment.setTargetFragment(this@EditExpenseFragment, 1)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.place_holder_addExpenseFragment, categoriesFragment)
                    .addToBackStack(null)
                    .commit()

            }
        }
    }

    fun receiveData(data: String) {
        category = data
        binding.apply {
            binding.tvBtnCategory?.text = if (data != "")
                "Выбрана: $data"
            else
                "Выберите категорию"

            if (resources.getStringArray(R.array.categoriesExpense)[0] == data) {
                tvGoalExpense.visibility = View.VISIBLE
                spinnerGoal.visibility = View.VISIBLE
            } else {
                tvGoalExpense.visibility = View.GONE
                spinnerGoal.visibility = View.GONE
            }
        }
    }

    var selectingCategory: String? = null
    private fun observeViewModel() {

        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            selectExpense = cost
            binding.apply {
                etTitle.setText(cost.titleOfCost.toString())
                etSum.setText(cost.moneyCost.toString())
                etMultyLineComment.setText(cost.comment)
                selectExpense = cost
                editViewModel.selectCost = cost
                selectingCategory = cost.category
                tvBtnCategory?.text = if (selectingCategory != "")
                    "Выбрана: $selectingCategory"
                else
                    "Выберите категорию"

            }
        }
        viewModel.getGoalsLivaData().observe(viewLifecycleOwner) { goals ->
            val activeGoals =
                goals.filter { it.status == "Active" || it.titleOfGoal == selectExpense.goal }
            goalForSpinner = activeGoals.map { it.titleOfGoal!! }.toMutableList()

            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoal.adapter = adapter
            editViewModel.goalsMutableList = activeGoals
            goalList = activeGoals.toMutableList()

            if (selectingCategory == "Цель") {
                binding.apply {
                    tvGoalExpense.visibility = View.VISIBLE
                    spinnerGoal.visibility = View.VISIBLE
                    spinnerGoal.setSelection(goalForSpinner.indexOf(selectExpense.goal))
                }
            }
            adapter.notifyDataSetChanged()

        }
    }

    private fun editGoal() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            val comment = etMultyLineComment.text.toString()

            if (editViewModel.checkExpenseData(
                    title,
                    formattedSum(sum),
                    category.ifBlank { selectingCategory ?: "" },
                    comment,
                    titleOfGoal,
                )
            ) {
                viewModel.setSelectedCost(editViewModel.selectCost)
                (activity as ExpensesActivity).closeFragments()
            } else {
                // Обработка ошибок
                Toast.makeText(
                    (activity as ExpensesActivity),
                    editViewModel.answerException,
                    Toast.LENGTH_SHORT
                ).show()
                if (editViewModel.answerException == "Сумма больше, чем нужно для достижения цели") {
                    val sumTipForGoal =
                        selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal + selectExpense.moneyCost
                    etSum.setText(sumTipForGoal.toString())
                }
            }
        }
    }

    private fun formattedSum(sum: String): String {
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
}
