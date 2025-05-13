package com.example.phonedialer.ui.dialer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialerViewModel : ViewModel() {
    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    init {
        _phoneNumber.value = ""
    }

    fun appendDigit(digit: String) {
        _phoneNumber.value = _phoneNumber.value + digit
    }

    fun removeLastDigit() {
        _phoneNumber.value = _phoneNumber.value?.dropLast(1) ?: ""
    }

    fun clearNumber() {
        _phoneNumber.value = ""
    }

    fun setPhoneNumber(number: String) {
        _phoneNumber.value = number
    }
}
