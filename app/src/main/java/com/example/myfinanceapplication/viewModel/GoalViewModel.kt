package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Tip

class GoalViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    private val goalsList = mutableListOf<Goal>()
    private val goalsLiveData = MutableLiveData<List<Goal>>()

    private var goalsCategoryList: MutableList<Goal> = mutableListOf()
    private var goalsCategoryLiveData = MutableLiveData<List<Goal>>()
    private var categoryGoal: String = "Active"
    private var oneRandomTipLiveData = MutableLiveData<Tip>()
    private val selectedCategory = MutableLiveData<String>()
    var fragmentIsOpen = false

    fun setSelectedCategory(category: String) {
        selectedCategory.value = category
        categoryGoal = category
    }

    fun getSelectedCategory(): LiveData<String> {
        return selectedCategory
    }

    fun getGoalsLiveData(): LiveData<List<Goal>> {
        return goalsLiveData
    }

    fun getGoalsCategoryLiveData(): LiveData<List<Goal>> {
        return goalsCategoryLiveData
    }

    fun getOneRandomTipLiveData(): LiveData<Tip> {
        return oneRandomTipLiveData
    }

    fun loadGoal() {
        dataRepository.getGoals().observeForever() { goals ->
            goalsList.clear()
            goalsList.addAll(goals)

            categoryGoal = selectedCategory.value ?: "Active"
            goalsCategoryList.clear()
            goalsCategoryList.addAll(goalsList.filter { it.status == categoryGoal }.toMutableList())

            goalsLiveData.value = goalsList
            goalsCategoryLiveData.value = goalsCategoryList
        }
    }

    fun loadGoalByCategory() {
        getSelectedCategory().observeForever { category ->
            // Фильтрация данных по категории и обновление RecyclerView
            goalsCategoryList = goalsList.filter { it.status == category }.toMutableList()
            goalsCategoryLiveData.value = goalsCategoryList
        }
    }

    fun loadOneRandomTipLiveData() {
        dataRepository.getOneTip("Goal").observeForever() { tip ->
            oneRandomTipLiveData.value = tip
        }
    }

    fun getTitleOfCategory(): String {
        categoryGoal = selectedCategory.value ?: "Active"
        when (categoryGoal) {
            "Active" -> return "Активные цели"
            "Deleted" -> return "Архивные цели"
            "Achieved" -> return "Достигнутые цели"
        }
        return "Цели"
    }

    fun deleteGoal() {
        dataRepository.deleteGoal(selectedGoal.value!!)
    }

    private val _selectedGoal = MutableLiveData<Goal>()
    val selectedGoal: LiveData<Goal> get() = _selectedGoal

    fun setSelectedGoal(goal: Goal) {
        _selectedGoal.value = goal
    }
}