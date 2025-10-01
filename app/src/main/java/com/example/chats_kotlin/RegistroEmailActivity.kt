package com.example.chats_kotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chats_kotlin.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import org.intellij.lang.annotations.Pattern


class RegistroEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Crear instancia de Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        // Evento del usuario en registro
        binding.btnRegistrar.setOnClickListener {
            validarInformacion()
        }
    }

    // Crear 4 variables para el registro
    private var nombres = " "
    private var email = " "
    private var password = " "
    private var r_password = " "

    private fun validarInformacion(){
        nombres = binding.etNombres.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        r_password = binding.etRPassword.text.toString().trim()

        // Validar campos
        if (nombres.isEmpty()){
            binding.etNombres.error = "Ingrese su nombre"
            binding.etNombres.requestFocus()

        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Correo invalido"
            binding.etEmail.requestFocus()

        }else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese una contraseña"
            binding.etPassword.requestFocus()

        }else if (r_password.isEmpty()){
            binding.etRPassword.error = "Repita la contraseña"
            binding.etRPassword.requestFocus()

        }else if (password != r_password){
            binding.etRPassword.error = "Las contraseñas no coinciden"
            binding.etRPassword.requestFocus()

        }else{
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                actualizarInformacion()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Fallo la creación de la cuenta debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Guardando información")
    }


}


