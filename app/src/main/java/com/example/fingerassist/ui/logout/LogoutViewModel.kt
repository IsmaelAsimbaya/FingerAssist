package com.example.fingerassist.ui.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogoutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Cerrando sesion..."
    }
    val text: LiveData<String> = _text
}