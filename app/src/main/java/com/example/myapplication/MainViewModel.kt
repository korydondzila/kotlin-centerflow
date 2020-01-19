package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {
    val numbers: MutableLiveData<List<Int>> = MutableLiveData(emptyList())

    fun addNumber() {
        val oldNumbers = numbers.value!!.toMutableList()
        val lastNumber = oldNumbers.lastOrNull()

        if (lastNumber != null) {
            oldNumbers.add(lastNumber + 1)
        } else {
            oldNumbers.add(1)
        }

        numbers.value = oldNumbers
    }
}
