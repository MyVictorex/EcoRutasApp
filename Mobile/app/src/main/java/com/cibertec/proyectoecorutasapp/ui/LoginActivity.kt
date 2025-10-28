package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.UsuarioApi
import com.cibertec.proyectoecorutasapp.data.dao.UsuarioDao
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
    private lateinit var usuarioDao: UsuarioDao
    private val usuarioApi = ApiClient.create(UsuarioApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()
        usuarioDao = UsuarioDao(this)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        // Ir a registro
        b.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }

        // Login manual (correo y contraseña)
        b.btnIniciarSesion.setOnClickListener {
            val email = b.etCorreo.text.toString().trim()
            val pass = b.etContrasena.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                toast(getString(R.string.msg_complete_fields))
                return@setOnClickListener
            }

            // 🔹 Intentar login con Firebase primero
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    loginBackend(email, pass)
                }
                .addOnFailureListener {
                    // 🔸 Sin conexión o error → intentar login local
                    val localUser = usuarioDao.autenticar(email, pass)
                    if (localUser != null) {
                        guardarUsuarioSesion(localUser)
                        toast("Inicio de sesión local exitoso")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        toast("Credenciales incorrectas o sin conexión")
                    }
                }
        }

        // Login con Google
        b.btnGoogle.setOnClickListener {
            val signInIntent = googleClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
    }

    // Launcher para el login de Google
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

    // Autenticación en Firebase con Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginGoogleBackend(
                        idToken = account?.idToken,
                        displayName = account?.displayName,
                        email = account?.email
                    )
                } else {
                    toast("Error al autenticar con Google")
                }
            }
    }

    // 🔹 Login contra el backend (correo y contraseña)
    private fun loginBackend(email: String, password: String) {
        val credenciales = mapOf("correo" to email, "password" to password)

        usuarioApi.login(credenciales).enqueue(object : Callback<com.cibertec.proyectoecorutasapp.models.LoginResponse> {
            override fun onResponse(
                call: Call<com.cibertec.proyectoecorutasapp.models.LoginResponse>,
                response: Response<com.cibertec.proyectoecorutasapp.models.LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!.usuario
                    if (usuario != null) {
                        usuarioDao.insertar(usuario)
                        guardarUsuarioSesion(usuario)
                        toast("Bienvenido ${usuario.nombre}")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        toast("Usuario no encontrado en respuesta del servidor")
                    }
                } else {
                    toast("Error en backend: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<com.cibertec.proyectoecorutasapp.models.LoginResponse>, t: Throwable) {
                val localUser = usuarioDao.autenticar(email, password)
                if (localUser != null) {
                    guardarUsuarioSesion(localUser)
                    toast("Inicio de sesión local (sin conexión)")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    toast("Error de red o usuario no encontrado")
                }
            }
        })
    }

    // 🔹 Login con Google hacia backend + guardar localmente
    private fun loginGoogleBackend(idToken: String?, displayName: String?, email: String?) {
        if (idToken.isNullOrEmpty()) {
            toast("Token inválido de Google")
            return
        }

        val tokenMap = mapOf("idToken" to idToken)

        usuarioApi.loginGoogle(tokenMap).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
                    usuarioDao.insertar(usuario)
                    guardarUsuarioSesion(usuario)
                    toast("Bienvenido ${usuario.nombre}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Si backend no lo tiene, crearlo localmente
                    val nameParts = displayName?.split(" ") ?: listOf()
                    val nuevoUsuario = Usuario(
                        id_usuario = null,
                        nombre = nameParts.getOrNull(0) ?: "",
                        apellido = nameParts.getOrNull(1) ?: "",
                        correo = email ?: "",
                        password = "",
                        rol = Usuario.Rol.usuario,
                        fecha_registro = null
                    )
                    usuarioDao.insertar(nuevoUsuario)
                    guardarUsuarioSesion(nuevoUsuario)
                    toast("Cuenta de Google sincronizada localmente")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                toast("Error de conexión: ${t.message}")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("usuario_id", -1)
        if (userId != -1 || auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 🔹 Guardar usuario en SharedPreferences
    private fun guardarUsuarioSesion(usuario: Usuario) {
        val prefs = getSharedPreferences("EcoRutasPrefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("usuario_id", usuario.id_usuario ?: -1)
            .putString("usuario_nombre", "${usuario.nombre} ${usuario.apellido ?: ""}")
            .putString("usuario_correo", usuario.correo)
            .apply()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
