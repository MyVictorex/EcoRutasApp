package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.UsuarioApi
import com.cibertec.proyectoecorutasapp.databinding.ActivityLoginBinding
import com.cibertec.proyectoecorutasapp.models.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private val usuarioApi = ApiClient.create(UsuarioApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        // Registro
        b.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }

        // Login con correo y contraseña
        b.btnIniciarSesion.setOnClickListener {
            val email = b.etCorreo.text?.toString()?.trim().orEmpty()
            val pass = b.etContrasena.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                toast(getString(R.string.msg_complete_fields))
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { toast(getString(R.string.msg_error, it.message ?: "")) }
        }

        // Login con Google
        b.btnGoogle.setOnClickListener {
            val signInIntent = googleClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
    }

    // Launcher para Google Sign-In
    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account)
            } else {
                toast("Error al iniciar sesión con Google")
            }
        }

    // Autenticación con Firebase usando Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val fullName = firebaseUser?.displayName ?: ""
                    val email = firebaseUser?.email

                    // Registrar en backend
                    loginGoogleBackend(account?.idToken, fullName, email)
                } else {
                    toast("Fallo la autenticación con Firebase")
                }
            }
    }

    // Registrar usuario en el backend tras login con Google
    private fun loginGoogleBackend(idToken: String?, displayName: String?, email: String?) {
        if (idToken.isNullOrEmpty()) return

        // Separar nombre y apellido del displayName
        val nameParts = displayName?.split(" ") ?: listOf()
        val nombre = nameParts.getOrNull(0) ?: ""
        val apellido = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else ""

        // Crear el objeto Usuario
        val usuario = Usuario(
            id_usuario = null,
            nombre = nombre,
            apellido = apellido,
            correo = email ?: "",
            password = null,
            rol = Usuario.Rol.usuario,
            fecha_registro = null
        )

        // Crear el Map para enviar a la API
        val tokenMap = mapOf("idToken" to idToken)

        // Enviar la solicitud a la API de login con Google
        usuarioApi.loginGoogle(tokenMap).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    toast("Bienvenido ${usuario.nombre}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    toast("Error en backend: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                toast("Error de red: ${t.message}")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
