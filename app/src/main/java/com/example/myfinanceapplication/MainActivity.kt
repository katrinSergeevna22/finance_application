package com.example.myfinanceapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    var mainTip = Tip()
    var mainGoal = Goal()
    private val dataRepository = DataRepository()
    private var balanceIncome = 0.00
    private var balanceExpense = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        observeViewModel()
        setupUI()
    }

    fun setupUI() {
        binding.apply {
            tvGreeting.text = ContextCompat.getString(this@MainActivity, R.string.greeting)
            tvTitleMoney.text = "Последняя неделя"

            toolbarMain.setNavigationOnClickListener {
                binding.drawerMain.openDrawer(GravityCompat.START)
            }
            ibGoals.setOnClickListener {
                navigateTo("GoalsActivity")
            }
            ibTips.setOnClickListener {
                navigateTo("TipsActivity")
            }
            ibIncome.setOnClickListener {
                navigateTo("IncomeActivity")
            }
            ibExpense.setOnClickListener {
                navigateTo("ExpenseActivity")
            }
            ibMainTip.setOnClickListener {
                navigateTo("TipsActivityWithSelect")
            }
            ibMainGoal.setOnClickListener {
                navigateTo("GoalsActivityWithSelect")
            }
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun observeViewModel() {
        binding.apply {
            viewModel.getBalance().observe(this@MainActivity) { balance ->
                tvBalance.text = balance.toString()
            }
            viewModel.getOneRandomGoal().observe(this@MainActivity) { goal ->
                mainGoal = goal
                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                    tvProgressMoney.text = ""
                    tvDate.text = ""
                } else {
                    tvMoneyGoal.text = goal.moneyGoal.toString()
                    tvProgressMoney.text =
                        goal.progressOfMoneyGoal.toString() + " из " + goal.moneyGoal.toString()
                    tvDate.text = goal.date
                    val totalAmountToSave = goal.moneyGoal
                    val currentAmountSaved = goal.progressOfMoneyGoal

                    val progress =
                        (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                    progressBar.progress = progress
                }
            }
            viewModel.getOneRandomTip().observe(this@MainActivity) { tip ->
                tvTitleTip.text = tip.title
                tvAdvice.text = tip.text
                mainTip = tip
            }
            viewModel.getSumIncome().observe(this@MainActivity) {sumIncome ->
                tvBalanceIncome.text = "+ " + String.format("%.2f", sumIncome)
            }
            viewModel.getSumExpense().observe(this@MainActivity) {sumExpense ->
                tvBalanceExpense.text = "- " + String.format("%.2f", sumExpense)
            }
        }
    }

    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@MainActivity, GoalsActivity::class.java)
                startActivity(intent)
            }
            "GoalsActivityWithSelect" -> {
                intent = Intent(this@MainActivity, GoalsActivity::class.java)
                intent.putExtra("selectedItem", mainGoal.titleOfGoal)
                startActivity(intent)
            }
            "TipsActivity" -> {
                intent = Intent(this@MainActivity, TipsActivity::class.java)
                startActivity(intent)
            }
            "TipsActivityWithSelect" -> {
                intent = Intent(this@MainActivity, TipsActivity::class.java)
                intent.putExtra("selectedItem", mainTip.title)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@MainActivity, IncomeActivity::class.java)
                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@MainActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }

            "AuthActivity" -> {
                AuthViewModel().exit()
                intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
            }

        }

    }
}
/*
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val dataRepository = DataRepository()
    private var balanceIncome = 0.00
    private var balanceExpense = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.setNavigationOnClickListener {
            binding.drawerMain.openDrawer(GravityCompat.START)
        }
    }

    override fun onStart() {
        super.onStart()
        getSumIncome()
        getSumExpense()
        binding.apply {
            tvGreeting.text = ContextCompat.getString(this@MainActivity, R.string.greeting)
            tvTitleMoney.text = "Последняя неделя"

            dataRepository.getUserBalance().observe(this@MainActivity, Observer {balance ->
                if (balance == null){
                    tvBalance.text = 0.0.toString()
                }
                else {
                    tvBalance.text = balance.toString()
                }
            })



            ibGoals.setOnClickListener {
                navigateTo("GoalsActivity")
            }
            ibTips.setOnClickListener {
                navigateTo("TipsActivity")
            }
            ibIncome.setOnClickListener{
                navigateTo("IncomeActivity")
            }
            ibExpense.setOnClickListener{
                navigateTo("ExpenseActivity")
            }

            val oneRandomGoalLiveData = dataRepository.getOneGoal()
            oneRandomGoalLiveData.observe(this@MainActivity, Observer { goal ->
                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                    tvProgressMoney.text = ""
                    tvDate.text = ""
                }
                else {
                    tvMoneyGoal.text = goal.moneyGoal.toString()
                    tvProgressMoney.text =
                        goal.progressOfMoneyGoal.toString() + " из " + goal.moneyGoal.toString()
                    tvDate.text = goal.date
                    val totalAmountToSave = goal.moneyGoal
                    val currentAmountSaved = goal.progressOfMoneyGoal

                    val progress =
                        (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                    progressBar.progress = progress
                }
            })

            val oneRandomTipLiveData = dataRepository.getOneTip()
            oneRandomTipLiveData.observe(this@MainActivity, Observer { tip ->
                tvTitleTip.text = tip.title
                tvAdvice.text = tip.text
            })

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }
                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }
        }
    }
    fun getSumIncome(){
        val incomeLiveData = dataRepository.getIncomes()
        if (incomeLiveData.value?.size == 0){
            binding.tvBalanceExpense.text = 0.0.toString()
        }
        else {
            incomeLiveData.observe(this, Observer { listCost ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = Calendar.getInstance().time

                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val lastWeekDate = calendar.time
                for (income in listCost) {
                    val incomeDate = sdf.parse(income.date)
                    if (incomeDate in lastWeekDate..currentDate) {
                        balanceIncome += income.moneyCost
                    }
                }
                binding.tvBalanceIncome.text = "+ " + String.format("%.2f", balanceIncome)
            })
        }
    }

    @SuppressLint("SetTextI18n")
    fun getSumExpense(){
        val expenseLiveData = dataRepository.getExpenses()
        if (expenseLiveData.value?.size == 0){
            binding.tvBalanceExpense.text = 0.0.toString()
        }
        else {
            expenseLiveData.observe(this, Observer { listCost ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = Calendar.getInstance().time
                Log.d("DateSdf", sdf.toString())
                Log.d("DateCur", currentDate.toString())
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                Log.d("DateCalendar", calendar.time.toString())
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                Log.d("DateCalendarMinus7", calendar.toString())
                val lastWeekDate = calendar.time
                Log.d("DateLastWeek", lastWeekDate.toString())
                for (income in listCost) {
                    val incomeDate = sdf.parse(income.date)
                    Log.d("DateIncomeDate", incomeDate.toString())
                    if (incomeDate in lastWeekDate..currentDate) {
                        balanceExpense += income.moneyCost
                    }
                }
                binding.tvBalanceExpense.text = "- " + String.format("%.2f", balanceExpense)
            })
        }
    }
    fun navigateTo(nameActivity:String){
        when(nameActivity){
            "GoalsActivity"-> {
                intent = Intent(this@MainActivity, GoalsActivity::class.java)
                startActivity(intent)
            }
            "TipsActivity"-> {
                intent = Intent(this@MainActivity, TipsActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity"-> {
                intent = Intent(this@MainActivity, IncomeActivity::class.java)
                startActivity(intent)
            }
            "ExpenseActivity"-> {
                intent = Intent(this@MainActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity"-> {
                //Firebase.auth.signOut()
                intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
            }
        }

    }
}

 */