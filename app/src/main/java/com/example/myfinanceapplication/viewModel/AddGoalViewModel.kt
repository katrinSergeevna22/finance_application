package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddGoalViewModel : ViewModel() {
    private val dataRepository = DataRepository()
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
            val moneyGoal = getInputValue(sum)
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
            val goal = Goal("", title, moneyGoal, 0.00, date, category, comment, "Active")

            saveGoalToBase(goal)
            return true
        }
        textOfToast = "Заполните все поля"
        return false
    }

    private fun saveGoalToBase(newGoal : Goal){
        dataRepository.writeGoalData(newGoal)
    }

    private fun getGoalsList() : List<String>{
        return viewModel.getGoalsLiveData().value?.map { it.titleOfGoal!! } ?: listOf()
    }

    private fun checkIsNumber(sum : String) : Boolean{
        return sum.matches(Regex("[0-9.]+"))
    }

    private fun checkIsTitle(sum : String) : Boolean{
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }

    private fun getInputValue(text: String): Double = try {
        val value = text.replace(",", ".").toDouble()
        // Округляем до 2 знаков после запятой
        BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    } catch (e: NumberFormatException) {
        0.00
    }
}