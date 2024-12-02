package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pantallas_sipal.databinding.ActivityPerfilBinding
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding

class Perfil : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val id_grupo = intent.getStringExtra("id_grupo")
        val nombre = intent.getStringExtra("primerNombre")
        val nombre2 = intent.getStringExtra("segundoNombre")
        val apellido1 = intent.getStringExtra("primerApellido")
        val apellido2 = intent.getStringExtra("segundoApellido")
        val correo = intent.getStringExtra("correo")
        val password = intent.getStringExtra("password")
        val grado = intent.getStringExtra("grado")
        val grupo = intent.getStringExtra("grupo")
        val sexo = intent.getStringExtra("sexo")
        val carrera = intent.getStringExtra("carrera")
        val estado = intent.getStringExtra("estado")

        binding.txtNombre.setText(nombre + " " + nombre2 + " " + apellido1 + " "
        + apellido2)
        binding.txtCorreo.setText(correo)
        binding.txtGrado.setText(grado + " " + grupo)
        binding.txtSexo.setText(sexo)

        binding.btnEditar.setOnClickListener {
            val intent = Intent(this@Perfil, EditarDatosAlumno::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra("id", id)
            intent.putExtra("id_grupo", id_grupo)
            intent.putExtra("primerNombre", nombre)
            intent.putExtra("segundoNombre", nombre2)
            intent.putExtra("primerApellido", apellido1)
            intent.putExtra("segundoApellido", apellido2)
            intent.putExtra("correo", correo)
            intent.putExtra("password", password)
            intent.putExtra("grado", grado)
            intent.putExtra("grupo", grupo)
            intent.putExtra("sexo", sexo)
            intent.putExtra("carrera", carrera)
            intent.putExtra("estado", estado)
            startActivity(intent)
        }

        binding.btnAtras.setOnClickListener {
            intent = Intent(this@Perfil, Vista_alumno::class.java)
            intent.putExtra("correoReferencia",correo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}