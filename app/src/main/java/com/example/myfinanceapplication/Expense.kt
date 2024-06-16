package com.example.myfinanceapplication

data class Expense(
    val costId: Int,
    var titleOfCost: String?,
    var moneyCost: Long,
    var progressOfMoneyCost: Long,
    var date: String?,
    var category: String?,
    var comment: String?,
)
