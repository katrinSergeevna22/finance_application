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
import com.example.myfinanceapplication.databinding.FragmentAddExpenseBinding
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.AddCostViewModel
import com.example.myfinanceapplication.viewModel.CostViewModel

class AddExpenseFragment : Fragment() {
    lateinit var binding: FragmentAddExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var addViewModel: AddCostViewModel
    private lateinit var adapter: ArrayAdapter<String>
    private var goalForSpinner = listOf<String>()
    var goalList = listOf<Goal>()
    var selectGoal = Goal()
    var titleOfGoal = ""
    var category = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[CostViewModel::class.java]
        addViewModel = ViewModelProvider(this)[AddCostViewModel::class.java]
        viewModel.loadGoals()

        observeViewModel()
        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddExpenseFragment()
    }

    fun setupUI() {
        binding.apply {
            tvGoalExpense.visibility = View.GONE
            spinnerGoal.visibility = View.GONE
            tvBtnCategory?.text = "Выберите категорию"

            ibtnCategory.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment())
                    .addToBackStack(null)
                    .commit()

                val categoriesFragment = CategoriesFragmentForExpense()
                categoriesFragment.setTargetFragment(this@AddExpenseFragment, 1)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.place_holder_addExpenseFragment, categoriesFragment)
                    .addToBackStack(null)
                    .commit()
            }
            binding.spinnerGoal.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position < goalList.size) {
                            selectGoal = goalList[position]
                            titleOfGoal = selectGoal.titleOfGoal.toString()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(
                            (activity as ExpensesActivity),
                            "Выберите цель",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ibSave.setOnClickListener {
                saveExpense()
            }

            ibClose.setOnClickListener {
                (activity as ExpensesActivity).closeFragments()
            }
        }
    }

    private fun saveExpense() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            val comment = etMultyLineComment.text.toString()

            if (addViewModel.checkDataExpense(
                    title,
                    formattedSum(sum),
                    category,
                    comment,
                    selectGoal,
                )
            ) {
                etTitle.text.clear()
                etSum.text.clear()
                etMultyLineComment.text.clear()

                (activity as ExpensesActivity).closeFragments()
            } else {
                // Обработка ошибок
                Toast.makeText(
                    (activity as ExpensesActivity),
                    addViewModel.answerException,
                    Toast.LENGTH_SHORT
                ).show()
                if (addViewModel.answerException == "Сумма больше, чем нужно для достижения цели") {
                    etSum.setText((selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal).toString())
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

    fun receiveData(data: String) {
        category = data
        binding.apply {
            tvBtnCategory?.text = if (data != "")
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

    private fun observeViewModel() {
        viewModel.getGoalsLivaData().observe(viewLifecycleOwner) { goals ->
            goalForSpinner = goals.map { it.titleOfGoal!! }
            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoal.adapter = adapter

            goalList = goals
            adapter.notifyDataSetChanged()

        }
    }
}
