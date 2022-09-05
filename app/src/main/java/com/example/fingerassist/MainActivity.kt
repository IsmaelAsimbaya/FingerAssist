package com.example.fingerassist

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.Utils.FingerAssist.Companion.db
import com.example.fingerassist.Utils.Notificaciones
import com.example.fingerassist.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_marcaciones,
                R.id.nav_perfil,
                R.id.nav_ajustes,
                R.id.nav_credits,
                R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        sp.saveCodigoFecha(generaCodigoFecha())

        existeCodigoMarcacion(object : CallBack {
            override fun onCallBack(value: Boolean) {
                if (value) {
                    if (!sp.getMarcacionDiaria()) {
                        creaPlantillaMarcacionDia()
                    }
                    existeMarcacionEntrada(object : CallBack {
                        override fun onCallBack(value: Boolean) {
                            if (value) {
                                if (!sp.getMarcacionesEntrada()) {
                                    val intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    Notificaciones(this@MainActivity).sendNotification(
                                        intent,
                                        "Recordatorio FingerAssist",
                                        "Recordatorio de Ingreso",
                                        "Recuerde realizar la marcacion de Ingreso ..."
                                    )
                                }
                            }
                        }
                    }, sp.getCodigoFecha())
                    existeMarcacionSalida(object : CallBack {
                        override fun onCallBack(value: Boolean) {
                            if (value) {
                                if (!sp.getMarcacionesSalida() && sp.getMarcacionesEntrada()) {
                                    val intent =
                                        Intent(
                                            this@MainActivity,
                                            LoginActivity::class.java
                                        )
                                    Notificaciones(this@MainActivity).sendNotification(
                                        intent,
                                        "Recordatorio FingerAssist",
                                        "Recordatorio de Salida",
                                        "Recuerde realizar la marcacion de Salida ..."
                                    )
                                }
                            }
                        }
                    }, sp.getCodigoFecha())
                    if (sp.getMarcacionesSalida() && sp.getMarcacionesEntrada()){
                        Toast.makeText(
                            this@MainActivity,
                            "Asistencia Completa :D",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }, sp.getCodigoFecha())

        lifecycleScope.launch(Dispatchers.IO) {
            cargaDatosUsuario(sp.getName(), navView)
        }

    }

    private fun cargaDatosUsuario(email: String?, navView: NavigationView) {
        val hView: View = navView.getHeaderView(0)
        val correo: TextView = hView.findViewById(R.id.userName)
        correo.setText(email)
        db.collection("users").document(email!!).get().addOnSuccessListener {

            val id: TextView = hView.findViewById(R.id.userId)
            id.setText(it.get("ci") as String?)

            val img: ImageView = hView.findViewById(R.id.profilePic)
            Picasso.get().load(it.get("img") as String?).resize(50, 50)
                .centerCrop().into(img)
        }

    }

    private fun existeCodigoMarcacion(myCallBack: CallBack, codigoMarcacion: String) {
        var aux: Boolean

        db.collection("users").document(sp.getName())
            .collection("marcaciones").document(codigoMarcacion)
            .get().addOnSuccessListener {
                if (!it.exists()) {
                    sp.saveMarcacionDiaria(false)
                } else {
                    sp.saveMarcacionDiaria(true)
                }
                aux = true
                if (aux) {
                    myCallBack.onCallBack(true)
                } else {
                    myCallBack.onCallBack(false)
                }
            }
    }

    private fun existeMarcacionEntrada(myCallBack: CallBack, codigoMarcacion: String) {
        var aux: Boolean
        db.collection("users").document(sp.getName())
            .collection("marcaciones").document(codigoMarcacion)
            .get().addOnSuccessListener {
                if ((it.get("entrada") as String) == "-") {
                    sp.saveMarcacionEntrada(false)
                } else {
                    sp.saveMarcacionEntrada(true)
                }
                aux = true
                if (aux) {
                    myCallBack.onCallBack(true)
                } else {
                    myCallBack.onCallBack(false)
                }
            }
    }

    private fun existeMarcacionSalida(myCallBack: CallBack, codigoMarcacion: String) {
        var aux: Boolean
        db.collection("users").document(sp.getName())
            .collection("marcaciones").document(codigoMarcacion)
            .get().addOnSuccessListener {
                if ((it.get("salida") as String) == "-") {
                    sp.saveMarcacionSalida(false)
                } else {
                    sp.saveMarcacionSalida(true)
                }
                aux = true
                if (aux) {
                    myCallBack.onCallBack(true)
                } else {
                    myCallBack.onCallBack(false)
                }
            }
    }

    private fun creaPlantillaMarcacionDia() {
        val date = Date()
        val numDia = obtenerNumDia(date)
        val name = obtenerNameDia(date)
        val data = hashMapOf(
            "atraso" to "-",
            "dia" to numDia,
            "entrada" to "-",
            "horario" to "-",
            "name" to name,
            "salida" to "-"
        )
        db.collection("users").document(sp.getName())
            .collection("marcaciones").document(sp.getCodigoFecha()).set(data)
    }

    private fun obtenerNumDia(date: Date): String {
        return DateFormat.format("dd", date).toString()
    }

    private fun obtenerNameDia(date: Date): String {
        var dia = DateFormat.format("EEEE", date).toString()
        var mesAno = DateFormat.format("MM/yy", date).toString()
        when (dia) {
            "Monday" -> dia = "LU"
            "lunes" -> dia = "LU"
            "Tuesday" -> dia = "MA"
            "martes" -> dia = "MA"
            "Wednesday" -> dia = "MI"
            "miércoles" -> dia = "MI"
            "Thursday" -> dia = "JU"
            "jueves" -> dia = "JU"
            "Friday" -> dia = "VI"
            "viernes" -> dia = "VI"
            "Saturday" -> dia = "SA"
            "sábado" -> dia = "SA"
            "Sunday" -> dia = "DO"
            "domingo" -> dia = "DO"
        }
        return dia + " " + mesAno
    }

    private fun generaCodigoFecha(): String {
        val date = Date()
        return DateFormat.format("dd_MM_yyyy", date).toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}