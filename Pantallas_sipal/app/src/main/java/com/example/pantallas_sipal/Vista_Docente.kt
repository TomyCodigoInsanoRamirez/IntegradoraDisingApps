package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding
import com.example.pantallas_sipal.databinding.ActivityVistaDocenteBinding
import com.google.android.material.navigation.NavigationView

class Vista_Docente : AppCompatActivity() {
    private lateinit var binding: ActivityVistaDocenteBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaDocenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.main
        navigationView = binding.navigationViewDocente
        toolbar = binding.toolbar


        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Diseño de Apps"

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itmPerfilProf -> {
                    // Navegar a la actividad Perfil
                    val intent = Intent(this@Vista_Docente, Perfil_Docente::class.java)
//                    intent.putExtra("id", id)
//                    intent.putExtra("primerNombre", primerNombre)
//                    intent.putExtra("segundoNombre", nombre2)
//                    intent.putExtra("primerApellido", apellido1)
//                    intent.putExtra("segundoApellido", apellido2)
//                    intent.putExtra("correo", correo)
//                    intent.putExtra("password", password)
//                    intent.putExtra("grado", grado)
//                    intent.putExtra("grupo", grupo)
//                    intent.putExtra("sexo", sexo)
//                    intent.putExtra("id_grupo", id_grupo)
//                    intent.putExtra("carrera", carrera)
//                    intent.putExtra("estado", estado)

                    startActivity(intent)
                    drawerLayout.closeDrawer(binding.navigationViewDocente) // Cerrar el drawer
                    true
                }
                R.id.itmCerrarSesionProf -> {
                    // Navegar a la actividad MainActivity
                    val intent = Intent(this@Vista_Docente, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(binding.navigationViewDocente) // Cerrar el drawer
                    finish() // Opcional: Cerrar la actividad actual
                    true
                }
                else -> false
            }
        }
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(binding.navigationViewDocente) // Abre el drawer
        }

    }
}