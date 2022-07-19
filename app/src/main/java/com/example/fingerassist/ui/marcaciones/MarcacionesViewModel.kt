package com.example.fingerassist.ui.marcaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MarcacionesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Marcaciones Fragment"
    }
    val text: LiveData<String> = _text
}