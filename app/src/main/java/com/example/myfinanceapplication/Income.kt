package com.example.myfinanceapplication

import java.util.Date

data class Income (
    val costId: Int,
    var titleOfCost: String?,
    var moneyCost: Long,
    var date: String?,
    var category: String?,
    var comment: String?,
)
