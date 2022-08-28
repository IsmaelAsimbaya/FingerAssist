package com.example.fingerassist.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fingerassist.CallBack
import com.example.fingerassist.MapsActivity
import com.example.fingerassist.R
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.PolyUtil

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val db = FirebaseFirestore.getInstance()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var canAuthenticate = false
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var poligono: Polygon
    private lateinit var lugarMarcacion: LatLng
    private lateinit var ubicacion: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        obtenerUbicacion()

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.marcIngreso.setOnClickListener {
            obtenerUbicacion()
            if (validarLugarMarcacion(ubicacion)) {
                authenticate {
                    if (it) {
                        textView.text = "Ingreso registrado"
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se encuentra en el area designada",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.marcSalida.setOnClickListener {
            obtenerUbicacion()
            if (validarLugarMarcacion(ubicacion)) {
                authenticate {
                    if (it) {
                        textView.text = "Salida registrado"
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se encuentra en el area designada",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        //cargar biometricas
        setupAuth()
        //cargar mapa
        /*Handler().postDelayed({
            createMap()
        }, 2000)*/
        cargaCoordenadas(object : CallBack {
            override fun onCallBack(value: Boolean) {
                if (value) {
                    createMap()
                }
            }
        })

        return root
    }

    //biometricas
    private fun setupAuth() {
        if (BiometricManager.from(requireContext()).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ) == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            canAuthenticate = true
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Esperando confirmacion...")
                .setSubtitle("Confirma utilizando su huella")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()
        }
    }

    private fun authenticate(auth: (auth: Boolean) -> Unit) {
        if (canAuthenticate) {
            BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        auth(true)
                    }
                }).authenticate(promptInfo)
        } else {
            auth(true)
        }
    }

    //Mapa
    private fun createMap() {
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        lugarMarcacion = cargaLugar()
        createMarkerLugar_de_Marcacion(lugarMarcacion)
        poligono = createAreaMarcacion()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)

        enableLocation()
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            requireContext() as Activity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermision()
        }
    }

    private fun requestLocationPermision() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireContext() as Activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                requireContext(),
                "Ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MapsActivity.REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MapsActivity.REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    requireContext(),
                    "Para activar la localizacion ve a ajustes y acpeta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    /*override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(requireContext(), "Para activar la localizacion ve a ajustes y acpeta los permisos", Toast.LENGTH_SHORT).show()

        }
    }*/

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "A tu ubicacion", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(
            requireContext(),
            "Estas en ${p0.latitude}, ${p0.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun cargaCoordenadas(myCallBack: CallBack) {
        //private fun cargaUbicacion() {
        var aux = false

        db.collection("users").document(sp.getName())
            .collection("horario").document("Administrativo")
            .get().addOnSuccessListener {
                val coord: GeoPoint = it.get("lugar") as GeoPoint
                val axis1: GeoPoint = it.get("axis1") as GeoPoint
                val axis2: GeoPoint = it.get("axis2") as GeoPoint
                val axis3: GeoPoint = it.get("axis3") as GeoPoint
                val axis4: GeoPoint = it.get("axis4") as GeoPoint
                sp.saveLatLng(setOf(coord.latitude.toString(), coord.longitude.toString()))
                sp.saveAxis1(setOf(axis1.latitude.toString(), axis1.longitude.toString()))
                sp.saveAxis2(setOf(axis2.latitude.toString(), axis2.longitude.toString()))
                sp.saveAxis3(setOf(axis3.latitude.toString(), axis3.longitude.toString()))
                sp.saveAxis4(setOf(axis4.latitude.toString(), axis4.longitude.toString()))
                aux = true
                if (aux) {
                    myCallBack.onCallBack(true)
                } else {
                    myCallBack.onCallBack(false)
                }
            }

    }

    private fun cargaLugar(): LatLng {
        val lat = sp.getLat()
        Log.println(Log.DEBUG, "test", "lat$lat")
        val lng = sp.getLng()
        Log.println(Log.DEBUG, "test", "lng$lng")
        return LatLng(lat.toDouble(), lng.toDouble())
    }

    private fun cargaAxis1(): LatLng {
        return LatLng(sp.getAxis1Lat().toDouble(), sp.getAxis1Lng().toDouble())
    }

    private fun cargaAxis2(): LatLng {
        return LatLng(sp.getAxis2Lat().toDouble(), sp.getAxis2Lng().toDouble())
    }

    private fun cargaAxis3(): LatLng {
        return LatLng(sp.getAxis3Lat().toDouble(), sp.getAxis3Lng().toDouble())
    }

    private fun cargaAxis4(): LatLng {
        val lat = sp.getAxis4Lat().toDouble()
        val lng = sp.getAxis4Lng().toDouble()
        return LatLng(lat, lng)
    }

    private fun createMarkerLugar_de_Marcacion(coordinates: LatLng) {
        Log.println(Log.DEBUG, "localizacion", "posicion$coordinates")
        val marker: MarkerOptions =
            MarkerOptions().position(coordinates).title("Lugar de Marcacion")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    private fun createAreaMarcacion(): Polygon {
        val poligono = map.addPolygon(
            PolygonOptions()
                .add(
                    cargaAxis1(),
                    cargaAxis2(),
                    cargaAxis3(),
                    cargaAxis4()
                )
        )
        poligono.tag = "AreaMarcacion"
        poligono.strokeWidth = 1.0f
        poligono.strokeColor = Color.parseColor("#FEAA0C")
        poligono.fillColor = Color.parseColor("#FEAA0C")
        return poligono
    }

    private fun validarLugarMarcacion(lugarMarcacion: LatLng): Boolean {
        val validar = PolyUtil.containsLocation(
            lugarMarcacion, poligono.points, false
        )
        return validar
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        try {
            if (isLocationPermissionGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireContext() as Activity) { task ->
                    if (task.isSuccessful) {
                        ubicacion = LatLng(task.result.latitude, task.result.longitude)
                    } else {
                        Log.d("", "Current location is null. Using defaults.")
                        Log.e("", "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun guardaMarcacion() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}