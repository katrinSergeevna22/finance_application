package com.example.myfinanceapplication.model

data class Goal(
    var goalId: String,
    var titleOfGoal: String?,
    var moneyGoal: Double,
    var progressOfMoneyGoal: Double,
    var date: String,
    var category: String?,
    var comment: String?,
    var status: String? = "Active",
)
{
    constructor() : this("", "", 0.00, 0.00, "", "", "", "")
}


