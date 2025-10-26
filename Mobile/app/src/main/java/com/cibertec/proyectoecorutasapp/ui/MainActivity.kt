package com.cibertec.proyectoecorutasapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
<<<<<<< HEAD
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
=======
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
>>>>>>> 67f5e2f (subiendo Avances)
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cibertec.proyectoecorutasapp.R
<<<<<<< HEAD
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
=======
import com.google.android.gms.location.*
>>>>>>> 67f5e2f (subiendo Avances)
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
<<<<<<< HEAD
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
=======
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.PolyUtil
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
>>>>>>> 67f5e2f (subiendo Avances)

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
<<<<<<< HEAD
=======
    private lateinit var locationCallback: LocationCallback

    private var origenLocation: LatLng? = null
    private var destinoLocation: LatLng? = null
    private var destinoMarker: Marker? = null
    private var polyline: Polyline? = null

    private lateinit var tvOrigen: TextView
    private lateinit var tvDestino: TextView
    private lateinit var btnTrazarRuta: Button

    private val client = OkHttpClient()
>>>>>>> 67f5e2f (subiendo Avances)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            showProfileMenu(profileIcon)
        }

=======
        // Inicializar Places
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyAgaBt-YhWFA11DS5kAn-LhExVa1HjPAwk")
        }

        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tvOrigen = findViewById(R.id.tvOrigen)
        tvDestino = findViewById(R.id.tvDestino)
        btnTrazarRuta = findViewById(R.id.btnTrazarRuta)

        // 🔹 Íconos del toolbar
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        // 🔹 Asignar eventos a los íconos
        menuIcon.setOnClickListener { showMainMenu(menuIcon) }
        profileIcon.setOnClickListener { showProfileMenu(profileIcon) }

        // 🔹 Autocomplete de destino
        tvDestino.setOnClickListener { abrirAutocomplete() }

        // 🔹 Botón para trazar ruta
        btnTrazarRuta.setOnClickListener {
            if (origenLocation != null && destinoLocation != null) {
                trazarRuta(origenLocation!!, destinoLocation!!)
            } else {
                Toast.makeText(this, "Selecciona un destino primero", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Configurar mapa
>>>>>>> 67f5e2f (subiendo Avances)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
<<<<<<< HEAD
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
=======

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
>>>>>>> 67f5e2f (subiendo Avances)
            return
        }

        googleMap.isMyLocationEnabled = true

<<<<<<< HEAD

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(userLatLng).title("Tu ubicación"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            } else {

                val lima = LatLng(-12.0464, -77.0428)
                googleMap.addMarker(MarkerOptions().position(lima).title("Lima, Perú"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
=======
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                origenLocation = LatLng(location.latitude, location.longitude)
                tvOrigen.text = "Ubicación actual obtenida"
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 15f))
            } else {
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setMaxUpdates(1)
                    .build()

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val loc = result.lastLocation ?: return
                        origenLocation = LatLng(loc.latitude, loc.longitude)
                        tvOrigen.text = "Ubicación actual obtenida"
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 15f))
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
>>>>>>> 67f5e2f (subiendo Avances)
            }
        }
    }

<<<<<<< HEAD
    private fun showProfileMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_perfil, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_editar -> {
                    Toast.makeText(this, "Editar perfil (por implementar)", Toast.LENGTH_SHORT).show()
=======
    private fun abrirAutocomplete() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val bounds = RectangularBounds.newInstance(
            LatLng(-18.35, -81.32), LatLng(-0.03, -68.65)
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setLocationRestriction(bounds)
            .setCountries(listOf("PE"))
            .build(this)

        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            destinoLocation = place.latLng
            tvDestino.text = place.name

            destinoMarker?.remove()
            destinoLocation?.let {
                destinoMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(it)
                        .title("Destino: ${place.name}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 14f))
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun trazarRuta(origen: LatLng, destino: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origen.latitude},${origen.longitude}" +
                "&destination=${destino.latitude},${destino.longitude}" +
                "&key=AIzaSyAgaBt-YhWFA11DS5kAn-LhExVa1HjPAwk"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al obtener ruta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)
                val routes = json.getJSONArray("routes")
                if (routes.length() == 0) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "No se encontró ruta", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val points = routes.getJSONObject(0)
                    .getJSONObject("overview_polyline")
                    .getString("points")

                val decodedPath = PolyUtil.decode(points)
                runOnUiThread {
                    polyline?.remove()
                    polyline = googleMap.addPolyline(
                        PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10f)
                    )

                    val builder = LatLngBounds.Builder()
                    decodedPath.forEach { builder.include(it) }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // 🔹 Menú principal (ícono de hamburguesa)
    private fun showMainMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_nav, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_alquiler -> {
                    startActivity(Intent(this, AlquilerActivity::class.java))
                    true
                }
                R.id.action_rutas -> {
                    startActivity(Intent(this, RutaActivity::class.java))
                    true
                }
                R.id.action_estadisticas -> {
                    startActivity(Intent(this, EstadisticasActivity::class.java))
                    true
                }
                R.id.action_logros -> {
                    startActivity(Intent(this, LogrosActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // 🔹 Menú de perfil
    private fun showProfileMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_perfil, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_editar -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
>>>>>>> 67f5e2f (subiendo Avances)
                    true
                }
                R.id.action_cerrar_sesion -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
