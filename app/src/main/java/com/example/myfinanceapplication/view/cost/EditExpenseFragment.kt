package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentEditExpenseBinding
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.view_model.CostViewModel
import com.example.myfinanceapplication.view_model.EditCostViewModel

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
//            spinnerCategory.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        parent: AdapterView<*>?,
//                        view: View?,
//                        position: Int,
//                        id: Long
//                    ) {
//                        if (spinnerCategory.selectedItem == "Цель") {
//                            spinnerGoal.visibility = View.VISIBLE
//                            tvGoalExpense.visibility = View.VISIBLE
//
//                            Log.d("categoryIndex", spinnerGoal.selectedItem.toString())
//                                /*
//                            spinnerGoal.visibility = View.VISIBLE
//                            tvGoalExpense.visibility = View.VISIBLE
//                            if (!goalForSpinner.contains(selectExpense.goal)){
//                                goalForSpinner.add(selectExpense.goal)
//                                adapter.notifyDataSetChanged()
//                            }
//
//
//                                 */
//                            Log.d("categoryIndex1", selectExpense.goal.toString())
//
//                            val categoriesGoalArray = goalForSpinner
//                            val categoryGoalToSet = selectExpense.goal
//
//                            Log.d("categoryIndex2", goalForSpinner.toString())
//                            Log.d("categoryIndex3", goalList.toString())
//                            val categoryGoalIndex = categoriesGoalArray.indexOf(categoryGoalToSet)
//                            Log.d("categoryIndex4", categoryGoalIndex.toString())
//                            if (categoryGoalIndex != -1) {
//                                Log.d("categoryIndex5", spinnerGoal.adapter.count.toString())
//                                spinnerGoal.setSelection(categoryGoalIndex)
//                            }
//
//                        } else {
//                            spinnerGoal.visibility = View.GONE
//                            tvGoalExpense.visibility = View.GONE
//                        }
//
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        //spinnerGoal.visibility = View.VISIBLE
//                        //tvGoalExpense.visibility = View.VISIBLE
//                    }
//                }
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
                //parentFragment?.view?.visibility = View.INVISIBLE
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment())
                    .addToBackStack(null)
                    .commit()

                val categoriesFragment = CategoriesFragment()
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
                Log.d("untilgoalList", selectExpense.goal)
                editViewModel.selectCost = cost
//                val categoriesArray = resources.getStringArray(R.array.categoriesExpense)
//                val categoryToSet = selectExpense.category
                selectingCategory = cost.category
                tvBtnCategory?.text = if (selectingCategory != "")
                    "Выбрана: $selectingCategory"
                else
                    "Выберите категорию"



//                val categoryIndex = categoriesArray.indexOf(categoryToSet)
//                if (categoryIndex != -1) {
//                    //spinnerCategory.setSelection(categoryIndex)
//                }

            }
        }
        viewModel.getGoalsLivaData().observe(viewLifecycleOwner, Observer { goals ->
            val activeGoals =
                goals.filter { it.status == "Active" || it.titleOfGoal == selectExpense.goal }
            goalForSpinner = activeGoals.map { it.titleOfGoal!! }.toMutableList()

            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Log.d("goalForSpinner3", adapter.toString())
            binding.spinnerGoal.adapter = adapter
            editViewModel.goalsMutableList = activeGoals
            goalList = activeGoals.toMutableList()
            Log.d("goalForSpinner1", goalList.toString())
            /*
                        if (!goalForSpinner.contains(selectExpense.goal)){
                            goalForSpinner.add(selectExpense.goal)
                            val goalSelect = goalList.filter { it.titleOfGoal == selectExpense.goal }[0]
                            goalList.add(goalSelect)
                            editViewModel.goalsMutableList = goalList
                        }

             */
            if (selectingCategory == "Цель"){
                binding.apply {
                    tvGoalExpense.visibility = View.VISIBLE
                    spinnerGoal.visibility = View.VISIBLE
                    spinnerGoal.setSelection(goalForSpinner.indexOf(selectExpense.goal))
                    Log.d("katrin_goal", selectExpense.goal)
                    Log.d("katrin_goal", goalForSpinner.indexOf(selectExpense.goal).toString())
                }
            }
            Log.d("goalForSpinner4", binding.spinnerGoal.adapter.toString())
            adapter.notifyDataSetChanged()

        })
    }

    private fun editGoal() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            //spinnerCategory.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()
            //val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            Log.d("goalList1", goalList.toString())
            Log.d("goalList2", selectExpense.goal)
            Log.d("goalList3", titleOfGoal)

            if (editViewModel.checkExpenseData(
                    title,
                    sum,
                    category,
                    comment,
                    titleOfGoal,
                    viewModel.getBalanceNow()
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
}
