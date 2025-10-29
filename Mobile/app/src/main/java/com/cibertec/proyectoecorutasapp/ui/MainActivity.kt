package com.cibertec.proyectoecorutasapp.ui

import com.cibertec.proyectoecorutasapp.repository.RutaRepository
import com.cibertec.proyectoecorutasapp.models.TipoRuta

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

        menuIcon.setOnClickListener { showMainMenu(menuIcon) }
        profileIcon.setOnClickListener { showProfileMenu(profileIcon) }

        setupSpinner()
        setupDestinoSearch()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnFinalizar.setOnClickListener { finalizarRuta() }
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

            // Primer intento (rápido)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        origenLocation = LatLng(location.latitude, location.longitude)
                        tvOrigen.text = "Ubicación actual obtenida"
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origenLocation!!, 16f))
                    } else {
                        // Segundo intento (forzado en tiempo real)
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
            origenLocation?.let { trazarRuta(it, latLng) }
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
        ).also { a ->
            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnModo.adapter = a
        }
    }

    // ✅ TRAZAR RUTA Y CREAR EN BD
    private fun trazarRuta(origen: LatLng, destino: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origen.latitude},${origen.longitude}&destination=${destino.latitude},${destino.longitude}&mode=$modoSeleccionado&key=${getString(R.string.google_maps_key)}"

        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: return)
                val path = PolyUtil.decode(
                    json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                )
                rutaCompleta = path

                runOnUiThread {
                    polyline?.remove()
                    polyline = googleMap.addPolyline(
                        PolylineOptions().addAll(path).color(Color.BLUE).width(10f)
                    )

                    val distanciaKm = path.size * 0.03
                    val tipo = TipoRuta.values()[spnModo.selectedItemPosition]
                    val idUsuario = getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE).getInt("usuario_id", -1)

                    RutaRepository(this@MainActivity).crearRutaAutomatica(
                        nombre = "Ruta ${System.currentTimeMillis()}",
                        puntoInicio = "${origen.latitude},${origen.longitude}",
                        puntoFin = "${destino.latitude},${destino.longitude}",
                        distancia = distanciaKm,
                        tipo = tipo,
                        idUsuario = idUsuario,
                        onSuccess = { rutaCreada ->
                            idRutaActual = rutaCreada.id_ruta
                            Toast.makeText(this@MainActivity, "Ruta creada ID: $idRutaActual", Toast.LENGTH_SHORT).show()
                        },
                        onError = { Toast.makeText(this@MainActivity, "No se creó ruta", Toast.LENGTH_SHORT).show() }
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
