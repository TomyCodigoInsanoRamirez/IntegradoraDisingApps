package com.example.pantallas_sipal

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding
import com.google.android.material.navigation.NavigationView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Vista_alumno : AppCompatActivity() {
    private lateinit var binding: ActivityVistaAlumnoBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private var id = ""
    private var id_grupo = ""
    private var primerNombre = ""
    private var nombre2 = ""
    private var apellido1 = ""
    private var apellido2 = ""
    private var correo = ""
    private var password = ""
    private var grado = ""
    private var grupo = ""
    private var sexo = ""
    private var carrera=""
    private var estado=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val correoReferencia = intent.getStringExtra("correoReferencia")

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
                    intent.putExtra("id", id)
                    intent.putExtra("primerNombre", primerNombre)
                    intent.putExtra("segundoNombre", nombre2)
                    intent.putExtra("primerApellido", apellido1)
                    intent.putExtra("segundoApellido", apellido2)
                    intent.putExtra("correo", correo)
                    intent.putExtra("password", password)
                    intent.putExtra("grado", grado)
                    intent.putExtra("grupo", grupo)
                    intent.putExtra("sexo", sexo)
                    intent.putExtra("id_grupo", id_grupo)
                    intent.putExtra("carrera", carrera)
                    intent.putExtra("estado", estado)

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

        lifecycleScope.launch {
            try {
                encontrarID(correoReferencia.toString())
                binding.imgQR.setImageBitmap(generateQRCode(id))
            } catch (e: Exception) {
                println("Error durante la solicitud: ${e.message}")
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val size = 512 // Tamaño del código QR
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun encontrarID(correoReferencia: String): Boolean {
        return suspendCoroutine { continuation ->
            val queue = Volley.newRequestQueue(this@Vista_alumno)
            val endPointDatosAlumno = "http://192.168.100.40:8080/v3/alumnos"
            val metodo = Request.Method.GET
            val body = null
            val listener = Response.Listener<JSONObject> { resultado ->
                try {
                    //var correo = ""
                    //var password = ""
                    var alumnoEncontrado = false
                    for (i in 0 until resultado.getJSONObject("alumnosResponse").getJSONArray("alumnos").length()) {
                        val alumno = resultado.getJSONObject("alumnosResponse").getJSONArray("alumnos").getJSONObject(i)
                        //correo = alumno.getString("correo")
                        if(alumno.getString("correo") == correoReferencia){
                            id = alumno.getString("id_alumno")

                            primerNombre=alumno.getString("primerNombre")
                            nombre2=alumno.getString("segundoNombre")
                            apellido1=alumno.getString("primerApellido")
                            apellido2=alumno.getString("segundoApellido")
                            correo=alumno.getString("correo")
                            password=alumno.getString("password")
                            sexo=alumno.getString("sexo")
                            id_grupo = alumno.getJSONObject("grupos").getString("id_grupo")
                            grado=alumno.getJSONObject("grupos").getString("grado")
                            grupo=alumno.getJSONObject("grupos").getString("grupo")
                            carrera=alumno.getJSONObject("grupos").getString("carrera")
                            estado=alumno.getJSONObject("grupos").getString("estado")

                            println("VALOR DE IDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
                            println(id)
                            break
                        }
                    }
                    continuation.resume(alumnoEncontrado)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
            val error = Response.ErrorListener { error ->
                continuation.resumeWithException(Exception(error.toString()))
            }

            val solicitud = object : JsonObjectRequest(metodo, endPointDatosAlumno, body, listener, error) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val auth = "root:root123"
                    val encodedAuth = Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
                    headers["Authorization"] = "Basic $encodedAuth"
                    return headers
                }
            }
            queue.add(solicitud)
        }
    }
}
