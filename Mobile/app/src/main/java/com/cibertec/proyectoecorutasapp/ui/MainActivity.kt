package com.cibertec.proyectoecorutasapp.ui

import com.cibertec.proyectoecorutasapp.repository.RutaRepository
import com.cibertec.proyectoecorutasapp.models.TipoRuta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

private var idRutaActual: Int? = null

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
    private lateinit var menuIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var btnTrazarRuta: Button

    private val client = OkHttpClient()
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private var rutaCompleta: List<LatLng> = emptyList()
    private var modoSeleccionado = "driving"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
        menuIcon = findViewById(R.id.menuIcon)
        profileIcon = findViewById(R.id.profileIcon)
        btnTrazarRuta = findViewById(R.id.btnTrazarRuta)

        menuIcon.setOnClickListener { showMainMenu(menuIcon) }
        profileIcon.setOnClickListener { showProfileMenu(profileIcon) }

        setupSpinner()
        setupDestinoSearch()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnFinalizar.setOnClickListener { finalizarRuta() }

        btnTrazarRuta.setOnClickListener {
            if (origenLocation == null) {
                Toast.makeText(this, "Obteniendo tu ubicación...", Toast.LENGTH_SHORT).show()
                mostrarUbicacionUsuario()
                return@setOnClickListener
            }
            if (destinoLocation == null) {
                Toast.makeText(this, "Selecciona un destino tocando el mapa o buscándolo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            trazarRuta(origenLocation!!, destinoLocation!!)
        }
    }

    // ✅ PERMISOS
    private fun tienePermisosUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisosUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    // ✅ MOSTRAR UBICACIÓN ACTUAL
    private fun mostrarUbicacionUsuario() {
        if (!tienePermisosUbicacion()) {
            solicitarPermisosUbicacion()
            return
        }

        try {
            googleMap.isMyLocationEnabled = true

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000L
            ).setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        origenLocation = LatLng(location.latitude, location.longitude)
                        tvOrigen.text = "Ubicación actual obtenida"
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 16f))
                    } else {
                        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                val loc = result.lastLocation ?: return
                                origenLocation = LatLng(loc.latitude, loc.longitude)
                                tvOrigen.text = "Ubicación actual (forzada)"
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 16f))
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }, mainLooper)
                    }
                }

        } catch (e: SecurityException) {
            Toast.makeText(this, "Permiso de ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ MAPA LISTO
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (tienePermisosUbicacion()) mostrarUbicacionUsuario()
        else solicitarPermisosUbicacion()

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
        }

        // 🔹 Verificar si viene desde un QR
        val qrInicio = intent.getStringExtra("qr_inicio")
        val qrFin = intent.getStringExtra("qr_fin")
        val qrNombre = intent.getStringExtra("qr_nombre")

        if (qrInicio != null && qrFin != null) {
            obtenerUbicacionActual { miUbicacion ->
                val destino = qrFin.split(",")
                val latFin = destino[0].toDouble()
                val lonFin = destino[1].toDouble()

                trazarRuta(
                    LatLng(miUbicacion.latitude, miUbicacion.longitude),
                    LatLng(latFin, lonFin)
                )

                Toast.makeText(
                    this,
                    "Ruta hacia ${qrNombre ?: "Destino"} iniciada desde tu ubicación 🚴‍♂️",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ✅ OBTENER UBICACIÓN ACTUAL CON CALLBACK
    private fun obtenerUbicacionActual(callback: (android.location.Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { callback(it) }
        }
    }

    // ✅ AUTOCOMPLETE
    private fun setupDestinoSearch() {
        tvDestino.setOnClickListener { abrirAutocomplete() }
    }

    private fun abrirAutocomplete() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val bounds = RectangularBounds.newInstance(
            LatLng(-18.35, -81.32), LatLng(-0.03, -68.65)
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(listOf("PE"))
            .setLocationRestriction(bounds)
            .build(this)
        startActivityForResult(intent, 2)
    }

    // ✅ SPINNER
    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this, R.array.modos_transporte, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnModo.adapter = adapter
        }

        spnModo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                modoSeleccionado = when (position) {
                    0 -> "bicycling"   // BICICLETA 🚴‍♂️
                    1 -> "driving"   // SCOOTER usa el mismo modo que bicicleta
                    2 -> "driving"     // CARPOOL usa modo auto
                    3 -> "bicycling"   // MONOPATÍN ELÉCTRICO similar a bici
                    4 -> "walking"     // SEGWAY lo tratamos como caminando
                    else -> "bicycling"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    // ✅ TRAZAR RUTA Y CREAR EN BD
    // ✅ TRAZAR RUTA Y CREAR EN BD (Versión segura)
    private fun trazarRuta(origen: LatLng, destino: LatLng) {
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origen.latitude},${origen.longitude}" +
                    "&destination=${destino.latitude},${destino.longitude}" +
                    "&mode=$modoSeleccionado" +
                    "&key=${getString(R.string.google_maps_key)}"

        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al conectar con el servidor. Verifica tu conexión.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string() ?: ""
                val json = JSONObject(jsonString)
                val routes = json.optJSONArray("routes")

                // ⚠️ Verificación: si no hay rutas
                if (routes == null || routes.length() == 0) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "🚫 Ninguna ruta disponible cerca del vehículo o del destino seleccionado.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return
                }

                // ✅ Obtener la primera ruta
                val overview = routes.getJSONObject(0)
                    .getJSONObject("overview_polyline")
                    .getString("points")

                val path = PolyUtil.decode(overview)
                rutaCompleta = path

                runOnUiThread {
                    polyline?.remove()
                    polyline = googleMap.addPolyline(
                        PolylineOptions()
                            .addAll(path)
                            .color(Color.BLUE)
                            .width(10f)
                    )

                    // Calcular distancia aproximada
                    val distanciaKm = path.size * 0.03
                    val tipo = TipoRuta.values()[spnModo.selectedItemPosition]
                    val idUsuario =
                        getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE).getInt("usuario_id", -1)

                    // Guardar ruta en la BD
                    RutaRepository(this@MainActivity).crearRutaAutomatica(
                        nombre = "Ruta ${System.currentTimeMillis()}",
                        puntoInicio = "${origen.latitude},${origen.longitude}",
                        puntoFin = "${destino.latitude},${destino.longitude}",
                        distancia = distanciaKm,
                        tipo = tipo,
                        idUsuario = idUsuario,
                        onSuccess = { rutaCreada ->
                            idRutaActual = rutaCreada.id_ruta
                            Toast.makeText(
                                this@MainActivity,
                                "Ruta creada ID: $idRutaActual",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = {
                            Toast.makeText(
                                this@MainActivity,
                                "Error al crear la ruta en la base de datos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        })
    }


    // ✅ FINALIZAR RUTA
    private fun finalizarRuta() {
        if (idRutaActual == null) {
            Toast.makeText(this, "No hay ruta creada", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Ruta finalizada ✅", Toast.LENGTH_SHORT).show()
        limpiarRuta()
    }

    private fun limpiarRuta() {
        polyline?.remove()
        destinoMarker?.remove()
        rutaCompleta = emptyList()
        destinoLocation = null
        idRutaActual = null
    }

    // ✅ MENÚ
    private fun showMainMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_nav, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_alquiler -> startActivity(Intent(this, AlquilerActivity::class.java))
                R.id.action_rutas -> startActivity(Intent(this, RutaActivity::class.java))
                R.id.action_estadisticas -> startActivity(Intent(this, EstadisticasActivity::class.java))
                R.id.action_logros -> startActivity(Intent(this, LogrosActivity::class.java))
                R.id.action_historial -> startActivity(Intent(this, HistorialRutaActivity::class.java))
                R.id.action_GenerarQR -> startActivity(Intent(this, QrRutaActivity::class.java))
            }
            true
        }
        popup.show()
    }

    private fun showProfileMenu(anchor: ImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_perfil, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_editar -> startActivity(Intent(this, PerfilActivity::class.java))
                R.id.action_cerrar_sesion -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }
        popup.show()
    }
}
