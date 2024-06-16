package com.example.myfinanceapplication

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
import com.example.myfinanceapplication.databinding.FragmentEditExpenseBinding

class EditExpenseFragment : Fragment() {
    lateinit var binding: FragmentEditExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var editViewModel: EditCostViewModel
    lateinit var selectExpense: Cost
    var goalForSpinner = mutableListOf<String>()
    var goalList = mutableListOf<Goal>()
    var selectGoal = Goal()
    var titleOfGoal = ""

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    fun setupUI(){
        binding.ibSave.setOnClickListener {
            editGoal()
        }
        binding.ibClose.setOnClickListener{
            (activity as ExpensesActivity).closeFragments()
        }
        binding.apply {
            spinnerCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (spinnerCategory.selectedItem == "Цель") {
                            spinnerGoal.visibility = View.VISIBLE
                            tvGoalExpense.visibility = View.VISIBLE

                            Log.d("categoryIndex", spinnerGoal.selectedItem.toString())
                            spinnerGoal.visibility = View.VISIBLE
                            tvGoalExpense.visibility = View.VISIBLE
                            if (!goalForSpinner.contains(selectExpense.goal)){
                                goalForSpinner.add(selectExpense.goal)
                                adapter.notifyDataSetChanged()
                            }

                            Log.d("categoryIndex1", selectExpense.goal.toString())

                            val categoriesGoalArray = goalForSpinner
                            val categoryGoalToSet = selectExpense.goal

                            Log.d("categoryIndex2", goalForSpinner.toString())
                            Log.d("categoryIndex3", categoryGoalToSet)
                            val categoryGoalIndex = categoriesGoalArray.indexOf(categoryGoalToSet)
                            Log.d("categoryIndex4", categoryGoalIndex.toString())
                            if (categoryGoalIndex != -1) {
                                Log.d("categoryIndex5", spinnerGoal.adapter.count.toString())
                                spinnerGoal.setSelection(categoryGoalIndex)
                            }

                        } else {
                            spinnerGoal.visibility = View.GONE
                            tvGoalExpense.visibility = View.GONE
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //spinnerGoal.visibility = View.VISIBLE
                        //tvGoalExpense.visibility = View.VISIBLE
                    }
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
        }
    }
    fun observeViewModel(){

        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            selectExpense = cost
            binding.apply {
                etTitle.setText(cost.titleOfCost.toString())
                etSum.setText(cost.moneyCost.toString())
                etMultyLineComment.setText(cost.comment)
                selectExpense = cost
                Log.d("untilgoalList", selectExpense.goal.toString())
                editViewModel.selectCost = cost
                val categoriesArray = resources.getStringArray(R.array.categoriesExpense)
                val categoryToSet = selectExpense.category

                val categoryIndex = categoriesArray.indexOf(categoryToSet)
                if (categoryIndex != -1) {
                    spinnerCategory.setSelection(categoryIndex)
                }

            }
        }
        viewModel.getGoalsLivaData().observe(viewLifecycleOwner, Observer { goals ->
            val activeGoals = goals.filter { it.status == "Active" || it.titleOfGoal == selectExpense.goal}
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
            Log.d("goalForSpinner4", binding.spinnerGoal.adapter.toString())
            adapter.notifyDataSetChanged()

        })
    }
    fun editGoal() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            val category = spinnerCategory.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()
            //val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            Log.d("goalList1", goalList.toString())
            Log.d("goalList2", selectExpense.goal)
            Log.d("goalList3", titleOfGoal)

            if (editViewModel.checkExpenseData(title, sum, category, comment, titleOfGoal, viewModel.getBalanceNow())) {
                viewModel.setSelectedCost(editViewModel.selectCost)
                (activity as ExpensesActivity).closeFragments()
            } else {
                // Обработка ошибок
                Toast.makeText((activity as ExpensesActivity), editViewModel.answerException, Toast.LENGTH_SHORT).show()
                if (editViewModel.answerException == "Сумма больше, чем нужно для достижения цели"){
                    val sumTipForGoal = selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal + selectExpense.moneyCost
                    etSum.setText(sumTipForGoal.toString())
                }
            }
        }
    }
}

/*
class EditExpenseFragment : Fragment() {
    lateinit var binding: FragmentEditExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var editViewModel: EditCostViewModel
    lateinit var selectExpense: Cost
    private val dataRepository = DataRepository()
    var goalMutableList = mutableListOf<Goal>()
    var selectGoal = Goal()
    var titleOfGoal = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditExpenseBinding.inflate(inflater)

        Log.d("FindExGoal", "FragmentCreate")

        binding.ibSave.setOnClickListener {
            editGoal()
        }

        binding.ibClose.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        viewModel = ViewModelProvider(requireActivity()).get(CostViewModel::class.java)
        editViewModel = ViewModelProvider(requireActivity()).get(EditCostViewModel::class.java)
        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            selectExpense = cost
            binding.apply {
                etTitle.setText(cost.titleOfCost.toString())
                etSum.setText(cost.moneyCost.toString())
                etMultyLineComment.setText(cost.comment)
                selectExpense = cost
                editViewModel.selectCost = cost
                val categoriesArray = resources.getStringArray(R.array.categoriesExpense)
                val categoryToSet = selectExpense.category

                val categoryIndex = categoriesArray.indexOf(categoryToSet)
                if (categoryIndex != -1) {
                    spinnerCategory.setSelection(categoryIndex)
                }
            }
        }
        binding.apply {
            spinnerCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //val selectedGoal = goalsList[position]

                        if (spinnerCategory.selectedItem == "Цель") {
                            spinnerGoal.visibility = View.VISIBLE
                            tvGoalExpense.visibility = View.VISIBLE
                        } else {
                            spinnerGoal.visibility = View.GONE
                            tvGoalExpense.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        spinnerGoal.visibility = View.VISIBLE
                        tvGoalExpense.visibility = View.VISIBLE
                    }
                }

            var goalForSpinner = mutableListOf<String>()

            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoal.adapter = adapter

            val goalLiveData: MutableLiveData<List<Goal>> = dataRepository.getGoals()
            goalLiveData.observe(viewLifecycleOwner, Observer { goalsItems ->
                goalMutableList.clear()
                goalMutableList.addAll(goalsItems)
                for (item in goalsItems) {
                    goalForSpinner.add(item.titleOfGoal.toString())
                }
                adapter.notifyDataSetChanged()
            })

            spinnerGoal.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectGoal = goalMutableList[position]
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
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = EditExpenseFragment()
    }
    fun editGoal() {
        binding.apply {
            val title = etTitle.text.toString()
            val sum: Long = etSum.text.toString().toLong()
            val category = spinnerCategory.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()
            //val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (title.isNotEmpty() && sum != 0L && category.isNotEmpty()) {
                val balance = viewModel.getBalanceNow()
                if (selectExpense.category == "Цель") {
                    val goalOld = goalMutableList.filter { it.titleOfGoal == selectExpense.goal }[0]

                    if (category == "Цель") {
                        val goalNew = goalMutableList.filter { it.titleOfGoal == titleOfGoal }[0]
                        Log.d("EditGoalProgress", selectExpense.moneyCost.toString())
                        Log.d("EditGoalProgressTitle", selectExpense.goal + " " + titleOfGoal)

                        if (goalOld != goalNew) {
                            viewModel.minusProgressGoal(
                                goalOld,
                                selectExpense.moneyCost
                            )
                            viewModel.addProgressGoal(
                                goalNew,
                                sum
                            )
                        } else {
                            if (selectExpense.moneyCost > sum) {
                                viewModel.minusProgressGoal(
                                    goalNew,
                                    selectExpense.moneyCost - sum
                                )
                            } else if (selectExpense.moneyCost < sum) {
                                viewModel.addProgressGoal(
                                    goalNew,
                                    sum - selectExpense.moneyCost
                                )
                            }
                        }
                    }
                    else {
                        viewModel.minusProgressGoal(
                            goalOld,
                            selectExpense.moneyCost
                        )
                    }


                }


                val newExpense = mapOf(
                    "costId" to selectExpense!!.costId,
                    "titleOfCost" to title,
                    "moneyCost" to sum,
                    "date" to selectExpense!!.date,
                    "category" to category,
                    "goal" to titleOfGoal,
                    "comment" to comment,
                )
                //selectIncome!!.titleOfCost = title
                //selectIncome!!.moneyCost = sum
                //selectIncome!!.category = category
                //viewModel.setSelectedCost(selectIncome!!)

                editViewModel.editExpenseToBase(newExpense, selectExpense!!)

                parentFragmentManager.popBackStack()
                //fragmentManager?.popBackStack()

            } else {
                // Обработка ошибок
                Toast.makeText((activity as ExpensesActivity), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

 */