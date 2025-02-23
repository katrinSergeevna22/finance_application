package com.example.myfinanceapplication.view.cost

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
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.MainActivity
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.databinding.ActivityExpensesBinding
import com.example.myfinanceapplication.view.AuthActivity
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.view.goals.GoalsActivity
import com.example.myfinanceapplication.view.tips.TipsActivity
import com.example.myfinanceapplication.view_model.AuthViewModel
import com.example.myfinanceapplication.view_model.CostViewModel
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
