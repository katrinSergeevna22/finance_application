package com.example.myfinanceapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    private val balanceLiveData = MutableLiveData<Double>()
    private val sumIncomeLiveData = MutableLiveData<Double>()
    private val sumExpenseLiveData = MutableLiveData<Double>()
    private val goalLiveData = MutableLiveData<Goal>()
    private val tipLiveData = MutableLiveData<Tip>()

    fun getBalance(): MutableLiveData<Double> {
        dataRepository.getUserBalance().observeForever { balance ->
            if (balance == null) {
                balanceLiveData.value = 0.0
            } else {
                balanceLiveData.value = balance
            }
        }
        return balanceLiveData
    }

    fun getOneRandomGoal(): MutableLiveData<Goal> {
        val oneRandomGoalLiveData = dataRepository.getOneGoal()
        oneRandomGoalLiveData.observeForever { goal ->
            goalLiveData.value = goal
        }
        return goalLiveData
    }

    fun getOneRandomTip(): MutableLiveData<Tip> {
        val oneRandomTipLiveData = dataRepository.getOneTip()
        oneRandomTipLiveData.observeForever { tip ->
            tipLiveData.value = tip
        }
        return tipLiveData
    }

    fun getSumIncome(): MutableLiveData<Double> {
        val incomeLiveData = dataRepository.getIncomes()
        if (incomeLiveData.value?.size == 0) {
            sumIncomeLiveData.value = 0.0
        } else {
            incomeLiveData.observeForever { listCost ->
                var balanceIncome = 0.0
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
                sumIncomeLiveData.value = balanceIncome
            }
        }
        return sumIncomeLiveData
    }

    fun getSumExpense(): MutableLiveData<Double> {
        val expenseLiveData = dataRepository.getExpenses()
        if (expenseLiveData.value?.size == 0) {
            sumExpenseLiveData.value = 0.0
        } else {
            expenseLiveData.observeForever { listCost ->
                var balanceExpense = 0.0
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = Calendar.getInstance().time
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val lastWeekDate = calendar.time
                for (income in listCost) {
                    val incomeDate = sdf.parse(income.date)
                    if (incomeDate in lastWeekDate..currentDate) {
                        balanceExpense += income.moneyCost
                    }
                }
                sumExpenseLiveData.value = balanceExpense
            }
        }
        return sumExpenseLiveData
    }
}