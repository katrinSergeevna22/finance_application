package com.example.myfinanceapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.ActivityMainBinding
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.view.AuthActivity
import com.example.myfinanceapplication.view.cost.ExpensesActivity
import com.example.myfinanceapplication.view.goals.GoalsActivity
import com.example.myfinanceapplication.view.cost.IncomeActivity
import com.example.myfinanceapplication.view.tips.TipsActivity
import com.example.myfinanceapplication.view_model.AuthViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var mainTip: Tip
    private lateinit var mainGoal: Goal

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
                if (mainGoal == Goal()){
                    navigateTo("GoalsActivity")
                }
                else {
                    navigateTo("GoalsActivityWithSelect")
                }
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

                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                    tvProgressMoney.text = ""
                    tvDate.text = ""
                } else {
                    mainGoal = goal
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
