package com.example.chats_kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chats_kotlin.databinding.ActivityMainBinding
import com.example.chats_kotlin.databinding.ActivityOpcionesLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class OpcionesLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInInClient = GoogleSignIn.getClient(this, gso)

        comprobarSesion()

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.opcionEmail.setOnClickListener{
            startActivity(Intent(applicationContext, LoginEmailActivity::class.java))
        }

        // Ingresar con Google
        binding.opcionGoogle.setOnClickListener {
            iniciarGoogle()
        }
    }

    private fun iniciarGoogle() {
        val googleSignIntent = mGoogleSignInInClient.signInIntent
        googleSignInActivityResultLauncher.launch(googleSignIntent)
    }

    private val googleSignInActivityResultLauncher = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticarCuentaGoogle(cuenta.idToken)

            }catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autenticarCuentaGoogle(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { authResultado ->
                // Condicion pra probar el user
                if (authResultado.additionalUserInfo!!.isNewUser) {
                    actualizarInfoUsuario()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    private fun actualizarInfoUsuario() {
        progressDialog.setMessage("Guardando información")

        val uidU = firebaseAuth.uid
        val nombresU = firebaseAuth.currentUser!!.displayName
        val emailU = firebaseAuth.currentUser!!.email
        val tiempoR = Constantes.obtenerTiempoDelD()

        // Enviar informacion a Firebase
        val datosUsuarios = HashMap<String, Any>()
        datosUsuarios["uid"] = "$uidU"
        datosUsuarios["nombres"] = "$nombresU"
        datosUsuarios["email"] = "$emailU"
        datosUsuarios["tiempoR"] = "$tiempoR"
        datosUsuarios["proveedor"] = "Google"
        datosUsuarios["estado"] = "Online"

        // Guardamos la informacion en Firebase
        val reference = FirebaseDatabase.getInstance().getReference("usuarios")
        reference.child(uidU!!)
            .setValue(datosUsuarios)

            .addOnCompleteListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

            .addOnFailureListener { e ->
                Toast.makeText(this, "Fallo la creación de la cuenta debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun comprobarSesion(){
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }




}










