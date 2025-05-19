package dev.app.peru.guidecsharp.Vista.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.awesomedialog.*
import com.google.zxing.integration.android.IntentIntegrator
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.AsistenciaOnlineDTO.AsistenciaOnlineDTO
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.AsistenciaPuertaDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.SucursalDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Sucursal
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.AsistenciaAD_sqlite
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentAsistenciaDiariaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.ResultSet

import java.util.*
import kotlin.collections.ArrayList

class AsistenciaDiariaFragment : Fragment(), TextWatcher, AdapterView.OnItemSelectedListener, AsistenciaAD_sqlite.MyClickListener {

    private var _binding: FragmentAsistenciaDiariaBinding? = null
    private val binding get() = _binding!!


    private lateinit var empleadoDAO: EmpleadoDAO
    private lateinit var sucursalDAO: SucursalDAO
    private var arraySedes = ArrayList<Sucursal>()
    private lateinit var adapterSedes: ArrayAdapter<Sucursal>
    private var lista = java.util.ArrayList<Any>()
    private lateinit var asistenciaPuertaDAO: AsistenciaPuertaDAO
    //private var lista = java.util.ArrayList<AsistenciaPuerta>()
    //private lateinit var adapter : RecyclerView.Adapter<AsistenciaVH_sqlite>
    private lateinit var adapter : AsistenciaAD_sqlite
    private var isOnlineMode: Boolean = false
    private lateinit var textViewDataSource: TextView
    lateinit var vCambiarHora: View
    lateinit var alertDialog: AlertDialog.Builder

    lateinit var vCambiarHora2: View
    lateinit var alertDialog2: AlertDialog.Builder

    lateinit var vCambiar_HI : View
    lateinit var alertDialog_HI: AlertDialog.Builder

    lateinit var vCambiar_HS : View
    lateinit var alertDialog_HS: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View? {
        _binding = FragmentAsistenciaDiariaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Globales.origenQR = "VERASISTENCIA"
        Globales.verasis = this

        sucursalDAO = ConexionSqlite.getInstancia(requireContext()).sucursalDAO()
        asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
        adapter = AsistenciaAD_sqlite(lista, this)
        textViewDataSource = binding.textViewDataSource
        binding.switchModeOnline.setOnCheckedChangeListener { _, isChecked ->
            isOnlineMode = isChecked

            textViewDataSource.text = if (isChecked) "Fuente: Servidor " else "Fuente: Dispositivo "

            binding.cboSedes.isEnabled = !isChecked

            updateSyncCounterVisibility()

            btnReportar()
        }

        isOnlineMode = binding.switchModeOnline.isChecked
        textViewDataSource.text = if (isOnlineMode) "Fuente: Servidor " else "Fuente: Dispositivo "
        binding.cboSedes.isEnabled = !isOnlineMode
        updateSyncCounterVisibility()

        binding.rvEmpleadoSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpleadoSqlite.adapter = adapter

        empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()

        adapterSedes = ArrayAdapter<Sucursal>(requireContext(), android.R.layout.simple_spinner_item, arraySedes)
        binding.cboSedes.adapter  = adapterSedes
        listarSedes()

        val c = Calendar.getInstance()
        var _diaOT = c.get(Calendar.DAY_OF_MONTH)
        var _mesOT = c.get(Calendar.MONTH)
        var _añoOT = c.get(Calendar.YEAR)
        onDateSelected(_diaOT, _mesOT, _añoOT)
        binding.etDate.setOnClickListener { showDatePickerDialog() }
        binding.btnBuscar.setOnClickListener { btnLeerQR_Click() }
        binding.txtBusqueda.addTextChangedListener(this)
        binding.cboSedes.onItemSelectedListener = this
        binding.txtItems.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            btnReportar()
        }

        binding.txtItems2.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            btnReportar()
        }



        vCambiarHora = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)
        construirAlertDialog()

        vCambiarHora2 = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)
        construirAlertDialog2()

        FunGeneral.FechaMostrar_AsistenciaDiaria_Stop(true)
        FunGeneral.FechaMostrar_AsistenciaDiaria(binding.txtFechaActual)
        FunGeneral.HoraMostrar_AsistenciaDiaria_Stop(true)
        FunGeneral.HoraMostrar_AsistenciaDiaria(binding.txtHoraActual)

        vCambiar_HI = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)
        vCambiar_HS = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)

        setHasOptionsMenu(true)
        return root
    }
    private fun updateCounters(dataList: List<Any>?) { // Aceptar lista nullable
        var campo = 0
        var retirados = 0
        var sincronizados = 0
        var porSincronizar = 0

        dataList?.forEach { item ->
            when (item) {
                is AsistenciaPuerta -> {
                    if (item.AP_HoraSalida.isEmpty()) campo++ else retirados++
                    if (item.AP_EstadoSincronizacion == "SINCRONIZADO" || item.AP_EstadoSincronizacion == "SINCRONIZADO 2/2") {
                        sincronizados++
                    } else {
                        porSincronizar++
                    }
                }
                is AsistenciaOnlineDTO -> {
                    if (item.AP_HoraSalida.isNullOrEmpty()) campo++ else retirados++

                }
            }
        }

        binding.txtItems.text = "EN CAMPO: $campo"
        binding.txtItems2.text = "RETIRADOS: $retirados"


        if (!isOnlineMode) {
            binding.txtItems3.text = "POR SINCRONIZAR: $porSincronizar"
            binding.txtItems4.text = "SINCRONIZADOS: $sincronizados"
        }
        updateSyncCounterVisibility()
    }
    private fun updateSyncCounterVisibility() {
        val visibility = if (isOnlineMode) View.GONE else View.VISIBLE
        binding.txtItems3.visibility = visibility
        binding.txtItems4.visibility = visibility
    }

    private fun btnLeerQR_Click() {

        var _fechaHoy = FunGeneral.fecha("dd/MM/YYYY")
        var _fechaConsulta = binding.etDate.text.toString()
        if (_fechaHoy == _fechaConsulta){
            Globales.showWarningMessage("Solo puede leer QR de otros días diferentes al de hoy", requireContext())
            return
        }

        val integrador = IntentIntegrator(requireActivity())
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrador.setPrompt("")
        integrador.setBeepEnabled(true)
        integrador.initiateScan()
    }

    var _huboCambios = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {

                if (_huboCambios){
                    _huboCambios = false
                    btnReportar()
                }
            } else {
                var codigoqr = result.contents

                var _codEmpleado = -1
                var objEmpleado = empleadoDAO.obtenerEmpleadoXQR(codigoqr)
                if (objEmpleado == null){
                    Toast.makeText(requireContext(), "QR de trabajador no existe", Toast.LENGTH_SHORT).show()
                    if (_huboCambios){
                        _huboCambios = false
                        btnReportar()
                    }
                    return
                }

                _codEmpleado = objEmpleado.PER_Codigo

                var _fechaAux = binding.etDate.text.toString()
                val _horaAux = binding.txtHoraActual.text.toString()


                val _fecha = FunGeneral.obtenerFecha_PorFormato(_fechaAux, "YYYYMMdd")
                val asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida(_codEmpleado, _fecha)

                if (asistencia == null){
                    Toast.makeText(requireContext(), "Trabajador no tiene salidas pendientes en el día ${_fechaAux}", Toast.LENGTH_SHORT).show()
                    if (_huboCambios){
                        _huboCambios = false
                        btnReportar()
                    }
                    return
                }

                if (asistencia.AP_HoraSalida != ""){
                    Toast.makeText(requireContext(), "Trabajador ya tiene hora de salida el día: ${_fechaAux}", Toast.LENGTH_SHORT).show()
                    if (_huboCambios){
                        _huboCambios = false
                        btnReportar()
                    }
                    return
                }

                _fechaAux = binding.txtFechaActual.text.toString()

                var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "YYYYMMdd HH:mm:ss")
                var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "dd/MM/YYYY hh:mm:ss a")

                asistenciaPuertaDAO.actualizarHoraSalida(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
                Toast.makeText(requireContext(), "Hora de Salida Registrada en: ${_fechaAux}", Toast.LENGTH_SHORT).show()

                _huboCambios = true
                btnLeerQR_Click()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        binding.etDate.setText("$_dia/$_mes/$year")

        btnReportar()
    }

    private fun showDatePickerDialog3() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected3(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    fun onDateSelected3(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        var f1 = vCambiarHora2.findViewById<EditText>(R.id.etDate)
        f1.setText("$_dia/$_mes/$year")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menuverasistencias, menu)
        menu.setGroupDividerEnabled(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_cambiar_hora_verasistencias -> {
                if (vCambiarHora2.parent != null) {
                    (vCambiarHora2.parent as ViewGroup).removeView(vCambiarHora2)
                    alertDialog2.setView(vCambiarHora2)
                }
                construirAlertDialog2()
                alertDialog2.show()
                return true
            }
            R.id.action_retirar_todos_verasistencias -> {
                FunGeneral.FechaMostrar_AsistenciaDiaria_Stop(true)
                return true
            }
            R.id.action_retirar_todos -> {
                if (vCambiarHora.parent != null) {
                    (vCambiarHora.parent as ViewGroup).removeView(vCambiarHora)
                    alertDialog.setView(vCambiarHora)
                }
                construirAlertDialog()
                alertDialog.show()
                return true
            }
            R.id.action_sincronizar_asistencia -> {

                if (isOnlineMode) {
                    Globales.showWarningMessage("La sincronización manual solo aplica a datos locales.", requireContext())
                    return true
                }


                val listaSincronizar = ArrayList<AsistenciaPuerta>()
                val asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
                val listaPendientes: List<AsistenciaPuerta> = try {
                    asistenciaPuertaDAO.listarAsistencias_ParaSincronizar()
                } catch (e: Exception) {
                    Log.e("SYNC_ALL", "Error al obtener lista para sincronizar de Room", e)
                    Globales.showErrorMessage("Error al leer datos locales pendientes.", requireContext())
                    emptyList()
                }



                var empleadosNoSincronizadosCount = 0
                val empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()

                for (asistenciaLocal in listaPendientes) {
                    val objEmpleado: Empleado? = try {
                        empleadoDAO.PA_ObtenerEmpleado_PorCodigo(asistenciaLocal.PER_Codigo)
                    } catch (e: Exception) {
                        Log.w("SYNC_ALL", "No se pudo obtener empleado local para PER_Codigo ${asistenciaLocal.PER_Codigo}", e)
                        null
                    }

                    if (objEmpleado != null) {
                        if (objEmpleado.PER_CodigoNube == -1) {

                            empleadosNoSincronizadosCount++
                        } else {

                            listaSincronizar.add(asistenciaLocal)
                        }
                    } else {

                        Log.w("SYNC_ALL", "Omitiendo asistencia AP_Codigo ${asistenciaLocal.AP_Codigo} por no encontrar empleado local.")
                    }
                }


                if (listaSincronizar.isNotEmpty()) {
                    var mensajeDialogo = "¿Desea sincronizar ${listaSincronizar.size} registro(s)?"
                    if (empleadosNoSincronizadosCount > 0) {
                        mensajeDialogo += "\n\n(Nota: Se omitirán $empleadosNoSincronizadosCount registros de empleados no sincronizados)."
                    }

                    AwesomeDialog.build(requireActivity())
                        .title("Confirmar Sincronización")
                        .body(mensajeDialogo)
                        .icon(R.drawable.question2)
                        .onPositive("Sincronizar") {
                            Sincronizar(listaSincronizar)
                        }
                        .onNegative("Cancelar") {

                        }
                } else {

                    var mensajeVacio = "No hay asistencias válidas pendientes para sincronizar."
                    if (empleadosNoSincronizadosCount > 0 && listaPendientes.isNotEmpty()) {
                        mensajeVacio = "Hay $empleadosNoSincronizadosCount asistencia(s) pendiente(s), pero pertenecen a empleados no sincronizados."
                    }
                    Globales.showWarningMessage(mensajeVacio, requireContext())
                }

                return true
            }
            /**R.id.action_sincronizar_asistencia -> {

                var _campo = 0
                var _retirados = 0
                var _nosincronizados = 0

                for (asistenciaPuerta in lista) {
//                    if (asistenciaPuerta.AP_HoraSalida == "") _campo++
//                    else _retirados++
                    var objEmpleado : Empleado = ConexionSqlite.getInstancia(requireContext()).empleadoDAO().PA_ObtenerEmpleado_PorCodigo(asistenciaPuerta.PER_Codigo)
                    if (objEmpleado != null){
                        if (objEmpleado.PER_CodigoNube == -1) _nosincronizados++
                    }
                }

//                if (_retirados == 0){
//                    Globales.showWarningMessage("Debe existir almenos 1 trabajador con hora de salida para sincronizar", requireContext())
//                    return true
//                }

//                if (_retirados == _nosincronizados){
//                    Globales.showWarningMessage("Primero debe subir data de los nuevos trabajadores", requireContext())
//                    return true
//                }

                if (_nosincronizados > 0){
                    //Toast.makeText(requireContext(), "Subiendo data, excepto: ${_nosincronizados} de trabajadores no sincronizados", Toast.LENGTH_LONG).show()
                }

                val listaSincronizar = ArrayList<AsistenciaPuerta>()

                val asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
                val listaAsistencia = asistenciaPuertaDAO.listarAsistencias_ParaSincronizar()

                for (asistenciaPuerta in listaAsistencia) {
//                    if (asistenciaPuerta.AP_HoraSalida == "") {
                        //Todavia están en campo
//                    }
//                    else
//                    {
                        //Están retirados
                        val objEmpleado : Empleado = ConexionSqlite.getInstancia(requireContext()).empleadoDAO().PA_ObtenerEmpleado_PorCodigo(asistenciaPuerta.PER_Codigo)
                        if (objEmpleado != null){
                            if (objEmpleado.PER_CodigoNube == -1) {
                                //Existen en dispostivo pero no están sincronizados
                            }
                            else{
                                //Solo sincronizar los que tienen codigo en SQL
                                listaSincronizar.add(asistenciaPuerta)
                            }
                        }
//                    }
                }

                if (listaSincronizar.size > 0){

                    var PI_Codigo : Int = -1

                    var datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
                    var obj = datosInicialesDAO.PA_ObtenerDatosIniciales()
                    if (obj == null){
                        Globales.showWarningMessage("Debe descargar datos para asignar una puerta al teléfono.", requireContext())
                        return true
                    }

                    PI_Codigo = obj.PI_Codigo

                    //if (PI_Codigo == -1){
                      //  Globales.showWarningMessage("Debe descargar datos para asignar una puerta al teléfono. \nCodigo de Puerta: -1 no válido", requireContext())
                        //return true
                    //}

                    AwesomeDialog.build((context) as Activity)
                        .title("Confirme")
                        .body("¿Desea sincronizar: "+ listaSincronizar.size +" registro(s)?")
                        .icon(R.drawable.success)
                        .onPositive("OK") {
                            Sincronizar(listaSincronizar)
                        }
                        .onNegative("Cancel") {

                        }
                }else{
                    Globales.showWarningMessage("No hay asistencias pendientes para sincronizar.", requireContext())
                }

                return true
            }**/



            else -> return super.onOptionsItemSelected(item)
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

    private fun showDatePickerDialog4() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected4(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    fun onDateSelected4(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        var f1 = vCambiar_HI.findViewById<EditText>(R.id.etDate)
        f1.setText("$_dia/$_mes/$year")
    }

    private fun showDatePickerDialog_HS() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected_HS(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    fun onDateSelected_HS(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        var f1 = vCambiar_HS.findViewById<EditText>(R.id.etDate)
        f1.setText("$_dia/$_mes/$year")
    }

    private fun construirAlertDialog() {


        var f1 = vCambiarHora.findViewById<EditText>(R.id.etDate)
        var _fecha = binding.etDate.text.toString()
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

            var _fecha = f1.text.toString()
            var _fechaAux = FunGeneral.obtenerFecha_PorFormato(_fecha, "dd/MM/YYYY")
            var _horaAux = _horaSelect

            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaSelect, "YYYYMMdd HH:mm:ss")
            var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "dd/MM/YYYY hh:mm:ss a")

            var _cantRetirados = 0
            for (item in lista) {

                if (item is AsistenciaPuerta) {

                    if (item.AP_HoraSalida.isEmpty()) {
                        _cantRetirados++
                        try {

                            asistenciaPuertaDAO.actualizarHoraSalida(_horasalida, _horasalida_mostrar, item.AP_Codigo)
                            Log.d("RETIRAR_TODOS", "Actualizando salida local para AP_Codigo ${item.AP_Codigo}")
                        } catch (e: Exception) {
                            Log.e("RETIRAR_TODOS", "Error al actualizar salida local para AP_Codigo ${item.AP_Codigo}", e)

                            _cantRetirados--
                        }
                    } else {
                        Log.d("RETIRAR_TODOS", "Omitiendo AP_Codigo ${item.AP_Codigo}, ya tiene salida local.")
                    }



                } else {

                    Log.d("RETIRAR_TODOS", "Omitiendo item de tipo ${item?.javaClass?.simpleName}")
                }

            }


            if (_cantRetirados > 0) {
                Toast.makeText(requireContext(), "Se actualizó la hora de salida para $_cantRetirados registros locales.", Toast.LENGTH_SHORT).show()
                btnReportar()
            } else {
                Toast.makeText(requireContext(), "No se encontraron registros locales sin hora de salida para actualizar.", Toast.LENGTH_SHORT).show()
            }

        }
        alertDialog.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }

    private fun construirAlertDialog2() {
        var f1 = vCambiarHora2.findViewById<EditText>(R.id.etDate)
        var _fecha = binding.txtFechaActual.text.toString()
        var _fechaConsulta = FunGeneral.obtenerFecha_PorFormato(_fecha, "dd/MM/YYYY")
        f1.setText(_fechaConsulta)
        f1.setOnClickListener { showDatePickerDialog3() }

        alertDialog2 = AlertDialog.Builder(requireContext())
        alertDialog2.setView(vCambiarHora2)
        alertDialog2.setCancelable(false)
        alertDialog2.setPositiveButton("Ok") { dialoginterface, i ->

            val tmrHoraNueva = vCambiarHora2.findViewById<TimePicker>(R.id.tmrHoraNueva)
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

            var _FechaSelect = f1.text.toString()

            binding.txtFechaActual.setText(_FechaSelect)
            binding.txtHoraActual.setText(_horaSelect)
            FunGeneral.FechaMostrar_AsistenciaDiaria_Stop(false)
            FunGeneral.HoraMostrar_AsistenciaDiaria_Stop(false)
        }
        alertDialog2.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }

    override fun onClick(position: Int, tipo: String) {
        if (position < 0 || position >= lista.size) {
            Log.e("AsistenciaDiaria", "onClick recibió posición inválida: $position")
            return
        }

        val obj = lista[position]

        when (tipo) {
            "Sincronizar" -> {

                if (obj is AsistenciaPuerta) {

                    val empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
                    val objEmpleado: Empleado? = try {
                        empleadoDAO.PA_ObtenerEmpleado_PorCodigo(obj.PER_Codigo)
                    } catch (e: Exception) {
                        Log.e("SYNC_CLICK", "Error al obtener empleado local para PER_Codigo ${obj.PER_Codigo}", e)
                        null
                    }


                    if (objEmpleado == null) {
                        Globales.showErrorMessage("No se pudo encontrar el empleado asociado a esta asistencia local (ID: ${obj.PER_Codigo}). No se puede sincronizar.", requireContext())
                        return
                    }

                    if (objEmpleado.PER_CodigoNube == -1) {
                        Globales.showWarningMessage("El trabajador '${objEmpleado.nombres_apellidos()}' no está sincronizado con el servidor (Código Nube es -1). Sincronice datos de empleados primero.", requireContext())
                        return
                    }


                    val listaSincronizar = ArrayList<AsistenciaPuerta>()
                    listaSincronizar.add(obj)
                    Sincronizar(listaSincronizar)

                } else {

                    Log.e("AsistenciaDiaria", "onClick tipo 'Sincronizar' recibido, pero el objeto en posición $position no es AsistenciaPuerta. Tipo: ${obj?.javaClass?.simpleName}")
                    Globales.showErrorMessage("Error interno: El tipo de dato seleccionado para sincronizar no es válido.", requireContext())
                }

            }

        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_editar_ingreso -> {

                if (vCambiar_HI.parent != null) {
                    (vCambiar_HI.parent as ViewGroup).removeView(vCambiar_HI)
                    alertDialog_HI.setView(vCambiar_HI)
                }
                construirAlertDialog_HI()
                return true
            }
            R.id.action_editar_salida -> {

                if (vCambiar_HS.parent != null) {
                    (vCambiar_HS.parent as ViewGroup).removeView(vCambiar_HS)
                    alertDialog_HS.setView(vCambiar_HS)
                }
                construirAlertDialog_HS()
                return true
            }
            R.id.action_eliminar_asistencia -> {
                Globales.showWarningMessageAndCuestions("¿Desea Eliminar La Asistencia?", requireContext(),
                    {
                        asistenciaPuertaDAO.PA_EliminarAsistencia_PorCodigo(DatosTareo.AP_Codigo)
                        btnReportar()
                    },
                    {

                    })
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun construirAlertDialog_HI() {

        var asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(DatosTareo.AP_Codigo)
        if (asistencia == null){
            Toast.makeText(requireContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show()
            return
        }

        if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
            Toast.makeText(requireContext(), "La asistencia ya fue sincronizada, no puede modificar", Toast.LENGTH_SHORT).show()
            return
        }

        var f1 = vCambiar_HI.findViewById<EditText>(R.id.etDate)
        var h1 = vCambiar_HI.findViewById<TimePicker>(R.id.tmrHoraNueva)

        var _fecha = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "dd/MM/YYYY")
        var _fechaConsulta = _fecha
        f1.setText(_fechaConsulta)
        f1.setOnClickListener { showDatePickerDialog4() }
        f1.isEnabled = false

        h1.hour = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "HH").toInt()
        h1.minute = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "mm").toInt()
        if (h1.hour >= 12){
            h1.setIs24HourView(false)
        }

        alertDialog_HI = AlertDialog.Builder(requireContext())
        alertDialog_HI.setView(vCambiar_HI)
        alertDialog_HI.setCancelable(false)
        alertDialog_HI.setPositiveButton("Ok") { dialoginterface, i ->

            val tmrHoraNueva = vCambiar_HI.findViewById<TimePicker>(R.id.tmrHoraNueva)
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

            var _FechaSelect = f1.text.toString()


            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "YYYYMMdd HH:mm:ss")
            var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "dd/MM/YYYY hh:mm:ss a")

            asistenciaPuertaDAO.actualizarHoraEntrada(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
            btnReportar()
        }
        alertDialog_HI.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
        alertDialog_HI.show()
    }

    private fun construirAlertDialog_HS() {

        var asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(DatosTareo.AP_Codigo)
        if (asistencia == null){
            Toast.makeText(requireContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show()
            return
        }

      if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
          Toast.makeText(requireContext(), "La asistencia ya fue sincronizada, no puede modificar", Toast.LENGTH_SHORT).show()
           return
      }

      if (asistencia.AP_HoraSalida == ""){
           Toast.makeText(requireContext(), "El trabajador sigue en campo, marque salida para cambiar dato", Toast.LENGTH_SHORT).show()
           return
       }

        var _horasalidaactual = ""
        if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
            if (asistencia.AP_HoraSalida == ""){
                _horasalidaactual = asistencia.AP_HoraEntrada
            }
            else{
                Toast.makeText(requireContext(), "El trabajador fue sincronizado y ya tiene hora de salida", Toast.LENGTH_SHORT).show()
                return
            }
        }
        else{
            if (asistencia.AP_HoraSalida == ""){
                _horasalidaactual = asistencia.AP_HoraEntrada
            }
            else{
                _horasalidaactual = asistencia.AP_HoraSalida
            }

        }

        var f1 = vCambiar_HS.findViewById<EditText>(R.id.etDate)
        var h1 = vCambiar_HS.findViewById<TimePicker>(R.id.tmrHoraNueva)

        var _fecha = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "dd/MM/YYYY")
        var _fechaConsulta = _fecha
        f1.setText(_fechaConsulta)
        f1.setOnClickListener { showDatePickerDialog_HS() }


        h1.hour = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "HH").toInt()
        h1.minute = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "mm").toInt()
        if (h1.hour >= 12){
            h1.setIs24HourView(false)
        }

        alertDialog_HS = AlertDialog.Builder(requireContext())
        alertDialog_HS.setView(vCambiar_HS)
        alertDialog_HS.setCancelable(false)
        alertDialog_HS.setPositiveButton("Ok") { dialoginterface, i ->

            val tmrHoraNueva = vCambiar_HS.findViewById<TimePicker>(R.id.tmrHoraNueva)
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

            var _FechaSelect = f1.text.toString()


            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "YYYYMMdd HH:mm:ss")
            var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "dd/MM/YYYY hh:mm:ss a")

            asistenciaPuertaDAO.actualizarHoraSalida(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
            btnReportar()
        }
        alertDialog_HS.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
        alertDialog_HS.show()
    }
    private fun Sincronizar(lista: java.util.ArrayList<AsistenciaPuerta>){
        var body:String = ""
        if (lista.size == 0){
            Globales.showWarningMessage("No hay asistencias pendientes para sincronizar.", requireContext())
            return
        }else{

            var PI_Codigo : Int = -1

            var xx = 0
            lista.iterator().forEach {

                val empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
                val objEmpleado : Empleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigo(it.PER_Codigo)

                var _codEmpleadoNube = -1
                if (objEmpleado != null){
                    _codEmpleadoNube = objEmpleado.PER_CodigoNube
                }

                if (xx == 0){
                    body = it.envioDatosNube()+"|${_codEmpleadoNube}"
                }else{
                    body += "%" + it.envioDatosNube()+"|${_codEmpleadoNube}"
                }

                xx++
            }

            var i = 0
            try{

                val rs = EmpleadoDALC.PA_ProcesarAsistenciaEmpleados_Android_ult3(body, PI_Codigo)
                while (rs.next()) {
                    i++
                    val rpta = rs.getString("Rpta")
                    val _ap_codigo = rs.getInt("AP_Codigo")
                    val _entrada = rs.getString("AP_HoraEntrada")
                    val _salida = rs.getString("AP_HoraSalida")

                    var _cuenta = 0;
                    if (_entrada != "") _cuenta++;
                    if (_salida != "") _cuenta++;

                    var _estadito = "";
                    if (_cuenta == 2) _estadito = "SINCRONIZADO 2/2";
                    else if (_cuenta == 1) _estadito = "SINCRONIZADO 1/2";
                    else _estadito = "NO SINCRONIZADO";

                    asistenciaPuertaDAO.actualizarHoraEntradaYSalida(_entrada, _salida, _estadito, _ap_codigo)
                }

                if (xx != i){
                    Globales.showWarningMessage("Se sincronizaron $i de $xx registros", requireContext())
                }
                else{
                    Globales.showSuccessMessage("Se sincronizaron $i registros", requireContext())
                }

                btnReportar()
            }catch (ex: Exception){
                Globales.showErrorMessage(ex.message.toString(), requireContext())
            }
        }
    }


    private fun listarSedes(){
        adapterSedes.clear()
        val lista = sucursalDAO.PA_ListarSucursal() as ArrayList<Sucursal>

        if (lista.isNotEmpty()) {
            lista.iterator().forEach {
                adapterSedes.add(it)
            }
        }
        adapterSedes.notifyDataSetChanged()
    }
    private fun btnReportar() {
        if (isOnlineMode) {

            fetchOnlineData()
        } else {

            fetchLocalData()
        }
    }
    private fun fetchOnlineData() {

        if (!FunGeneral.tieneConexionInternet(requireContext())) {
            Globales.showErrorMessage("Se requiere conexión para ver datos online.", requireContext())
            lista.clear()
            adapter.notifyDataSetChanged()
            updateCounters(null)


            return
        }


        val _fecha = binding.etDate.text.toString()
        val _fechaConsulta = FunGeneral.obtenerFecha_PorFormato(_fecha, "YYYYMMdd")
        val buscar = "%${binding.txtBusqueda.text}%"


        val _solocampo = when {
            binding.txtItems.isChecked && !binding.txtItems2.isChecked -> "SI"
            !binding.txtItems.isChecked && binding.txtItems2.isChecked -> "SIx"
            binding.txtItems.isChecked && binding.txtItems2.isChecked -> "xx"
            else -> "NO"
        }


        val frm = FunGeneral.mostrarCargando(requireContext(), "Consultando servidor...")

        try {
            frm.show()


            viewLifecycleOwner.lifecycleScope.launch {
                val onlineResults =  ArrayList<AsistenciaOnlineDTO>()
                var errorMsg: String? = null

                withContext(Dispatchers.IO) {
                    var rs: ResultSet? = null
                    try {
                        Log.d("FETCH_ONLINE", "Ejecutando PA_ListarAsistenciaPuerta_Android_Online con Fecha: $_fechaConsulta, Buscar: $buscar, SoloCampo: $_solocampo")
                        rs = EmpleadoDALC.PA_ListarAsistenciaPuerta_Android_Online(_fechaConsulta, buscar, _solocampo)
                        while (rs.next()) {

                            val dto = AsistenciaOnlineDTO(
                                AP_Codigo = rs.getInt("AP_Codigo"),
                                AP_Fecha = rs.getString("AP_Fecha"),
                                AP_HoraEntrada = rs.getString("AP_HoraEntrada"),
                                AP_HoraSalida = rs.getString("AP_HoraSalida"),
                                PER_Codigo = rs.getInt("PER_Codigo"),
                                AP_EstadoSincronizacion = rs.getString("AP_EstadoSincronizacion"),
                                CodigoQR = rs.getString("CodigoQR"),
                                Empleado = rs.getString("Empleado"),
                                Puerta = rs.getString("Puerta"),
                                Sede = rs.getString("Sede")
                            )
                            onlineResults.add(dto)
                        }
                    } catch (e: Exception) {
                        errorMsg = "Error al consultar servidor: ${e.message}"
                        Log.e("FETCH_ONLINE", errorMsg, e)
                    } finally {

                        try {
                            rs?.statement?.connection?.close()
                        } catch (closeEx: Exception) {
                            Log.e("FETCH_ONLINE", "Error al cerrar conexión SQL", closeEx)
                        }
                    }
                }


                if (frm.isShowing) {
                    frm.dismiss()
                }

                if (errorMsg != null) {

                    Log.d("FETCH_ONLINE", errorMsg!!)
                    lista.clear()
                } else {
                    lista.clear()
                    lista.addAll(onlineResults)
                    Log.d("FETCH_ONLINE", "Datos online cargados: ${lista.size} registros.")
                }
                adapter.notifyDataSetChanged()
                updateCounters(lista)
            }
        } catch (dialogEx: Exception){
            Log.e("FETCH_ONLINE", "Error con el diálogo de carga", dialogEx)
            if (frm.isShowing) { frm.dismiss()}
        }
    }
    private fun fetchLocalData() {

        if (arraySedes.isEmpty() && binding.cboSedes.adapter.count == 0) {

            Globales.showWarningMessage("Migre las sedes desde configuración.", requireContext())
            return
        }
        if (binding.cboSedes.selectedItemPosition == -1 && arraySedes.isNotEmpty()) {

        }



        val sedePosition = binding.cboSedes.selectedItemPosition.takeIf { it >= 0 && it < arraySedes.size } ?: 0
        val SUC_Codigo = arraySedes.getOrNull(sedePosition)?.SUC_Codigo ?: -1L


        val _fecha = binding.etDate.text.toString()
        val _fechaConsulta = FunGeneral.obtenerFecha_PorFormato(_fecha, "YYYYMMdd")
        val buscar = "%${binding.txtBusqueda.text}%"


        val _solocampo = when {
            binding.txtItems.isChecked && !binding.txtItems2.isChecked -> "SI"
            !binding.txtItems.isChecked && binding.txtItems2.isChecked -> "SIx"
            binding.txtItems.isChecked && binding.txtItems2.isChecked -> "xx"
            else -> "NO"
        }


        val localResults: List<AsistenciaPuerta> = try {
            if (SUC_Codigo == -1L) {
                asistenciaPuertaDAO.listarAsistenciaPorFecha(_fechaConsulta, buscar, _solocampo)
            } else {
                asistenciaPuertaDAO.listarAsistenciaPorFechaSede(_fechaConsulta, SUC_Codigo.toInt(), buscar, _solocampo)
            }
        } catch (e: Exception) {
            Log.e("FETCH_LOCAL", "Error consultando Room", e)
            Globales.showErrorMessage("Error al leer datos locales.", requireContext())
            emptyList()
        }


        lista.clear()
        lista.addAll(localResults)
        adapter.notifyDataSetChanged()
        updateCounters(lista)
        Log.d("FETCH_LOCAL", "Datos locales cargados: ${lista.size} registros.")
    }



    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnReportar()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        btnReportar()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}