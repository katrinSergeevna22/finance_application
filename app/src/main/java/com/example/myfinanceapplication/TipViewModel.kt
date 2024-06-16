package com.example.myfinanceapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TipViewModel : ViewModel() {
    private val dataRepository = DataRepository()
    private val tipsList = mutableListOf<Tip>()
    private val tipsLiveData = MutableLiveData<List<Tip>>()

    fun getTipsLiveData(): LiveData<List<Tip>> {
        return tipsLiveData
    }

    fun loadFinancialAdvices() {
        dataRepository.getFinancialAdvices().observeForever() { tips ->
            tipsList.clear()
            tipsList.addAll(tips)
            tipsLiveData.value = tipsList
        }
    }
}