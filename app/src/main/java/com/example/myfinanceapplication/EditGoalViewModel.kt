package com.example.myfinanceapplication

import androidx.lifecycle.ViewModel

class EditGoalViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    lateinit var selectGoal : Goal
    var exception = "Заполните все поля"
    val viewModel = GoalViewModel()
    init {
        viewModel.loadGoal()
    }
    fun checkData(title : String, sumCost : String, category: String, comment: String) : Boolean{
        if (title.isNotEmpty() && sumCost.isNotEmpty() && category.isNotEmpty()) {
            if (!checkIsTitle(title)) {
                exception = "Некорректный ввод названия!"
                return false
            }
            if (!checkIsNumber(sumCost)) {
                exception = "Некорректный ввод суммы!"
                return false
            }
            if (title.length > 25){
                exception = "Слишком длинное название!"
                return false
            }
            val sum = sumCost.toLong()
            if (sum > 1000000000000L){
                exception = "Укажите реальную сумму"
                return false
            }
            if (comment.length > 240){
                exception = "Слишком длинный комментарий"
                return false
            }
            if (title != selectGoal.titleOfGoal && getGoalsList().contains(title)){
                exception = "Цель с таким названием уже существует"
                return false
            }
            if (sum != selectGoal.moneyGoal) {
                if (sum < selectGoal.progressOfMoneyGoal) {
                    exception =
                        "Сумма, которую нужно накопить не может быть меньше накопленной суммы"
                    return false
                }
                if (sum == selectGoal.progressOfMoneyGoal){
                    selectGoal.status = "Achieved"
                }
                if (selectGoal.status == "Achieved" && sum > selectGoal.moneyGoal) {
                    selectGoal.status = "Active"
                }
            }
            val newGoalData = mapOf(
                "goalId" to selectGoal.goalId,
                "titleOfGoal" to  title,
                "moneyGoal" to sum,
                "progressOfMoneyGoal" to selectGoal.progressOfMoneyGoal,
                "date" to selectGoal.date,
                "category" to category,
                "comment" to comment,
                "status" to selectGoal.status.toString(),
            )
            selectGoal.titleOfGoal = title
            selectGoal.moneyGoal = sum
            selectGoal.category = category
            selectGoal.comment = comment
            editGoalToBase(newGoalData, selectGoal)
            return true
        }
        return false
    }
    fun editGoalToBase(newGoal: Map<String, Any>, selectGoal: Goal) {
        dataRepository.editGoalToBase(newGoal, selectGoal)
    }
    fun getGoalsList() : List<String>{
        return viewModel.getGoalsLiveData().value?.map { it.titleOfGoal!! } ?: listOf()
    }
    fun checkIsNumber(sum : String) : Boolean{
        return sum.matches(Regex("[0-9.]+"))
    }
    fun checkIsTitle(sum : String) : Boolean{
        return sum.matches(Regex("[a-zA-Zа-яА-Я0-9.,\\s]+"))
    }
}