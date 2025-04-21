package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddGoalViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    //private var goalTitleList = listOf<String>()
    var textOfToast = ""
    val viewModel = GoalViewModel()
    init {
        viewModel.loadGoal()
    }

    fun checkData(title : String, sum : String, category: String, comment: String) : Boolean{
        if (title.isNotEmpty() && sum.isNotEmpty() && category.isNotEmpty()) {
            if (!checkIsTitle(title)) {
                textOfToast = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sum)) {
                textOfToast = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25){
                textOfToast = "Слишком длинное название!"
                return false
            }
            val moneyGoal = sum.toLong()
            if (moneyGoal > 1000000000000L){
                textOfToast = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240){
                textOfToast = "Слишком длинный комментарий"
                return false
            }
            if (getGoalsList().contains(title)){
                textOfToast = "Цель с таким названием уже существует"
                return false
            }
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val goal = Goal("", title, moneyGoal, 0, date, category, comment, "Active")

            saveGoalToBase(goal)
            return true
        }
        textOfToast = "Заполните все поля"
        return false
    }
    fun saveGoalToBase(newGoal : Goal){
        dataRepository.writeGoalData(newGoal)
    }
    fun getGoalsList() : List<String>{
        return viewModel.getGoalsLiveData().value?.map { it.titleOfGoal!! } ?: listOf()
    }
    fun checkIsNumber(sum : String) : Boolean{
        return sum.matches(Regex("[0-9]+"))
    }
    fun checkIsTitle(sum : String) : Boolean{
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }
}