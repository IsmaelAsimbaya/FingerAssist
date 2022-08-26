package com.example.fingerassist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //val sp = getSharedPreferences("sessions", Context.MODE_PRIVATE)
        checkLogin()
        setup()
    }

    private fun setup() {
        title = "Autenticacion"

        binding.logginButton.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                login(binding.editTextTextEmailAddress.text.toString(),binding.editTextTextPassword.text.toString(), sp.getSharedPreference())
            }
        }
    }

    private fun login(user: String, password: String, sp: SharedPreferences) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(user, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    with(sp.edit()) {
                        putString("user", user)
                    }.apply()
                    if (binding.recordarSesion.isChecked) {
                        with(sp.edit()) {
                            putString("password", password)
                            putBoolean("remember",true)
                        }.apply()
                    } else {
                        with(sp.edit()) {
                            putBoolean("remember",false)
                        }.apply()
                    }
                    showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    finish()
                } else {
                    showAlert()
                }
            }
    }

    private fun checkLogin() {
        if (sp.getSharedPreference().getBoolean("remember",false)){
            showHome(sp.getSharedPreference().getString("user",""), ProviderType.BASIC)
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

    private fun showHome(email: String?, provider: ProviderType) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(mainIntent)
    }
}