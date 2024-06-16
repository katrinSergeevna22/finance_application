package com.example.myfinanceapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.CostAdapter
import com.example.myfinanceapplication.databinding.ActivityExpensesBinding
import kotlinx.coroutines.launch

class ExpensesActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesBinding
    private lateinit var adapter: CostAdapter
    lateinit var viewModel: CostViewModel
    val newBackgroundFragment = BackgroundFragment.newInstance()
    val infoExpenseFragment = InfoExpenseFragment.newInstance()
    val newAddExpenseFragment = AddExpenseFragment.newInstance()
    var mainTip = Tip()
    var mainGoal = Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)

        lifecycle.coroutineScope.launch {
            viewModel.apply {
                //loadBalance().join()
                loadGoals()
                loadExpenses()
                loadOneRandomTipLiveData("Expenses")
                loadOneRandomGoalLiveData()
            }
        }
        setupUI()
        observeViewModel()
        /*
        val addGoalFromInfoGoal = intent.getStringExtra("AddExpense")
        if (addGoalFromInfoGoal != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addExpenseFragment, AddExpenseFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

         */
    }

    private fun setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }
        adapter = CostAdapter {
            viewModel.setSelectedCost(it)

            supportFragmentManager.beginTransaction()
                .replace(R.id.backgroundFragment, newBackgroundFragment)
                .addToBackStack(null)
                .commit()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoExpenseFragment, infoExpenseFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ExpensesActivity)
            rcView.adapter = adapter

            val itemDecoration =
                DividerItemDecoration(this@ExpensesActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this@ExpensesActivity,
                    R.drawable.shape_indent_rcview
                )!!
            )
            rcView.addItemDecoration(itemDecoration)

            ibAddExpense.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.place_holder_addExpenseFragment, newAddExpenseFragment)
                    .addToBackStack(null)
                    .commit()

            }

            ibMainGoal.setOnClickListener {
                navigateTo("GoalsActivityWithSelect")
            }
            ibMainTip.setOnClickListener {
                navigateTo("TipsActivityWithSelect")
            }

            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }

                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }
        }
    }

    private fun observeViewModel() {
        viewModel.balanceLiveData.observe(this, Observer { balance ->
            binding.tvBalanceExpense.text = balance.toString()
            Log.d("ShowBalance3", viewModel.getBalanceNow().toString())
            Log.d("ShowBalance4", balance.toString())
        })
        viewModel.getExpenseLiveData().observe(this) { expenseList ->
            adapter.submitList(expenseList)
            adapter.notifyDataSetChanged()
        }
        viewModel.getOneRandomGoalLiveData().observe(this) {goal ->
            mainGoal = goal
            binding.apply {
                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                }else {
                    tvMoneyGoal.text = goal.moneyGoal.toString()
                    val totalAmountToSave = goal.moneyGoal
                    val currentAmountSaved = goal.progressOfMoneyGoal

                    val progress =
                        (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                    progressBar.progress = progress
                }
            }
        }
        viewModel.getOneRandomTipLiveData().observe(this) {tip->
            mainTip = tip
            binding.tvTitleTip.text = tip.title
        }
    }
    fun closeFragments(){
        supportFragmentManager.popBackStack()
        supportFragmentManager.popBackStack()
    }
    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@ExpensesActivity, GoalsActivity::class.java)
                startActivity(intent)
            }
            "GoalsActivityWithSelect" -> {
                intent = Intent(this@ExpensesActivity, GoalsActivity::class.java)
                intent.putExtra("selectedItem", mainGoal.titleOfGoal)
                startActivity(intent)
            }
            "TipsActivity" -> {
                intent = Intent(this@ExpensesActivity, TipsActivity::class.java)
                startActivity(intent)
            }
            "TipsActivityWithSelect" -> {
                intent = Intent(this@ExpensesActivity, TipsActivity::class.java)
                intent.putExtra("selectedItem", mainTip.title)
                startActivity(intent)
            }
            "MainActivity" -> {
                intent = Intent(this@ExpensesActivity, MainActivity::class.java)
                startActivity(intent)
            }

            "IncomeActivity" -> {
                intent = Intent(this@ExpensesActivity, IncomeActivity::class.java)

                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@ExpensesActivity, ExpensesActivity::class.java)

                startActivity(intent)
            }
            "AuthActivity" -> {
                AuthViewModel().exit()
                intent = Intent(this@ExpensesActivity, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}
    /*


    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId = auth.currentUser!!.uid
    private var expenseList: MutableList<Cost> = mutableListOf()
    private var countOfExpenses = 0
    private var balance = 0.0override
    fun onStart() {
        super.onStart()

        getBalance()
        var adapter = CostAdapter {
            viewModel.setSelectedCost(it)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoExpenseFragment, InfoExpenseFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }


        adapter.submitList(expenseList)

        lifecycle.coroutineScope.launch {
            // Слушатель для получения списка целей из базы данных
            val expenseLiveData = dataRepository.getExpenses()
            //goalsList.clear()
            expenseLiveData.observe(this@ExpensesActivity, Observer { expenseItem ->
                expenseList.clear()
                expenseList.addAll(expenseItem)
                //countOfExpenses = expenseList.size
                //binding.tvBalanceExpense.text = countOfExpenses.toString()
                adapter.notifyDataSetChanged()
            })

            /*
            val expenseRef = database.getReference("users").child(userId).child("expense")
            expenseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    expenseList.clear()

                    for (expenseSnapshot in snapshot.children) {
                        val expense = expenseSnapshot.getValue(Cost::class.java)
                        expense?.let {
                            expenseList.add(it)
                        }
                    }

                    countOfExpenses = expenseList.size
                    binding.tvIncome.text = countOfExpenses.toString()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ExpenseActivity", "Failed to read value.", error.toException())
                }
            })

             */
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ExpensesActivity)
            rcView.adapter = adapter

            val itemDecoration = DividerItemDecoration(this@ExpensesActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(this@ExpensesActivity, R.drawable.shape_indent_rcview)!!)
            rcView.addItemDecoration(itemDecoration)

            val oneRandomGoalLiveData = dataRepository.getOneGoal()
            oneRandomGoalLiveData.observe(this@ExpensesActivity, Observer { goal ->
                tvTitleGoal.text = goal.titleOfGoal
                tvMoneyGoal.text = goal.moneyGoal.toString()
                val totalAmountToSave =  goal.moneyGoal
                val currentAmountSaved = goal.progressOfMoneyGoal

                val progress = (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                progressBar.progress = progress
            })

            val oneRandomTipLiveData = dataRepository.getOneTip("Expenses")
            oneRandomTipLiveData.observe(this@ExpensesActivity, Observer { tip ->
                tvTitleTip.text = tip.title
            })

            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }
                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }
                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }
                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }
                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }
                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }


        }
        binding.ibAddExpense.setOnClickListener {

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addExpenseFragment, AddExpenseFragment.newInstance())
                .addToBackStack(null)
                .commit()

        }
    }
    fun showSelectGoal(){
        if (viewModel.selectedCost.value?.category == "Цель"){

        }
    }

    fun getBalance() : Double{
        dataRepository.getUserBalance().observe(this@ExpensesActivity, Observer {
            balance = it
            Log.d("GetBalance", balance.toString())
            binding.tvBalanceExpense.text = balance.toString()
        })
        return balance
    }

    fun saveExpenseToBase(newCost: Cost){
        dataRepository.writeExpenseData(newCost)
        val balance = getBalance()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(balance - newCost.moneyCost.toDouble())
        /*
        val costRef = database.getReference("users").child(userId).child("income").push()
        val costId = costRef.key
        newCost.costId = costId.toString()
        costRef.setValue(newCost)

         */
    }
    fun addProgressGoal(goal: Goal, sum: Long){
        val newSumProgress = goal.progressOfMoneyGoal + sum
        var status = goal.status
        if (newSumProgress == goal.moneyGoal) status = "Achieved"
        val newGoalData = mapOf(
            "goalId" to goal.goalId,
            "titleOfGoal" to  goal.titleOfGoal.toString(),
            "moneyGoal" to goal.moneyGoal,
            "progressOfMoneyGoal" to newSumProgress,
            "date" to goal.date,
            "category" to goal.category.toString(),
            "comment" to goal.comment.toString(),
            "status" to status.toString(),
        )
        dataRepository.editGoalToBase(newGoalData, goal)
    }

    fun minusProgressGoal(goal: Goal, sum: Long){
        val newSumProgress = goal.progressOfMoneyGoal - sum
        var status = goal.status
        //if (newSumProgress == goal.moneyGoal) status = "Achived"
        if (status == "Achieved" && newSumProgress < goal.moneyGoal) status = "Active"
        val newGoalData = mapOf(
            "goalId" to goal.goalId,
            "titleOfGoal" to  goal.titleOfGoal.toString(),
            "moneyGoal" to goal.moneyGoal,
            "progressOfMoneyGoal" to newSumProgress,
            "date" to goal.date,
            "category" to goal.category.toString(),
            "comment" to goal.comment.toString(),
            "status" to status.toString(),
        )
        dataRepository.editGoalToBase(newGoalData, goal)
    }

    fun editExpenseToBase(newExpense: Map<String, Any>, selectExpense: Cost) {
        val newSum = newExpense.get("moneyCost").toString().toDouble()
        val pastSum = selectExpense.moneyCost.toString().toDouble()
        val balance = getBalance()

        if (newSum > pastSum) {
            dataRepository.updateUserBalance(balance - (newSum - pastSum))
        }
        else{
            dataRepository.updateUserBalance(balance + (pastSum - newSum))
        }

        selectExpense!!.titleOfCost = newExpense["titleOfCost"].toString()
        selectExpense!!.moneyCost = newExpense["moneyCost"].toString().toLong()
        selectExpense!!.category = newExpense["category"].toString()
        selectExpense.comment = newExpense["comment"].toString()
        viewModel.setSelectedCost(selectExpense!!)
        dataRepository.editExpenseToBase(newExpense, selectExpense)

        /*
        database.getReference("users").child(userId).child("income").child(selectIncome.costId)
            .updateChildren(newIncome)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }

         */
    }

    fun deleteExpense(){
        val balance = getBalance()
        val selectExpense = viewModel.selectedCost.value
        if (selectExpense != null){
            val sum = selectExpense.moneyCost

            dataRepository.updateUserBalance(balance + sum)
            dataRepository.deleteExpense(viewModel.selectedCost.value)
        }
    }

    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@ExpensesActivity, GoalsActivity::class.java)
                startActivity(intent)
            }

            "TipsActivity" -> {
                intent = Intent(this@ExpensesActivity, TipsActivity::class.java)
                startActivity(intent)
            }

            "MainActivity" -> {
                intent = Intent(this@ExpensesActivity, MainActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@ExpensesActivity, IncomeActivity::class.java)

                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@ExpensesActivity, ExpensesActivity::class.java)

                startActivity(intent)
            }
            "AuthActivity" -> {
                intent = Intent(this@ExpensesActivity, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}

     */
