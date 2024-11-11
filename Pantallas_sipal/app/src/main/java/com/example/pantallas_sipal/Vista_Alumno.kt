package com.example.pantallas_sipal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding

class Vista_Alumno : AppCompatActivity() {
    private lateinit var binding: ActivityVistaAlumnoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}