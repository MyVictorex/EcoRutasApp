package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.databinding.ActivityPerfilBinding
import com.cibertec.proyectoecorutasapp.models.Usuario
import com.cibertec.proyectoecorutasapp.repository.UsuarioRepository

class PerfilActivity : AppCompatActivity() {

    private lateinit var b: ActivityPerfilBinding
    private lateinit var repository: UsuarioRepository
    private var usuarioActual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(b.root)

        repository = UsuarioRepository(this)

        // 🔹 Cargar el usuario actual
        cargarUsuarioActual()

        // 🔹 Modo editar
        b.btnEditarPerfil.setOnClickListener { mostrarModoEdicion(true) }

        // 🔹 Guardar cambios
        b.btnGuardarCambios.setOnClickListener { guardarCambios() }

        // 🔹 Cerrar sesión
        b.btnCerrarSesion.setOnClickListener { cerrarSesion() }

        // 🔹 Cancelar edición
        b.btnCancelarEdicion.setOnClickListener { mostrarModoEdicion(false) }
    }

    // --- Cargar usuario desde prefs o backend
    private fun cargarUsuarioActual() {
        val prefs = getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("usuario_id", -1)

        if (idUsuario != -1) {
            // 🔹 Intentar cargar localmente primero
            val localUsuario = repository.obtenerUsuarioLocal(idUsuario)
            if (localUsuario != null) {
                usuarioActual = localUsuario
                mostrarUsuario(localUsuario)
            }

            // 🔹 Luego intentar sincronizar con backend
            repository.obtenerUsuarioPorId(
                idUsuario,
                onSuccess = { usuario ->
                    usuarioActual = usuario
                    mostrarUsuario(usuario)
                },
                onError = {
                    if (localUsuario == null) {
                        Toast.makeText(this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
        }
    }



    // --- Mostrar usuario en vistas y EditText
    private fun mostrarUsuario(usuario: Usuario) {
        // TextViews
        b.tvNombreUsuario.text = "${usuario.nombre} ${usuario.apellido ?: ""}"
        b.tvCorreoUsuario.text = usuario.correo

        // EditText (rellena aunque no esté en modo edición)
        b.etNombrePerfil.setText(usuario.nombre)
        b.etApellidoPerfil.setText(usuario.apellido ?: "")
        b.etCorreoPerfil.setText(usuario.correo)
        b.etContrasenaPerfil.setText(usuario.password ?: "")
    }

    // --- Alternar entre modo vista y edición
    private fun mostrarModoEdicion(editar: Boolean) {
        if (editar) {
            b.layoutDatos.visibility = View.GONE
            b.layoutEdicion.visibility = View.VISIBLE

            // 🔹 Rellenar EditText cada vez que se activa la edición
            usuarioActual?.let { usuario ->
                b.etNombrePerfil.setText(usuario.nombre)
                b.etApellidoPerfil.setText(usuario.apellido ?: "")
                b.etCorreoPerfil.setText(usuario.correo)
                b.etContrasenaPerfil.setText(usuario.password ?: "")
            }

        } else {
            b.layoutDatos.visibility = View.VISIBLE
            b.layoutEdicion.visibility = View.GONE
        }
    }

    // --- Guardar cambios en backend y local
    private fun guardarCambios() {
        val usuario = usuarioActual?.copy(
            nombre = b.etNombrePerfil.text.toString(),
            apellido = b.etApellidoPerfil.text.toString(),
            correo = b.etCorreoPerfil.text.toString(),
            password = b.etContrasenaPerfil.text.toString()
        ) ?: return

        repository.actualizarUsuario(
            usuario,
            onSuccess = {
                Toast.makeText(this, "Perfil actualizado ✅", Toast.LENGTH_SHORT).show()
                usuarioActual = usuario
                mostrarModoEdicion(false)
                mostrarUsuario(usuario)
            },
            onError = {
                Toast.makeText(this, "Error al actualizar: $it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- Cerrar sesión
    private fun cerrarSesion() {
        val prefs = getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE)
        prefs.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada 👋", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
