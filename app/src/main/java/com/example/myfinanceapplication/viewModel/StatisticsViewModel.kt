package com.example.myfinanceapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatisticsViewModel : ViewModel() {
    private val dataRepository = DataRepository()

    private val incomesMutableLiveData = MutableLiveData<List<Cost>>()
    val incomesLiveData: LiveData<List<Cost>> = incomesMutableLiveData

    private val expensesMutableLiveData = MutableLiveData<List<Cost>>()
    val expensesLiveData: LiveData<List<Cost>> = expensesMutableLiveData

    private val goalsMutableLiveData = MutableLiveData<List<Goal>>()
    val goalsLiveData: LiveData<List<Goal>> = goalsMutableLiveData

    private val costsMutableLiveData = MutableLiveData<List<Cost>>()
    val costsLiveData: LiveData<List<Cost>> = costsMutableLiveData

    init {
        fetchEntityList()
        fetchListOfCost()
    }

    private fun fetchEntityList() {
        dataRepository.getIncomes().observeForever { incomes ->
            incomesMutableLiveData.value = incomes
            Log.d("katrin_stat_vm", incomes.toString())
        }

        dataRepository.getExpenses().observeForever { expenses ->
            expensesMutableLiveData.value = expenses
        }

        dataRepository.getGoals().observeForever { goals ->
            goalsMutableLiveData.value = goals
        }
    }

    fun getProgressOfGoals(): Map<String, Long> {
        val categoryList = listOf("Достигнутые цели", "Недостигнутые цели")
        val mutableMap: MutableMap<String, Long> = mutableMapOf()
        mutableMap[categoryList[0]] =
            goalsLiveData.value?.count { it.progressOfMoneyGoal == it.moneyGoal }?.toLong() ?: 0L
        mutableMap[categoryList[1]] =
            goalsLiveData.value?.count { it.progressOfMoneyGoal != it.moneyGoal }?.toLong() ?: 0L
        return mutableMap
    }

    private fun fetchListOfCost() {
        incomesLiveData.observeForever { incomes ->
            if(incomes != null) {
                val listOfCost = mutableListOf<Cost>()
                listOfCost.addAll(incomes)
                if (expensesLiveData.value != null){
                    listOfCost.addAll(expensesLiveData.value ?: listOf())
                }
                costsMutableLiveData.value = listOfCost.sortedBy { it.date }
            }
        }
    }
}