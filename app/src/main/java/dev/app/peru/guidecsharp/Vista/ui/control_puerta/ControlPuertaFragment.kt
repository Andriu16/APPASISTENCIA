package dev.app.peru.guidecsharp.Vista.ui.control_puerta

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSql
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSqlImagenes
import dev.app.peru.guidecsharp.AccesoSql.Modelo.RegistroPuertaData
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.Vista.ui.ver_control_puerta.VerControlPuertaFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.http2.Http2Reader
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.Conector
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.ConsumoApi
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.EmpleadoApiResponse
import java.sql.Timestamp

import android.widget.Spinner


class ControlPuertaFragment : Fragment() , Conector{


    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private lateinit var btnRegistrar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var rgTipoRegistro: RadioGroup
    private lateinit var rbIngreso: RadioButton
    private lateinit var rbSalida: RadioButton

    private lateinit var tilDni: TextInputLayout
    private lateinit var etDni: TextInputEditText

    private lateinit var etNombres: EditText

    private lateinit var etApellidos: EditText

    private lateinit var etarea: AutoCompleteTextView
    private lateinit var etmotivo_ingreso: EditText
    private lateinit var etPlacaVehiculo: EditText
    private lateinit var etObservaciones: EditText
    private lateinit var ivImagen1: ImageView
    private lateinit var ivImagen2: ImageView
    private lateinit var ivImagen3: ImageView
    private lateinit var ivImagen4: ImageView
    private lateinit var btnAgregarFoto1: Button
    private lateinit var btnAgregarFoto2: Button
    private lateinit var btnAgregarFoto3: Button
    private lateinit var btnAgregarFoto4: Button
    private lateinit var btnBuscarRENIEC: Button
    private lateinit var tilPersonalAutorizo: TextInputLayout

    private lateinit var spinnerPersonalAutorizo: Spinner



    private lateinit var consumoApi: ConsumoApi
    private var progressDialogReniec: ProgressDialog? = null



    private val listaNombresPersonal = mutableListOf<String>()
    private lateinit var adapterPersonalAutorizo: ArrayAdapter<String>


    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var isRunnableActive = false


    private var imagen1Bytes: ByteArray? = null
    private var imagen2Bytes: ByteArray? = null
    private var imagen3Bytes: ByteArray? = null
    private var imagen4Bytes: ByteArray? = null


    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var currentImageSlot = 0
    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var tempPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityResultLaunchers()

        setHasOptionsMenu(true)
        consumoApi = ConsumoApi(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_control_de_puerta, container, false)
        initViews(view)
        setupListeners()
        setFechaYHoraActual()
        setupAdapters()
        iniciarContadorTiempoReal()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarPersonalAutorizo()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menulista_ver_regitro_puertas, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ver_puertas -> {

                Toast.makeText(context, "Ver Registro De Puertas", Toast.LENGTH_SHORT).show()

                navegarAVerRegistrosPuertas()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun navegarAVerRegistrosPuertas() {

        findNavController().navigate(R.id.nav_ver_control_puerta)



    }

    private fun initViews(view: View) {

        btnFecha = view.findViewById(R.id.btnFecha)
        btnHora = view.findViewById(R.id.btnHora)
        btnRegistrar = view.findViewById(R.id.btnRegistrar)
        btnLimpiar = view.findViewById(R.id.btnLimpiar)

        rgTipoRegistro = view.findViewById(R.id.rgTipoRegistro)
        rbIngreso = view.findViewById(R.id.rbIngreso)
        rbSalida = view.findViewById(R.id.rbSalida)
        tilDni = view.findViewById(R.id.tilDni)
        etDni = view.findViewById(R.id.etDni)
        etarea = view.findViewById(R.id.actvEmpresaArea)
        etNombres = view.findViewById(R.id.etNombres)

        etApellidos= view.findViewById(R.id.etApellidos)

        etmotivo_ingreso = view.findViewById(R.id.etmotivo_ingreso)
        etPlacaVehiculo = view.findViewById(R.id.etPlacaVehiculo)
        etObservaciones = view.findViewById(R.id.etObservaciones)

        ivImagen1 = view.findViewById(R.id.ivImagen1)
        ivImagen2 = view.findViewById(R.id.ivImagen2)
        ivImagen3 = view.findViewById(R.id.ivImagen3)
        ivImagen4 = view.findViewById(R.id.ivImagen4)
        btnAgregarFoto1 = view.findViewById(R.id.btnAgregarFoto1)
        btnAgregarFoto2 = view.findViewById(R.id.btnAgregarFoto2)
        btnAgregarFoto3 = view.findViewById(R.id.btnAgregarFoto3)
        btnAgregarFoto4 = view.findViewById(R.id.btnAgregarFoto4)
        btnBuscarRENIEC = view.findViewById(R.id.btnBuscarRENIEC)

        tilPersonalAutorizo = view.findViewById(R.id.tilPersonalAutorizo)
        spinnerPersonalAutorizo = view.findViewById(R.id.spinnerPersonalAutorizo)
    }

    private fun setupAdapters() {

        adapterPersonalAutorizo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaNombresPersonal)

        adapterPersonalAutorizo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerPersonalAutorizo.adapter = adapterPersonalAutorizo
    }

    private fun cargarPersonalAutorizo() {
        viewLifecycleOwner.lifecycleScope.launch {
            val nombres = cargarPersonalAutorizoDesdeBD()
            if (nombres != null) {
                actualizarSelectorPersonal(nombres)
            } else {
                Toast.makeText(context, "Error al cargar lista de personal", Toast.LENGTH_SHORT).show()
                actualizarSelectorPersonal(listOf("Error al cargar"))
            }
        }
    }

    private suspend fun cargarPersonalAutorizoDesdeBD(): List<String>? {

        Log.d("PersonalAutorizo", "Iniciando carga de personal desde BD...")
        return withContext(Dispatchers.IO) {
            var queryResult: ConexionSqlImagenes.QueryResult? = null
            val nombres = mutableListOf<String>()
            nombres.add("Seleccione...")

            try {
                Log.d("PersonalAutorizo", "Estableciendo conexión...")
                val conexion = ConexionSqlImagenes("true")
                val sql = "SELECT emp_apellidos, emp_nombres FROM RRHH.EMPLEADO ORDER BY emp_apellidos, emp_nombres"
                Log.d("PersonalAutorizo", "Ejecutando consulta: $sql")

                queryResult = conexion.ejecutarConsultaParametrizada(sql, ArrayList())
                Log.d("PersonalAutorizo", "Consulta ejecutada.")

                val resultSet = queryResult.resultSet
                if (resultSet == null) {
                    Log.e("PersonalAutorizo", "ResultSet nulo.")
                    return@withContext null
                }

                Log.d("PersonalAutorizo", "Procesando ResultSet...")
                var rowCount = 0
                while (resultSet.next()) {
                    rowCount++
                    val apellidos = resultSet.getString("emp_apellidos") ?: ""
                    val nombresEmp = resultSet.getString("emp_nombres") ?: ""
                    val nombreCompleto = "$apellidos $nombresEmp".trim()
                    Log.d("PersonalAutorizo", "Fila $rowCount: $nombreCompleto")
                    if (nombreCompleto.isNotEmpty()) {
                        nombres.add(nombreCompleto)
                    }
                }
                Log.i("PersonalAutorizo", "Procesamiento finalizado. Total filas leídas: $rowCount. Nombres añadidos: ${nombres.size -1}")

                nombres

            } catch (e: SQLException) { Log.e("PersonalAutorizo", "Error SQL", e); null
            } catch (e: Exception) { Log.e("PersonalAutorizo", "Error general", e); null
            } finally { queryResult?.closeAll(); Log.d("PersonalAutorizo", "Recursos cerrados.") }
        }
    }

    private fun actualizarSelectorPersonal(nuevosNombres: List<String>) {
        Log.d("PersonalAutorizo", "Actualizando spinner. Recibidos: ${nuevosNombres.size} elementos.")
        listaNombresPersonal.clear()
        listaNombresPersonal.addAll(nuevosNombres)
        Log.d("PersonalAutorizo", "listaNombresPersonal ahora tiene: ${listaNombresPersonal.size} elementos.")
        if (listaNombresPersonal.size > 1) Log.d("PersonalAutorizo", "Primeros nombres: ${listaNombresPersonal.take(5)}")


        adapterPersonalAutorizo.notifyDataSetChanged()
        Log.d("PersonalAutorizo", "Adapter notificado.")


        if (::spinnerPersonalAutorizo.isInitialized && listaNombresPersonal.isNotEmpty()) {


            val valorPorDefecto = "ADMINISTRADOR -"
            val indicePorDefecto = listaNombresPersonal.indexOf(valorPorDefecto)

            if (indicePorDefecto != -1) {

                spinnerPersonalAutorizo.setSelection(indicePorDefecto, false)
                Log.d("PersonalAutorizo", "Selección por defecto establecida a '$valorPorDefecto' (índice $indicePorDefecto).")
            } else {

                spinnerPersonalAutorizo.setSelection(0, false)
                Log.w("PersonalAutorizo", "Valor por defecto '$valorPorDefecto' no encontrado en la lista. Seleccionando índice 0.")
            }


        } else if (!::spinnerPersonalAutorizo.isInitialized){
            Log.w("PersonalAutorizo", "spinnerPersonalAutorizo no inicializado al intentar setear selección.")
        } else {
            Log.w("PersonalAutorizo", "listaNombresPersonal está vacía, no se puede setear selección.")
        }
    }

    private fun iniciarContadorTiempoReal() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                actualizarHoraActual()

                handler.postDelayed(this, 1000)
            }
        }

        iniciarContador()
    }

    private fun iniciarContador() {
        if (!isRunnableActive) {
            handler.post(runnable)
            isRunnableActive = true
        }
    }

    private fun detenerContador() {
        if (isRunnableActive) {
            handler.removeCallbacks(runnable)
            isRunnableActive = false
        }
    }

    override fun onResume() {
        super.onResume()
        iniciarContador()
    }

    override fun onPause() {
        super.onPause()
        detenerContador()
    }

    override fun onDestroyView() {
        detenerContador()
        super.onDestroyView()
    }

    private fun setupListeners() {

        btnFecha.setOnClickListener { mostrarDatePicker() }
        btnHora.setOnClickListener { mostrarTimePicker() }
        btnRegistrar.setOnClickListener { registrarEntradaSalida() }
        btnLimpiar.setOnClickListener { limpiarFormulario() }
        btnBuscarRENIEC.setOnClickListener { buscarReniec() }

        btnAgregarFoto1.setOnClickListener { handleAgregarFotoClick(1) }
        btnAgregarFoto2.setOnClickListener { handleAgregarFotoClick(2) }
        btnAgregarFoto3.setOnClickListener { handleAgregarFotoClick(3) }
        btnAgregarFoto4.setOnClickListener { handleAgregarFotoClick(4) }
        ivImagen1.setOnLongClickListener { clearImage(1); true }
        ivImagen2.setOnLongClickListener { clearImage(2); true }
        ivImagen3.setOnLongClickListener { clearImage(3); true }
        ivImagen4.setOnLongClickListener { clearImage(4); true }


    }


    private fun buscarReniec() {
        val dni = etDni.text.toString().trim()


        if (dni.isEmpty()) {
            tilDni.error = "Ingrese DNI"
            etDni.requestFocus()
            return
        }
        if (dni.length != 8) {
            tilDni.error = "DNI debe tener 8 dígitos"
            etDni.requestFocus()
            return
        }


        tilDni.error = null
        tilDni.isErrorEnabled = false


        activity?.currentFocus?.let { view ->
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }


        showProgressDialog("Buscando en RENIEC...")
        setFormEnabled(false)


        consumoApi.consultarDni(dni)
    }




    override fun exitoConsultaDni(empleado: EmpleadoApiResponse) {

        hideProgressDialog()
        setFormEnabled(true)

        if (empleado.mensaje == "No encontrado" || empleado.nombres.isNullOrEmpty()) {
            Toast.makeText(context, "DNI no encontrado en RENIEC.", Toast.LENGTH_LONG).show()

            etNombres.setText("")
            etApellidos.setText("")
            etDni.requestFocus()
        } else {

            etNombres.setText(empleado.nombres ?: "")

            val apellidoCompleto = "${empleado.apellido_paterno ?: ""} ${empleado.apellido_materno ?: ""}".trim()
            etApellidos.setText(apellidoCompleto)

            Toast.makeText(context, "Datos encontrados.", Toast.LENGTH_SHORT).show()


            etarea.requestFocus()




        }
    }

    override fun fallaConsultaDni(error: String) {


        hideProgressDialog()
        setFormEnabled(true)

        Log.e("ReniecError", "Error consultando DNI: ${
            error(
                message = TODO()
            )
        }")
        Toast.makeText(context, "Error al consultar RENIEC. Verifique conexión o intente más tarde.", Toast.LENGTH_LONG).show()
        etDni.requestFocus()

    }


    private fun showProgressDialog(message: String) {
        if (progressDialogReniec == null) {
            progressDialogReniec = ProgressDialog(context)
            progressDialogReniec?.setCancelable(false)
        }
        progressDialogReniec?.setMessage(message)
        progressDialogReniec?.show()
    }

    private fun hideProgressDialog() {
        progressDialogReniec?.dismiss()
    }

    private fun setFormEnabled(enabled: Boolean) {

        etDni.isEnabled = enabled
        btnBuscarRENIEC.isEnabled = enabled
        etNombres.isEnabled = enabled
        etApellidos.isEnabled = enabled


    }



    private fun setFechaYHoraActual() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val fechaActual = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year)

        if (::btnFecha.isInitialized) {
            btnFecha.text = fechaActual
        } else {
            Log.w("ControlPuerta", "setFechaYHoraActual called before views initialized")
        }


        actualizarHoraActual()
    }

    private fun actualizarHoraActual() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)


        val amPm = if (hour < 12) "a. m." else "p. m."
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val horaActual = String.format(Locale.getDefault(), "%02d:%02d:%02d %s",
            displayHour, minute, second, amPm)

        if (::btnHora.isInitialized) {
            btnHora.text = horaActual
        }
    }

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val fecha = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, yearSelected)
                if (::btnFecha.isInitialized) {
                    btnFecha.text = fecha
                }
            },
            year, month, day
        ).show()
    }

    private fun mostrarTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minuteSelected ->

                val second = Calendar.getInstance().get(Calendar.SECOND)


                val amPm = if (hourOfDay < 12) "a. m." else "p. m."
                val displayHour = when {
                    hourOfDay == 0 -> 12
                    hourOfDay > 12 -> hourOfDay - 12
                    else -> hourOfDay
                }

                val hora = String.format(Locale.getDefault(), "%02d:%02d:%02d %s",
                    displayHour, minuteSelected, second, amPm)

                if (::btnHora.isInitialized) {
                    btnHora.text = hora



                    detenerContador()



                    handler.postDelayed({
                        iniciarContador()
                    }, 2000)
                }
            },
            hour, minute, false
        ).show()
    }





    private fun setupActivityResultLaunchers() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("Permissions", "Permission granted")
                    when (lastRequestedPermission) {
                        readImagePermission -> launchGallery()
                        Manifest.permission.CAMERA -> launchCamera()
                    }
                } else {
                    Log.d("Permissions", "Permission denied: $lastRequestedPermission")
                    Toast.makeText(context, "Permiso necesario para esta acción", Toast.LENGTH_LONG).show()
                }
            }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    Log.d("ImageSelection", "Image URI received from gallery: $uri")
                    processImageUri(uri)
                } ?: run {
                    Log.e("ImageSelection", "Image URI from gallery is null")
                    Toast.makeText(context, "No se pudo obtener la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ImageSelection", "Photo taken, URI: $tempPhotoUri")
                tempPhotoUri?.let {
                    processImageUri(it)
                } ?: run {
                    Log.e("ImageSelection", "Temporary photo URI is null")
                    Toast.makeText(context, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("ImageSelection", "Camera canceled or failed. ResultCode: ${result.resultCode}")
            }
        }
    }

    private var lastRequestedPermission: String = ""

    private fun handleAgregarFotoClick(slot: Int) {
        currentImageSlot = slot
        Log.d("ImageSelection", "Add photo button clicked for slot: $currentImageSlot")


        val options = arrayOf("Tomar foto", "Seleccionar de galería")

        context?.let { ctx ->
            AlertDialog.Builder(ctx)
                .setTitle("Seleccione una opción")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> checkPermissionAndLaunchCamera()
                        1 -> checkPermissionAndLaunchGallery()
                    }
                }
                .show()
        }
    }


    private fun checkPermissionAndLaunchCamera() {
        lastRequestedPermission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permissions", "Camera permission already granted")
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Log.d("Permissions", "Showing rationale for camera permission")
                Toast.makeText(context, "Se necesita acceso a la cámara para tomar fotos.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                Log.d("Permissions", "Requesting camera permission")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    private fun checkPermissionAndLaunchGallery() {
        lastRequestedPermission = readImagePermission
        when {
            ContextCompat.checkSelfPermission(requireContext(), readImagePermission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permissions", "Storage permission already granted")
                launchGallery()
            }
            shouldShowRequestPermissionRationale(readImagePermission) -> {
                Log.d("Permissions", "Showing rationale for storage permission")
                Toast.makeText(context, "Se necesita acceso al almacenamiento para elegir fotos.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(readImagePermission)
            }
            else -> {
                Log.d("Permissions", "Requesting storage permission")
                requestPermissionLauncher.launch(readImagePermission)
            }
        }
    }

    private fun launchCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().time)
            val imageFileName = "JPEG_" + timeStamp + "_"

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            tempPhotoUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
            }

            cameraLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            Log.e("CameraLaunch", "Error launching camera", e)
            Toast.makeText(context, "No se pudo abrir la cámara: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchGallery() {
        Log.d("ImageSelection", "Launching gallery intent")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            galleryLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("ImageSelection", "Error launching gallery", e)
            Toast.makeText(context, "No se pudo abrir la galería.", Toast.LENGTH_LONG).show()
        }
    }

    private fun processImageUri(uri: Uri) {
        Log.d("ImageProcessing", "Processing URI: $uri for slot $currentImageSlot")
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = uriToBitmap(uri)
            if (bitmap != null) {
                Log.d("ImageProcessing", "Bitmap created successfully")
                val imageBytes = bitmapToByteArray(bitmap, Bitmap.CompressFormat.JPEG, 80)
                withContext(Dispatchers.Main) {
                    if (imageBytes != null) {
                        Log.d("ImageProcessing", "ByteArray created successfully (${imageBytes.size} bytes)")
                        getImageViewForSlot(currentImageSlot)?.setImageBitmap(bitmap)
                        saveImageBytes(currentImageSlot, imageBytes)
                        Toast.makeText(context, "Imagen ${currentImageSlot} cargada", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("ImageProcessing", "Failed to convert Bitmap to ByteArray")
                        Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.e("ImageProcessing", "Failed to convert URI to Bitmap")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No se pudo cargar la imagen desde la URI", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val contentResolver = requireContext().contentResolver
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            }
        } catch (e: Exception) {
            Log.e("UriToBitmap", "Error converting URI to Bitmap", e)
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray? {
        return try {
            ByteArrayOutputStream().use { stream ->
                bitmap.compress(format, quality, stream)
                stream.toByteArray()
            }
        } catch (e: Exception) {
            Log.e("BitmapToByteArray", "Error converting Bitmap to ByteArray", e)
            null
        } catch (oom: OutOfMemoryError) {
            Log.e("BitmapToByteArray", "OutOfMemoryError converting Bitmap", oom)

            null
        }
    }

    private fun getImageViewForSlot(slot: Int): ImageView? {
        return when (slot) {
            1 -> if(::ivImagen1.isInitialized) ivImagen1 else null
            2 -> if(::ivImagen2.isInitialized) ivImagen2 else null
            3 -> if(::ivImagen3.isInitialized) ivImagen3 else null
            4 -> if(::ivImagen4.isInitialized) ivImagen4 else null
            else -> null
        }
    }

    private fun saveImageBytes(slot: Int, bytes: ByteArray?) {
        when (slot) {
            1 -> imagen1Bytes = bytes
            2 -> imagen2Bytes = bytes
            3 -> imagen3Bytes = bytes
            4 -> imagen4Bytes = bytes
        }
        Log.d("ImageSave", "Image bytes saved for slot $slot. Size: ${bytes?.size ?: "null"}")
    }

    private fun clearImage(slot: Int) {
        val imageView = getImageViewForSlot(slot)
        imageView?.setImageResource(android.R.drawable.ic_menu_camera)
        saveImageBytes(slot, null)

        if (isAdded && context != null) {
            Toast.makeText(context, "Imagen $slot eliminada", Toast.LENGTH_SHORT).show()
        }
        Log.d("ImageClear", "Image cleared for slot $slot")
    }



    private fun registrarEntradaSalida() {

        val fechaRegistroStr = btnFecha.text.toString()
        val horaRegistroStr = btnHora.text.toString()
        val tipoEvento = if (rbIngreso.isChecked) "Ingreso" else "Salida"
        val dni = etDni.text.toString().trim()
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()

        val area = etarea.text.toString().trim()
        val motivoIngreso = etmotivo_ingreso.text.toString().trim()
        val placaVehiculo = etPlacaVehiculo.text.toString().trim().ifEmpty { null }
        val observaciones = etObservaciones.text.toString().trim().ifEmpty { null }
        var personalSeleccionado: String? = null
        if (::spinnerPersonalAutorizo.isInitialized && spinnerPersonalAutorizo.selectedItemPosition > 0) {

            personalSeleccionado = spinnerPersonalAutorizo.selectedItem as? String
        }
        val personalAutoriza = personalSeleccionado?.trim()?.ifEmpty { null }

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || personalSeleccionado.isNullOrEmpty()) {
            Toast.makeText(context, "DNI, Nombres y Apellido Paterno son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }


        val nombresCompletos = "$nombres $apellidos".trim()
        val fechaHoraRegistroSQL = convertirFechaHoraSQL(fechaRegistroStr, horaRegistroStr)
        val fechaHoraSistemaSQL = fechaHoraActualSQL()



        val registroData = RegistroPuertaData(

            fechaRegistro = fechaHoraRegistroSQL,
            fechaSistema = fechaHoraSistemaSQL,
            tipoEvento = tipoEvento,
            dni = dni,
            nombresApellidos = nombresCompletos,
            area = area,
            motivoIngreso = motivoIngreso,
            placaVehiculo = placaVehiculo,
            personalAutorizo = personalSeleccionado,
            observacion = observaciones,
            estado = "Activo",
            otros1 = null,
            otros2 = null,
            img1 = imagen1Bytes,
            img2 = imagen2Bytes,
            img3 = imagen3Bytes,
            img4 = imagen4Bytes
        )

        val algunaImagenAdjuntada = imagen1Bytes != null || imagen2Bytes != null || imagen3Bytes != null || imagen4Bytes != null

        val progressDialog = ProgressDialog(context).apply {
            setMessage("Registrando...")
            setCancelable(false)
            show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            var success = false

            var caughtException: Exception? = null
            var errorMsg = "Error desconocido al procesar el registro"

            withContext(Dispatchers.IO) {

                val sqlConPlaceholders = "EXEC [dbo].[InsertarRegistroPuertaVigilancia] " +
                        "@RI_FechaRegistro=?, @RI_FechaSistema=?, @RI_TipoEvento=?, @RI_DNI=?, " +
                        "@RI_NombresApellidos=?, @RI_Area=?, @RI_MotivoIngreso=?, @RI_PlacaVehiculo=?, " +
                        "@RI_PersonalAutorizo=?, @RI_Observacion=?, @RI_Otros1=?, @RI_Otros2=?, " +
                        "@RI_Estado=?, @RI_Img1=?, @RI_Img2=?, @RI_Img3=?, @RI_Img4=?"


                val parametros = ArrayList<Any?>()

                val timestampRegistro = convertirFechaHoraATimestamp(registroData.fechaRegistro)
                val timestampSistema = convertirFechaHoraATimestamp(registroData.fechaSistema)

                parametros.add(timestampRegistro)
                parametros.add(timestampSistema)
                parametros.add(registroData.tipoEvento)
                parametros.add(registroData.dni)
                parametros.add(registroData.nombresApellidos)
                parametros.add(registroData.area)
                parametros.add(registroData.motivoIngreso)
                parametros.add(registroData.placaVehiculo)
                parametros.add(registroData.personalAutorizo)
                parametros.add(registroData.observacion)
                parametros.add(registroData.otros1)
                parametros.add(registroData.otros2)
                parametros.add(registroData.estado)
                parametros.add(registroData.img1)
                parametros.add(registroData.img2)
                parametros.add(registroData.img3)
                parametros.add(registroData.img4)

                try {

                    val conexionImagenes = ConexionSqlImagenes("true")


                    val filas: Int = conexionImagenes.ejecutarActualizacionParametrizada(sqlConPlaceholders, parametros)



                    if (filas >= 0) {
                        success = true
                        Log.i("RegistroDB", "Actualización parametrizada exitosa. Filas afectadas: $filas")
                    } else {

                        errorMsg = "La ejecución devolvió un número inesperado de filas afectadas: $filas"
                        Log.w("RegistroDB", errorMsg)
                    }

                } catch (e: SQLException) {

                    e.printStackTrace()
                    errorMsg = "Error SQL (${e.errorCode}): ${e.message}"
                    Log.e("RegistroDB_SQL", errorMsg, e)
                    caughtException = e
                } catch (e: IllegalArgumentException) {

                    e.printStackTrace()
                    errorMsg = "Error de parámetros: ${e.message}"
                    Log.e("RegistroDB_Param", errorMsg, e)
                    caughtException = e
                } catch (e: Exception) {

                    e.printStackTrace()
                    errorMsg = "Error inesperado durante la conexión/ejecución: ${e.message}"
                    Log.e("RegistroDB_General", errorMsg, e)
                    caughtException = e
                }
            }

            progressDialog.dismiss()
            if (success) {

                val mensajeExito = if (algunaImagenAdjuntada) {
                    "Registro guardado correctamente con imágenes."
                } else {
                    "Registro guardado correctamente."
                }
                Toast.makeText(context, mensajeExito, Toast.LENGTH_LONG).show()
                limpiarFormulario()
            } else {

                Toast.makeText(context, "Error al registrar: $errorMsg", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun convertirFechaHoraATimestamp(fechaHoraStr: String?): Timestamp? {
        if (fechaHoraStr == null) return null
        return try {

            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val parsedDate = formato.parse(fechaHoraStr)
            parsedDate?.let { Timestamp(it.time) }
        } catch (e: Exception) {
            Log.e("TimestampConversion", "Error al convertir '$fechaHoraStr' a Timestamp", e)
            null
        }
    }

    private fun buildInsertQuery(data: RegistroPuertaData): String {
        fun formatSqlValue(value: String?): String {
            return value?.let { "'${it.replace("'", "''")}'" } ?: "NULL"
        }
        fun formatSqlBinary(value: ByteArray?): String {
            return "NULL"
        }

        return "EXEC [dbo].[InsertarRegistroPuertaVigilancia] " +
                "@RI_FechaRegistro = ${formatSqlValue(data.fechaRegistro)}, " +
                "@RI_FechaSistema = ${formatSqlValue(data.fechaSistema)}, " +
                "@RI_TipoEvento = ${formatSqlValue(data.tipoEvento)}, " +
                "@RI_DNI = ${formatSqlValue(data.dni)}, " +
                "@RI_NombresApellidos = ${formatSqlValue(data.nombresApellidos)}, " +
                "@RI_Area = ${formatSqlValue(data.area)}, " +
                "@RI_MotivoIngreso = ${formatSqlValue(data.motivoIngreso)}, " +
                "@RI_PlacaVehiculo = ${formatSqlValue(data.placaVehiculo)}, " +
                "@RI_PersonalAutorizo = ${formatSqlValue(data.personalAutorizo)}, " +
                "@RI_Observacion = ${formatSqlValue(data.observacion)}, " +
                "@RI_Otros1 = ${formatSqlValue(data.otros1)}, " +
                "@RI_Otros2 = ${formatSqlValue(data.otros2)}, " +
                "@RI_Estado = ${formatSqlValue(data.estado)}, " +
                "@RI_Img1 = ${formatSqlBinary(data.img1)}, " +
                "@RI_Img2 = ${formatSqlBinary(data.img2)}, " +
                "@RI_Img3 = ${formatSqlBinary(data.img3)}, " +
                "@RI_Img4 = ${formatSqlBinary(data.img4)}"
    }


    private fun convertirFechaHoraSQL(fecha: String, hora: String): String {
        return try {

            val formatoEntradaFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


            val formatoEntradaHora = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())


            val formatoSalida = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val fechaParseada = formatoEntradaFecha.parse(fecha)
            val horaParseada = formatoEntradaHora.parse(hora)

            if (fechaParseada != null && horaParseada != null) {
                val calFecha = Calendar.getInstance().apply { time = fechaParseada }
                val calHora = Calendar.getInstance().apply { time = horaParseada }
                val calFinal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, calFecha.get(Calendar.YEAR))
                    set(Calendar.MONTH, calFecha.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calFecha.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, calHora.get(Calendar.MINUTE))
                    set(Calendar.SECOND, calHora.get(Calendar.SECOND))
                    set(Calendar.MILLISECOND, 0)
                }
                formatoSalida.format(calFinal.time)
            } else {
                Log.w("DateConversion", "Failed to parse date/time: '$fecha' '$hora'. Falling back.")
                fechaHoraActualSQL()
            }
        } catch (e: Exception) {
            Log.e("DateConversion", "Error parsing date/time: '$fecha' '$hora'", e)
            fechaHoraActualSQL()
        }
    }

    private fun fechaHoraActualSQL(): String {
        val cal = Calendar.getInstance()
        val formatoSQL = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatoSQL.format(cal.time)
    }


    private fun limpiarFormulario() {

        etDni.setText("")
        etNombres.setText("")
        etApellidos.setText("")
        etarea.setText("", false)
        etmotivo_ingreso.setText("")
        etPlacaVehiculo.setText("")
        etObservaciones.setText("")

        if (::spinnerPersonalAutorizo.isInitialized && listaNombresPersonal.isNotEmpty()) {
            spinnerPersonalAutorizo.setSelection(0)
        }






        setFechaYHoraActual()
        rgTipoRegistro.check(R.id.rbIngreso)

        ivImagen1.setImageResource(android.R.drawable.ic_menu_camera); imagen1Bytes = null
        ivImagen2.setImageResource(android.R.drawable.ic_menu_camera); imagen2Bytes = null
        ivImagen3.setImageResource(android.R.drawable.ic_menu_camera); imagen3Bytes = null
        ivImagen4.setImageResource(android.R.drawable.ic_menu_camera); imagen4Bytes = null

        etDni.requestFocus()
        Log.d("FormClear", "Form cleared successfully")
    }

}