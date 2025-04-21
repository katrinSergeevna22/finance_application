package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfinanceapplication.model.DataRepository
import com.example.myfinanceapplication.model.Tip

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