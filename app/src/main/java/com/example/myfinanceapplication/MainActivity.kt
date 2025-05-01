package com.example.myfinanceapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.ActivityMainBinding
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.utils.NavigationTitle
import com.example.myfinanceapplication.utils.getIntentForNavigation
import com.example.myfinanceapplication.utils.navigationForNavigationView

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
            tvTitleMoney.text = getString(R.string.title_last_week)

            toolbarMain.setNavigationOnClickListener {
                binding.drawerMain.openDrawer(GravityCompat.START)
            }
            ibGoals.setOnClickListener {
                navigateTo(NavigationTitle.GOALS)
            }
            ibTips.setOnClickListener {
                navigateTo(NavigationTitle.TIPS)
            }
            ibIncome.setOnClickListener {
                navigateTo(NavigationTitle.INCOME)
            }
            ibExpense.setOnClickListener {
                navigateTo(NavigationTitle.EXPENSE)
            }
            ibMainTip.setOnClickListener {
                navigateTo(NavigationTitle.TIPS_WITH_SELECT, tvTitleTip.text.toString())
            }
            ibMainGoal.setOnClickListener {
                if (mainGoal == Goal()) {
                    navigateTo(NavigationTitle.GOALS)
                } else {
                    navigateTo(NavigationTitle.GOALS_WITH_SELECT, mainGoal.titleOfGoal.toString())
                }
            }
            navigationView.setNavigationItemSelectedListener {
                startActivity(
                    navigationForNavigationView(
                        context = this@MainActivity,
                        itemId = it.itemId
                    )
                )
                true
            }
        }

    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
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
            viewModel.getSumIncome().observe(this@MainActivity) { sumIncome ->
                tvBalanceIncome.text = "+ " + String.format("%.2f", sumIncome)
            }
            viewModel.getSumExpense().observe(this@MainActivity) { sumExpense ->
                tvBalanceExpense.text = "- " + String.format("%.2f", sumExpense)
            }
        }
    }

    private fun navigateTo(nameActivity: NavigationTitle, titleOfGoalsOrTips: String = "") {
        startActivity(
            getIntentForNavigation(
                context = this,
                nameActivity = nameActivity,
                titleOfGoalsOrTips = titleOfGoalsOrTips
            )
        )
    }
}
