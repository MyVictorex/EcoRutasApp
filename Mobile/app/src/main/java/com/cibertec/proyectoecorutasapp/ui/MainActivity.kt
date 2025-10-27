package com.cibertec.proyectoecorutasapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cibertec.proyectoecorutasapp.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var locationCallback: LocationCallback

    private var origenLocation: LatLng? = null
    private var destinoLocation: LatLng? = null
    private var destinoMarker: Marker? = null
    private var polyline: Polyline? = null

    private lateinit var tvOrigen: TextView
    private lateinit var tvDestino: TextView
    private lateinit var btnTrazarRuta: Button
    private lateinit var spnModo: Spinner

    private var modoSeleccionado: String = "driving"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Places
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tvOrigen = findViewById(R.id.tvOrigen)
        tvDestino = findViewById(R.id.tvDestino)
        btnTrazarRuta = findViewById(R.id.btnTrazarRuta)
        spnModo = findViewById(R.id.spnModo)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        // Menús
        menuIcon.setOnClickListener { showMainMenu(menuIcon) }
        profileIcon.setOnClickListener { showProfileMenu(profileIcon) }

        setupSpinner()
        setupDestinoSearch()

        btnTrazarRuta.setOnClickListener {
            if (origenLocation != null && destinoLocation != null) {
                trazarRuta(origenLocation!!, destinoLocation!!)
            } else {
                Toast.makeText(this, "Selecciona un destino primero", Toast.LENGTH_SHORT).show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.modos_transporte,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnModo.adapter = adapter
        }

        spnModo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                modoSeleccionado = when (position) {
                    0 -> "bicycling"
                    1 -> "driving"
                    2 -> "walking"
                    else -> "driving"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDestinoSearch() {
        tvDestino.setOnClickListener { abrirAutocomplete() }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        googleMap.isMyLocationEnabled = true

        googleMap.setOnMapClickListener { latLng ->
            destinoLocation = latLng
            tvDestino.text = "Destino seleccionado en mapa"
            destinoMarker?.remove()
            destinoMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Destino seleccionado")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }

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
            }
        }
    }

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
        if (requestCode == 2) {
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
    }

    private fun trazarRuta(origen: LatLng, destino: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origen.latitude},${origen.longitude}" +
                "&destination=${destino.latitude},${destino.longitude}" +
                "&mode=$modoSeleccionado" +
                "&key=${getString(R.string.google_maps_key)}"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@MainActivity, "Error al obtener ruta", Toast.LENGTH_SHORT).show() }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)
                val routes = json.getJSONArray("routes")
                if (routes.length() == 0) {
                    runOnUiThread { Toast.makeText(this@MainActivity, "No se encontró ruta", Toast.LENGTH_SHORT).show() }
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
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 🔹 Menú principal (tres rayas)
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
