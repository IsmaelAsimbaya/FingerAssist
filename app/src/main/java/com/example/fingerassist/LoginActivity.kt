package com.example.fingerassist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.Utils.Preferences
import com.example.fingerassist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        checkLogin()
        setup()
    }

    private fun setup() {
        title = "Autenticacion"

        binding.logginButton.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                login(binding.editTextTextEmailAddress.text.toString(),binding.editTextTextPassword.text.toString(), sp)
            }
        }
    }

    private fun login(user: String, password: String, sp: Preferences) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(user, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sp.saveUser(user)
                    if (binding.recordarSesion.isChecked) {
                        //putString("password", password)
                        sp.saveRemember(true)
                    } else {
                        sp.saveRemember(false)
                    }
                    showHome()
                    finish()
                } else {
                    showAlert()
                }
            }
    }

    private fun checkLogin() {
        if (sp.getRemember()){
            showHome()
            finish()
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Usuario no encontrado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }
}