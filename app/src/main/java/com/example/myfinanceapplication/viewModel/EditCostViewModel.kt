package com.example.myfinanceapplication.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.model.Cost
import com.example.myfinanceapplication.model.DataRepository

class EditCostViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    val viewModel = CostViewModel()
    lateinit var selectCost: Cost
    var balance = 0.0
    lateinit var goalsMutableList: List<Goal>
    var answerException = ""

    fun checkIncomeData(
        title: String,
        sumCost: String,
        category: String,
        comment: String,
        balanceNow: Double
    ): Boolean {
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
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            val sum = sumCost.toLong()
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
                    "costId" to selectCost!!.costId,
                    "titleOfCost" to title,
                    "moneyCost" to sum,
                    "date" to selectCost!!.date,
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

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome.get("moneyCost")
        if (newSum != selectIncome.moneyCost) {
            var diff =
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
        selectIncome.moneyCost = newIncome["moneyCost"].toString().toLong()
        selectIncome.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        //setSelectedCost(selectIncome)
        dataRepository.editIncomeToBase(newIncome, selectIncome)
    }

    fun checkIsNumber(sum: String): Boolean {
        return sum.matches(Regex("[0-9]+"))
    }

    fun checkIsTitle(sum: String): Boolean {
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }

    fun checkExpenseData(
        title: String,
        sumCost: String,
        category: String,
        comment: String,
        titleOfGoal: String,
        balanceNow: Double
    ): Boolean {

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
            if (title.length > 25) {
                answerException = "Слишком длинное название!"
                return false
            }
            val sum = sumCost.toLong()
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
                //Toast.makeText((activity as ExpensesActivity), "Недостаочно средст", Toast.LENGTH_SHORT).show()
            }
            if (selectCost.category == "Цель") {
                Log.d("goalOld", selectCost.goal)
                Log.d("goalOldList", goalsMutableList.toString())
                val goalOld = goalsMutableList.filter { it.titleOfGoal == selectCost.goal }[0]
                if (category == "Цель") {
                    Log.d("goalOld", selectCost.goal)
                    val goalNew = goalsMutableList.filter { it.titleOfGoal == titleOfGoal }[0]
                    Log.d("EditGoalProgress", selectCost.moneyCost.toString())
                    Log.d("EditGoalProgressTitle", selectCost.goal + " " + titleOfGoal)

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
                            if (goalNew.moneyGoal < sum + goalNew.progressOfMoneyGoal - selectCost.moneyCost) {
                                answerException = "Сумма больше, чем нужно для достижения цели"

                                //etSum.setText((selectGoal.moneyGoal - selectGoal.progressOfMoneyGoal).toString())
                                return false
                            }
                            viewModel.addProgressGoal(
                                goalNew,
                                sum - selectCost.moneyCost
                            )
                        }
                    }
                } else {
                    viewModel.minusProgressGoal(
                        goalOld,
                        selectCost.moneyCost
                    )
                }
            } else if (category == "Цель") {
                val goalNew = goalsMutableList.filter { it.titleOfGoal == titleOfGoal }[0]
                viewModel.addProgressGoal(
                    goalNew,
                    sum
                )
            }
            val newExpense = mapOf(
                "costId" to selectCost!!.costId,
                "titleOfCost" to title,
                "moneyCost" to sum,
                "date" to selectCost!!.date,
                "category" to category,
                "goal" to titleOfGoal,
                "comment" to comment,
            )
            //selectIncome!!.titleOfCost = title
            //selectIncome!!.moneyCost = sum
            //selectIncome!!.category = category
            //viewModel.setSelectedCost(selectIncome!!)

            editExpenseToBase(newExpense, selectCost!!)
            return true

        } else {
            answerException = "Заполните все поля"
            return false
        }
    }

    fun editExpenseToBase(newExpense: Map<String, Any>, selectExpense: Cost) {
        val newSum = newExpense.get("moneyCost").toString().toDouble()
        val pastSum = selectExpense.moneyCost.toString().toDouble()
        //val balance = viewModel.getBalanceNow()

        if (newSum > pastSum) {
            dataRepository.updateUserBalance(balance - (newSum - pastSum))
        } else {
            dataRepository.updateUserBalance(balance + (pastSum - newSum))
        }

        selectExpense!!.titleOfCost = newExpense["titleOfCost"].toString()
        selectExpense!!.moneyCost = newExpense["moneyCost"].toString().toLong()
        selectExpense!!.category = newExpense["category"].toString()
        selectExpense.comment = newExpense["comment"].toString()
        if (selectExpense.category == "Цель") selectExpense.goal = newExpense["goal"].toString()
        //viewModel.setSelectedCost(selectExpense!!)
        dataRepository.editExpenseToBase(newExpense, selectExpense)


    }
}