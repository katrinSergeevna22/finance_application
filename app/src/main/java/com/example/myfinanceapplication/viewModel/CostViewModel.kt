package com.example.myfinanceapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip

class CostViewModel : ViewModel() {

    //var goalMutableList = mutableListOf<Goal>()
    private val dataRepository = DataRepository()
    private val _selectedCost = MutableLiveData<Cost>()
    val selectedCost: LiveData<Cost> get() = _selectedCost

    private val incomesList = mutableListOf<Cost>()
    private val incomesLiveData = MutableLiveData<List<Cost>>()
    private val expensesList = mutableListOf<Cost>()
    private val expensesLiveData = MutableLiveData<List<Cost>>()

    private var oneRandomTipLiveData = MutableLiveData<Tip>()
    private var oneRandomGoalLiveData = MutableLiveData<Goal>()

    private val balanceMutableLiveData = MutableLiveData<Double>()
    val balanceLiveData: LiveData<Double> = balanceMutableLiveData

    private val goalsListLiveData = MutableLiveData<List<Goal>>()
    private var goalsList = listOf<Goal>()

    init {
        fetchBalanceNow()
    }

    private fun fetchBalanceNow() {
        dataRepository.getUserBalance().observeForever {
            balanceMutableLiveData.value = it
            Log.d("GetBalanceLiveData", balanceLiveData.value.toString())
        }
    }

    fun getBalance() = balanceLiveData.value?.toDouble() ?: 0.0

    private fun updateBalance(newBalance: Double) {
        dataRepository.updateUserBalance(newBalance)
    }

    fun saveIncomeToBaseNew(newCost: Cost) {
        val currentBalance = balanceLiveData.value ?: 0.0
        val newBalance = currentBalance + newCost.moneyCost.toDouble()

        updateBalance(newBalance)
        dataRepository.writeIncomeData(newCost)
    }

    fun editIncomeToBaseNew(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome["moneyCost"] as Double
        val diff = newSum - selectIncome.moneyCost.toDouble()

        val currentBalance = balanceLiveData.value ?: 0.0
        val newBalance = currentBalance + diff

        updateBalance(newBalance)
        selectIncome.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toDouble()
        selectIncome.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        setSelectedCost(selectIncome)
        dataRepository.editIncomeToBase(newIncome, selectIncome)
    }

    fun deleteIncomeNew() {
        val currentBalance = balanceLiveData.value ?: 0.0
        val sum: Double = selectedCost.value?.moneyCost?.toDouble() ?: 0.0

        val newBalance = currentBalance - sum
        updateBalance(newBalance)
        dataRepository.deleteIncome(selectedCost.value)
    }


    fun setSelectedCost(cost: Cost) {
        _selectedCost.value = cost
    }

    fun getIncomeLiveData(): LiveData<List<Cost>> {
        return incomesLiveData
    }

    fun loadIncomes() {
        dataRepository.getIncomes().observeForever() { income ->
            incomesList.clear()
            incomesList.addAll(income)
            incomesLiveData.value = incomesList
        }
    }

    fun getExpenseLiveData(): LiveData<List<Cost>> {
        return expensesLiveData
    }

    fun loadExpenses() {
        dataRepository.getExpenses().observeForever() { expense ->
            expensesList.clear()
            expensesList.addAll(expense)
            expensesLiveData.value = expensesList
        }
    }

    fun getIncomeCategory(): List<String>? {
        val set = incomesList.mapNotNull { it.category }.toSet()
        Log.d("katrin_getExpense", expensesList.toString())
        Log.d("katrin_getExpense_category", set.toString())
        return if (set.isNotEmpty()) set.toList() else null
    }

    fun sorter(mode: ModeSorter) {
        when (mode) {
            ModeSorter.AscendingIncome -> incomesLiveData.value =
                incomesList.sortedByDescending { it.moneyCost }

            ModeSorter.DescendingIncome -> incomesLiveData.value =
                incomesList.sortedBy { it.moneyCost }

            ModeSorter.DateIncome -> incomesLiveData.value =
                incomesList.sortedBy { it.date }

            ModeSorter.AscendingExpense -> expensesLiveData.value =
                expensesList.sortedByDescending { it.moneyCost }

            ModeSorter.DescendingExpense -> expensesLiveData.value =
                expensesList.sortedBy { it.moneyCost }

            ModeSorter.DateExpense -> expensesLiveData.value =
                expensesList.sortedBy { it.date }
        }
    }

    fun searchIncome(pathOfTitle: String) {
        incomesLiveData.value = incomesList.filter {
            it.titleOfCost?.lowercase()?.contains(pathOfTitle.lowercase().trim()) == true
        }
    }

    fun searchExpense(pathOfTitle: String) {
        expensesLiveData.value = expensesList.filter {
            it.titleOfCost?.lowercase()?.contains(pathOfTitle.lowercase().trim()) == true
        }
    }

    fun filterByCategoryForIncome(category: String) {
        val incomeListByCategory =
            incomesList.toList().filter { it.category == category }
        incomesLiveData.value = incomeListByCategory
    }

    fun resetFilters() {
        incomesLiveData.value = incomesList
        expensesLiveData.value = expensesList
    }

    fun getExpenseCategory(): List<String>? {
        val set = expensesList.mapNotNull { it.category }.toSet()
        Log.d("katrin_getExpense", expensesList.toString())
        Log.d("katrin_getExpense_category", set.toString())
        return if (set.isNotEmpty()) set.toList() else null
    }

    fun filterByCategory(category: String) {
        val expensesListByCategory =
            expensesList.toList().filter { it.category == category }
        expensesLiveData.value = expensesListByCategory
    }

    fun getOneRandomTipLiveData(): LiveData<Tip> {
        return oneRandomTipLiveData
    }

    fun loadOneRandomTipLiveData(category: String) {
        dataRepository.getOneTip(category).observeForever() { tip ->
            oneRandomTipLiveData.value = tip
        }
    }

    fun getOneRandomGoalLiveData(): LiveData<Goal> {
        return oneRandomGoalLiveData
    }

    fun loadOneRandomGoalLiveData() {
        dataRepository.getOneGoal().observeForever() { goal ->
            oneRandomGoalLiveData.value = goal
        }
    }

    fun saveIncomeToBase(newCost: Cost) {
        dataRepository.writeIncomeData(newCost)
        dataRepository.updateUserBalance(newCost.moneyCost.toDouble() + getBalance())
    }

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome.get("moneyCost")
        if (newSum != selectIncome.moneyCost) {
            val diff = newSum.toString().toDouble() - selectIncome.moneyCost.toString().toDouble()
            //val balance = 0.0//getBalance()
            dataRepository.updateUserBalance(getBalance() + diff)
        }
        selectIncome.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toDouble()
        selectIncome.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        setSelectedCost(selectIncome)
        dataRepository.editIncomeToBase(newIncome, selectIncome)
    }

    fun deleteIncome() {
        val sum = selectedCost.value?.moneyCost
        if (sum != null) {
            dataRepository.updateUserBalance(getBalance() - sum)
            dataRepository.deleteIncome(selectedCost.value)
        }
    }

    fun saveExpenseToBase(newCost: Cost) {
        dataRepository.writeExpenseData(newCost)
        Log.d("Balance", getBalance().toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        dataRepository.updateUserBalance(getBalance() - newCost.moneyCost.toDouble())
    }

    fun addProgressGoal(goal: Goal, sum: Double) {
        val newSumProgress = goal.progressOfMoneyGoal + sum
        var status = goal.status
        if (newSumProgress.toLong() == goal.moneyGoal) status = "Achieved"
        val newGoalData = mapOf(
            "goalId" to goal.goalId,
            "titleOfGoal" to goal.titleOfGoal.toString(),
            "moneyGoal" to goal.moneyGoal,
            "progressOfMoneyGoal" to newSumProgress,
            "date" to goal.date,
            "category" to goal.category.toString(),
            "comment" to goal.comment.toString(),
            "status" to status.toString(),
        )
        dataRepository.editGoalToBase(newGoalData, goal)
    }

    fun minusProgressGoal(goal: Goal, sum: Double) {
        val newSumProgress = goal.progressOfMoneyGoal - sum
        var status = goal.status
        //if (newSumProgress == goal.moneyGoal) status = "Achived"
        if (status == "Achieved" && newSumProgress < goal.moneyGoal) status = "Active"
        val newGoalData = mapOf(
            "goalId" to goal.goalId,
            "titleOfGoal" to goal.titleOfGoal.toString(),
            "moneyGoal" to goal.moneyGoal,
            "progressOfMoneyGoal" to newSumProgress,
            "date" to goal.date,
            "category" to goal.category.toString(),
            "comment" to goal.comment.toString(),
            "status" to status.toString(),
        )
        dataRepository.editGoalToBase(newGoalData, goal)
    }

    fun deleteExpense() {
        val balance = balanceLiveData.value?.toDouble() ?: 0.0
        val selectExpense = selectedCost.value
        if (selectExpense != null) {
            val sum = selectExpense.moneyCost

            dataRepository.updateUserBalance(balance + sum)
            dataRepository.deleteExpense(selectedCost.value)
        }
    }

    fun loadGoals() {
        dataRepository.getGoals().observeForever { goalsItems ->
            Log.d("goalForSpinner-2", goalsItems.toString())
            goalsList = goalsItems
            Log.d("goalForSpinner-1", goalsItems.filter { it.status == "Active" }.toString())
            goalsListLiveData.value = goalsItems
            Log.d("goalForSpinner-1", goalsListLiveData.value.toString())
        }
    }

    fun getGoalsLivaData(): MutableLiveData<List<Goal>> {
        return goalsListLiveData
    }
}

enum class ModeSorter {
    AscendingIncome, DescendingIncome, DateIncome, AscendingExpense, DescendingExpense, DateExpense
}