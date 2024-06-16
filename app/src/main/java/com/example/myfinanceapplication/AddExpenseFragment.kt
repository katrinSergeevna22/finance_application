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
import com.example.myfinanceapplication.databinding.FragmentAddExpenseBinding

class AddExpenseFragment : Fragment() {
    lateinit var binding: FragmentAddExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var addViewModel: AddCostViewModel
    private lateinit var adapter: ArrayAdapter<String>
    var goalForSpinner = listOf<String>()
    var goalList = listOf<Goal>()
    var selectGoal = Goal()
    var titleOfGoal = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddExpenseBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)
        addViewModel = ViewModelProvider(this).get(AddCostViewModel::class.java)
        viewModel.loadGoals()
        //CoroutineScope(Dispatchers.Main).launch { addViewModel.loadActiveGoals() }

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
            val categoriesArray =
                resources.getStringArray(com.example.myfinanceapplication.R.array.categoriesExpense)

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
            binding.spinnerGoal.onItemSelectedListener =
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
                        Toast.makeText((activity as ExpensesActivity), "Выберите цель", Toast.LENGTH_SHORT).show()
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

    fun saveExpense() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val sum = etSum.text.toString().replace(" ", "")
            val category = spinnerCategory.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()

            if (addViewModel.checkDataExpense(title, sum, category, comment, selectGoal, viewModel.getBalanceNow())) {
                (activity as ExpensesActivity).closeFragments()
            } else {
                // Обработка ошибок
                Toast.makeText((activity as ExpensesActivity), addViewModel.answerException, Toast.LENGTH_SHORT).show()
                if (addViewModel.answerException == "Сумма больше, чем нужно для достижения цели"){
                    etSum.setText((selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal).toString())
                }
            }
        }
    }
    private fun observeViewModel() {
        viewModel.getGoalsLivaData().observe(viewLifecycleOwner, Observer { goals ->
            goalForSpinner = goals.map { it.titleOfGoal!! }
            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Log.d("goalForSpinner3", adapter.toString())
            binding.spinnerGoal.adapter = adapter

            goalList = goals
            Log.d("goalForSpinner1", goalList.toString())

            Log.d("goalForSpinner4", binding.spinnerGoal.adapter.toString())
            adapter.notifyDataSetChanged()

        })
    }
}

/*
class AddExpenseFragment : Fragment() {
    lateinit var binding: FragmentAddExpenseBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var addViewModel: AddCostViewModel
    private val dataRepository = DataRepository()
    var goalMutableList = mutableListOf<Goal>()

    var selectGoal = Goal()
    var titleOfGoal = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddExpenseBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)
        addViewModel = ViewModelProvider(this).get(AddCostViewModel::class.java)

        /*
        val goalsList = listOf(Goal(), Goal())
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, goalsList.map { it.titleOfGoal })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGoal.adapter = adapter


        // Обработка выбора цели из Spinner
        binding.spinnerGoal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGoal = goalsList[position]
                // Действия при выборе цели
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Действия при отсутствии выбора
            }
        }
*/

        binding.apply {
            val categoriesArray =
                resources.getStringArray(com.example.myfinanceapplication.R.array.categoriesExpense)

            /*
            if (spinnerCategory.selectedItem == categoryToSet) {
                spinnerGoal.setVisibility(View.VISIBLE)
                tvGoalExpense.visibility = View.VISIBLE
            } else {
                spinnerGoal.setVisibility(View.INVISIBLE)
                tvGoalExpense.visibility = View.INVISIBLE
            }

             */

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

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoal.adapter = adapter

            val goalLiveData : MutableLiveData<List<Goal>> = dataRepository.getGoals()
            goalLiveData.observe(viewLifecycleOwner, Observer { goalsItems ->
                goalMutableList.clear()
                goalMutableList.addAll(goalsItems.filter { it.status == "Active" })
                for (item in goalsItems.filter { it.status == "Active" }){
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
                        Toast.makeText((activity as ExpensesActivity), "Выберите цель", Toast.LENGTH_SHORT).show()
                    }
                }

            /*
            if (spinnerCategory.selectedItem == 1) {
                spinnerGoal.setVisibility(View.VISIBLE)
                tvGoalExpense.visibility = View.VISIBLE
            }
            else{
                spinnerGoal.setVisibility(View.GONE)
                tvGoalExpense.visibility = View.GONE
            }

             */
            ibSave.setOnClickListener {
                saveExpense()
            }

            ibClose.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddExpenseFragment()
    }


    fun saveExpense() {
        binding.apply {
            val title = etTitle.text.toString()
            val sum: Long = etSum.text.toString().toLong()
            val category = spinnerCategory.selectedItem.toString()

            //var goalExpense = Goal()
            //var titleGoalExpense = ""
            /*
            if (category == "Цель") {
                goalExpense = goalMutableList[spinnerGoal.selectedItemPosition]
                titleGoalExpense = spinnerGoal.selectedItem.toString()
                    //spinnerGoal.selectedItem.toString()
            }

             */
            val comment = etMultyLineComment.text.toString()
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (title.isNotEmpty() && sum != 0L && category.isNotEmpty()) {
                val balance = viewModel.getBalanceNow()
                if (sum > balance){
                    Toast.makeText((activity as ExpensesActivity), "Недостаочно средст", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (category == "Цель") {
                        if (selectGoal != Goal()) {
                            val cost = Cost("", title, sum, date, category, titleOfGoal, comment)
                            if (selectGoal.moneyGoal < sum + selectGoal.progressOfMoneyGoal){
                                Toast.makeText((activity as ExpensesActivity), "Сумма больше, чем нужно для достижения цели", Toast.LENGTH_SHORT).show()

                                etSum.setText((selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal).toString())
                            }
                            else {
                                viewModel.addProgressGoal(selectGoal, sum)
                                addViewModel.saveExpenseToBase(cost)
                                parentFragmentManager.popBackStack()
                            }
                        } else {
                            Toast.makeText(
                                (activity as ExpensesActivity),
                                "Выберите цель",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val cost = Cost("", title, sum, date, category, comment)
                        addViewModel.saveExpenseToBase(cost)
                        parentFragmentManager.popBackStack()
                    }
                }
            } else {
                // Обработка ошибок
                Toast.makeText((activity as ExpensesActivity), "Заполние все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

 */
