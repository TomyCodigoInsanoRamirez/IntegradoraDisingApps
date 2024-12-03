package com.example.pantallas_sipal

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pantallas_sipal.databinding.ActivityVistaAlumnoBinding
import com.example.pantallas_sipal.databinding.ActivityVistaDocenteBinding
import com.google.android.material.navigation.NavigationView
import com.google.auth.oauth2.GoogleCredentials
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.math.BigInteger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Vista_Docente : AppCompatActivity() {
    private lateinit var binding: ActivityVistaDocenteBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private var foto = ""
    lateinit var posicionFechaDeHoy : BigInteger
    private lateinit var resultLauncher: ActivityResultLauncher<Intent> //QR
    lateinit var textoEscaneado : String //QR
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


        val btnScan = binding.btnEscanear
        //val tvResult = binding.tvResult

        // Registrar el resultado del escaneo QR
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val scanResult = IntentIntegrator.parseActivityResult(result.resultCode, data)
            if (scanResult != null && scanResult.contents != null) {
                // Actualizar el valor escaneado
                textoEscaneado = scanResult.contents
                //tvResult.text = "Resultado: ${scanResult.contents}"

                // Ejecutar la lógica dependiente del QR escaneado
                procesarQR(textoEscaneado)
            } else {
                //tvResult.text = "Escaneo cancelado"
            }
        }

        btnScan.setOnClickListener {
            IntentIntegrator(this@Vista_Docente).apply {
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // Escanear solo QR
                setPrompt("Escaneando código QR...")
                setBeepEnabled(true)
                setCameraId(0) // Usar la cámara trasera
                setBarcodeImageEnabled(false)
                setOrientationLocked(false)
                resultLauncher.launch(createScanIntent())
            }
        }

        binding.btnAsistencia.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val accessToken = withContext(Dispatchers.IO) { getAccessToken() }
                    val fecha = obtenerFecha().toString()
                    posicionFechaDeHoy = withContext(Dispatchers.IO) {
                        suspendCancellableCoroutine<BigInteger?> { continuation ->
                            findStringInRow(
                                "1iV8jHcPqhLqo_NN9vRMWSOATsna6o5etOk3BwX9jPBs",
                                accessToken,
                                1,
                                fecha
                            ) { columna ->
                                if (columna != null) {
                                    continuation.resume(columna.toBigInteger(), null)
                                } else {
                                    println("No se encontró la fecha '$fecha' en la fila 1")
                                    continuation.resume(null, null)
                                }
                            }
                        }
                    } ?: throw Exception("No se encontró la columna para la fecha actual")
                    for (i in 0 until 30){
                        ponerFaltas("1iV8jHcPqhLqo_NN9vRMWSOATsna6o5etOk3BwX9jPBs",accessToken,".",i,posicionFechaDeHoy)
                    }
                } catch (e: Exception) {
                    println("Error al procesar el QR: ${e.message}")
                }
            }
        }

    }


    private fun procesarQR(donde: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val accessToken = withContext(Dispatchers.IO) { getAccessToken() }
                val fecha = obtenerFecha().toString()
                lifecycleScope.launch {
                    try {
                        // Espera el resultado de encontrarFoto
                        val fotoEncontrada = encontrarFoto(donde)


                            // Decodifica y asigna la imagen solo si la foto fue encontrada
                            val bitmap = decodeBase64ToBitmap(foto)
                            if (bitmap != null) {
                                val builder = AlertDialog.Builder(this@Vista_Docente)
                                builder.setTitle("¿Deseas poner la asistencia?")
                                builder.setMessage("Asegurate que el codigo QR sea de la persona que aprece en la foto.")

                                val imageView = ImageView(this@Vista_Docente)
                                imageView.setImageBitmap(bitmap) // Reemplaza con tu imagen

                                // Convertir 400 dp a px
                                fun dpToPx(dp: Int, context: Context): Int {
                                    val density = context.resources.displayMetrics.density
                                    return (dp * density).toInt()
                                }

                                val widthPx = dpToPx(200, this@Vista_Docente)
                                val heightPx = dpToPx(200, this@Vista_Docente)

                                val frameLayout = FrameLayout(this@Vista_Docente)
                                val params = FrameLayout.LayoutParams(
                                    widthPx,  // Ancho en píxeles
                                    heightPx  // Alto en píxeles
                                )

                                params.gravity = Gravity.CENTER
                                imageView.layoutParams = params

                                frameLayout.addView(imageView)

                                builder.setView(frameLayout)

                                builder.setPositiveButton("No, no es él.") { dialog, _ -> dialog.dismiss() }
                                builder.setNegativeButton("Poner asistencia") { dialog, _ ->
                                    lifecycleScope.launch {
                                        posicionFechaDeHoy = withContext(Dispatchers.IO) {
                                            suspendCancellableCoroutine<BigInteger?> { continuation ->
                                                findStringInRow(
                                                    "1iV8jHcPqhLqo_NN9vRMWSOATsna6o5etOk3BwX9jPBs",
                                                    accessToken,
                                                    1,
                                                    fecha
                                                ) { columna ->
                                                    if (columna != null) {
                                                        continuation.resume(columna.toBigInteger(), null)
                                                    } else {
                                                        println("No se encontró la fecha '$fecha' en la fila 1")
                                                        continuation.resume(null, null)
                                                    }
                                                }
                                            }
                                        } ?: throw Exception("No se encontró la columna para la fecha actual")

                                        // Encontrar celda con el carácter especificado
                                        val (fila, columna) = withContext(Dispatchers.IO) {
                                            suspendCancellableCoroutine<Pair<BigInteger, BigInteger>?> { continuation ->
                                                findCellWithCharacter(
                                                    "1iV8jHcPqhLqo_NN9vRMWSOATsna6o5etOk3BwX9jPBs",
                                                    accessToken,
                                                    "A1:Z30",
                                                    donde
                                                ) { fila, columna ->
                                                    if (fila != null && columna != null) {
                                                        continuation.resume(Pair(fila.toBigInteger(), columna.toBigInteger()), null)
                                                    } else {
                                                        println("No se encontró el carácter '$donde' en el rango especificado.")
                                                        continuation.resume(null, null)
                                                    }
                                                }
                                            }
                                        } ?: throw Exception("No se encontró la celda con el carácter '$donde'")

                                        // Actualizar celda en la hoja de cálculo
                                        updateCell(
                                            "1iV8jHcPqhLqo_NN9vRMWSOATsna6o5etOk3BwX9jPBs",
                                            accessToken,
                                            "X",
                                            fila,
                                            posicionFechaDeHoy
                                        )

                                        println("Celda actualizada correctamente")
                                    }
                                }

                                builder.create().show()

                            } else {
                                println("Error al decodificar la imagen base64.")
                            }

                    } catch (e: Exception) {
                        println("Error durante la solicitud: ${e.message}")
                    }
                }


            } catch (e: Exception) {
                println("Error al procesar el QR: ${e.message}")
            }
        }
    }


    private fun getAccessToken(): String {
        val credentialsStream: InputStream = resources.openRawResource(R.raw.client_secret_679108842925_n8pe917ugtu7c9mvdktqjm32hgf654gh_apps_googleusercontent_com)
        val credentials = GoogleCredentials.fromStream(credentialsStream)
            .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }

    private fun updateCell(spreadsheetId: String, accessToken: String, word: String, rowIndex: BigInteger, columnIndex: BigInteger) {
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

        val requestBody = JSONObject().apply {
            put("requests", JSONArray().apply {
                put(JSONObject().apply {
                    put("updateCells", JSONObject().apply {
                        put("rows", JSONArray().apply {
                            put(JSONObject().apply { // Una sola celda
                                put("values", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("userEnteredValue", JSONObject().apply {
                                            put("stringValue", word)
                                        })
                                        put("userEnteredFormat", JSONObject().apply {
                                            put("textFormat", JSONObject().apply {
                                                put("fontSize", 10)
                                            })
                                        })
                                    })
                                })
                            })
                        })
                        put("fields", "userEnteredValue,userEnteredFormat")
                        put("range", JSONObject().apply {
                            put("sheetId", 0) // ID numérico de la hoja
                            put("startRowIndex", rowIndex.toInt()) // Índice base 0
                            put("startColumnIndex", columnIndex.toInt()) // Índice base 0
                            put("endRowIndex", rowIndex.toInt() + 1) // Rango de una sola celda
                            put("endColumnIndex", columnIndex.toInt() + 1)
                        })
                    })
                })
            })
        }


        val headers = mapOf("Authorization" to "Bearer $accessToken")

        val request = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener<JSONObject> { response ->
                println("Actualización exitosa: $response")
            },
            Response.ErrorListener { error ->
                println("Error en la actualización: ${error.message}")
            }) {
            override fun getHeaders(): Map<String, String> = headers
        }

        Volley.newRequestQueue(this).add(request)
    }


    private fun findCellWithCharacter(
        spreadsheetId: String,
        accessToken: String,
        range: String, // Rango como "A1:Z30"
        character: String,
        callback: (fila: Int?, columna: Int?) -> Unit
    ) {
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values:batchGet"
        val headers = mapOf("Authorization" to "Bearer $accessToken")
        val requestUrl = "$url?ranges=$range"

        val request = object : JsonObjectRequest(
            Request.Method.GET, requestUrl, null,
            Response.Listener<JSONObject> { response ->
                try {
                    val valueRanges = response.getJSONArray("valueRanges")
                    val values = valueRanges.getJSONObject(0).getJSONArray("values")

                    for (fila in 0 until values.length()) {
                        val row = values.getJSONArray(fila)

                        // Iterar sobre las columnas de la fila actual
                        for (columna in 0 until row.length()) {
                            if (row.getString(columna) == character) {
                                callback(fila, columna) // Devuelve fila y columna
                                return@Listener
                            }
                        }
                    }

                    // Si no se encuentra el carácter
                    callback(null, null)
                } catch (e: Exception) {
                    println("Error al procesar la respuesta: ${e.message}")
                    callback(null, null)
                }
            },
            Response.ErrorListener { error ->
                println("Error en la solicitud: ${error.message}")
                callback(null, null)
            }) {
            override fun getHeaders(): Map<String, String> = headers
        }

        Volley.newRequestQueue(this).add(request)
    }

    fun obtenerFecha(): String {
        val currentDate = LocalDate.now()
        return currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))  // Devuelve la fecha en formato personalizado
    }

    private fun findStringInRow(
        spreadsheetId: String,
        accessToken: String,
        rowNumber: Int, // Número de fila a buscar (1 para la primera fila, etc.)
        searchString: String, // Cadena a buscar
        callback: (columna: Int?) -> Unit
    ) {
        val range = "A$rowNumber:Z$rowNumber" // Rango de búsqueda, limitado a la fila
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values:batchGet"
        val headers = mapOf("Authorization" to "Bearer $accessToken")
        val requestUrl = "$url?ranges=$range"

        val request = object : JsonObjectRequest(
            Request.Method.GET, requestUrl, null,
            Response.Listener<JSONObject> { response ->
                try {
                    val valueRanges = response.getJSONArray("valueRanges")
                    val values = valueRanges.getJSONObject(0).getJSONArray("values")

                    // Solo se espera una fila en el rango
                    if (values.length() > 0) {
                        val row = values.getJSONArray(0)

                        // Iterar por las columnas de la fila
                        for (columna in 0 until row.length()) {
                            if (row.getString(columna) == searchString) {
                                callback(columna ) // Convertir índice a base 1
                                return@Listener
                            }
                        }
                    }

                    // Si no encuentra el string
                    callback(null)
                } catch (e: Exception) {
                    println("Error al procesar la respuesta: ${e.message}")
                    callback(null)
                }
            },
            Response.ErrorListener { error ->
                println("Error en la solicitud: ${error.message}")
                callback(null)
            }) {
            override fun getHeaders(): Map<String, String> = headers
        }

        Volley.newRequestQueue(this).add(request)
    }


    suspend fun encontrarFoto(id: String) : Boolean{
        return suspendCoroutine { continuation ->
            val queue = Volley.newRequestQueue(this@Vista_Docente)
            val endPointDatosAlumno = "http://192.168.21.75:8080/v3/alumnos/$id"
            val metodo = Request.Method.GET
            val body = null
            val listener = Response.Listener<JSONObject> { resultado ->
                try {
                    //var correo = ""
                    //var password = ""
                    var alumnoEncontrado = false
                    println("resultadooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo")
                    println(resultado)
                    foto = resultado.getJSONObject("alumnosResponse").getJSONArray("alumnos").getJSONObject(0).getString("foto")
                    println(foto)
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


    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    fun blurBitmap(bitmap: Bitmap, radius: Float, context: Context): Bitmap {
        val outputBitmap = Bitmap.createBitmap(bitmap)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createFromBitmap(renderScript, outputBitmap)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setRadius(radius)
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(outputBitmap)
        renderScript.destroy()
        return outputBitmap
    }


    private fun ponerFaltas(spreadsheetId: String, accessToken: String, word: String, rowIndex: Int, columnIndex: BigInteger) {
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

        val requestBody = JSONObject().apply {
            put("requests", JSONArray().apply {
                put(JSONObject().apply {
                    put("updateCells", JSONObject().apply {
                        put("rows", JSONArray().apply {
                            put(JSONObject().apply { // Una sola celda
                                put("values", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("userEnteredValue", JSONObject().apply {
                                            put("stringValue", word)
                                        })
                                        put("userEnteredFormat", JSONObject().apply {
                                            put("textFormat", JSONObject().apply {
                                                put("fontSize", 10)
                                            })
                                        })
                                    })
                                })
                            })
                        })
                        put("fields", "userEnteredValue,userEnteredFormat")
                        put("range", JSONObject().apply {
                            put("sheetId", 0) // ID numérico de la hoja
                            put("startRowIndex", rowIndex.toInt()) // Índice base 0
                            put("startColumnIndex", columnIndex.toInt()) // Índice base 0
                            put("endRowIndex", rowIndex.toInt() + 1) // Rango de una sola celda
                            put("endColumnIndex", columnIndex.toInt() + 1)
                        })
                    })
                })
            })
        }


        val headers = mapOf("Authorization" to "Bearer $accessToken")

        val request = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener<JSONObject> { response ->
                println("Actualización exitosa: $response")
            },
            Response.ErrorListener { error ->
                println("Error en la actualización: ${error.message}")
            }) {
            override fun getHeaders(): Map<String, String> = headers
        }

        Volley.newRequestQueue(this).add(request)
    }
}