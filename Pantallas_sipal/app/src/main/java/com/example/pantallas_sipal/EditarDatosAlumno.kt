package com.example.pantallas_sipal

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pantallas_sipal.databinding.ActivityEditarDatosAlumnoBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class EditarDatosAlumno : AppCompatActivity() {
    private lateinit var binding: ActivityEditarDatosAlumnoBinding
    private var id = ""
    private var id_grupo = ""
    private var nombre1modificado = ""
    private var nombre2modificado = ""
    private var apellido1modificado = ""
    private var apellido2modificado = ""
    private var contraActual = ""
    private var contraNueva = ""
    private var confirmarContra = ""
    private var sexo = ""
    private var grado = ""
    private var grupo = ""
    private var correo = ""
    private var carrera = ""
    private var estado = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarDatosAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("id").toString()
        id_grupo = intent.getStringExtra("id_grupo").toString()
        val nombre = intent.getStringExtra("primerNombre")
        val nombre2 = intent.getStringExtra("segundoNombre")
        val apellido1 = intent.getStringExtra("primerApellido")
        val apellido2 = intent.getStringExtra("segundoApellido")
        val password = intent.getStringExtra("password")
        grado = intent.getStringExtra("grado").toString()
        grupo = intent.getStringExtra("grupo").toString()
        sexo = intent.getStringExtra("sexo").toString()
        carrera = intent.getStringExtra("carrera").toString()
        estado = intent.getStringExtra("estado").toString()
        correo = intent.getStringExtra("correo").toString()

        binding.txtNombreActual.setText(nombre)
        binding.txtSegundoNombre.setText(nombre2)
        binding.txtApellido1.setText(apellido1)
        binding.txtApellido2.setText(apellido2)


        binding.btnGuardarDatos.setOnClickListener {
             nombre1modificado = binding.edtNuevoNombre.text.toString()
             nombre2modificado = binding.edtNuevoSegundoNombre.text.toString()
             apellido1modificado = binding.edtNuevoApellido.text.toString()
             apellido2modificado = binding.edtNuevoApellido2.text.toString()
             contraActual=binding.edtContra.text.toString()
             contraNueva = binding.edtCambiarContra.text.toString()
             confirmarContra = binding.edtConfirmarContra.text.toString()
             if(nombre1modificado == ""){
                 nombre1modificado = nombre.toString()
             }
             if(nombre2modificado == ""){
                 nombre2modificado = nombre2.toString()
             }
             if(apellido1modificado == ""){
                apellido1modificado = apellido1.toString()
            }

            if(apellido2modificado == ""){
                apellido2modificado = apellido2.toString()
            }
            contraActual = password.toString()
            println("uno: "+binding.edtContra.text.toString())
            if(binding.edtContra.text.toString() != ""){
                println("dos: "+binding.edtContra.text.toString()+" "+password)
                if(binding.edtContra.text.toString() != password){
                    val builder = AlertDialog.Builder(this@EditarDatosAlumno)
                    builder.setTitle("CONTRASEÑA INCORRECTA")
                    builder.setMessage("La contraseña que ingresaste no es la misma que tenemos registrada.")
                    builder.setPositiveButton("De acuerdo"){dialog,_->

                    }
                    builder.show()
                    return@setOnClickListener // Detenemos el flujo

                }else{
                    println("tres: "+binding.edtCambiarContra.text.toString()+" "+binding.edtConfirmarContra.text.toString())
                    if(binding.edtCambiarContra.text.toString() != binding.edtConfirmarContra.text.toString()){
                        val builder = AlertDialog.Builder(this@EditarDatosAlumno)
                        builder.setTitle("INCOMPATIBILIDAD DE CONTRASEÑAS")
                        builder.setMessage("La contraseña en el campor de nueva contraseña debe ser el mismo que en el de confirmar.")
                        builder.setPositiveButton("De acuerdo"){dialog,_->

                        }
                        builder.show()

                        return@setOnClickListener // Detenemos el flujo
                    }else{
                        contraActual = contraNueva
                        //cambiar()
                    }
                }
            }


            lifecycleScope.launch {
                try {
                    actualizarAlumno(id)
                    //binding.imgQR.setImageBitmap(generateQRCode(id))
                    cambiar()
                } catch (e: Exception) {
                    println("Error durante la solicitud: ${e.message}")
                }
            }


            //Toast.makeText(this@EditarDatosAlumno,"Datos editados con exito", Toast.LENGTH_SHORT).show()
        }

    }
    suspend fun actualizarAlumno(id: String): Boolean {
        return suspendCoroutine { continuation ->
            val queue = Volley.newRequestQueue(this@EditarDatosAlumno)
            val endPointDatosAlumno = "http://192.168.100.40:8080/v3/alumnos/${id}"
            val metodo = Request.Method.PUT
            val body = JSONObject()
            val grupos = JSONObject()
            body.put("id_alumno", id)
            body.put("primerNombre", nombre1modificado)
            body.put("segundoNombre", nombre2modificado)
            body.put("primerApellido", apellido1modificado)
            body.put("segundoApellido", apellido2modificado)
            body.put("correo", correo)
            body.put("password", contraActual)
            body.put("sexo", sexo)
            grupos.put("id_grupo", id_grupo)
            grupos.put("grado", grado)
            grupos.put("grupos", grupo)
            grupos.put("carrera", carrera)
            grupos.put("estado", estado)
            body.put("grupos", grupos)

            val jsonString = body.toString()
            println(jsonString) // Solo para verificar el resultado en la consola
            val listener = Response.Listener<JSONObject> { resultado ->
                try {
                    //var correo = ""
                    //var password = ""
                    var alumnoEncontrado = false
                    Toast.makeText(this@EditarDatosAlumno, "Actualización exitosa"
                    , Toast.LENGTH_SHORT).show()
                    continuation.resume(alumnoEncontrado)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
            val error = Response.ErrorListener { error ->
                Toast.makeText(this@EditarDatosAlumno, "No se actualizó"
                    , Toast.LENGTH_SHORT).show()
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

    private fun cambiar(){
        val intent = Intent(this@EditarDatosAlumno, Perfil::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("primerNombre",nombre1modificado)
        intent.putExtra("segundoNombre",nombre2modificado)
        intent.putExtra("primerApellido",apellido1modificado)
        intent.putExtra("segundoApellido",apellido2modificado)
        intent.putExtra("correo",correo)
        intent.putExtra("sexo",sexo)
        intent.putExtra("grado",grado)
        intent.putExtra("grupo",grupo)
        startActivity(intent)
    }
}