package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pantallas_sipal.databinding.ActivityPerfilDocenteBinding
import com.example.pantallas_sipal.databinding.ActivityVistaDocenteBinding

class Perfil_Docente : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilDocenteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilDocenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditarDocente.setOnClickListener {
            intent = Intent(this@Perfil_Docente, Editar_Datos_Docente::class.java)
            //intent.putExtra("correoReferencia",correo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        binding.btnAtrasDocente.setOnClickListener {
            intent = Intent(this@Perfil_Docente, Vista_Docente::class.java)
            //intent.putExtra("correoReferencia",correo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}