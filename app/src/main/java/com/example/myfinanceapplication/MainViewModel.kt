package com.example.myfinanceapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
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

    fun getBalance() : MutableLiveData<Double> {
        dataRepository.getUserBalance().observeForever { balance ->
            if (balance == null) {
                balanceLiveData.value = 0.0
            } else {
                balanceLiveData.value = balance
            }
        }
        return balanceLiveData
    }
    fun getOneRandomGoal() : MutableLiveData<Goal> {
        val oneRandomGoalLiveData = dataRepository.getOneGoal()
        oneRandomGoalLiveData.observeForever { goal ->
            goalLiveData.value = goal
        }
        return goalLiveData
    }
    fun getOneRandomTip() : MutableLiveData<Tip> {
        val oneRandomTipLiveData = dataRepository.getOneTip()
        oneRandomTipLiveData.observeForever { tip ->
            tipLiveData.value = tip
        }
        return tipLiveData
    }
    fun getSumIncome() : MutableLiveData<Double> {
        val incomeLiveData = dataRepository.getIncomes()
        if (incomeLiveData.value?.size == 0){
            sumIncomeLiveData.value = 0.0
        }
        else {
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
                //binding.tvBalanceIncome.text = "+ " + String.format("%.2f", balanceIncome)
            }
        }
        return sumIncomeLiveData
    }

    fun getSumExpense() : MutableLiveData<Double> {
        val expenseLiveData = dataRepository.getExpenses()
        if (expenseLiveData.value?.size == 0) {
            sumExpenseLiveData.value = 0.0
        } else {
            expenseLiveData.observeForever { listCost ->
                var balanceExpense = 0.0
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
                sumExpenseLiveData.value = balanceExpense
                //binding.tvBalanceExpense.text = "- " + String.format("%.2f", balanceExpense)
            }
        }
        return sumExpenseLiveData
    }
}