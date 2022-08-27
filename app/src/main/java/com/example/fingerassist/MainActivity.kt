package com.example.fingerassist

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.databinding.ActivityMainBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        /* binding.appBarMain.fab.setOnClickListener { view ->
             Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
         }*/
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
            Picasso.get().load(it.get("img") as String?).into(img)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            cargaUbicacion()
        }

    }

    private fun cargaUbicacion() {
        db.collection("users").document(sp.getName())
            .collection("horario").document("Administrativo")
            .get().addOnSuccessListener {
                val coord: GeoPoint = it.get("lugar") as GeoPoint
                val lat: String = coord.latitude.toString()
                Log.println(Log.DEBUG, "localizacion", "latitud$lat")
                val lng: String = coord.longitude.toString()
                Log.println(Log.DEBUG, "localizacion", "longitud$lng")
                with(sp.getSharedPreference().edit()) {
                    putString("lat", lat)
                    putString("lng", lng)
                }.apply()
            }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}