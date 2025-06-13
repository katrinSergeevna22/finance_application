package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddCostViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    var answerException = ""
    val viewModel = CostViewModel()

    fun checkDataIncome(
        title: String,
        sumCost: String,
        category: String,
        comment: String,
    ): Boolean {
        if (title.isNotEmpty() && sumCost.isNotEmpty() && category.isNotEmpty()) {
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            val sum = getInputValue(sumCost)
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            if (sum > 1000000000000L) {
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240) {
                answerException = "Слишком длинный комментарий"
                return false
            }
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val cost = Cost("", title, sum, date, category, comment, false)
            saveIncomeToBase(cost)
            return true
        }
        return false
    }

    private fun saveIncomeToBase(newCost: Cost) {
        val balance = viewModel.balanceLiveData.value?.toDouble() ?: 0.0
        dataRepository.updateUserBalance(newCost.moneyCost + balance)
        dataRepository.writeIncomeData(newCost)
    }

    fun checkDataExpense(
        title: String,
        sum: String,
        category: String,
        comment: String,
        selectGoal: Goal,
    ): Boolean {
        if (title.isNotEmpty() && sum.isNotEmpty() && category.isNotEmpty()) {
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sum)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            val sumCost = getInputValue(sum)
            if (sumCost > 1000000000000L) {
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240) {
                answerException = "Слишком длинный комментарий"
                return false
            }
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val titleOfGoal = selectGoal.titleOfGoal ?: ""
            val balance = viewModel.getBalance()
            if (sumCost > balance) {
                answerException = "Недостаочно средст на балансе"
                return false
            } else {
                if (category == "Цель") {
                    if (selectGoal != Goal()) {
                        val cost = Cost("", title, sumCost, date, category, titleOfGoal, comment)
                        if (selectGoal.moneyGoal < sumCost + selectGoal.progressOfMoneyGoal) {
                            answerException = "Сумма больше, чем нужно для достижения цели"
                            return false
                        } else {
                            viewModel.addProgressGoal(selectGoal, sumCost)
                            saveExpenseToBase(cost)
                            return true
                        }
                    } else {
                        answerException = "Выберите цель"
                        return false
                    }
                } else {
                    val cost = Cost("", title, sumCost, date, category, comment)
                    saveExpenseToBase(cost)
                    return true
                }
            }
        } else {
            // Обработка ошибок
            answerException = "Заполние все поля"
            return false
        }
    }

    // Функция для получения значения как Double
    private fun getInputValue(text: String): Double = try {
        val value = text.replace(",", ".").toDouble()
        // Округляем до 2 знаков после запятой
        BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    } catch (e: NumberFormatException) {
        0.00
    }

    private fun saveExpenseToBase(newCost: Cost) {
        val balance = viewModel.getBalance()
        dataRepository.updateUserBalance(balance - newCost.moneyCost)
        dataRepository.writeExpenseData(newCost)
    }

    private fun checkIsNumber(sum: String): Boolean {
        return sum.matches(Regex("[0-9.]+"))
    }

    private fun checkIsTitle(sum: String): Boolean {
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }
}