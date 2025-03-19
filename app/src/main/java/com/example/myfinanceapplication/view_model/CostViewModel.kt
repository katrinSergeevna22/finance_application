package com.example.myfinanceapplication.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.model.Cost

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

    //private var balanceLiveData = MutableLiveData<Double>()
    var balance = 0.0
    var itsEdit: Boolean = false
    val balanceLiveData = MutableLiveData<Double>()

    val goalsListLiveData = MutableLiveData<List<Goal>>()
    var goalsList = listOf<Goal>()

    init {
        getBalanceNow()
    }

    fun getBalanceNow(): Double {
        dataRepository.getUserBalance().observeForever() {
            balance = it
            balanceLiveData.value = it
            Log.d("GetBalance", balance.toString())
            Log.d("GetBalanceLiveData", balanceLiveData.value.toString())
            //binding.tvBalanceIncome.text = balance.toString()
        }
        return balance
    }

    fun updateBalance(newBalance: Double) {
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
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toLong()
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

    fun getExpenseCategory(): List<String> {
        val set = expensesList.mapNotNull { it.category }
        return set.toList()
    }

    fun filterByCategory(category: String) {
        val expensesListByCategory =
            expensesList?.toList()?.filter { it.category == category } ?: listOf()
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
        Log.d("SaveBalance", balance.toString())
        //val balance = 0.0//getBalance()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(newCost.moneyCost.toDouble() + balance)
    }

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome.get("moneyCost")
        if (newSum != selectIncome.moneyCost) {
            val diff = newSum.toString().toDouble() - selectIncome.moneyCost.toString().toDouble()
            //val balance = 0.0//getBalance()
            dataRepository.updateUserBalance(balance + diff)
        }
        selectIncome.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toLong()
        selectIncome.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        setSelectedCost(selectIncome)
        dataRepository.editIncomeToBase(newIncome, selectIncome)
    }

    fun deleteIncome() {
        //val balance = 0.0//getBalance()
        val sum = selectedCost.value?.moneyCost
        if (sum != null) {
            dataRepository.updateUserBalance(balance - sum)
            dataRepository.deleteIncome(selectedCost.value)
        }
    }

    fun saveExpenseToBase(newCost: Cost) {
        dataRepository.writeExpenseData(newCost)
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(balance - newCost.moneyCost.toDouble())
    }

    fun addProgressGoal(goal: Goal, sum: Long) {
        val newSumProgress = goal.progressOfMoneyGoal + sum
        var status = goal.status
        if (newSumProgress == goal.moneyGoal) status = "Achieved"
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

    fun minusProgressGoal(goal: Goal, sum: Long) {
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
        val balance = getBalanceNow()
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