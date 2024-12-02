package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pantallas_sipal.databinding.ActivityEditarDatosDocenteBinding

class Editar_Datos_Docente : AppCompatActivity() {
    private lateinit var binding: ActivityEditarDatosDocenteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarDatosDocenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuardarDatosDocente.setOnClickListener {
            intent = Intent(this@Editar_Datos_Docente, Perfil_Docente::class.java)
            //intent.putExtra("correoReferencia",correo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }
}