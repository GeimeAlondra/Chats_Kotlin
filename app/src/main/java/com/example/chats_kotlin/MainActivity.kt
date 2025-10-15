package com.example.chats_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chats_kotlin.Fragmentos.FragmentChats
import com.example.chats_kotlin.Fragmentos.FragmentPerfil
import com.example.chats_kotlin.Fragmentos.FragmentUsuarios
import com.example.chats_kotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser == null){
            irOpcionesLogin()
        }

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fragmento por defecto
        verFragmentoPerfil()

        binding.bottomNV.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.item_perfil -> {
                    // Visualizar el fragmento perfil
                    verFragmentoPerfil()
                    true
                }

                R.id.item_usuarios -> {
                    // Visualizar el fragmento usuarios
                    verFragmentoUsuario()
                    true
                }

                R.id.item_chats -> {
                    // Visualizar el fragmento chats
                    verFragmentoChat()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun irOpcionesLogin() {
        startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
        finishAffinity()
    }

    private fun verFragmentoPerfil(){
        binding.tvTitulo.text = "Perfil"

        val fragmentPerfil = FragmentPerfil()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id, fragmentPerfil, "Fragment Perfil")
        fragmentTransaction.commit()
    }

    private fun verFragmentoUsuario(){
        binding.tvTitulo.text = "Usuario"

        val fragmentUsuarios = FragmentUsuarios()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id, fragmentUsuarios, "Fragment Usuario")
        fragmentTransaction.commit()
    }

    private fun verFragmentoChat(){
        binding.tvTitulo.text = "Chat"

        val fragmentChats = FragmentChats()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id, fragmentChats, "Fragment Chat")
        fragmentTransaction.commit()
    }

}