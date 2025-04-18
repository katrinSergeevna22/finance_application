package com.example.myfinanceapplication.view.cost

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.CostAdapter
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityExpensesBinding
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.model.utils.NavigationTitle
import com.example.myfinanceapplication.model.utils.getIntentForNavigation
import com.example.myfinanceapplication.model.utils.navigationForNavigationView
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.view_model.CostViewModel
import com.example.myfinanceapplication.view_model.ModeSorter
import kotlinx.coroutines.launch

class ExpensesActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesBinding
    private lateinit var adapter: CostAdapter
    lateinit var viewModel: CostViewModel

    private var mainTip = Tip()
    private var mainGoal = Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)

        lifecycle.coroutineScope.launch {
            viewModel.apply {
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
        val newBackgroundFragment = BackgroundFragment()
        val infoExpenseFragment = InfoExpenseFragment()
        val newAddExpenseFragment = AddExpenseFragment()

        binding.apply {
            toolbarGoal.setNavigationOnClickListener {
                drawerGoal.openDrawer(GravityCompat.START)
            }
            adapter = CostAdapter {
                viewModel.setSelectedCost(it)
                visibilityFilter(View.GONE)
                visibilitySearch(View.GONE)
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

            rcView.layoutManager = LinearLayoutManager(this@ExpensesActivity)
            rcView.adapter = adapter

            val itemDecoration =
                DividerItemDecoration(this@ExpensesActivity, LinearLayoutManager.VERTICAL)
            ContextCompat.getDrawable(
                this@ExpensesActivity,
                R.drawable.shape_indent_rcview
            )?.let {
                itemDecoration.setDrawable(
                    it
                )
            }
            rcView.addItemDecoration(itemDecoration)

            ibAddExpense.setOnClickListener {
                visibilityFilter(View.GONE)
                visibilitySearch(View.GONE)
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
                startActivity(
                    getIntentForNavigation(
                        context = this@ExpensesActivity,
                        nameActivity = NavigationTitle.GOALS_WITH_SELECT,
                        titleOfGoalsOrTips = mainGoal.titleOfGoal.toString()
                    )
                )
            }
            ibMainTip.setOnClickListener {
                startActivity(
                    getIntentForNavigation(
                        context = this@ExpensesActivity,
                        nameActivity = NavigationTitle.TIPS_WITH_SELECT,
                        titleOfGoalsOrTips = mainTip.title.toString()
                    )
                )
            }

            toolbarGoal.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_filter_category -> {
                        //item.isChecked = !item.isChecked
                        val menuItemSearchIsChecked = toolbarGoal.menu.getItem(4).isChecked
                        if (menuItemSearchIsChecked) {
                            toolbarGoal.menu.getItem(4).isChecked = false
                            visibilitySearch(View.GONE)
                        }
                        Log.d("katrin_menu_id", item.isChecked.toString())
                        if (item.isChecked) {
                            visibilityFilter(View.GONE)
                        } else {
                            visibilityFilter(View.VISIBLE)
                            fetchSpinnerCategory()
                        }
                    }

                    R.id.item_filter_ascending_sum -> {
                        val menuItemSearchIsChecked = toolbarGoal.menu.getItem(2).isChecked
                        if (menuItemSearchIsChecked) {
                            toolbarGoal.menu.getItem(2).isChecked = false
                            visibilitySearch(View.GONE)
                        }
                        if (item.isChecked) {
                            viewModel.resetFilters()
                        } else {
                            viewModel.sorter(ModeSorter.AscendingIncome)
                        }
                        item.isChecked = !item.isChecked
                    }

                    R.id.item_filter_descending_sum -> {
                        val menuItemSearchIsChecked = toolbarGoal.menu.getItem(1).isChecked
                        if (menuItemSearchIsChecked) {
                            toolbarGoal.menu.getItem(1).isChecked = false
                            visibilitySearch(View.GONE)
                        }
                        if (item.isChecked) {
                            viewModel.resetFilters()
                        } else {
                            viewModel.sorter(ModeSorter.DescendingIncome)
                        }
                        item.isChecked = !item.isChecked
                    }

                    R.id.item_filter_descending_date -> {
                        if (item.isChecked) {
                            viewModel.resetFilters()
                        } else {
                            viewModel.sorter(ModeSorter.DateIncome)
                        }
                        item.isChecked = !item.isChecked
                    }

                    R.id.item_search -> {
                        val menuItemSearchIsChecked = toolbarGoal.menu.getItem(0).isChecked
                        if (menuItemSearchIsChecked) {
                            toolbarGoal.menu.getItem(0).isChecked = false
                            visibilityFilter(View.GONE)
                        }
                        if (item.isChecked) {
                            visibilitySearch(View.GONE)
                        } else {
                            visibilitySearch(View.VISIBLE)
                        }
                    }
                }
                true
            }

            ibClose?.setOnClickListener {
                visibilityFilter(View.GONE)
                visibilitySearch(View.GONE)
            }

            ibSaveCategory?.setOnClickListener {
                if (toolbarGoal.menu.getItem(0).isChecked) {
                    val selectedItem = spinnerCategory?.selectedItem?.toString()
                    if (selectedItem != null) {
                        viewModel.filterByCategory(selectedItem)
                    } else {
                        Toast.makeText(
                            this@ExpensesActivity,
                            "Выберите категорию фильтрации или если хотите закрыть - нажмите крестик",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val text = etSearch?.text
                    if (!text.isNullOrEmpty()) {
                        viewModel.searchIncome(text.toString())
                    } else {
                        Toast.makeText(
                            this@ExpensesActivity,
                            "Введите текст в поисковую строку или если хотите закрыть - нажмите крестик",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                navigationView.setNavigationItemSelectedListener {
                    startActivity(
                        navigationForNavigationView(
                            context = this@ExpensesActivity,
                            itemId = it.itemId
                        )
                    )
                    true
                }
            }
        }
    }

    private fun fetchSpinnerCategory() {
        val categoriesList =
            resources.getStringArray(R.array.categoriesExpense).toMutableList()
        categoriesList.addAll(
            viewModel.getExpenseCategory()?.filter { !categoriesList.contains(it) }.let {
                if ((it?.size ?: 0) > 4) it?.subList(0, 4) else it
            } ?: listOf()
        )

        Log.d("katrin_category", categoriesList.toString())
        val adapter =
            ArrayAdapter(
                this@ExpensesActivity,
                android.R.layout.simple_spinner_item,
                categoriesList
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory?.adapter = adapter
    }

    private fun visibilitySearch(visibility: Int) {
        binding.apply {
            val oldIsChecked = toolbarGoal.menu.getItem(0).isChecked
            if ((oldIsChecked && visibility == View.GONE)
                || (!oldIsChecked && visibility == View.VISIBLE)
            ) {
                if (oldIsChecked) {
                    viewModel.resetFilters()
                    toolbarGoal.menu.getItem(0).isChecked = false
                } else if (!oldIsChecked) {
                    toolbarGoal.menu.getItem(0).isChecked = true
                }

                ivFilterCategory?.visibility = visibility
                tvTitleSearch?.visibility = visibility
                etSearch?.visibility = visibility
                ibClose?.visibility = visibility
                ibSaveCategory?.visibility = visibility
                tvSave?.visibility = visibility
            }
        }
    }

    private fun visibilityFilter(visibility: Int) {
        viewModel.resetFilters()
        binding.apply {
            val oldIsChecked = toolbarGoal.menu.getItem(0).isChecked
            toolbarGoal.menu.getItem(0).isChecked = !oldIsChecked
            ivFilterCategory?.visibility = visibility
            tvTitleCategory?.visibility = visibility
            spinnerCategory?.visibility = visibility
            ibClose?.visibility = visibility
            ibSaveCategory?.visibility = visibility
            tvSave?.visibility = visibility
            // toolbarGoal.menu.getItem(0).isChecked = visibility == View.VISIBLE
        }
    }

    lateinit var categoriesList: List<String>

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.balanceLiveData.observe(this, Observer { balance ->
            binding.tvBalanceExpense.text = balance.toString()
            Log.d("ShowBalance3", viewModel.getBalanceNow().toString())
            Log.d("ShowBalance4", balance.toString())
        })
        viewModel.getExpenseLiveData().observe(this) { expenseList ->
            categoriesList = expenseList.mapNotNull { it.category }
            adapter.submitList(expenseList)
            adapter.notifyDataSetChanged()
        }
        viewModel.getOneRandomGoalLiveData().observe(this) { goal ->
            mainGoal = goal
            binding.apply {
                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                } else {
                    tvMoneyGoal.text = goal.moneyGoal.toString()
                    val totalAmountToSave = goal.moneyGoal
                    val currentAmountSaved = goal.progressOfMoneyGoal

                    val progress =
                        (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                    progressBar.progress = progress
                }
            }
        }
        viewModel.getOneRandomTipLiveData().observe(this) { tip ->
            mainTip = tip
            binding.tvTitleTip.text = tip.title
        }
    }

    fun closeFragments() {
        supportFragmentManager.popBackStack()
        supportFragmentManager.popBackStack()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
