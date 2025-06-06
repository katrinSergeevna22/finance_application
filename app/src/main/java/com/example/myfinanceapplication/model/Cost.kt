package com.example.myfinanceapplication.model

data class Cost(
    var costId: String,
    var titleOfCost: String?,
    var moneyCost: Double,
    var date: String,
    var category: String?,
    var comment: String?,
    var isExpense: Boolean = true,
)
{
    constructor() : this("", "", 0.00, "", "", "")
    var goal: String = ""
    constructor(
        costId: String,
        titleOfCost: String?,
        moneyCost: Double,
        date: String,
        category: String?,
        goal: String,
        comment: String?,
    ): this(costId, titleOfCost, moneyCost, date, category, comment){
        this.goal = goal
    }
}