package com.example.fingerassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fingerassist.R
import com.example.fingerassist.databinding.ActivityLoginBinding
import com.example.fingerassist.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup
        setup()
    }

    private fun setup(){
        title = "Autenticacion"
        

    }
}