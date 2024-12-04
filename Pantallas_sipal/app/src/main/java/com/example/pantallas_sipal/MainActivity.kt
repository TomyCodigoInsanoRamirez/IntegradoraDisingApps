package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pantallas_sipal.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var correo = ""
    var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var usuario =""
        var passwordRecivida =""
        var AlumnoEncontrado = false
        var DocenteEncontrado = false
        val queue = Volley.newRequestQueue(this@MainActivity)
        binding.btnIniciarSesion.setOnClickListener {
            usuario = binding.edtCorreo.text.toString().trim();
            passwordRecivida = binding.edtContraseA.text.toString().trim()
            val builder = AlertDialog.Builder(this@MainActivity)
            if(usuario == "" || passwordRecivida == ""){
                builder.setTitle("CAMPOS INCOMPLETOS")
                builder.setMessage("Debes llenar los dos campos. Correo y contraseña.")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("De acuerdo"){dialog,_->}
                builder.show()
            }else{

                println("Solicitud enviadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                lifecycleScope.launch {
                    try {
                        println("buscando informaciónnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
                        AlumnoEncontrado = realizarSolicitud(usuario, passwordRecivida)
                        if(AlumnoEncontrado){
                            println("Se encontró un alumno")
                            val intent = Intent(this@MainActivity, Vista_alumno::class.java)
                            intent.putExtra("correoReferencia",usuario)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }else if(realizarSolicitudDocente(usuario,passwordRecivida)){
                            println("Se encontró un docente")
                            Toast.makeText(this@MainActivity,"Llendo a pantalla docente",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, Vista_Docente::class.java)
                            //intent.putExtra("correoReferencia",usuario)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }else{
                            builder.setTitle("CREDENCIALES INCORRECTAS")
                            builder.setMessage("Asegurate de haber escrito correctamente tu correo y contraseña")
                            builder.setPositiveButton("De acuerdo"){dialog,_->

                            }
                            builder.show()
                        }
                        // DocenteEncontrado= realizarSolicitudDocente(usuario,passwordRecivida)
                    } catch (e: Exception) {
                        println("Error durante la solicitud: ${e.message}")
                    }
                }

            }
        }
    }

    suspend fun realizarSolicitud(usuario: String, passwordRecibida: String): Boolean {
        return suspendCoroutine { continuation ->
            val queue = Volley.newRequestQueue(this@MainActivity)
            val endPointDatosAlumno = "http://192.168.41.75:8080/v3/alumnos"
            val metodo = Request.Method.GET
            val body = null
            val listener = Response.Listener<JSONObject> { resultado ->
                try {
                    //var correo = ""
                    //var password = ""
                    var alumnoEncontrado = false
                    for (i in 0 until resultado.getJSONObject("alumnosResponse").getJSONArray("alumnos").length()) {
                        val alumno = resultado.getJSONObject("alumnosResponse").getJSONArray("alumnos").getJSONObject(i)
                        correo = alumno.getString("correo")
                        password = alumno.getString("password")
                        if (usuario == correo && password == passwordRecibida) {
                            alumnoEncontrado = true
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

    suspend fun realizarSolicitudDocente(usuario: String, passwordRecibida: String): Boolean {
        return suspendCoroutine { continuation ->
            val queue = Volley.newRequestQueue(this@MainActivity)
            val endPointDatosDocente = "http://192.168.41.75:8080/v4/docente"
            val metodo = Request.Method.GET
            val body = null
            val listener = Response.Listener<JSONObject> { resultado ->
                try {
                    //var correo = ""
                    //var password = ""
                    var docenteEncontrado = false
                    for (i in 0 until resultado.getJSONObject("docenteResponse").getJSONArray("docentes").length()) {
                        val doc = resultado.getJSONObject("docenteResponse").getJSONArray("docentes").getJSONObject(i)
                        correo = doc.getString("correo")
                        password = doc.getString("password")
                        if (usuario == correo && password == passwordRecibida) {
                            docenteEncontrado = true
                            break
                        }
                    }
                    continuation.resume(docenteEncontrado)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
            val error = Response.ErrorListener { error ->
                continuation.resumeWithException(Exception(error.toString()))
            }

            val solicitud = object : JsonObjectRequest(metodo, endPointDatosDocente, body, listener, error) {
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