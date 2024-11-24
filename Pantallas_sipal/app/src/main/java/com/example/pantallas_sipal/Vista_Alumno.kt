package com.example.pantallas_sipal

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding

class Vista_alumno : AppCompatActivity() {
    private lateinit var binding: ActivityVistaAlumnoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}