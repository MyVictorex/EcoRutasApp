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
import androidx.core.content.ContextCompat
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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
    private lateinit var spnModo: Spinner
    private lateinit var btnFinalizar: Button

    private var modoSeleccionado: String = "driving"
    private val client = OkHttpClient()

    private var recorridoPolyline: Polyline? = null
    private var rutaCompleta: List<LatLng> = emptyList()
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private var esRutaLibre: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tvOrigen = findViewById(R.id.tvOrigen)
        tvDestino = findViewById(R.id.tvDestino)
        tvTiempoEstimado = findViewById(R.id.tvTiempoEstimado)
        spnModo = findViewById(R.id.spnModo)
        btnFinalizar = findViewById(R.id.btnFinalizarRuta)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        menuIcon.setOnClickListener { showMainMenu(menuIcon) }
        profileIcon.setOnClickListener { showProfileMenu(profileIcon) }

        setupSpinner()
        setupDestinoSearch()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Revisar si llegaron coordenadas desde RutaAdapter
        val latDestino = intent.getDoubleExtra("lat_destino", Double.NaN)
        val lngDestino = intent.getDoubleExtra("lng_destino", Double.NaN)
        if (!latDestino.isNaN() && !lngDestino.isNaN()) {
            destinoLocation = LatLng(latDestino, lngDestino)
            tvDestino.text = "Destino: $latDestino, $lngDestino"
            esRutaLibre = false
        }

        // 🔹 Botón Finalizar Ruta
        btnFinalizar.setOnClickListener {
            finalizarRuta()
        }
    }

    // ------------------- Spinner -------------------
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
                if (origenLocation != null && destinoLocation != null) {
                    trazarRuta(origenLocation!!, destinoLocation!!)
                    esRutaLibre = false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // ------------------- Método para obtener tipo de transporte -------------------
    private fun obtenerTipoDesdeSpinner(): String {
        return when (spnModo.selectedItemPosition) {
            0 -> "BICICLETA"
            1 -> "SCOOTER"
            2 -> "MONOPATIN_ELECTRICO"
            3 -> "SEGWAY"
            4 -> "CARPOOL"
            else -> "BICICLETA"
        }
    }

    // ------------------- Destino -------------------
    private fun setupDestinoSearch() {
        tvDestino.setOnClickListener { abrirAutocomplete() }
    }

    // ------------------- Map -------------------
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        val ecoRents = listOf(
            LatLng(-12.0657, -77.0375),
            LatLng(-12.0605, -77.0416),
            LatLng(-12.0565, -77.0370),
            LatLng(-12.0630, -77.0350),
            LatLng(-12.0580, -77.0390)
        )
        val nombres = listOf("Cibertec", "Plaza Bolognesi", "Av. Wilson", "Av. Arequipa", "Guzmán Blanco")

        ecoRents.forEachIndexed { i, loc ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(loc)
                    .title("EcoRent - ${nombres[i]}")
                    .snippet("Punto de alquiler y recarga")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecoRents[0], 14f))

        if (tienePermisosUbicacion()) {
            mostrarUbicacionUsuario()
        } else {
            solicitarPermisosUbicacion()
        }

        googleMap.setOnMapClickListener { latLng ->
            destinoLocation = latLng
            destinoMarker?.remove()
            destinoMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Destino seleccionado")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            tvDestino.text = "Destino: ${latLng.latitude}, ${latLng.longitude}"
            origenLocation?.let { trazarRuta(it, destinoLocation!!) }
            esRutaLibre = true
        }
    }

    // ------------------- Permisos -------------------
    private fun tienePermisosUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisosUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun mostrarUbicacionUsuario() {
        if (!tienePermisosUbicacion()) {
            solicitarPermisosUbicacion()
            return
        }
        try {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        origenLocation = LatLng(location.latitude, location.longitude)
                        tvOrigen.text = "Ubicación actual obtenida"
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 15f))
                        destinoLocation?.let { trazarRuta(origenLocation!!, it) }
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Permiso de ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            mostrarUbicacionUsuario()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------- Autocomplete -------------------
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
                    origenLocation?.let { o -> trazarRuta(o, it) }
                    esRutaLibre = false
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ------------------- Trazar Ruta -------------------
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
                rutaCompleta = decodedPath

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

                    iniciarSeguimientoRuta()
                }
            }
        })
    }

    // ------------------- Seguimiento -------------------
    private fun iniciarSeguimientoRuta() {
        if (!tienePermisosUbicacion()) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L
        ).build()

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation ?: return
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    actualizarProgresoRuta(userLatLng)
                }
            }, mainLooper)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun actualizarProgresoRuta(ubicacionActual: LatLng) {
        if (rutaCompleta.isEmpty()) return

        val puntoCercano = rutaCompleta.minByOrNull { punto ->
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                ubicacionActual.latitude, ubicacionActual.longitude,
                punto.latitude, punto.longitude,
                results
            )
            results[0]
        } ?: return

        val index = rutaCompleta.indexOf(puntoCercano)
        if (index <= 0) return

        val recorrido = rutaCompleta.subList(0, index)

        recorridoPolyline?.remove()
        recorridoPolyline = googleMap.addPolyline(
            PolylineOptions()
                .addAll(recorrido)
                .color(Color.GREEN)
                .width(10f)
        )
    }

    // ------------------- Finalizar Ruta -------------------
    private fun finalizarRuta() {
        if (rutaCompleta.isEmpty()) {
            Toast.makeText(this, "No hay ruta para finalizar", Toast.LENGTH_SHORT).show()
            return
        }

        if (esRutaLibre) {
            val json = JSONObject()
            json.put("usuario_id", auth.currentUser?.uid)
            json.put("ruta", rutaCompleta.map { listOf(it.latitude, it.longitude) })
            json.put("tipo", obtenerTipoDesdeSpinner()) // <-- Aquí usamos el spinner

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://tuapi.com/registrar_ruta_libre") // reemplaza con tu endpoint
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error al registrar ruta", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ruta libre registrada correctamente", Toast.LENGTH_SHORT).show()
                        limpiarRuta()
                    }
                }
            })
        } else {
            Toast.makeText(this, "Ruta finalizada", Toast.LENGTH_SHORT).show()
            limpiarRuta()
        }
    }

    private fun limpiarRuta() {
        polyline?.remove()
        recorridoPolyline?.remove()
        destinoMarker?.remove()
        rutaCompleta = emptyList()
        destinoLocation = null
        esRutaLibre = false

        tvDestino.text = "Destino: --"
        tvTiempoEstimado.text = "Tiempo estimado: --"
    }

    // ------------------- Menús -------------------
    private fun showMainMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_nav, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_alquiler -> { startActivity(Intent(this, AlquilerActivity::class.java)); true }
                R.id.action_rutas -> { startActivity(Intent(this, RutaActivity::class.java)); true }
                R.id.action_estadisticas -> { startActivity(Intent(this, EstadisticasActivity::class.java)); true }
                R.id.action_logros -> { startActivity(Intent(this, LogrosActivity::class.java)); true }
                R.id.action_historial -> { startActivity(Intent(this, HistorialRutaActivity::class.java)); true }
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
                R.id.action_editar -> { startActivity(Intent(this, PerfilActivity::class.java)); true }
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
