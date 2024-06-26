package com.example.tw_okta_and

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyViewModel : ViewModel() {
    private val _message = MutableStateFlow("Hello World!")
    val message = _message.asStateFlow()

    private val _logs = MutableStateFlow("")
    val logs = _logs.asStateFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun addLog(log: String) {
        _logs.value += "\n$log"
    }
}