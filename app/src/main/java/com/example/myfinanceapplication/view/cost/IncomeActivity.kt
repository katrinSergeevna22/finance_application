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
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapplication.view.cost.adapterCost.CostAdapter
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityIncomeBinding
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.utils.NavigationTitle
import com.example.myfinanceapplication.utils.getIntentForNavigation
import com.example.myfinanceapplication.utils.navigationForNavigationView
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.CostViewModel
import com.example.myfinanceapplication.viewModel.ModeSorter
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.view.get


class IncomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityIncomeBinding
    lateinit var viewModel: CostViewModel
    private lateinit var adapter: CostAdapter
    private var mainTip = Tip()
    private var mainGoal = Goal()
    var categoriesList: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CostViewModel::class.java]

        lifecycle.coroutineScope.launch {
            viewModel.apply {
                loadIncomes()
                loadOneRandomTipLiveData("Income")
                loadOneRandomGoalLiveData()
            }
        }
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val newBackgroundFragment = BackgroundFragment.newInstance()
        val infoIncomeFragment = InfoIncomeFragment.newInstance()
        val newAddIncomeFragment = AddIncomeFragment.newInstance()

        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener {
                    drawerGoal.openDrawer(GravityCompat.START)
                }

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.item_filter_category -> {
                            //item.isChecked = !item.isChecked
//                            val menuItemSearchIsChecked = toolbar.menu.getItem(4).isChecked
//                            if (menuItemSearchIsChecked) {
//                                toolbar.menu.getItem(4).isChecked = false
//                                visibilitySearch(View.GONE)
//                            }
                            Log.d("katrin_menu_id", item.isChecked.toString())
                            if (item.isChecked) {
                                visibilityFilter(View.GONE)
                            } else {
                                if (isCheckedMenuItem()) unselectedMenuItem()
                                visibilityFilter(View.VISIBLE)
                                fetchSpinnerCategory()
                            }
                        }

                        R.id.item_filter_ascending_sum -> {
//                            val menuItemSearchIsChecked = toolbar.menu.getItem(2).isChecked
//                            if (menuItemSearchIsChecked) {
//                                toolbar.menu.getItem(2).isChecked = false
//                                visibilitySearch(View.GONE)
//                            }
                            if (item.isChecked) {
                                viewModel.resetFilters()
                            } else {
                                if (isCheckedMenuItem()) unselectedMenuItem()
                                viewModel.sorter(ModeSorter.AscendingIncome)
                            }
                            item.isChecked = !item.isChecked
                        }

                        R.id.item_filter_descending_sum -> {
//                            val menuItemSearchIsChecked = toolbar.menu.getItem(1).isChecked
//                            if (menuItemSearchIsChecked) {
//                                toolbar.menu.getItem(1).isChecked = false
//                                visibilitySearch(View.GONE)
//                            }
                            if (item.isChecked) {
                                viewModel.resetFilters()
                            } else {
                                if (isCheckedMenuItem()) unselectedMenuItem()
                                viewModel.sorter(ModeSorter.DescendingIncome)
                            }
                            item.isChecked = !item.isChecked
                        }

                        R.id.item_filter_descending_date -> {
                            if (item.isChecked) {
                                viewModel.resetFilters()
                            } else {
                                if (isCheckedMenuItem()) unselectedMenuItem()
                                viewModel.sorter(ModeSorter.DateIncome)
                            }
                            item.isChecked = !item.isChecked
                        }

                        R.id.item_search -> {
//                            val menuItemSearchIsChecked = toolbar.menu.getItem(0).isChecked
//                            if (menuItemSearchIsChecked) {
//                                toolbar.menu.getItem(0).isChecked = false
//                                visibilityFilter(View.GONE)
//                            }
                            if (item.isChecked) {
                                visibilitySearch(View.GONE)
                            } else {
                                if (isCheckedMenuItem()) unselectedMenuItem()
                                visibilitySearch(View.VISIBLE)
                            }
                        }
                    }
                    true
                }
            }

            ibClose?.setOnClickListener {
                visibilityFilter(View.GONE)
                visibilitySearch(View.GONE)
            }

            ibSaveCategory?.setOnClickListener {
                if (toolbar.menu[0].isChecked) {
                    val selectedItem = spinnerCategory?.selectedItem?.toString()
                    if (selectedItem != null) {
                        viewModel.filterByCategoryForIncome(selectedItem)
                    } else {
                        Toast.makeText(
                            this@IncomeActivity,
                            getString(R.string.cost_toast_text_empty_filter),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val text = etSearch?.text
                    if (!text.isNullOrEmpty()) {
                        viewModel.searchIncome(text.toString())
                    } else {
                        Toast.makeText(
                            this@IncomeActivity,
                            getString(R.string.cost_toast_text_empty_search),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
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
                    .replace(R.id.place_holder_infoIncomeFragment, infoIncomeFragment)
                    .addToBackStack(null)
                    .commit()
            }

            rcView.layoutManager = LinearLayoutManager(this@IncomeActivity)
            rcView.adapter = adapter

            val itemDecoration =
                DividerItemDecoration(this@IncomeActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this@IncomeActivity,
                    R.drawable.shape_indent_rcview
                )!!
            )
            rcView.addItemDecoration(itemDecoration)

            ibAddIncome.setOnClickListener {
                visibilityFilter(View.GONE)
                visibilitySearch(View.GONE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.place_holder_addIncomeFragment, newAddIncomeFragment)
                    .addToBackStack(null)
                    .commit()

            }
            ibMainGoal.setOnClickListener {
                startActivity(
                    getIntentForNavigation(
                        context = this@IncomeActivity,
                        nameActivity = NavigationTitle.GOALS_WITH_SELECT,
                        titleOfGoalsOrTips = mainGoal.titleOfGoal.toString()
                    )
                )
            }
            ibMainTip.setOnClickListener {
                startActivity(
                    getIntentForNavigation(
                        context = this@IncomeActivity,
                        nameActivity = NavigationTitle.TIPS_WITH_SELECT,
                        titleOfGoalsOrTips = mainTip.title.toString()
                    )
                )
            }
            navigationView.setNavigationItemSelectedListener {
                startActivity(
                    navigationForNavigationView(
                        context = this@IncomeActivity,
                        itemId = it.itemId
                    )
                )
                true
            }
        }
    }

    private fun fetchSpinnerCategory() {
        val categoriesList =
            resources.getStringArray(R.array.categoriesIncome).toMutableList()
        categoriesList.addAll(
            viewModel.getIncomeCategory()?.filter { !categoriesList.contains(it) }.let {
                if ((it?.size ?: 0) > 4) it?.subList(0, 4) else it
            } ?: listOf()
        )

        Log.d("katrin_category", categoriesList.toString())
        val adapter =
            ArrayAdapter(
                this@IncomeActivity,
                android.R.layout.simple_spinner_item,
                categoriesList
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory?.adapter = adapter
    }

    private fun visibilitySearch(visibility: Int) {
        binding.apply {

            if (visibility == View.GONE) {
                viewModel.resetFilters()
                toolbar.menu.getItem(4).isChecked = false
            } else {
                toolbar.menu.getItem(4).isChecked = true
            }

            ivFilterCategory?.visibility = visibility
            tvTitleSearch?.visibility = visibility
            etSearch?.visibility = visibility
            ibClose?.visibility = visibility
            ibSaveCategory?.visibility = visibility
            tvSave?.visibility = visibility
        }
    }


    private fun visibilityFilter(visibility: Int) {
        binding.apply {

            if (visibility == View.GONE) {
                viewModel.resetFilters()
                toolbar.menu.getItem(0).isChecked = false
            } else {
                toolbar.menu.getItem(0).isChecked = true
            }
            ivFilterCategory?.visibility = visibility
            tvTitleCategory?.visibility = visibility
            spinnerCategory?.visibility = visibility
            ibClose?.visibility = visibility
            ibSaveCategory?.visibility = visibility
            tvSave?.visibility = visibility
        }
    }

    private fun isCheckedMenuItem(): Boolean {
        var isChecked = false
        binding.toolbar.menu.forEach { isChecked = it.isChecked.or(isChecked) }
        return isChecked
    }

    private fun unselectedMenuItem() {
        binding.toolbar.menu.forEach { it.isChecked = false }
        viewModel.resetFilters()
        visibilityFilter(View.GONE)
        visibilitySearch(View.GONE)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.apply {
            balanceLiveData.observe(this@IncomeActivity, Observer { balance ->
                binding.tvBalanceIncome.text = formatBalance(balance)
            })
            getIncomeLiveData().observe(this@IncomeActivity) { incomeList ->
                categoriesList = incomeList.mapNotNull { it.category }
                adapter.submitList(incomeList)
                adapter.notifyDataSetChanged()
            }
            getOneRandomGoalLiveData().observe(this@IncomeActivity) { goal ->
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
            getOneRandomTipLiveData().observe(this@IncomeActivity) { tip ->
                mainTip = tip
                binding.tvTitleTip.text = tip.title
            }
        }
    }

    private fun formatBalance(value: Double): String {
        val balance = if (value == value.toInt().toDouble()) {
            // Если число целое - показываем без десятичной части
            value.toInt().toString()
        } else {
            // Если дробное - показываем 2 знака после запятой
            "%.2f".format(Locale.US, value).replace(",", ".")
        }
        return balance
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


