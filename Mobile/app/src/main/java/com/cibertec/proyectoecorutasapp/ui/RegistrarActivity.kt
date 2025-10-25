package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.UsuarioApi
import com.cibertec.proyectoecorutasapp.databinding.ActivityRegistrarBinding
import com.cibertec.proyectoecorutasapp.models.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegistrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val usuarioApi = ApiClient.create(UsuarioApi::class.java)

    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 👉 Botón de registro manual
        b.btnCrearCuenta.setOnClickListener {
            val nombre = b.etNombreCompleto.text.toString().trim()
            val apellido = b.etApellido.text.toString().trim()  // Obtener el apellido
            val email = b.etCorreo.text.toString().trim()
            val pass = b.etContrasena.text.toString().trim()
            val confirmar = b.etConfirmarContrasena.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || confirmar.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                toast(getString(R.string.msg_complete_fields))
                return@setOnClickListener
            }

            if (pass != confirmar) {
                toast(getString(R.string.msg_password_mismatch))
                return@setOnClickListener
            }

            // Crear usuario con nombre y apellido
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {

                    val nuevoUsuario = Usuario(
                        id_usuario = null,
                        nombre = nombre,
                        apellido = apellido,
                        correo = email,
                        password = pass,
                        rol = Usuario.Rol.usuario,
                        fecha_registro = null
                    )

                    registrarEnBackend(nuevoUsuario)
                }
                .addOnFailureListener {
                    toast(getString(R.string.msg_error, it.message ?: "Error desconocido"))
                }
        }

        // 👉 Registro con Google
        b.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun registrarEnBackend(usuario: Usuario) {
        usuarioApi.registrarUsuario(usuario).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    toast(getString(R.string.msg_user_registered))
                    startActivity(Intent(this@RegistrarActivity, LoginActivity::class.java))
                    finish()
                } else {
                    toast("Error al registrar en backend: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                toast("Error de red: ${t.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        val firebaseUser = auth.currentUser
                        val fullName = firebaseUser?.displayName ?: ""

                        // Separar nombre y apellido
                        val nameParts = fullName.split(" ")
                        val nombre = if (nameParts.isNotEmpty()) nameParts[0] else ""
                        val apellido = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else ""

                        val nuevoUsuario = Usuario(
                            id_usuario = null,
                            nombre = nombre,
                            apellido = apellido,
                            correo = firebaseUser?.email ?: "",
                            password = "",
                            rol = Usuario.Rol.usuario,
                            fecha_registro = null
                        )

                        registrarEnBackend(nuevoUsuario)
                    }
                    .addOnFailureListener {
                        toast("Error: ${it.message}")
                    }


            } catch (e: ApiException) {
                toast("Error Google Sign-In: ${e.message}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
