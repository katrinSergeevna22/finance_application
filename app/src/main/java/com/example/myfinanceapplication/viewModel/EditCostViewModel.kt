package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Goal
import java.math.BigDecimal
import java.math.RoundingMode

class EditCostViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    val viewModel = CostViewModel()
    lateinit var selectCost: Cost
    lateinit var goalsMutableList: List<Goal>
    var answerException = ""

    fun checkIncomeData(
        title: String,
        sumCost: String,
        category: String,
        comment: String,
    ): Boolean {
        if (title.isNotEmpty() && sumCost.isNotEmpty() && category.isNotEmpty()) {
            val balance = viewModel.balanceLiveData.value?.toDouble() ?: 0.0
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            val sum = getInputValue(sumCost)
            if (sum > 1000000000000) {
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240) {
                answerException = "Слишком длинный комментарий"
                return false
            }

            if (sum < selectCost.moneyCost && selectCost.moneyCost - sum > balance) {
                answerException = "Недостаточно средств"
                return false
            } else {
                val newIncome = mapOf(
                    "costId" to selectCost.costId,
                    "titleOfCost" to title,
                    "moneyCost" to sum,
                    "date" to selectCost.date,
                    "category" to category,
                    "comment" to comment,
                    "isExpense" to false,
                )

                editIncomeToBase(newIncome, selectCost)
                return true
            }
        }
        answerException = "Заполните все поля"
        return false
    }

    private fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val balance = viewModel.getBalance()

        val newSum = newIncome["moneyCost"]
        if (newSum != selectIncome.moneyCost) {
            if (newSum.toString().toDouble() > selectIncome.moneyCost.toString().toDouble()) {
                val diff =
                    newSum.toString().toDouble() - selectIncome.moneyCost.toString().toDouble()
                dataRepository.updateUserBalance(balance + diff)
            } else {
                val diff =
                    selectIncome.moneyCost.toString().toDouble() - newSum.toString().toDouble()
                dataRepository.updateUserBalance(balance - diff)
            }
        }
        selectIncome.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toDouble()
        selectIncome.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        dataRepository.editIncomeToBase(newIncome, selectIncome)
    }

    private fun checkIsNumber(sum: String): Boolean {
        return sum.matches(Regex("[0-9.]+"))
    }

    private fun checkIsTitle(sum: String): Boolean {
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }

    fun checkExpenseData(
        title: String,
        sumCost: String,
        category: String,
        comment: String,
        titleOfGoal: String,
    ): Boolean {

        if (title.isNotEmpty() && sumCost.isNotEmpty() && category.isNotEmpty()) {
            val balance = viewModel.getBalance()
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            val sum = getInputValue(sumCost)
            if (sum > 1000000000000L) {
                answerException = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240) {
                answerException = "Слишком длинный комментарий"
                return false
            }

            if (sum > selectCost.moneyCost && sum - selectCost.moneyCost > balance) {
                answerException = "Недостаочно средст на балансе"
                return false
            }

            if (category == "Цель" && titleOfGoal.isBlank()) {
                answerException = "Выберите цель, если список целей пуст - " +
                        "добавьте новые активные цели"
                return false
            }

            if (selectCost.category == "Цель") {
                val goalOld = goalsMutableList.find { it.titleOfGoal == selectCost.goal }
                if (goalOld != null) {
                    if (category == "Цель") {
                        val goalNew = goalsMutableList.find { it.titleOfGoal == titleOfGoal }

                        if (goalNew != null) {
                            if (goalOld != goalNew) {
                                viewModel.minusProgressGoal(
                                    goalOld,
                                    selectCost.moneyCost
                                )
                                viewModel.addProgressGoal(
                                    goalNew,
                                    sum
                                )
                            } else {
                                if (selectCost.moneyCost > sum) {
                                    viewModel.minusProgressGoal(
                                        goalNew,
                                        selectCost.moneyCost - sum
                                    )
                                } else if (selectCost.moneyCost < sum) {
                                    if (goalNew.moneyGoal < sum
                                        + goalNew.progressOfMoneyGoal - selectCost.moneyCost
                                    ) {
                                        answerException =
                                            "Сумма больше, чем нужно для достижения цели"
                                        return false
                                    }
                                    viewModel.addProgressGoal(
                                        goalNew,
                                        sum - selectCost.moneyCost
                                    )
                                }
                            }
                        }
                    } else {
                        viewModel.minusProgressGoal(
                            goalOld,
                            selectCost.moneyCost
                        )
                    }
                }
            } else if (category == "Цель") {
                val goalNew = goalsMutableList.find { it.titleOfGoal == titleOfGoal }
                if (goalNew != null) {
                    viewModel.addProgressGoal(
                        goalNew,
                        sum
                    )
                }
            }
            val newExpense = mapOf(
                "costId" to selectCost.costId,
                "titleOfCost" to title,
                "moneyCost" to sum,
                "date" to selectCost.date,
                "category" to category,
                "goal" to titleOfGoal,
                "comment" to comment,
            )

            editExpenseToBase(newExpense, selectCost)
            return true

        } else {
            answerException = "Заполните все поля"
            return false
        }
    }

    private fun editExpenseToBase(newExpense: Map<String, Any>, selectExpense: Cost) {
        val newSum = newExpense["moneyCost"].toString().toDouble()
        val pastSum = selectExpense.moneyCost.toString().toDouble()
        val balance = viewModel.getBalance()

        if (newSum > pastSum) {
            dataRepository.updateUserBalance(balance - (newSum - pastSum))
        } else {
            dataRepository.updateUserBalance(balance + (pastSum - newSum))
        }

        selectExpense.titleOfCost = newExpense["titleOfCost"].toString()
        selectExpense.moneyCost = newExpense["moneyCost"].toString().toDouble()
        selectExpense.category = newExpense["category"].toString()
        selectExpense.comment = newExpense["comment"].toString()
        if (selectExpense.category == "Цель") selectExpense.goal = newExpense["goal"].toString()

        dataRepository.editExpenseToBase(newExpense, selectExpense)
    }

    private fun getInputValue(text: String): Double = try {
        val value = text.replace(",", ".").toDouble()
        // Округляем до 2 знаков после запятой
        BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    } catch (e: NumberFormatException) {
        0.00
    }
}