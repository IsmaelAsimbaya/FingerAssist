package com.example.fingerassist.ui.ajustes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AjustesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Ajustes Fragment"
    }
    val text: LiveData<String> = _text
}