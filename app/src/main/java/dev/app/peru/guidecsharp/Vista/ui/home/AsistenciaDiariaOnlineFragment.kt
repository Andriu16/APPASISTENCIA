package dev.app.peru.guidecsharp.Vista.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.*
import com.google.zxing.integration.android.IntentIntegrator
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.AsistenciaPuerta_sql
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.AsistenciaPuertaDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.SucursalDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.DatosIniciales
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Sucursal
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.AsistenciaAD_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.AsistenciaVH_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.AsistenciaAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.AsistenciaVH_sqlite
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentAsistenciaDiariaBinding
import dev.app.peru.guidecsharp.databinding.FragmentAsistenciaDiariaOnlineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList

class AsistenciaDiariaOnlineFragment : Fragment(), TextWatcher, AdapterView.OnItemSelectedListener, AsistenciaAD_sqlite.MyClickListener,
    AsistenciaAD_sql.MyClickListener {

    private var _binding: FragmentAsistenciaDiariaOnlineBinding? = null
    private val binding get() = _binding!!


    private var lista = java.util.ArrayList<AsistenciaPuerta_sql>()
    private lateinit var adapter : RecyclerView.Adapter<AsistenciaVH_sql>

    lateinit var vCambiarHora: View
    lateinit var alertDialog: AlertDialog.Builder

    lateinit var vCambiarHora2: View
    lateinit var alertDialog2: AlertDialog.Builder

    lateinit var vCambiar_HI : View
    lateinit var alertDialog_HI: AlertDialog.Builder

    lateinit var vCambiar_HS : View
    lateinit var alertDialog_HS: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View? {
        _binding = FragmentAsistenciaDiariaOnlineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Globales.origenQR = "VERASISTENCIA2"
        Globales.verasis2 = this

        adapter = AsistenciaAD_sql(lista, this)
        binding.rvEmpleadoSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpleadoSqlite.adapter = adapter

        val c = Calendar.getInstance()
        var _diaOT = c.get(Calendar.DAY_OF_MONTH)
        var _mesOT = c.get(Calendar.MONTH)
        var _añoOT = c.get(Calendar.YEAR)
        onDateSelected(_diaOT, _mesOT, _añoOT)
        binding.etDate.setOnClickListener { showDatePickerDialog() }
        binding.btnBuscar.setOnClickListener { btnLeerQR_Click() }
        binding.txtBusqueda.addTextChangedListener(this)
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

        btnReportar()

        return root
    }
    

    private fun btnLeerQR_Click() {
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
                //Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                if (_huboCambios){
                    _huboCambios = false
                    btnReportar()
                }
            } else {
                var codigoqr = result.contents

                var _fechaAux = binding.etDate.text.toString()
                val _horaAux = binding.txtHoraActual.text.toString()

                //Solo para salidas
                val _fecha = FunGeneral.obtenerFecha_PorFormato(_fechaAux, "YYYYMMdd")

                val frm = FunGeneral.mostrarCargando(requireContext())
                frm.show()

                var rs : ResultSet? = null
                var _msj = ""

                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        rs = EmpleadoDALC.PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida_Android(codigoqr, _fecha)
                    } catch (e: Exception) {
                        _msj = e.message.toString()
                    }

                    activity?.runOnUiThread {
                        frm.dismiss()

                        if (_msj != "") {
                            Globales.showWarningMessage(_msj, requireContext())
                            return@runOnUiThread
                        }

                        var AP_Codigo : Int = -1
                        var AP_HoraSalida : String = ""
                        while (rs!!.next()) {
                            AP_Codigo = rs!!.getInt("AP_Codigo")
                            AP_HoraSalida = rs!!.getString("AP_HoraSalida")
                        }

                        if (AP_Codigo == -1){
                            Toast.makeText(requireContext(), "Trabajador no tiene salidas pendientes en el día ${_fechaAux}", Toast.LENGTH_SHORT).show()
                            if (_huboCambios){
                                _huboCambios = false
                            }
                        }
                        else if (AP_HoraSalida != ""){
                            Toast.makeText(requireContext(), "Trabajador ya tiene hora de salida el día: ${_fechaAux}", Toast.LENGTH_SHORT).show()
                            if (_huboCambios){
                                _huboCambios = false
                            }
                        }
                        else{
                            _fechaAux = binding.txtFechaActual.text.toString()

                            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_fechaAux, _horaAux, "YYYYMMdd HH:mm:ss")

                            var PI_Codigo : Int = -1

                            var datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
                            var obj = datosInicialesDAO.PA_ObtenerDatosIniciales()
                            if (obj != null) PI_Codigo = obj.PI_Codigo

                            frm.show()

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    rs = EmpleadoDALC.PA_ActualizarHoraSalida_Android(AP_Codigo, _horasalida, PI_Codigo, Globales.cod)
                                } catch (e: Exception) {
                                    _msj = e.message.toString()
                                }

                                activity?.runOnUiThread {
                                    frm.dismiss()

                                    if (_msj != "") {
                                        Globales.showWarningMessage(_msj, requireContext())
                                        return@runOnUiThread
                                    }

                                    Toast.makeText(requireContext(), "Hora de Salida Registrada en: ${_fechaAux}", Toast.LENGTH_SHORT).show()

                                    _huboCambios = true
                                    btnLeerQR_Click()
                                }
                            }
                        }
                    }
                }
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

        //Valores iniciales
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
            for (asistenciaPuerta in lista) {

            }
            btnReportar()

            if (_cantRetirados == 0){
                Globales.showWarningMessage("No hay asistencias disponibles para actualizar hora de salida.", requireContext())
            }
        }
        alertDialog.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }

    private fun construirAlertDialog2() {
        //Valores iniciales
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

    override fun onClick(position: Int,tipo:String){
        when(tipo){
            "Sincronizar" -> {
                val obj = lista[position]


            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_editar_ingreso -> {
                //Mostrar hora de ingreso para edicion
                if (vCambiar_HI.parent != null) {
                    (vCambiar_HI.parent as ViewGroup).removeView(vCambiar_HI)
                    alertDialog_HI.setView(vCambiar_HI)
                }
                construirAlertDialog_HI()
                return true
            }
            R.id.action_editar_salida -> {
                //Mostrar hora de salida para edicion
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
                        //asistenciaPuertaDAO.PA_EliminarAsistencia_PorCodigo(DatosTareo.AP_Codigo)
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
        //Valores iniciales
//        var asistencia  // asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(DatosTareo.AP_Codigo)
//        if (asistencia == null){
//            Toast.makeText(requireContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
//            Toast.makeText(requireContext(), "La asistencia ya fue sincronizada, no puede modificar", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        var f1 = vCambiar_HI.findViewById<EditText>(R.id.etDate)
//        var h1 = vCambiar_HI.findViewById<TimePicker>(R.id.tmrHoraNueva)
//
//        var _fecha = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "dd/MM/YYYY")
//        var _fechaConsulta = _fecha
//        f1.setText(_fechaConsulta)
//        f1.setOnClickListener { showDatePickerDialog4() }
//        f1.isEnabled = false
//
//        h1.hour = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "HH").toInt()
//        h1.minute = FunGeneral.obtenerFechaHora_PorFormato_Nube(asistencia.AP_HoraEntrada, "mm").toInt()
//        if (h1.hour >= 12){
//            h1.setIs24HourView(false)
//        }
//
//        alertDialog_HI = AlertDialog.Builder(requireContext())
//        alertDialog_HI.setView(vCambiar_HI)
//        alertDialog_HI.setCancelable(false)
//        alertDialog_HI.setPositiveButton("Ok") { dialoginterface, i ->
//
//            val tmrHoraNueva = vCambiar_HI.findViewById<TimePicker>(R.id.tmrHoraNueva)
//            var hour = tmrHoraNueva.hour
//            var minute = tmrHoraNueva.minute
//
//            var am_pm = ""
//            when {
//                hour == 0 -> {
//                    hour += 12
//                    am_pm = "a. m."
//                }
//                hour == 12 -> {
//                    am_pm = "p. m."
//                }
//                hour > 12 -> {
//                    hour -= 12
//                    am_pm = "p. m."
//                }
//                else -> {
//                    am_pm = "a. m."
//                }
//            }
//
//            var calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
//            var _seg = calendar.time.seconds
//
//            var _horaSelect = ""
//            val hora = if(hour < 10)"0"+hour else hour
//            val min = if(minute < 10)"0"+minute else minute
//            val sec = if(_seg < 10) "0"+_seg else _seg
//            _horaSelect = "${hora}:${min}:${sec} ${am_pm}"
//
//            var _FechaSelect = f1.text.toString()
//
//            //Fecha y hora actualizar
//            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "YYYYMMdd HH:mm:ss")
//            var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "dd/MM/YYYY hh:mm:ss a")
//
//            //asistenciaPuertaDAO.actualizarHoraEntrada(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
//            btnReportar()
//        }
//        alertDialog_HI.setNegativeButton("Cerrar") { dialoginterface, i ->
//            dialoginterface.dismiss()
//        }
//        alertDialog_HI.show()
    }

    private fun construirAlertDialog_HS() {
        //Valores iniciales
//        var asistencia = asistenciaPuertaDAO.PA_ObtenerAsistencia_PorCodigo(DatosTareo.AP_Codigo)
//        if (asistencia == null){
//            Toast.makeText(requireContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show()
//            return
//        }

//        if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
//            Toast.makeText(requireContext(), "La asistencia ya fue sincronizada, no puede modificar", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (asistencia.AP_HoraSalida == ""){
//            Toast.makeText(requireContext(), "El trabajador sigue en campo, marque salida para cambiar dato", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        var _horasalidaactual = ""
//        if (asistencia.AP_EstadoSincronizacion == "SINCRONIZADO"){
//            if (asistencia.AP_HoraSalida == ""){
//                _horasalidaactual = asistencia.AP_HoraEntrada
//            }
//            else{
//                Toast.makeText(requireContext(), "El trabajador fue sincronizado y ya tiene hora de salida", Toast.LENGTH_SHORT).show()
//                return
//            }
//        }
//        else{
//            if (asistencia.AP_HoraSalida == ""){
//                _horasalidaactual = asistencia.AP_HoraEntrada
//            }
//            else{
//                _horasalidaactual = asistencia.AP_HoraSalida
//            }
//
//        }
//
//        var f1 = vCambiar_HS.findViewById<EditText>(R.id.etDate)
//        var h1 = vCambiar_HS.findViewById<TimePicker>(R.id.tmrHoraNueva)
//
//        var _fecha = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "dd/MM/YYYY")
//        var _fechaConsulta = _fecha
//        f1.setText(_fechaConsulta)
//        f1.setOnClickListener { showDatePickerDialog_HS() }
//        //f1.isEnabled = false
//
//        h1.hour = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "HH").toInt()
//        h1.minute = FunGeneral.obtenerFechaHora_PorFormato_Nube(_horasalidaactual, "mm").toInt()
//        if (h1.hour >= 12){
//            h1.setIs24HourView(false)
//        }
//
//        alertDialog_HS = AlertDialog.Builder(requireContext())
//        alertDialog_HS.setView(vCambiar_HS)
//        alertDialog_HS.setCancelable(false)
//        alertDialog_HS.setPositiveButton("Ok") { dialoginterface, i ->
//
//            val tmrHoraNueva = vCambiar_HS.findViewById<TimePicker>(R.id.tmrHoraNueva)
//            var hour = tmrHoraNueva.hour
//            var minute = tmrHoraNueva.minute
//
//            var am_pm = ""
//            when {
//                hour == 0 -> {
//                    hour += 12
//                    am_pm = "a. m."
//                }
//                hour == 12 -> {
//                    am_pm = "p. m."
//                }
//                hour > 12 -> {
//                    hour -= 12
//                    am_pm = "p. m."
//                }
//                else -> {
//                    am_pm = "a. m."
//                }
//            }
//
//            var calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
//            var _seg = calendar.time.seconds
//
//            var _horaSelect = ""
//            val hora = if(hour < 10)"0"+hour else hour
//            val min = if(minute < 10)"0"+minute else minute
//            val sec = if(_seg < 10) "0"+_seg else _seg
//            _horaSelect = "${hora}:${min}:${sec} ${am_pm}"
//
//            var _FechaSelect = f1.text.toString()
//
//            //Fecha y hora actualizar
//            var _horasalida = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "YYYYMMdd HH:mm:ss")
//            var _horasalida_mostrar = FunGeneral.obtenerFechaHora_PorFormato(_FechaSelect, _horaSelect, "dd/MM/YYYY hh:mm:ss a")
//
//            asistenciaPuertaDAO.actualizarHoraSalida(_horasalida, _horasalida_mostrar, asistencia.AP_Codigo)
//            btnReportar()
//        }
//        alertDialog_HS.setNegativeButton("Cerrar") { dialoginterface, i ->
//            dialoginterface.dismiss()
//        }
//        alertDialog_HS.show()
    }

    private fun Sincronizar(lista: java.util.ArrayList<AsistenciaPuerta>){
        var body:String = ""
        if (lista.size == 0){
            Globales.showWarningMessage("No hay asistencias pendientes para sincronizar.", requireContext())
            return
        }else{
            var PI_Codigo : Int = -1

            var datosInicialesDAO = ConexionSqlite.getInstancia(requireContext()).datosInicialesDAO()
            var obj = datosInicialesDAO.PA_ObtenerDatosIniciales()
            if (obj == null){
                Globales.showWarningMessage("Debe descargar datos para asignar una puerta al teléfono.", requireContext())
                return
            }

            PI_Codigo = obj.PI_Codigo

            //if (PI_Codigo == -1){
                //Globales.showWarningMessage("Debe descargar datos para asignar una puerta al teléfono. \nCodigo de Puerta: -1 no válido", requireContext())
              //  return
            //}

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

            //Log.i("body", body)
            //return

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

                    //asistenciaPuertaDAO.actualizarHoraEntradaYSalida(_entrada, _salida, _estadito, _ap_codigo)
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

    var _mostrarcargando = "SI"
    private fun btnReportar(){
        var _fecha = binding.etDate.text.toString()
        var _fechaConsulta = FunGeneral.obtenerFecha_PorFormato(_fecha, "YYYYMMdd")

        val buscar = "%"+binding.txtBusqueda.text.toString()+"%"

        var _solocampo = "NO"
        if (binding.txtItems.isChecked){
            _solocampo = "SI"
        }

        if (binding.txtItems2.isChecked){
            _solocampo = "SIx"
        }

        if (binding.txtItems.isChecked && binding.txtItems2.isChecked){
            _solocampo = "xx"
        }

        val frm = FunGeneral.mostrarCargando(requireContext())

        if (_mostrarcargando == "NO"){
            _mostrarcargando = "SI"
        }else{
            frm.show()
        }

        var rs : ResultSet? = null
        var _msj = ""

        var _listita = kotlin.collections.ArrayList<AsistenciaPuerta_sql>()

        CoroutineScope(Dispatchers.IO).launch {

            try {
                rs = EmpleadoDALC.PA_ListarAsistenciaPuerta_Android_Online(_fechaConsulta, buscar, _solocampo)
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    Globales.showWarningMessage(_msj, requireContext())
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = AsistenciaPuerta_sql()
                    obj.AP_Codigo = rs!!.getInt("AP_Codigo")
                    obj.AP_Fecha = rs!!.getString("AP_Fecha")
                    obj.AP_HoraEntrada = rs!!.getString("AP_HoraEntrada")
                    obj.AP_HoraSalida = rs!!.getString("AP_HoraSalida")
                    obj.PER_Codigo = rs!!.getInt("PER_Codigo")
                    obj.AP_EstadoSincronizacion = rs!!.getString("AP_EstadoSincronizacion")
                    obj.CodigoQR = rs!!.getString("CodigoQR")
                    obj.Empleado = rs!!.getString("Empleado")
                    obj.Puerta = rs!!.getString("Puerta")
                    obj.Sede = rs!!.getString("Sede")
                    _listita.add(obj)
                }

                lista.clear()
                lista.addAll(_listita)
                adapter.notifyDataSetChanged()

                var _campo = 0
                var _retirados = 0
                var _sincronizados = 0
                var _porsincronizar = 0
                for (asistenciaPuerta in lista) {
                    if (asistenciaPuerta.AP_HoraSalida == "") _campo++
                    else _retirados++

                    if (asistenciaPuerta.AP_EstadoSincronizacion == "SINCRONIZADO") _sincronizados++
                    else if (asistenciaPuerta.AP_EstadoSincronizacion == "SINCRONIZADO 2/2") _sincronizados++
                    else _porsincronizar++
                }

                binding.txtItems.text = "EN CAMPO: ${_campo}"
                binding.txtItems2.text = "RETIRADOS: ${_retirados}"

                binding.txtItems3.text = "POR SINCRONIZAR: ${_porsincronizar}"
                binding.txtItems4.text = "SINCRONIZADOS: ${_sincronizados}"
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        _mostrarcargando = "NO"
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