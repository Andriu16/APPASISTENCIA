package dev.app.peru.guidecsharp.Vista.ui.home
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.AsistenciaPuertaDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.TipoDocumentoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.DatosIniciales
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TipoDocumento
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.Conector
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.ConsumoApi
import dev.app.peru.guidecsharp.ConsumoApis.Reniec.EmpleadoApiResponse
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import android.os.Looper
import android.util.Log
class TomarAsistenciaFragment : Fragment(), TextWatcher,
    AdapterView.OnItemLongClickListener, Conector {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var tipoDocumentoDAO: TipoDocumentoDAO
    private lateinit var empleadoDAO: EmpleadoDAO
    private lateinit var asistenciaPuertaDAO: AsistenciaPuertaDAO
    private var documenArray = ArrayList<TipoDocumento>()
    private lateinit var DocumentoAdapter: ArrayAdapter<TipoDocumento>
    private var Estado = "NO SINCRONIZADO"
    private lateinit var consumoApi: ConsumoApi
    lateinit var vCambiarHora: View
    lateinit var alertDialog: AlertDialog.Builder
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Globales.origenQR = "ASISTENCIA"
        Globales.asist = this
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        consumoApi = ConsumoApi(this)
        tipoDocumentoDAO = ConexionSqlite.getInstancia(requireContext()).tipoDocumentoDAO()
        empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
        asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
        binding.btnLeerQR.setOnClickListener { btnLeerQR_Click() }
        binding.btnNuevo.setOnClickListener { btnNuevo_Click() }
        binding.btnBuscarCodigoQR.setOnClickListener { btnBuscarCodigoQR_Click() }
        binding.btnGuardar.setOnClickListener { btnGuardar_Click() }
        binding.btnBuscarRENIEC.setOnClickListener { btnBuscarReniec_Click() }
        binding.txtCodigoQr.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val qrCode = s?.toString() ?: ""
                if (qrCode.isNotBlank()) {
                    val empleadoLocal = empleadoDAO.obtenerEmpleadoXQR(qrCode)
                    if (empleadoLocal != null) {
                        binding.txtMaterno.setText(empleadoLocal.PER_Materno)
                        binding.txtPaterno.setText(empleadoLocal.PER_Paterno)
                        binding.txtDirecion.setText(empleadoLocal.PER_Direccion)
                        binding.txtNombres.setText(empleadoLocal.PER_Nombres)
                        binding.txtNroDoc.setText(empleadoLocal.PER_NroDoc)
                        binding.btnGuardar.isEnabled = true
                        ocultarTeclado()
                    } else {
                        if (FunGeneral.tieneConexionInternet(requireContext())) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                buscarEmpleadoOnlinePorQR(qrCode)
                            }
                            limpiarCamposNoQR()
                            binding.btnGuardar.isEnabled = false
                        } else {
                            limpiarCamposNoQR()
                            binding.btnGuardar.isEnabled = false
                        }
                    }
                } else {
                    limpiarCampos()
                    binding.btnGuardar.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable) { }
        })
        DocumentoAdapter = ArrayAdapter<TipoDocumento>(requireContext(), android.R.layout.simple_spinner_item, documenArray)
        binding.cboTipoDoc.adapter = DocumentoAdapter
        listarDocumento()
        inicio()
        vCambiarHora = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)
        construirAlertDialog()
        setHasOptionsMenu(true)
        return root
    }
    private fun performSearch(qrCode: String) {
        val empleadoLocal = empleadoDAO.obtenerEmpleadoXQR(qrCode)
        if (empleadoLocal != null) {
            binding.txtMaterno.setText(empleadoLocal.PER_Materno)
            binding.txtPaterno.setText(empleadoLocal.PER_Paterno)
            binding.txtDirecion.setText(empleadoLocal.PER_Direccion)
            binding.txtNombres.setText(empleadoLocal.PER_Nombres)
            binding.txtNroDoc.setText(empleadoLocal.PER_NroDoc)
            binding.btnGuardar.isEnabled = true
            ocultarTeclado()
        } else {
            if (FunGeneral.tieneConexionInternet(requireContext())) {
                viewLifecycleOwner.lifecycleScope.launch {
                    buscarEmpleadoOnlinePorQR(qrCode)
                }
            } else {
                limpiarCamposNoQR()
                binding.btnGuardar.isEnabled = false
                Toast.makeText(requireContext(), "Empleado no en BD local. Sin conexión.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private suspend fun buscarEmpleadoOnlinePorQR(qrCode: String) {
        val frm = FunGeneral.mostrarCargando(requireContext(), "Buscando online...")
        frm.show()
        var empleadoOnline: Empleado? = null
        var errorMsg: String? = null
        withContext(Dispatchers.IO) {
            try {
                val rs = EmpleadoDALC.PA_ObtenerEmpleadoPorQR_Android(qrCode)
                if (rs.next()) {
                    val codigoNube = rs.getInt("PER_CodigoNube")
                    val empleadoEncontrado = Empleado(
                        PER_Codigo = 0,
                        PER_CodigoQR = rs.getString("PER_CodigoQR") ?: qrCode,
                        TD_Codigo = rs.getString("TD_Codigo") ?: "01",
                        PER_NroDoc = rs.getString("PER_NroDoc") ?: "",
                        PER_Nombres = rs.getString("PER_Nombres") ?: "N/A",
                        PER_Paterno = rs.getString("PER_Paterno") ?: "N/A",
                        PER_Materno = rs.getString("PER_Materno") ?: "N/A",
                        PER_Direccion = rs.getString("PER_Direccion") ?: "--",
                        PER_FechaRegistro = rs.getString("PER_FechaRegistro") ?: FunGeneral.fecha("yyyyMMdd HH:mm:ss"),
                        PER_CodigoNube = codigoNube,
                        PER_Origen = rs.getString("PER_Origen") ?: "SQLServer",
                        PER_EstadoSincronizacion = "SINCRONIZADO",
                        SUC_Codigo = rs.getInt("SUC_Codigo"),
                        SelectTareo = "",
                        PER_HoraEntrada = ""
                    )
                    empleadoDAO.guardarEmpleado(empleadoEncontrado)
                    empleadoOnline = empleadoDAO.obtenerEmpleadoXCNUBE(codigoNube)
                } else {
                    errorMsg = "Empleado no encontrado en el servidor."
                }
                rs?.statement?.connection?.close()
            } catch (e: Exception) {
                errorMsg = "Error conexión/DB: ${e.message}"
            }
        }
        frm.dismiss()
        val empleadoCargado = empleadoOnline
        if (empleadoCargado != null) {
            llenarCampos(empleadoCargado)
            binding.btnGuardar.isEnabled = true
            ocultarTeclado()
        } else {
            limpiarCamposNoQR()
            binding.btnGuardar.isEnabled = false
        }
    }
    private fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    private fun limpiarCamposNoQR() {
        binding.txtMaterno.text?.clear()
        binding.txtPaterno.text?.clear()
        binding.txtDirecion.text?.clear()
        binding.txtNombres.text?.clear()
        binding.txtNroDoc.text?.clear()
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.home, menu)
        menu.setGroupDividerEnabled(true)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_verasistencias -> {
                findNavController().navigate(R.id.action_nav_home_to_asistenciaDiariaFragment)
                return true
            }
            R.id.action_cambiar_hora -> {
                if (vCambiarHora.parent != null) {
                    (vCambiarHora.parent as ViewGroup).removeView(vCambiarHora)
                    alertDialog.setView(vCambiarHora)
                }
                construirAlertDialog()
                alertDialog.show()
                return true
            }
            R.id.action_seguir_hora -> {
                FunGeneral.FechaMostrar_TomarAsistencia_Stop(true)
                FunGeneral.HoraMostrar_TomarAsistencia_Stop(true)
                return true
            }
            R.id.action_descargar_data_tomaasistencia -> {
                return true
            }
            R.id.action_descargar_datos -> {
                var _escanear = "SI"
                if (Globales.cod == 12){
                    Toast.makeText(requireContext(), "Se va a descargar toda la data y no podrá subir data", Toast.LENGTH_SHORT).show()
                    _escanear = "NO"
                }
                if (_escanear == "SI"){
                    val listaSincronizar = ArrayList<AsistenciaPuerta>()
                    val asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
                    val listaAsistencia = asistenciaPuertaDAO.listarAsistencias_ParaSincronizar()
                    if (listaAsistencia.size > 0){
                        Globales.showWarningMessage("Hay asistencias pendientes para sincronizar. Debe subir data", requireContext())
                        return true
                    }
                    Toast.makeText(requireContext(), "Escanee QR de la puerta", Toast.LENGTH_SHORT).show()
                    Globales.origenQR2 = "PUERTA"
                    val integrador = IntentIntegrator(requireActivity())
                    integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    integrador.setPrompt("")
                    integrador.setBeepEnabled(true)
                    integrador.initiateScan()
                }
                else{
                    var PI_Codigo : Int = -1
                    var PI_Descripcion: String = "TODO"
                    var datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
                    datosInicialesDAO.PA_EliminarDatosIniciales()
                    datosInicialesDAO.crear(DatosIniciales(0, PI_Codigo, PI_Descripcion))
                    FunGeneral.descargarData_TomarAsistencia(requireContext(), PI_Codigo)
                    listarDocumento()
                    binding.cboTipoDoc.setSelection(1)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun intentarSincronizarAsistenciaIndividual(asistenciaLocal: AsistenciaPuerta) {
        if (!FunGeneral.tieneConexionInternet(requireContext())) {
            Log.d("SYNC_INDIVIDUAL", "Sin conexión, no se sincronizará automáticamente la asistencia ${asistenciaLocal.AP_Codigo}")
            return
        }
        val empleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigo(asistenciaLocal.PER_Codigo)
        if (empleado == null) {
            Log.e("SYNC_INDIVIDUAL", "Error: No se encontró el empleado local con código ${asistenciaLocal.PER_Codigo}")
            return
        }
        val codEmpleadoNube = empleado.PER_CodigoNube
        if (codEmpleadoNube == -1) {
            Log.w("SYNC_INDIVIDUAL", "Advertencia: El empleado ${empleado.nombres_apellidos()} no está sincronizado (CodigoNube = -1). No se puede sincronizar su asistencia automáticamente.")
            return
        }
        val datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
        val datosIni = datosInicialesDAO.PA_ObtenerDatosIniciales()
        val piCodigo = datosIni?.PI_Codigo ?: -1
        Log.d("SYNC_INDIVIDUAL", "Usando PI_Codigo: $piCodigo para la sincronización.")
        val codPersonalRegistro = Globales.cod
        val body = asistenciaLocal.envioDatosNube() + "|${codEmpleadoNube}"
        viewLifecycleOwner.lifecycleScope.launch {
            var exitoSync = false
            var errorMsg: String? = null
            Log.d("SYNC_INDIVIDUAL", "Intentando sincronizar: $body con PI_Codigo: $piCodigo")
            withContext(Dispatchers.IO) {
                try {
                    val rs = EmpleadoDALC.PA_ProcesarAsistenciaEmpleados_Android_ult3(body, piCodigo)
                    if (rs.next()) {
                        val rpta = rs.getString("Rpta")
                        val apCodigoDevuelto = rs.getInt("AP_Codigo")
                        val entradaDevuelta = rs.getString("AP_HoraEntrada") ?: ""
                        val salidaDevuelta = rs.getString("AP_HoraSalida") ?: ""
                        var cuenta = 0
                        if (entradaDevuelta.isNotEmpty()) cuenta++
                        if (salidaDevuelta.isNotEmpty()) cuenta++
                        val nuevoEstadoSync = when (cuenta) {
                            2 -> "SINCRONIZADO 2/2"
                            1 -> "SINCRONIZADO 1/2"
                            else -> "NO SINCRONIZADO"
                        }
                        val filasActualizadas = asistenciaPuertaDAO.actualizarHoraEntradaYSalida(
                            entradaDevuelta,
                            salidaDevuelta,
                            nuevoEstadoSync,
                            asistenciaLocal.AP_Codigo
                        )
                        if (filasActualizadas > 0) {
                            exitoSync = true
                            Log.d("SYNC_INDIVIDUAL", "Asistencia ${asistenciaLocal.AP_Codigo} actualizada localmente a estado: $nuevoEstadoSync")
                        } else {
                            errorMsg = "Error al actualizar estado local post-sync para ${asistenciaLocal.AP_Codigo}"
                            Log.e("SYNC_INDIVIDUAL", errorMsg!!)
                        }
                    } else {
                        errorMsg = "El SP no devolvió resultados para la asistencia ${asistenciaLocal.AP_Codigo}."
                    }
                    rs?.statement?.connection?.close()
                } catch (e: Exception) {
                    errorMsg = "Error durante sincronización individual: ${e.message}"
                    Log.e("SYNC_INDIVIDUAL", errorMsg, e)
                }
            }
            if (exitoSync) {
                Log.i("SYNC_INDIVIDUAL", "Asistencia ${asistenciaLocal.AP_Codigo} sincronizada automáticamente.")
            } else {
                Log.w("SYNC_INDIVIDUAL", "Fallo sincronización automática para ${asistenciaLocal.AP_Codigo}: ${errorMsg ?: "Error desconocido"}")
            }
        }
    }
    private fun showDatePickerDialog2() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected2(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    fun onDateSelected2(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"
        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"
        var f1 = vCambiarHora.findViewById<EditText>(R.id.etDate)
        f1.setText("$_dia/$_mes/$year")
    }
    private fun construirAlertDialog() {
        var f1 = vCambiarHora.findViewById<EditText>(R.id.etDate)
        var _fecha = binding.txtFecha.text.toString()
        var _fechaConsulta = FunGeneral.obtenerFecha_PorFormato(_fecha, "dd/MM/YYYY")
        f1.setText(_fechaConsulta)
        f1.setOnClickListener { showDatePickerDialog2() }
        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(vCambiarHora)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Ok") { dialoginterface, i ->
            val tmrHoraNueva = vCambiarHora.findViewById<TimePicker>(R.id.tmrHoraNueva)
            var hour = tmrHoraNueva.hour
            var minute = tmrHoraNueva.minute
            var am_pm = ""
            when {
                hour == 0 -> {
                    hour += 12
                    am_pm = "a. m."
                }
                hour == 12 -> {
                    am_pm = "p. m."
                }
                hour > 12 -> {
                    hour -= 12
                    am_pm = "p. m."
                }
                else -> {
                    am_pm = "a. m."
                }
            }
            var calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
            var _seg = calendar.time.seconds
            var _horaSelect = ""
            val hora = if(hour < 10)"0"+hour else hour
            val min = if(minute < 10)"0"+minute else minute
            val sec = if(_seg < 10) "0"+_seg else _seg
            _horaSelect = "${hora}:${min}:${sec} ${am_pm}"
            var _fechaSelect = f1.text.toString()
            binding.txtFecha.setText(_fechaSelect)
            binding.txtHora.setText(_horaSelect)
            FunGeneral.FechaMostrar_TomarAsistencia_Stop(false)
            FunGeneral.HoraMostrar_TomarAsistencia_Stop(false)
        }
        alertDialog.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }
    private fun btnLeerQR_Click() {
        quitarErrores()
        if (binding.rdbIngreso.isChecked == false && binding.rbdSalida.isChecked == false) {
            Toast.makeText(requireContext(), "Indique: INGRESO o SALIDA", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.cboTipoDoc.count == 0) {
            Globales.showWarningMessage("Primero Migre los tipos de Documentos",requireContext())
            return
        }
        activar(false)
        limpiarCampos()
        val integrador = IntentIntegrator(requireActivity())
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrador.setPrompt("")
        integrador.setBeepEnabled(true)
        integrador.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                binding.btnGuardar.isEnabled = true
            } else {
                binding.txtCodigoQr.setText(result.contents)
                val aux = buscarEmpleadoXQR(binding.txtCodigoQr.text.toString())
                if (aux != null) {
                    binding.btnGuardar.isEnabled = false
                    llenarCampos(aux)
                    binding.btnGuardar.isEnabled = true
                } else {
                    Globales.showWarningMessage("El empleado no existe. Regístrelo",requireContext())
                    binding.btnGuardar.isEnabled = true
                    limpiarCampos()
                    activar(true)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    public fun onActivityResult2(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
            } else {
                var PI_Codigo : Int = -1
                var PI_Descripcion: String = ""
                try{
                    var data = result.contents.toString().split('@')
                    PI_Codigo = data[0].toString().toInt()
                    PI_Descripcion = data[1].toString()
                }
                catch(ex: java.lang.Exception) {
                    Toast.makeText(requireContext(), "QR NO COMPATIBLE", Toast.LENGTH_SHORT).show()
                    return
                }
                var datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
                datosInicialesDAO.PA_EliminarDatosIniciales()
                datosInicialesDAO.crear(DatosIniciales(0, PI_Codigo, PI_Descripcion))
                FunGeneral.descargarData_TomarAsistencia(requireContext(), PI_Codigo)
                listarDocumento()
                binding.cboTipoDoc.setSelection(1)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun listarDocumento() {
        DocumentoAdapter.clear()
        val lista = tipoDocumentoDAO.listarTipoDocumento() as ArrayList<TipoDocumento>
        if (lista.isNotEmpty()) {
            lista.iterator().forEach {
                DocumentoAdapter.add(it)
            }
        }
        DocumentoAdapter.notifyDataSetChanged()
    }
    private fun addEmpleado(
        PER_CodigoQR: String,
        TD_Codigo: String,
        PER_NroDoc: String,
        PER_Nombre: String,
        PER_Paterno: String,
        PER_Materno: String = "--",
        PER_Direccion: String = "--",
        PER_FechaRegistro: String,
        PER_CodigoNube: Int,
        PER_Origen: String,
        PER_EstadoSincronizacion: String,
        SUC_Codigo: Int,
    ) {
        val obj = Empleado(
            0, PER_CodigoQR, TD_Codigo, PER_NroDoc, PER_Nombre, PER_Paterno,
            PER_Materno, PER_Direccion, PER_FechaRegistro, PER_CodigoNube,
            PER_Origen, PER_EstadoSincronizacion, SUC_Codigo, "", "")
        empleadoDAO.guardarEmpleado(obj)
    }
    private fun buscarEmpleadoXQR(campo: String): Empleado? {
        val empleadotop = empleadoDAO.obtenerEmpleadoXQR(campo)
        if (empleadotop != null) {
            return Empleado(
                empleadotop.PER_Codigo,
                empleadotop.PER_CodigoQR,
                empleadotop.TD_Codigo,
                empleadotop.PER_NroDoc,
                empleadotop.PER_Nombres,
                empleadotop.PER_Paterno,
                empleadotop.PER_Materno,
                empleadotop.PER_Direccion,
                empleadotop.PER_FechaRegistro,
                empleadotop.PER_CodigoNube,
                empleadotop.PER_Origen,
                empleadotop.PER_EstadoSincronizacion,
                empleadotop.SUC_Codigo,
                "",
                ""
            )
        } else {
            activar(true)
            return null
        }
    }
    var _continuarLeyendo = "NO"
    private fun llenarCampos(empl: Empleado) {
        binding.txtMaterno.setText(empl.PER_Materno)
        binding.txtPaterno.setText(empl.PER_Paterno)
        binding.txtDirecion.setText(empl.PER_Direccion)
        binding.txtNombres.setText(empl.PER_Nombres)
        binding.txtNroDoc.setText(empl.PER_NroDoc)
        binding.txtCodigoQr.setText(empl.PER_CodigoQR)
        _continuarLeyendo = "SI"
        btnGuardar_Click()
    }
    private fun activar(activar: Boolean) {
        binding.btnLeerQR.isEnabled = true
        binding.btnNuevo.isEnabled = true
        binding.txtCodigoQr.isEnabled = false
        binding.txtNombres.isEnabled = activar
        binding.txtPaterno.isEnabled = activar
        binding.txtMaterno.isEnabled = activar
        binding.txtNroDoc.isEnabled = activar
        binding.cboTipoDoc.isEnabled = activar
        binding.txtDirecion.isEnabled = activar
        binding.btnBuscarRENIEC.isEnabled = activar
    }
    private fun quitarErrores(){
        binding.tilNroDoc.isErrorEnabled = false
        binding.tilNombres.isErrorEnabled = false
        binding.tilPaterno.isErrorEnabled = false
        binding.tilNroDoc.error = null
        binding.tilNombres.error = null
        binding.tilPaterno.error = null
    }
    private fun btnNuevo_Click() {
        quitarErrores()
        if (binding.rdbIngreso.isChecked == false && binding.rbdSalida.isChecked == false) {
            Toast.makeText(requireContext(), "Indique: INGRESO o SALIDA", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.cboTipoDoc.count == 0) {
            Globales.showWarningMessage("Primero Migre los tipos de Documentos",requireContext())
            return
        }
        activar(true)
        limpiarCampos()
        binding.tilNroDoc.requestFocus()
        binding.txtNroDoc.isSelected = true
        binding.txtNroDoc.isFocusable = true
        binding.txtNroDoc.requestFocus()
        val imm : InputMethodManager = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.txtNroDoc, InputMethodManager.SHOW_IMPLICIT
        )
    }
    private fun btnBuscarCodigoQR_Click(){
        quitarErrores()
        if (binding.rdbIngreso.isChecked == false && binding.rbdSalida.isChecked == false) {
            Toast.makeText(requireContext(), "Indique: INGRESO o SALIDA", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.cboTipoDoc.count == 0) {
            Globales.showWarningMessage("Primero Migre los tipos de Documentos",requireContext())
            return
        }
        activar(false)
        binding.txtCodigoQr.isEnabled = true
        limpiarCampos()
        binding.txtCodigoQr.requestFocus()
        val imm : InputMethodManager = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.txtCodigoQr, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun btnGuardar_Click() {
        if (!binding.rdbIngreso.isChecked && !binding.rbdSalida.isChecked) {
            Toast.makeText(requireContext(), "Indique: INGRESO o SALIDA", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedRadioButtonId = binding.rdbGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) return
        if (binding.cboTipoDoc.count == 0) {
            Globales.showWarningMessage("Primero Migre los tipos de Documentos", requireContext())
            return
        }
        val datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
        val datosIni = datosInicialesDAO.PA_ObtenerDatosIniciales()
        val piCodigo = datosIni?.PI_Codigo
        if (piCodigo == null) {
            Globales.showWarningMessage("No se ha configurado la puerta/ubicación. No se puede registrar online.", requireContext())
        }
        var _codEmpleado = -1
        var _codEmpleadoNube = -1
        var objEmpleado = buscarEmpleadoXQR(binding.txtCodigoQr.text.toString())
        if (!binding.txtCodigoQr.isEnabled) {
            objEmpleado = buscarEmpleadoXQR(binding.txtNroDoc.text.toString())
        }
        var _msjNuevoEmpleado = ""
        if (objEmpleado == null) {
            if (ValidarCampos()) {
                addEmpleado(
                    binding.txtNroDoc.text.toString(),
                    obtercodigodoc(binding.cboTipoDoc.selectedItem.toString()),
                    binding.txtNroDoc.text.toString(),
                    binding.txtNombres.text.toString(),
                    binding.txtPaterno.text.toString(),
                    binding.txtMaterno.text.toString(),
                    binding.txtDirecion.text.toString(),
                    FunGeneral.fecha("YYYYMMdd HH:mm:ss"),
                    -1,
                    "SQLite",
                    Estado,
                    -2
                )
                objEmpleado = buscarEmpleadoXQR(binding.txtNroDoc.text.toString())
                if (objEmpleado != null) {
                    _codEmpleado = objEmpleado.PER_Codigo
                    _codEmpleadoNube = objEmpleado.PER_CodigoNube
                    _msjNuevoEmpleado = "Trabajador Registrado"
                }
            }
        } else {
            _codEmpleado = objEmpleado.PER_Codigo
            _codEmpleadoNube = objEmpleado.PER_CodigoNube
        }
        if (_codEmpleado == -1) {
            Globales.showWarningMessage("Empleado no encontrado o no se pudo registrar localmente.", requireContext())
            return
        }
        val _fechaAux = binding.txtFecha.text.toString()
        val _horaAux = binding.txtHora.text.toString()
        val _fecha = FunGeneral.obtenerFecha_PorFormato(_fechaAux, "YYYYMMdd")
        when (selectedRadioButtonId) {
            R.id.rdbIngreso -> {
                val asistenciaAuxiliarDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaauxiliarDAO()
                val objVacaciones = asistenciaAuxiliarDAO.PA_ObtenerAsistenciaAuxiliarDAO(_codEmpleadoNube, _fecha)
                if (objVacaciones != null) {
                    Globales.showWarningMessage("Trabajador tiene vacaciones, no puede tomar asistencia", requireContext())
                    return
                }
                val asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorEmpleadoFecha(_codEmpleado, _fecha)
                if (asistencia != null) {
                    val _msj2 = "Ya existe hora de ingreso para: ${objEmpleado?.nombres_apellidos()}.\n¿Agregar Nuevo Ingreso?"
                    Globales.showWarningMessageAndCuestions(_msj2, requireContext(), {
                        registrarIngreso(_codEmpleado, _fechaAux, _horaAux, _msjNuevoEmpleado, _codEmpleadoNube, piCodigo)
                    }, {
                        activar(false)
                        limpiarCampos()
                        if (_continuarLeyendo == "SI") {
                            _continuarLeyendo = "NO"
                            btnLeerQR_Click()
                        }
                    })
                    return
                } else {
                    registrarIngreso(_codEmpleado, _fechaAux, _horaAux, _msjNuevoEmpleado, _codEmpleadoNube, piCodigo)
                }
            }
            R.id.rbdSalida -> {
                val asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida(_codEmpleado, _fecha)
                if (asistencia == null) {
                    Toast.makeText(requireContext(), "Primero registre hora de ingreso de ${objEmpleado?.nombres_apellidos()} para el día ${_fechaAux}", Toast.LENGTH_LONG).show()
                    if (_continuarLeyendo == "SI") {
                        _continuarLeyendo = "NO"
                        btnLeerQR_Click()
                    } else {
                    }
                    return
                }
                if (asistencia.AP_HoraSalida.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Ya existe hora de salida registrada para: ${objEmpleado?.nombres_apellidos()} el día ${_fechaAux}", Toast.LENGTH_LONG).show()
                    if (_continuarLeyendo == "SI") {
                        _continuarLeyendo = "NO"
                        btnLeerQR_Click()
                    } else {
                    }
                    return
                }
                val _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "YYYYMMdd HH:mm:ss")
                val _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "dd/MM/YYYY hh:mm:ss a")
                var filasActualizadasSalida = 0
                try {
                    filasActualizadasSalida = asistenciaPuertaDAO.actualizarHoraSalida(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
                } catch (e: Exception) {
                    Log.e("GUARDAR_SQLITE_SALIDA", "Error al actualizar hora salida local para AP_Codigo ${asistencia.AP_Codigo}", e)
                    Globales.showErrorMessage("Error crítico al guardar hora de salida localmente.", requireContext())
                    return
                }
                if (filasActualizadasSalida > 0) {
                    Log.d("GUARDAR_SQLITE_SALIDA", "Hora de salida actualizada localmente para AP_Codigo ${asistencia.AP_Codigo}")
                    Toast.makeText(requireContext(), "Hora de Salida Registrada Localmente", Toast.LENGTH_SHORT).show()
                    val asistenciaActualizada = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(asistencia.AP_Codigo)
                    if (asistenciaActualizada != null) {
                        intentarSincronizarAsistenciaIndividual(asistenciaActualizada)
                    } else {
                        Log.e("SYNC_INDIVIDUAL", "CRÍTICO: No se pudo recuperar la asistencia actualizada ${asistencia.AP_Codigo} después de guardar salida local.")
                        Globales.showWarningMessage("Error al recuperar datos post-guardado. Sincronización manual requerida.", requireContext())
                    }
                } else {
                    Log.w("GUARDAR_SQLITE_SALIDA", "No se actualizaron filas al guardar hora de salida local para AP_Codigo ${asistencia.AP_Codigo}.")
                    Toast.makeText(requireContext(), "No se pudo actualizar la hora de salida localmente (registro no encontrado?).", Toast.LENGTH_LONG).show()
                }
                limpiarCampos()
                activar(false)
                if (_continuarLeyendo == "SI") {
                    _continuarLeyendo = "NO"
                    btnLeerQR_Click()
                }
            }
        }
    }
    private fun registrarIngreso(
        codEmpleado: Int,
        fechaAux: String,
        horaAux: String,
        msjNuevoEmpleado: String,
        codEmpleadoNube: Int,
        piCodigo: Int?
    ) {
        val _fecha = FunGeneral.obtenerFecha_PorFormato(fechaAux, "YYYYMMdd")
        val _horaingreso = FunGeneral.obtenerFechaHora_PorFormato(fechaAux, horaAux, "YYYYMMdd HH:mm:ss")
        val _horaingreso_mostrar = FunGeneral.obtenerFechaHora_PorFormato(fechaAux, horaAux, "dd/MM/YYYY hh:mm:ss a")
        val objAsistencia = AsistenciaPuerta(
            0, _fecha, _horaingreso, _horaingreso_mostrar, "", "",
            codEmpleado, "Habilitado", "NO SINCRONIZADO"
        )
        var idLocalGuardado: Long = -1
        var asistenciaGuardada: AsistenciaPuerta? = null
        try {
            idLocalGuardado = asistenciaPuertaDAO.guardarAsistencia(objAsistencia)
            if (idLocalGuardado > 0) {
                asistenciaGuardada = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(idLocalGuardado.toInt())
                if (msjNuevoEmpleado == "") {
                    Toast.makeText(requireContext(), "Hora de Ingreso Registrada Localmente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Trabajador y Hora de Ingreso Registrados Localmente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Globales.showErrorMessage("Error al guardar localmente (ID inválido).", requireContext())
                return
            }
        } catch (e: Exception) {
            Log.e("GUARDAR_SQLITE", "Error al guardar asistencia local", e)
            Globales.showErrorMessage("Error al guardar localmente.", requireContext())
            return
        }
        if (asistenciaGuardada != null) {
            intentarSincronizarAsistenciaIndividual(asistenciaGuardada)
        } else {
            Log.e("SYNC_INDIVIDUAL", "No se pudo recuperar la asistencia con ID $idLocalGuardado para sincronizar.")
        }
        if (FunGeneral.tieneConexionInternet(requireContext()) && codEmpleadoNube != -1 && piCodigo != null) {
            val frm = FunGeneral.mostrarCargando(requireContext(), "Registrando online...")
            frm.dismiss()
        }
        if (msjNuevoEmpleado == "") {
            Toast.makeText(requireContext(), "Hora de Ingreso Registrada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Trabajador y Hora de Ingreso Registrados", Toast.LENGTH_SHORT).show()
        }
        activar(false)
        limpiarCampos()
        if (_continuarLeyendo == "SI") {
            _continuarLeyendo = "NO"
            btnLeerQR_Click()
        }
    }
    private fun btnBuscarReniec_Click() {
        quitarErrores()
        if (binding.cboTipoDoc.count == 0) {
            Globales.showWarningMessage("Primero Migre los tipos de Documentos",requireContext())
            return
        }
        var TD_Codigo : String = obtercodigodoc(binding.cboTipoDoc.selectedItem.toString())
        var Tam : Int = binding.txtNroDoc.text.toString().length
        if (TD_Codigo != "01"){
            Globales.showErrorMessage("Solo puede buscar DNI con esta opción", requireContext())
            return
        }
        if (Tam == 0){
            binding.tilNroDoc.error = "INGRESE Nº DNI"
            binding.txtNroDoc.requestFocus()
            return
        }
        if (Tam != 8){
            binding.tilNroDoc.error = "DNI DEBE CONTENER 8 DÍGITOS"
            binding.txtNroDoc.requestFocus()
            return
        }
        activar(false)
        val dni = binding.txtNroDoc.text.toString()
        consumoApi.consultarDni(dni)
    }
    private fun obtercodigodoc(descripcion: String): String {
        val documentotop = tipoDocumentoDAO.PA_ObtenerTipoDocumento_PorDescripcion(descripcion)
        if (documentotop != null) {
            return documentotop.TD_Codigo
        } else {
            activar(true)
            return "1"
        }
    }
    private fun ValidarCampos(): Boolean {
        var TD_Codigo : String = obtercodigodoc(binding.cboTipoDoc.selectedItem.toString())
        var Tam : Int = binding.txtNroDoc.text.toString().length
        var isVal = true
        if (binding.txtNroDoc.text.toString().isBlank()) {
            isVal = false
            binding.tilNroDoc.error = "CAMPO OBLIGATORIO"
        }
        else if (TD_Codigo == "01" && Tam != 8){
            binding.tilNroDoc.error = "DNI DEBE CONTENER 8 DÍGITOS"
        }
        else if (TD_Codigo == "04" && Tam > 12) {
            isVal = false
            binding.tilNroDoc.error = "CARNÉ EXT. DEBE CONTENER MÁXIMO 12 DÍGITOS"
        }
        else if (TD_Codigo == "06" && Tam != 11) {
            isVal = false
            binding.tilNroDoc.error = "R.U.C. DEBE CONTENER 11 DÍGITOS"
        }
        else if (TD_Codigo == "07" && Tam > 12) {
            isVal = false
            binding.tilNroDoc.error = "PASAPORTE DEBE CONTENER MÁXIMO 12 DÍGITOS"
        }
        else if (TD_Codigo == "11" && Tam > 15) {
            isVal = false
            binding.tilNroDoc.error = "PART. NAC. DEBE CONTENER MÁXIMO 15 DÍGITOS"
        }
        else if (TD_Codigo == "00" && Tam > 15) {
            isVal = false
            binding.tilNroDoc.error = "LONGITUD MÁXIMA: 15 DÍGITOS"
        }
        else {
            binding.tilNroDoc.isErrorEnabled = false
        }
        if (binding.txtNombres.text.toString().isBlank()) {
            isVal = false
            binding.tilNombres.error = "CAMPO OBLIGATORIO"
        } else {
            binding.tilNombres.isErrorEnabled = false
        }
        if (binding.txtPaterno.text.toString().isBlank()) {
            isVal = false
            binding.tilPaterno.error = "CAMPO OBLIGATORIO"
        } else {
            binding.tilPaterno.isErrorEnabled = false
        }
        return isVal
    }
    private fun inicio() {
        activar(false)
        binding.txtHora.isEnabled = false
        binding.txtFecha.isEnabled = false
        FunGeneral.FechaMostrar_TomarAsistencia_Stop(true)
        FunGeneral.FechaMostrar_TomarAsistencia(binding.txtFecha)
        FunGeneral.HoraMostrar_TomarAsistencia_Stop(true)
        FunGeneral.HoraMostrar_TomarAsistencia(binding.txtHora)
        binding.cboTipoDoc.setSelection(1)
    }
    private fun limpiarCampos() {
        binding.txtMaterno.text?.clear()
        binding.txtPaterno.text?.clear()
        binding.txtDirecion.text?.clear()
        binding.txtNombres.text?.clear()
        binding.txtNroDoc.text?.clear()
        binding.txtCodigoQr.text?.clear()
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }
    override fun afterTextChanged(p0: Editable?) {
        TODO("Not yet implemented")
    }
    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        TODO("Not yet implemented")
    }
    override fun exitoConsultaDni(empleado: EmpleadoApiResponse) {
        if (empleado.mensaje == "No encontrado") {
            Globales.showErrorMessage("PERSONA NO ENCONTRADA O DNI NO EXISTE", requireContext())
            binding.txtNroDoc.requestFocus()
            activar(true)
        } else {
            binding.txtNroDoc.isEnabled = false
            binding.txtNombres.isEnabled = false
            binding.txtPaterno.isEnabled = false
            binding.txtMaterno.isEnabled = false
            binding.txtDirecion.isEnabled = true
            binding.txtNombres.setText(empleado.nombres)
            binding.txtPaterno.setText(empleado.apellido_paterno)
            binding.txtMaterno.setText(empleado.apellido_materno)
            if (empleado.direccion == "") empleado.direccion = "---";
            binding.txtDirecion.setText(empleado.direccion)
            binding.txtDirecion.requestFocus()
        }
    }
    override fun fallaConsultaDni(error: String) {
        Globales.showErrorMessage("PERSONA NO ENCONTRADA O DNI NO EXISTE", requireContext())
        binding.txtNroDoc.requestFocus()
        activar(true)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}