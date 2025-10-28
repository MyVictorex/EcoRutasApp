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

    private var origenLocation: LatLng? = null
    private var destinoLocation: LatLng? = null
    private var destinoMarker: Marker? = null
    private var polyline: Polyline? = null

    private lateinit var tvOrigen: TextView
    private lateinit var tvDestino: TextView
    private lateinit var tvTiempoEstimado: TextView
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
        tvTiempoEstimado = findViewById(R.id.tvTiempoEstimado)
        btnTrazarRuta = findViewById(R.id.btnTrazarRuta)
        spnModo = findViewById(R.id.spnModo)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

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

    // Configura el Spinner de modos de transporte
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
                    2 -> "driving"
                    3 -> "bicycling"
                    4 -> "walking"
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

        // --- Marcadores EcoRent cerca de Cibertec, Plaza Bolognesi y Wilson ---
        val ecoRent1 = LatLng(-12.0657, -77.0375) // Frente a Cibertec
        val ecoRent2 = LatLng(-12.0605, -77.0416) // Plaza Bolognesi
        val ecoRent3 = LatLng(-12.0565, -77.0370) // Av. Wilson
        val ecoRent4 = LatLng(-12.0630, -77.0350) // Av. Arequipa
        val ecoRent5 = LatLng(-12.0580, -77.0390) // Cerca Av. Guzmán Blanco

        googleMap.addMarker(
            MarkerOptions()
                .position(ecoRent1)
                .title("EcoRent - Sede Cibertec")
                .snippet("Alquiler de bicicletas y scooters")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions()
                .position(ecoRent2)
                .title("EcoRent - Plaza Bolognesi")
                .snippet("Punto de alquiler y recarga")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions()
                .position(ecoRent3)
                .title("EcoRent - Av. Wilson")
                .snippet("Bicicletas eléctricas disponibles")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions()
                .position(ecoRent4)
                .title("EcoRent - Av. Arequipa")
                .snippet("Scooters eléctricos disponibles")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions()
                .position(ecoRent5)
                .title("EcoRent - Guzmán Blanco")
                .snippet("Punto rápido de alquiler")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecoRent1, 14f))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            mostrarUbicacionUsuario()
        }
    }

    private fun mostrarUbicacionUsuario() {
        try {
            // Verificar permisos antes de activar la ubicación
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    100
                )
                return
            }

            googleMap.isMyLocationEnabled = true

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        origenLocation = LatLng(location.latitude, location.longitude)
                        tvOrigen.text = "Ubicación actual obtenida"
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 15f))
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                    }
                }

        } catch (e: SecurityException) {
            // Captura cualquier error de seguridad si el permiso fue revocado mientras tanto
            e.printStackTrace()
            Toast.makeText(this, "Permiso de ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            mostrarUbicacionUsuario()
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
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al obtener ruta", Toast.LENGTH_SHORT).show()
                    tvTiempoEstimado.text = "Tiempo estimado: --"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)
                val routes = json.getJSONArray("routes")

                if (routes.length() == 0) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "No se encontró ruta", Toast.LENGTH_SHORT).show()
                        tvTiempoEstimado.text = "Tiempo estimado: --"
                    }
                    return
                }

                val route = routes.getJSONObject(0)
                val overview = route.getJSONObject("overview_polyline").getString("points")
                val decodedPath = PolyUtil.decode(overview)

                val legs = route.getJSONArray("legs")
                val durationText = legs.getJSONObject(0)
                    .getJSONObject("duration")
                    .getString("text")

                runOnUiThread {
                    polyline?.remove()
                    polyline = googleMap.addPolyline(
                        PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10f)
                    )

                    tvTiempoEstimado.text = "Tiempo estimado: $durationText"

                    val builder = LatLngBounds.Builder()
                    decodedPath.forEach { builder.include(it) }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
                }
            }
        })
    }

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
