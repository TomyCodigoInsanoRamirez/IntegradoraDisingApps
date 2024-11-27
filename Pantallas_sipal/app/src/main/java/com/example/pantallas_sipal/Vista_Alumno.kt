package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding
import com.google.android.material.navigation.NavigationView

class Vista_alumno : AppCompatActivity() {
    private lateinit var binding: ActivityVistaAlumnoBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accediendo a las vistas a través de binding
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itmPerfil -> {
                    // Navegar a la actividad Perfil
                    val intent = Intent(this@Vista_alumno, Perfil::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(binding.navigationView) // Cerrar el drawer
                    true
                }
                R.id.itmCerrarSesion -> {
                    // Navegar a la actividad MainActivity
                    val intent = Intent(this@Vista_alumno, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(binding.navigationView) // Cerrar el drawer
                    finish() // Opcional: Cerrar la actividad actual
                    true
                }
                else -> false
            }
        }
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(binding.navigationView) // Abre el drawer
        }
    }
}
