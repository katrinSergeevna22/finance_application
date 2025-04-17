package com.example.myfinanceapplication.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Cost
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddCostViewModel  : ViewModel() {
    private val dataRepository = DataRepository()
    var balance = 0.0
    var answerException = ""
    val viewModel = CostViewModel()
    val goalsListLiveData = MutableLiveData<List<Goal>>()
    var goalsList = listOf<Goal>()
    var sumNeedToGoal = 0L
    init {
        //loadActiveGoals()
    }
    fun checkDataIncome(title : String, sumCost : String, category: String, comment: String, balanceNow : Double) : Boolean{
        if (title.isNotEmpty() && sumCost.isNotEmpty() && category.isNotEmpty()) {
            balance = balanceNow
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            val sum = sumCost.toLong()
            if (title.length > 25){
                answerException = "Слишком длинное название!"
                return false
            }
            if (sum > 1000000000000L){
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240){
                answerException = "Слишком длинный комментарий"
                return false
            }
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val cost = Cost("", title, sum, date, category, comment, false)
            balance = balanceNow
            saveIncomeToBase(cost)
            return true
        }
        return false
    }
    private fun saveIncomeToBase(newCost: Cost){
        dataRepository.writeIncomeData(newCost)
        Log.d("SaveBalance", balance.toString())
        //val balance = 0.0//getBalance()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(newCost.moneyCost.toDouble() + balance)
    }

    fun checkDataExpense(title : String, sum : String, category: String, comment: String, selectGoal : Goal, balanceNow : Double) : Boolean{
        if (title.isNotEmpty() && sum.isNotEmpty() && category.isNotEmpty()) {
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sum)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25){
                answerException = "Слишком длинное название!"
                return false
            }
            val sumCost = sum.toLong()
            if (sumCost > 1000000000000L){
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240){
                answerException = "Слишком длинный комментарий"
                return false
            }
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val titleOfGoal = selectGoal.titleOfGoal!!
            balance = balanceNow
            if (sumCost > balance){
                answerException = "Недостаочно средст на балансе"
                return false
                //Toast.makeText((activity as ExpensesActivity), "Недостаочно средст", Toast.LENGTH_SHORT).show()
            }
            else {
                if (category == "Цель") {
                    if (selectGoal != Goal()) {
                        val cost = Cost("", title, sumCost, date, category, titleOfGoal, comment)
                        if (selectGoal.moneyGoal < sumCost + selectGoal.progressOfMoneyGoal){
                            answerException = "Сумма больше, чем нужно для достижения цели"

                            //etSum.setText((selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal).toString())
                            return false
                        }
                        else {
                            viewModel.addProgressGoal(selectGoal, sumCost)
                            saveExpenseToBase(cost)
                            //parentFragmentManager.popBackStack()
                            return true
                        }
                    } else {
                        answerException = "Выберите цель"
                        return false
                    }
                } else {
                    val cost = Cost("", title, sumCost, date, category, comment)
                    saveExpenseToBase(cost)
                    //parentFragmentManager.popBackStack()
                    return true
                }
            }
        } else {
            // Обработка ошибок
            answerException = "Заполние все поля"
            return false
        }
    }
    private fun saveExpenseToBase(newCost: Cost){
        dataRepository.writeExpenseData(newCost)
        //val balance = viewModel.getBalanceNow()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(balance - newCost.moneyCost.toDouble())

    }
    private fun checkIsNumber(sum : String) : Boolean{
        return sum.matches(Regex("[0-9]+"))
    }
    private fun checkIsTitle(sum : String) : Boolean{
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }
}