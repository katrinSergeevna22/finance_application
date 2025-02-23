package com.example.myfinanceapplication.model

data class Tip(
    var tipId: String,
    var title: String?,
    var text: String?,
    var theme: String?,
)
{
    constructor() : this("", "",  "", "")
}