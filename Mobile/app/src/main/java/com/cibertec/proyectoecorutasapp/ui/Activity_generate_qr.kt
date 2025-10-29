package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

class QrRutaActivity : AppCompatActivity() {

    private lateinit var ivQrRuta: ImageView
    private lateinit var btnGenerarQr: Button
    private lateinit var btnEscanearQr: Button
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_generate_qr)

        ivQrRuta = findViewById(R.id.ivQrRuta)
        btnGenerarQr = findViewById(R.id.btnGenerarQr)
        btnEscanearQr = findViewById(R.id.btnEscanearQr)
        tvResultado = findViewById(R.id.tvResultado)

        btnGenerarQr.setOnClickListener { generarQrDeRuta() }
        btnEscanearQr.setOnClickListener { iniciarEscaneo() }
    }


    private fun generarQrDeRuta() {
        val rutaJson = JSONObject().apply {
            put("tipo", "ruta")
            put("nombre", "Ruta al Parque Central")
            put("inicio", "-12.0453,-77.0311")
            put("fin", "-12.0521,-77.0400")
            put("distancia_km", 3.2)
        }.toString()

        val bmp = crearQr(rutaJson, 800)
        ivQrRuta.setImageBitmap(bmp)
        Toast.makeText(this, "QR generado correctamente ✅", Toast.LENGTH_SHORT).show()
    }

    private fun crearQr(texto: String, size: Int): Bitmap {
        val matrix = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, size, size)
        val pixels = IntArray(size * size) { i ->
            if (matrix.get(i % size, i / size)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }


    private fun iniciarEscaneo() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el código QR de la ruta")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.setOrientationLocked(false) // 🔹 Permitir rotación
        integrator.captureActivity = CaptureActivityLandscape::class.java // 🔹 Forzar landscape
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            } else {
                procesarQr(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun procesarQr(data: String) {
        try {
            val json = JSONObject(data)
            if (json.getString("tipo") == "ruta") {
                val nombre = json.getString("nombre")
                val inicio = json.getString("inicio")
                val fin = json.getString("fin")
                val distancia = json.getDouble("distancia_km")

                val mensaje = """
                    🗺️ Ruta detectada:
                    Nombre: $nombre
                    Inicio: $inicio
                    Fin: $fin
                    Distancia: ${"%.2f".format(distancia)} km
                """.trimIndent()

                tvResultado.text = mensaje

                // ✅ Abrir mapa con la ruta del QR
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("qr_inicio", inicio)
                    putExtra("qr_fin", fin)
                    putExtra("qr_nombre", nombre)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "El QR no pertenece a una ruta válida", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "QR no válido", Toast.LENGTH_SHORT).show()
        }
    }
}
