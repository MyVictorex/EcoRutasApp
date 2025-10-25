package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }


        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleClient = GoogleSignIn.getClient(this, gso)

        b.btnIniciarSesion.setOnClickListener {
            val email = b.etCorreo.text?.toString()?.trim().orEmpty()
            val pass = b.etContrasena.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                toast(getString(R.string.msg_complete_fields))
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { toast(getString(R.string.msg_error, it.message ?: "")) }
        }


        b.btnGoogle.setOnClickListener {
            val signInIntent = googleClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
    }


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

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Bienvenido ${auth.currentUser?.displayName}")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    toast("Fallo la autenticación con Firebase")
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun RedireccionarMain(nombre: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nombreUsuario", nombre)
        startActivity(intent)
        finish()
    }



    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
