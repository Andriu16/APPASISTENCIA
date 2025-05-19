package dev.app.peru.guidecsharp.Vista.ui.gallery

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta_Cap
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.DetalleProductividadAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.DetalleProductividadVH_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.DetalleTareoAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.DetalleTareoVH_sqlite
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentDetalleTareoBinding
import java.util.*
import kotlin.collections.ArrayList


class DetalleTareoFragment : Fragment(), TextWatcher, DetalleTareoAD_sqlite.MyClickListener,
    DetalleProductividadAD_sqlite.MyClickListener {
    private var _binding: FragmentDetalleTareoBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var tareoDAO: TareoDAO
    private lateinit var cultivoDAO: CultivoDAO
    private lateinit var laboresDAO: LaboresDAO
    private lateinit var actividadDAO: ActividadDAO

    private lateinit var tareoLaboresEmpleadoDAO: TareoLaboresEmpleadoDAO
    private var lista = ArrayList<TareoLaboresEmpleado>()
    private lateinit var adapter : RecyclerView.Adapter<DetalleTareoVH_sqlite>

    private lateinit var tareoLaboresEmpleado_ProductividadDAO: TareoLaboresEmpleado_ProductividadDAO
    private lateinit var empleadoDAO: EmpleadoDAO

    lateinit var vCambiarHoras: View
    lateinit var alertDialog: AlertDialog.Builder

    lateinit var vCambiarProductividad: View
    lateinit var alertDialog_Prod: AlertDialog.Builder

    lateinit var vCambiarHora: View
    lateinit var alertDialog_Hora: AlertDialog.Builder

    lateinit var asistenciaPuerta_CapDAO: AsistenciaPuerta_CapDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDetalleTareoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Globales.origenQR = "DETALLETAREO"
        Globales.detalletareo = this

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        asistenciaPuerta_CapDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuerta_CapDAO()

        tareoDAO = ConexionSqlite.getInstancia(requireContext()).tareoDAO()
        cultivoDAO = ConexionSqlite.getInstancia(requireContext()).cultivoDAO()
        laboresDAO = ConexionSqlite.getInstancia(requireContext()).laboresDAO()
        actividadDAO = ConexionSqlite.getInstancia(requireContext()).actividadDAO()

        empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
        tareoLaboresEmpleadoDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleadoDAO()
        tareoLaboresEmpleado_ProductividadDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleado_ProductividadDAO()
        adapter = DetalleTareoAD_sqlite(lista, this)

        binding.txtBusqueda.addTextChangedListener(this)
        binding.btnLeerQR.setOnClickListener { btnLeerQR_Click() }
        cargarDatos()

        vCambiarHoras = layoutInflater.inflate(R.layout.flotante_cambiar_hora, null)
        vCambiarProductividad = layoutInflater.inflate(R.layout.flotante_cambiar_productividad, null)

        _TipoHora = "ENCURSO"
        _FechaHora = FunGeneral.fecha("YYYYMMdd HH:mm:ss")

        vCambiarHora = layoutInflater.inflate(R.layout.flotante_selecciona_hora, null)
        construirAlertDialog_Hora()

        setHasOptionsMenu(true)
        return root
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_eliminar_trabajador_tareo -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                Globales.showWarningMessageAndCuestions("¿Desea Eliminar El Trabajador?", requireContext(),
                    {
                        tareoLaboresEmpleadoDAO.PA_EliminarTrabajador_PorCodigo(DatosTareo.idDetalle)

                        var _detalles = tareoLaboresEmpleadoDAO.PA_ListarDetalleTareo_PorCodigo(_idTareo, "%%")
                        tareoDAO.actualizarCantTrabajadores(_idTareo, _detalles.size)

                        binding.txtNroTrabDetalle.text = "Por ${_tipoTareo} : ${_detalles.size} Trabajador(es)"

                        btnReportar()
                    },
                    {

                    })
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun construirAlertDialog_Hora() {
        //Valores iniciales
        var f1 = vCambiarHora.findViewById<EditText>(R.id.etDate)
        var _fechaConsulta = FunGeneral.fecha("dd/MM/YYYY")
        f1.setText(_fechaConsulta)
        f1.isEnabled = false

        alertDialog_Hora = AlertDialog.Builder(requireContext())
        alertDialog_Hora.setView(vCambiarHora)
        alertDialog_Hora.setCancelable(false)
        alertDialog_Hora.setPositiveButton("Ok") { dialoginterface, i ->

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

            _FechaHora = FunGeneral.obtenerFechaHora_PorFormato(_fechaConsulta, _horaSelect, "YYYYMMdd HH:mm:ss")
            _TipoHora = "DETENIDO"
        }
        alertDialog_Hora.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }

    fun btnLeerQR_Click(){
        if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
            Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
            return
        }

        val integrador = IntentIntegrator(requireActivity())
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrador.setPrompt("")
        integrador.setBeepEnabled(true)
        integrador.initiateScan()
    }

    var _TipoHora = ""
    var _FechaHora = ""
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
                var codigoQR : String = result.contents
                var aux = tareoLaboresEmpleadoDAO.PA_ObtenerTareoLaboresEmpleado_PorCodigoQR(_idTareo, codigoQR)
                if (aux == null){
                    Globales.showErrorMessage("El trabajador no se encuentra en la labor", requireContext())
                    return
                }

                if (_TipoHora == "ENCURSO"){
                    _FechaHora = FunGeneral.fecha("YYYYMMdd HH:mm:ss")
                }
                else if (_FechaHora == ""){
                    Globales.showWarningMessage("Debe seleccionar hora para el registro de hora", requireContext())
                    return
                }

                var _msj = ""
                if (aux.TLE_Dia == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraDia(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de ingreso guardada"
                }
                else if (aux.TLE_Dia2 == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraDia2(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de salida guardada"
                }
                else if (aux.TLE_Tarde == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraTarde(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de ingreso guardada"
                }
                else if (aux.TLE_Tarde2 == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraTarde2(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de salida guardada"
                }
                else if (aux.TLE_Noche == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraNoche(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de ingreso guardada"
                }
                else if (aux.TLE_Noche2 == 0){
                    tareoLaboresEmpleadoDAO.actualizaHoraNoche2(aux.idDetalle, 1, _FechaHora)
                    _msj  = "Hora de salida guardada"
                }
                else{
                    Toast.makeText(requireContext(), "Trabajador ya tiene su horario completo. No se registró la información", Toast.LENGTH_LONG).show()
                    btnLeerQR_Click()
                    return
                }

                Toast.makeText(requireContext(), _msj, Toast.LENGTH_SHORT).show()
                calcularHoras_Return(aux.idDetalle)
                _huboCambios = true
                btnLeerQR_Click()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun calcularHoras_Return(idDetalle: Long) {
        var _totalDeHoras = 0f

        var obj = tareoLaboresEmpleadoDAO.PA_ObtenerTareoLaboresEmpleado_PorCodigoInterno(idDetalle)
        if (obj != null){
            //*************************************** Horas de la mañana ****************************
            //***************************************************************************************
            if (obj.TLE_Dia == 1 && obj.TLE_Dia2 == 1){
                var _iniDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraInicio)
                var _finDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraFin)

                var diff : Long = _finDia_DD.time - _iniDia_DD.time
                var xSeg = diff / 1000
                var xMin_Mañana = xSeg / 60

                _totalDeHoras += xMin_Mañana
            }

            //*************************************** Horas de la tarde *****************************
            //***************************************************************************************
            if (obj.TLE_Tarde == 1 && obj.TLE_Tarde2 == 1){
                var _iniTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraInicioTarde)
                var _finTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraFinTarde)

                var diff2 : Long = _finTarde_DD.time - _iniTarde_DD.time
                var xSeg2 = diff2 / 1000
                var xMin_Tarde = xSeg2 / 60

                _totalDeHoras += xMin_Tarde
            }

            //*************************************** Horas de la noche *****************************
            //***************************************************************************************
            if (obj.TLE_Noche == 1 && obj.TLE_Noche2 == 1){
                var _iniNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraInicioNoche)
                var _finNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Nube_Date(obj.TLE_HoraFinNoche)

                var diff3 : Long = _finNoche_DD.time - _iniNoche_DD.time
                var xSeg3 = diff3 / 1000
                var xMin_Noche = xSeg3 / 60

                _totalDeHoras += xMin_Noche
            }

            tareoLaboresEmpleadoDAO.actualizaHorasTrabajadas_JC(idDetalle, _totalDeHoras)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_vertareo, menu)
        menu.setGroupDividerEnabled(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    var PER_CodigoNube : Int = -1
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_pausar_cambiar_hora_2 -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                if (vCambiarHora.parent != null) {
                    (vCambiarHora.parent as ViewGroup).removeView(vCambiarHora)
                    alertDialog_Hora.setView(vCambiarHora)
                }
                construirAlertDialog_Hora()
                alertDialog_Hora.show()
                return true
            }
            R.id.action_hora_actual_2 -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                _TipoHora = "ENCURSO"
                _FechaHora = ""
                return true
            }
            R.id.action_cambiar_horas_masivo -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                PER_CodigoNube = -1

                if (vCambiarHoras.parent != null) {
                    (vCambiarHoras.parent as ViewGroup).removeView(vCambiarHoras)
                    alertDialog.setView(vCambiarHoras)
                }
                construirAlertDialog("Masivo")
                alertDialog.show()

                return true
            }
            R.id.action_cambiar_productividad_masivo -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                if (binding.txtNroTrabDetalle.text.toString().contains("JORNAL") == true)
                {
                    Globales.showWarningMessage("No puede manipular Productividad si el tareo es por JORNAL", requireContext())
                    return true
                }

                PER_CodigoNube = -1
                _idDetalle = -1

                if (vCambiarProductividad.parent != null) {
                    (vCambiarProductividad.parent as ViewGroup).removeView(vCambiarProductividad)
                    alertDialog_Prod.setView(vCambiarProductividad)
                }
                construirAlertDialog_Prod()
                alertDialog_Prod.show()

                return true
            }
            R.id.action_agregar_trabajador_conasistencia -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                var _fecha = FunGeneral.fecha("YYYYMMdd")
                var lista2 = asistenciaPuerta_CapDAO.listarAsistencia_CapPorFecha_Seleccionados(_fecha, "SI", "%%") as java.util.ArrayList<AsistenciaPuerta_Cap>
                for (dd in lista2) {
                    asistenciaPuerta_CapDAO.actualizarSeleccion(dd.id, "NO", "")
                }

                //findNavController().navigate(R.id.action_detalleTareoFragment_to_addTrabajadorConAsistenciaFragment)
                return true
            }
            R.id.action_agregar_trabajador_sinasistencia -> {
                if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado, solo puede ver los datos", requireContext())
                    return true
                }

                var lista2 = empleadoDAO.actualizarSeleccionados("")
                //findNavController().navigate(R.id.action_detalleTareoFragment_to_addTrabajadorSinAsistenciaFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    var _idTareo : Long = -1
    var _idLabor : Long = -1
    var _tipoTareo = ""
    fun cargarDatos(){
        _idTareo = DatosTareo._CodTareoSelect

        var obj = tareoDAO.PA_ObtenerTareo_PorCodigo(_idTareo)
        if (obj != null){
            var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(obj.CU_Codigo)
            var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(obj.Lab_Codigo)
            var objActividad = actividadDAO.PA_ObtenerActividad_PorCodigo(obj.AC_Codigo)

            binding.txtCultivoDetalle.text = "Cultivo: ${objCultivo.CU_Descripcion}"
            binding.txtLaborDetalle.text = "Labor: ${objLabor.Lab_Descripcion} - ${objActividad.AC_Descripcion}"
            binding.txtNroTrabDetalle.text = "Por ${obj.TA_TipoTareo} : ${obj._Trabajadores} Trabajador(es)"

            _tipoTareo = obj.TA_TipoTareo
            _idLabor = obj.Lab_Codigo
        }

        btnReportar()
    }

    fun btnReportar(){

        val bb = "%"+binding.txtBusqueda.text.toString()+"%"
        lista = tareoLaboresEmpleadoDAO.PA_ListarDetalleTareo_PorCodigo(_idTareo, bb) as ArrayList<TareoLaboresEmpleado>

        adapter = DetalleTareoAD_sqlite(lista, this)
        binding.rvEmpleadoSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpleadoSqlite.adapter = adapter
        binding.txtItems.text = "Resultados de búsqueda: ${lista.size}"

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnReportar()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    var _idDetalle : Long = -1
    override fun onClick(position: Int, tipo: String) {
        when(tipo){
            "Cambiar Horas" -> {
                PER_CodigoNube = lista[position].PER_CodigoObrero

                if (vCambiarHoras.parent != null) {
                    (vCambiarHoras.parent as ViewGroup).removeView(vCambiarHoras)
                    alertDialog.setView(vCambiarHoras)
                }
                construirAlertDialog("Unico")
                alertDialog.show()
            }

            "Productividad" -> {
                if (binding.txtNroTrabDetalle.text.toString().contains("JORNAL") == true)
                {
                    Globales.showWarningMessage("No puede manipular Productividad si el tareo es por JORNAL", requireContext())
                    return
                }

                PER_CodigoNube = lista[position].PER_CodigoObrero
                _idDetalle = lista[position].idDetalle

                if (vCambiarProductividad.parent != null) {
                    (vCambiarProductividad.parent as ViewGroup).removeView(vCambiarProductividad)
                    alertDialog_Prod.setView(vCambiarProductividad)
                }
                construirAlertDialog_Prod()
                alertDialog_Prod.show()
            }
        }
    }

    fun click_fecha_texto(editText: EditText, texto: String){
        editText.setText(texto)
    }

    fun click_am_pm(editText: EditText){
        editText.setOnClickListener {
            if (editText.text.toString() == "a.m.") editText.setText("p.m.")
            else editText.setText("a.m.")

            calcularTotalHoras()
        }
    }

    fun click_fecha(editText: EditText){
        editText.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year, editText) }
            datePicker.show(parentFragmentManager, "datePicker")
        }
    }

    fun onDateSelected(day: Int, month: Int, year: Int, editText: EditText) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        editText.setText("$_dia/$_mes/$year")
        calcularTotalHoras()
    }

    fun txtCambioHora(editText: EditText, nextEditText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 2){
                    var _entero = FunGeneral.getEntero(s.toString())
                    if (_entero > 12) {
                        editText.setText("")
                    }
                    else{
                        nextEditText.requestFocus()
                        nextEditText.setSelection(nextEditText.text.toString().length)
                    }
                }
                else if (s.toString().length == 1){
                    var _entero = FunGeneral.getEntero(s.toString())
                    if (_entero < 10 && _entero > 1) {
                        editText.setText("0${_entero}")
                        nextEditText.requestFocus()
                        nextEditText.setSelection(nextEditText.text.toString().length)
                    }
                }
                calcularTotalHoras()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    fun txtCambioMin(editText: EditText, nextEditText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 2){
                    var _entero = FunGeneral.getEntero(s.toString())
                    if (_entero > 59) {
                        editText.setText("")
                    }
                    else{
                        nextEditText.requestFocus()
                        nextEditText.setSelection(nextEditText.text.toString().length)
                    }
                }
                calcularTotalHoras()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    var _totalHoras : Float = 0f
    fun calcularTotalHoras() {
        _totalHoras = 0f

        var f1 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha1)
        var f11 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha11)
        var f2 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha2)
        var f22 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha22)
        var f3 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha3)
        var f33 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha33)

        var h1 = vCambiarHoras.findViewById<EditText>(R.id.txtHora1)
        var h11 = vCambiarHoras.findViewById<EditText>(R.id.txtHora11)
        var h2 = vCambiarHoras.findViewById<EditText>(R.id.txtHora2)
        var h22 = vCambiarHoras.findViewById<EditText>(R.id.txtHora22)
        var h3 = vCambiarHoras.findViewById<EditText>(R.id.txtHora3)
        var h33 = vCambiarHoras.findViewById<EditText>(R.id.txtHora33)

        var m1 = vCambiarHoras.findViewById<EditText>(R.id.txtMin1)
        var m11 = vCambiarHoras.findViewById<EditText>(R.id.txtMin11)
        var m2 = vCambiarHoras.findViewById<EditText>(R.id.txtMin2)
        var m22 = vCambiarHoras.findViewById<EditText>(R.id.txtMin22)
        var m3 = vCambiarHoras.findViewById<EditText>(R.id.txtMin3)
        var m33 = vCambiarHoras.findViewById<EditText>(R.id.txtMin33)

        var am1 = vCambiarHoras.findViewById<EditText>(R.id.txtampm1)
        var am11 = vCambiarHoras.findViewById<EditText>(R.id.txtampm11)
        var am2 = vCambiarHoras.findViewById<EditText>(R.id.txtampm2)
        var am22 = vCambiarHoras.findViewById<EditText>(R.id.txtampm22)
        var am3 = vCambiarHoras.findViewById<EditText>(R.id.txtampm3)
        var am33 = vCambiarHoras.findViewById<EditText>(R.id.txtampm33)

        var chk_ini_dia = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_dia)
        var chk_fin_dia = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_dia)

        var chk_ini_tarde = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_tarde)
        var chk_fin_tarde = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_tarde)

        var chk_ini_noche = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_noche)
        var chk_fin_noche = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_noche)

        var _hh = 0
        var _mm = 0
        var _tt = ""

        var _hhTT = ""
        var _mmTT = ""

        //*************************************** Horas de la mañana ****************************
        //***************************************************************************************
        _hh = FunGeneral.getEntero(h1.text.toString())
        _mm = FunGeneral.getEntero(m1.text.toString())
        _tt = am1.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _iniDia : String = "${f1.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        _hh = FunGeneral.getEntero(h11.text.toString())
        _mm = FunGeneral.getEntero(m11.text.toString())
        _tt = am11.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _finDia : String = "${f11.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        if (chk_ini_dia.isChecked && chk_fin_dia.isChecked){
            if (_iniDia.length > 20 && _finDia.length > 20){
                var _iniDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniDia)
                var _finDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finDia)

                var diff : Long = _finDia_DD.time - _iniDia_DD.time
                var xSeg = diff / 1000
                var xMin_Mañana = xSeg / 60

                _totalHoras += xMin_Mañana
            }
        }

        //*************************************** Horas de la tarde *****************************
        //***************************************************************************************
        _hh = FunGeneral.getEntero(h2.text.toString())
        _mm = FunGeneral.getEntero(m2.text.toString())
        _tt = am2.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _iniTarde : String = "${f2.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        _hh = FunGeneral.getEntero(h22.text.toString())
        _mm = FunGeneral.getEntero(m22.text.toString())
        _tt = am22.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _finTarde : String = "${f22.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        if (chk_ini_tarde.isChecked && chk_fin_tarde.isChecked){
            if (_iniTarde.length > 20 && _finTarde.length > 20){
                var _iniTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniTarde)
                var _finTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finTarde)

                var diff2 : Long = _finTarde_DD.time - _iniTarde_DD.time
                var xSeg2 = diff2 / 1000
                var xMin_Tarde = xSeg2 / 60

                _totalHoras += xMin_Tarde
            }
        }

        //*************************************** Horas de la noche *****************************
        //***************************************************************************************
        _hh = FunGeneral.getEntero(h3.text.toString())
        _mm = FunGeneral.getEntero(m3.text.toString())
        _tt = am3.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _iniNoche : String = "${f3.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        _hh = FunGeneral.getEntero(h33.text.toString())
        _mm = FunGeneral.getEntero(m33.text.toString())
        _tt = am33.text.toString()

        _hhTT = _hh.toString()
        _mmTT = _mm.toString()

        if (_hh < 10) _hhTT = "0${_hh}"
        if (_mm < 10) _mmTT = "0${_mm}"

        var _finNoche : String = "${f33.text.toString()} ${_hhTT}:${_mmTT}:00 ${_tt}"

        if (chk_ini_noche.isChecked && chk_fin_noche.isChecked){
            if (_iniNoche.length > 20 && _finNoche.length > 20){
                var _iniNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniNoche)
                var _finNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finNoche)

                var diff3 : Long = _finNoche_DD.time - _iniNoche_DD.time
                var xSeg3 = diff3 / 1000
                var xMin_Noche = xSeg3 / 60

                _totalHoras += xMin_Noche
            }
        }

        var txtTotalHoras = vCambiarHoras.findViewById<TextView>(R.id.txtTotalHoras)
        txtTotalHoras.setText("TOTAL HORAS: [ ${FunGeneral.convertirMinutosEnTexto(_totalHoras)} ]")
    }

    private fun construirAlertDialog(forma: String) {
        //Valores iniciales
        var f1 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha1)
        var f11 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha11)
        var f2 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha2)
        var f22 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha22)
        var f3 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha3)
        var f33 = vCambiarHoras.findViewById<EditText>(R.id.txtFecha33)

        var h1 = vCambiarHoras.findViewById<EditText>(R.id.txtHora1)
        var h11 = vCambiarHoras.findViewById<EditText>(R.id.txtHora11)
        var h2 = vCambiarHoras.findViewById<EditText>(R.id.txtHora2)
        var h22 = vCambiarHoras.findViewById<EditText>(R.id.txtHora22)
        var h3 = vCambiarHoras.findViewById<EditText>(R.id.txtHora3)
        var h33 = vCambiarHoras.findViewById<EditText>(R.id.txtHora33)

        var m1 = vCambiarHoras.findViewById<EditText>(R.id.txtMin1)
        var m11 = vCambiarHoras.findViewById<EditText>(R.id.txtMin11)
        var m2 = vCambiarHoras.findViewById<EditText>(R.id.txtMin2)
        var m22 = vCambiarHoras.findViewById<EditText>(R.id.txtMin22)
        var m3 = vCambiarHoras.findViewById<EditText>(R.id.txtMin3)
        var m33 = vCambiarHoras.findViewById<EditText>(R.id.txtMin33)

        var am1 = vCambiarHoras.findViewById<EditText>(R.id.txtampm1)
        var am11 = vCambiarHoras.findViewById<EditText>(R.id.txtampm11)
        var am2 = vCambiarHoras.findViewById<EditText>(R.id.txtampm2)
        var am22 = vCambiarHoras.findViewById<EditText>(R.id.txtampm22)
        var am3 = vCambiarHoras.findViewById<EditText>(R.id.txtampm3)
        var am33 = vCambiarHoras.findViewById<EditText>(R.id.txtampm33)

        var _fecha = "" //FunGeneral.fecha("dd/MM/YYYY")
        var _fecha2 = "" //FunGeneral.fecha_nextDay("dd/MM/YYYY")
        click_fecha_texto(f1, _fecha)
        click_fecha_texto(f11, _fecha)
        click_fecha_texto(f2, _fecha)
        click_fecha_texto(f22, _fecha)
        click_fecha_texto(f3, _fecha)
        click_fecha_texto(f33, _fecha2)

        click_fecha_texto(h1, _fecha)
        click_fecha_texto(h11, _fecha)
        click_fecha_texto(h2, _fecha)
        click_fecha_texto(h22, _fecha)
        click_fecha_texto(h3, _fecha)
        click_fecha_texto(h33, _fecha2)

        click_fecha_texto(m1, _fecha)
        click_fecha_texto(m11, _fecha)
        click_fecha_texto(m2, _fecha)
        click_fecha_texto(m22, _fecha)
        click_fecha_texto(m3, _fecha)
        click_fecha_texto(m33, _fecha2)

        click_am_pm(am1)
        click_am_pm(am11)
        click_am_pm(am2)
        click_am_pm(am22)
        click_am_pm(am3)
        click_am_pm(am33)

        click_fecha(f1)
        click_fecha(f11)
        click_fecha(f2)
        click_fecha(f22)
        click_fecha(f3)
        click_fecha(f33)

        txtCambioHora(h1, m1)
        txtCambioHora(h11, m11)
        txtCambioHora(h2, m2)
        txtCambioHora(h22, m22)
        txtCambioHora(h3, m3)
        txtCambioHora(h33, m33)

        txtCambioMin(m1, h11)
        txtCambioMin(m11, h2)
        txtCambioMin(m2, h22)
        txtCambioMin(m22, h3)
        txtCambioMin(m3, h33)
        txtCambioMin(m33, m33)

        var chk_ini_dia = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_dia)
        chk_ini_dia.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f1.isEnabled = isChecked
            h1.isEnabled = isChecked
            m1.isEnabled = isChecked
            am1.isEnabled = isChecked

            calcularTotalHoras()
        }

        var chk_fin_dia = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_dia)
        chk_fin_dia.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f11.isEnabled = isChecked
            h11.isEnabled = isChecked
            m11.isEnabled = isChecked
            am11.isEnabled = isChecked

            calcularTotalHoras()
        }

        var chk_ini_tarde = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_tarde)
        chk_ini_tarde.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f2.isEnabled = isChecked
            h2.isEnabled = isChecked
            m2.isEnabled = isChecked
            am2.isEnabled = isChecked

            calcularTotalHoras()
        }

        var chk_fin_tarde = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_tarde)
        chk_fin_tarde.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f22.isEnabled = isChecked
            h22.isEnabled = isChecked
            m22.isEnabled = isChecked
            am22.isEnabled = isChecked

            calcularTotalHoras()
        }

        var chk_ini_noche = vCambiarHoras.findViewById<CheckBox>(R.id.chk_ini_noche)
        chk_ini_noche.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f3.isEnabled = isChecked
            h3.isEnabled = isChecked
            m3.isEnabled = isChecked
            am3.isEnabled = isChecked

            calcularTotalHoras()
        }

        var chk_fin_noche = vCambiarHoras.findViewById<CheckBox>(R.id.chk_fin_noche)
        chk_fin_noche.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            f33.isEnabled = isChecked
            h33.isEnabled = isChecked
            m33.isEnabled = isChecked
            am33.isEnabled = isChecked

            calcularTotalHoras()
        }

        //Cargar datos iniciales...
        var objDetalle = tareoLaboresEmpleadoDAO.PA_ObtenerTareoLaboresEmpleado_PorCodigo(_idTareo, PER_CodigoNube)
        if (objDetalle != null){
            //*************************************** Horas de la mañana ****************************
            //***************************************************************************************
            if (objDetalle.TLE_Dia == 1){
                f1.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicio, "dd/MM/YYYY"))
                h1.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicio, "hh"))
                m1.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicio, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicio, "HH").toInt()>=12){
                    am1.setText("p.m.")
                }
                else{
                    am1.setText("a.m.")
                }
                chk_ini_dia.isChecked = true
            }
            else chk_ini_dia.isChecked = false

            if (objDetalle.TLE_Dia2 == 1){
                f11.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFin, "dd/MM/YYYY"))
                h11.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFin, "hh"))
                m11.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFin, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFin, "HH").toInt()>=12){
                    am11.setText("p.m.")
                }
                else{
                    am11.setText("a.m.")
                }

                chk_fin_dia.isChecked = true
            }
            else chk_fin_dia.isChecked = false

            //*************************************** Horas de la tarde *****************************
            //***************************************************************************************
            if (objDetalle.TLE_Tarde == 1)
            {
                f2.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioTarde, "dd/MM/YYYY"))
                h2.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioTarde, "hh"))
                m2.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioTarde, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioTarde, "HH").toInt()>=12){
                    am2.setText("p.m.")
                }
                else{
                    am2.setText("a.m.")
                }

                chk_ini_tarde.isChecked = true
            }
            else chk_ini_tarde.isChecked = false

            if (objDetalle.TLE_Tarde2 == 1){
                f22.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinTarde, "dd/MM/YYYY"))
                h22.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinTarde, "hh"))
                m22.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinTarde, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinTarde, "HH").toInt()>=12){
                    am22.setText("p.m.")
                }
                else{
                    am22.setText("a.m.")
                }

                chk_fin_tarde.isChecked = true
            }
            else chk_fin_tarde.isChecked = false

            //*************************************** Horas de la noche *****************************
            //***************************************************************************************
            if (objDetalle.TLE_Noche == 1) {
                f3.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioNoche, "dd/MM/YYYY"))
                h3.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioNoche, "hh"))
                m3.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioNoche, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraInicioNoche, "HH").toInt()>=12){
                    am3.setText("p.m.")
                }
                else{
                    am3.setText("a.m.")
                }

                chk_ini_noche.isChecked = true
            }
            else chk_ini_noche.isChecked = false

            if (objDetalle.TLE_Noche2 == 1){
                f33.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinNoche, "dd/MM/YYYY"))
                h33.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinNoche, "hh"))
                m33.setText(FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinNoche, "mm"))
                if (FunGeneral.obtenerFechaHora_PorFormato_Nube(objDetalle.TLE_HoraFinNoche, "HH").toInt()>=12){
                    am33.setText("p.m.")
                }
                else{
                    am33.setText("a.m.")
                }

                chk_fin_noche.isChecked = true
            }
            else chk_fin_noche.isChecked = false
        }
        else{
            chk_ini_dia.isChecked = false
            chk_fin_dia.isChecked = false
            chk_ini_tarde.isChecked = false
            chk_fin_tarde.isChecked = false
            chk_ini_noche.isChecked = false
            chk_fin_noche.isChecked = false
        }
        calcularTotalHoras()

        if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
            chk_ini_dia.isEnabled = false
            chk_fin_dia.isEnabled = false
            chk_ini_tarde.isEnabled = false
            chk_fin_tarde.isEnabled = false
            chk_ini_noche.isEnabled = false
            chk_fin_noche.isEnabled = false

            f1.isEnabled = false
            f11.isEnabled = false
            f2.isEnabled = false
            f22.isEnabled = false
            f3.isEnabled = false
            f33.isEnabled = false

            h1.isEnabled = false
            h11.isEnabled = false
            h2.isEnabled = false
            h22.isEnabled = false
            h3.isEnabled = false
            h33.isEnabled = false

            m1.isEnabled = false
            m11.isEnabled = false
            m2.isEnabled = false
            m22.isEnabled = false
            m3.isEnabled = false
            m33.isEnabled = false

            am1.isEnabled = false
            am11.isEnabled = false
            am2.isEnabled = false
            am22.isEnabled = false
            am3.isEnabled = false
            am33.isEnabled = false
        }

        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(vCambiarHoras)
        alertDialog.setCancelable(false)
        if (DatosTareo._EstadoTareoSelect != "SINCRONIZADO"){
            alertDialog.setPositiveButton("Aceptar") { dialoginterface, i ->


                if (PER_CodigoNube == -1){
                    for (dd in lista) {
                        //Procesar cambio de horas masivo
                        if (chk_ini_dia.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Dia = 0

                            //*************************************** Horas de la mañana ****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(h1.text.toString())
                            _mm = FunGeneral.getEntero(m1.text.toString())
                            _tt = am1.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniFechaDia : String = "${f1.text.toString()}"
                            var _iniHoraDia : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraInicio = ""
                            if (chk_ini_dia.isChecked && (_iniFechaDia.trim().length + _iniHoraDia.trim().length) > 20){
                                _TLE_HoraInicio = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaDia, _iniHoraDia, "YYYYMMdd HH:mm:ss")
                                _TLE_Dia = 1
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraDia(dd.idDetalle, _TLE_Dia, _TLE_HoraInicio)
                            dd.TLE_Dia = _TLE_Dia
                            dd.TLE_HoraInicio = _TLE_HoraInicio
                        }
                        if (chk_fin_dia.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Dia2 = 0

                            _hh = FunGeneral.getEntero(h11.text.toString())
                            _mm = FunGeneral.getEntero(m11.text.toString())
                            _tt = am11.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finFechaDia : String = "${f11.text.toString()}"
                            var _finHoraDia : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraFin = ""
                            if (chk_fin_dia.isChecked && (_finFechaDia.trim().length + _finHoraDia.trim().length) > 20){
                                _TLE_HoraFin = FunGeneral.obtenerFechaHora_PorFormato(_finFechaDia, _finHoraDia, "YYYYMMdd HH:mm:ss")
                                _TLE_Dia2 = 1
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraDia2(dd.idDetalle, _TLE_Dia2, _TLE_HoraFin)
                            dd.TLE_Dia2 = _TLE_Dia2
                            dd.TLE_HoraFin = _TLE_HoraFin
                        }
                        if (chk_ini_tarde.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Tarde = 0

                            //*************************************** Horas de la tarde *****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(h2.text.toString())
                            _mm = FunGeneral.getEntero(m2.text.toString())
                            _tt = am2.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniFechaTarde : String = "${f2.text.toString()}"
                            var _iniHoraTarde : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraInicioTarde = ""
                            if (chk_ini_tarde.isChecked && (_iniFechaTarde.trim().length + _iniHoraTarde.trim().length) > 20){
                                _TLE_HoraInicioTarde = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaTarde, _iniHoraTarde, "YYYYMMdd HH:mm:ss")
                                _TLE_Tarde = 1
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraTarde(dd.idDetalle, _TLE_Tarde, _TLE_HoraInicioTarde)
                            dd.TLE_Tarde = _TLE_Tarde
                            dd.TLE_HoraInicioTarde = _TLE_HoraInicioTarde
                        }
                        if (chk_fin_tarde.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Tarde2 = 0

                            _hh = FunGeneral.getEntero(h22.text.toString())
                            _mm = FunGeneral.getEntero(m22.text.toString())
                            _tt = am22.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finFechaTarde : String = "${f22.text.toString()}"
                            var _finHoraTarde : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraFinTarde = ""
                            if (chk_fin_tarde.isChecked && (_finFechaTarde.trim().length + _finHoraTarde.trim().length) > 20){
                                _TLE_HoraFinTarde = FunGeneral.obtenerFechaHora_PorFormato(_finFechaTarde, _finHoraTarde, "YYYYMMdd HH:mm:ss")
                                _TLE_Tarde2 = 1
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraTarde2(dd.idDetalle, _TLE_Tarde2, _TLE_HoraFinTarde)
                            dd.TLE_Tarde2 = _TLE_Tarde2
                            dd.TLE_HoraFinTarde = _TLE_HoraFinTarde
                        }
                        if (chk_ini_noche.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Noche = 0

                            //*************************************** Horas de la noche *****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(h3.text.toString())
                            _mm = FunGeneral.getEntero(m3.text.toString())
                            _tt = am3.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniFechaNoche : String = "${f3.text.toString()}"
                            var _iniHoraNoche : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraInicioNoche = ""
                            if (chk_ini_noche.isChecked && (_iniFechaNoche.trim().length + _iniHoraNoche.trim().length) > 20){
                                _TLE_HoraInicioNoche = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaNoche, _iniHoraNoche, "YYYYMMdd HH:mm:ss")
                                _TLE_Noche = 1
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraNoche(dd.idDetalle, _TLE_Noche, _TLE_HoraInicioNoche)
                            dd.TLE_Noche = _TLE_Noche
                            dd.TLE_HoraInicioNoche = _TLE_HoraInicioNoche
                        }
                        if (chk_fin_noche.isChecked){
                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            var _TLE_Noche2 = 0

                            _hh = FunGeneral.getEntero(h33.text.toString())
                            _mm = FunGeneral.getEntero(m33.text.toString())
                            _tt = am33.text.toString()

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finFechaNoche : String = "${f33.text.toString()}"
                            var _finHoraNoche : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                            var _TLE_HoraFinNoche = ""
                            if (chk_fin_noche.isChecked && (_finFechaNoche.trim().length + _finHoraNoche.trim().length) > 20){
                                _TLE_HoraFinNoche = FunGeneral.obtenerFechaHora_PorFormato(_finFechaNoche, _finHoraNoche, "YYYYMMdd HH:mm:ss")
                                _TLE_Noche2
                            }

                            tareoLaboresEmpleadoDAO.actualizaHoraNoche2(dd.idDetalle, _TLE_Noche2, _TLE_HoraFinNoche)
                            dd.TLE_Noche2 = _TLE_Noche2
                            dd.TLE_HoraFinNoche = _TLE_HoraFinNoche
                        }

                        //Recalcular horas
                        if (1 == 1){
                            _totalHoras = 0f

                            var _hh = 0
                            var _mm = 0
                            var _tt = ""

                            var _hhTT = ""
                            var _mmTT = ""

                            //*************************************** Horas de la mañana ****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicio, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicio, "mm"))
                            _tt = (FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicio, "a"))

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniDia : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicio, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFin, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFin, "mm"))
                            _tt = FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFin, "a")

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finDia : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFin, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            if (dd.TLE_Dia == 1 && dd.TLE_Dia2 == 1){
                                if (_iniDia.length > 20 && _finDia.length > 20){
                                    var _iniDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniDia)
                                    var _finDia_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finDia)

                                    var diff : Long = _finDia_DD.time - _iniDia_DD.time
                                    var xSeg = diff / 1000
                                    var xMin_Mañana = xSeg / 60

                                    _totalHoras += xMin_Mañana
                                }
                            }

                            //*************************************** Horas de la tarde *****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioTarde, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioTarde, "mm"))
                            _tt = FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioTarde, "a")

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniTarde : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioTarde, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinTarde, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinTarde, "mm"))
                            _tt = FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinTarde, "a")

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finTarde : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinTarde, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            if (dd.TLE_Tarde == 1 && dd.TLE_Tarde2 == 1){
                                if (_iniTarde.length > 20 && _finTarde.length > 20){
                                    var _iniTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniTarde)
                                    var _finTarde_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finTarde)

                                    var diff2 : Long = _finTarde_DD.time - _iniTarde_DD.time
                                    var xSeg2 = diff2 / 1000
                                    var xMin_Tarde = xSeg2 / 60

                                    _totalHoras += xMin_Tarde
                                }
                            }

                            //*************************************** Horas de la noche *****************************
                            //***************************************************************************************
                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioNoche, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioNoche, "mm"))
                            _tt = FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioNoche, "a")

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _iniNoche : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraInicioNoche, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            _hh = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinNoche, "hh"))
                            _mm = FunGeneral.getEntero(FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinNoche, "mm"))
                            _tt = FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinNoche, "a")

                            _hhTT = _hh.toString()
                            _mmTT = _mm.toString()

                            if (_hh < 10) _hhTT = "0${_hh}"
                            if (_mm < 10) _mmTT = "0${_mm}"

                            var _finNoche : String = "${FunGeneral.obtenerFechaHora_PorFormato_Nube(dd.TLE_HoraFinNoche, "dd/MM/YYYY")} ${_hhTT}:${_mmTT}:00 ${_tt}"

                            if (dd.TLE_Noche == 1 && dd.TLE_Noche2 == 1){
                                if (_iniNoche.length > 20 && _finNoche.length > 20){
                                    var _iniNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_iniNoche)
                                    var _finNoche_DD = FunGeneral.obtenerFechaHora_PorFormato_Date(_finNoche)

                                    var diff3 : Long = _finNoche_DD.time - _iniNoche_DD.time
                                    var xSeg3 = diff3 / 1000
                                    var xMin_Noche = xSeg3 / 60

                                    _totalHoras += xMin_Noche
                                }
                            }

                            tareoLaboresEmpleadoDAO.actualizaHorasTrabajadas_JC(dd.idDetalle, _totalHoras)
                        }
                    }
                }
                else {
                    var _hh = 0
                    var _mm = 0
                    var _tt = ""

                    var _hhTT = ""
                    var _mmTT = ""

                    var _TLE_Dia = 0
                    var _TLE_Dia2 = 0
                    var _TLE_Tarde = 0
                    var _TLE_Tarde2 = 0
                    var _TLE_Noche = 0
                    var _TLE_Noche2 = 0

                    //*************************************** Horas de la mañana ****************************
                    //***************************************************************************************
                    _hh = FunGeneral.getEntero(h1.text.toString())
                    _mm = FunGeneral.getEntero(m1.text.toString())
                    _tt = am1.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _iniFechaDia : String = "${f1.text.toString()}"
                    var _iniHoraDia : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    _hh = FunGeneral.getEntero(h11.text.toString())
                    _mm = FunGeneral.getEntero(m11.text.toString())
                    _tt = am11.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _finFechaDia : String = "${f11.text.toString()}"
                    var _finHoraDia : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    var _TLE_HoraInicio = ""
                    if (chk_ini_dia.isChecked && (_iniFechaDia.trim().length + _iniHoraDia.trim().length) > 20){
                        _TLE_HoraInicio = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaDia, _iniHoraDia, "YYYYMMdd HH:mm:ss")
                        _TLE_Dia = 1
                    }

                    var _TLE_HoraFin = ""
                    if (chk_fin_dia.isChecked && (_finFechaDia.trim().length + _finHoraDia.trim().length) > 20){
                        _TLE_HoraFin = FunGeneral.obtenerFechaHora_PorFormato(_finFechaDia, _finHoraDia, "YYYYMMdd HH:mm:ss")
                        _TLE_Dia2 = 1
                    }

                    //*************************************** Horas de la tarde *****************************
                    //***************************************************************************************
                    _hh = FunGeneral.getEntero(h2.text.toString())
                    _mm = FunGeneral.getEntero(m2.text.toString())
                    _tt = am2.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _iniFechaTarde : String = "${f2.text.toString()}"
                    var _iniHoraTarde : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    _hh = FunGeneral.getEntero(h22.text.toString())
                    _mm = FunGeneral.getEntero(m22.text.toString())
                    _tt = am22.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _finFechaTarde : String = "${f22.text.toString()}"
                    var _finHoraTarde : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    var _TLE_HoraInicioTarde = ""
                    if (chk_ini_tarde.isChecked && (_iniFechaTarde.trim().length + _iniHoraTarde.trim().length) > 20){
                        _TLE_HoraInicioTarde = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaTarde, _iniHoraTarde, "YYYYMMdd HH:mm:ss")
                        _TLE_Tarde = 1
                    }

                    var _TLE_HoraFinTarde = ""
                    if (chk_fin_tarde.isChecked && (_finFechaTarde.trim().length + _finHoraTarde.trim().length) > 20){
                        _TLE_HoraFinTarde = FunGeneral.obtenerFechaHora_PorFormato(_finFechaTarde, _finHoraTarde, "YYYYMMdd HH:mm:ss")
                        _TLE_Tarde2 = 1
                    }

                    //*************************************** Horas de la noche *****************************
                    //***************************************************************************************
                    _hh = FunGeneral.getEntero(h3.text.toString())
                    _mm = FunGeneral.getEntero(m3.text.toString())
                    _tt = am3.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _iniFechaNoche : String = "${f3.text.toString()}"
                    var _iniHoraNoche : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    _hh = FunGeneral.getEntero(h33.text.toString())
                    _mm = FunGeneral.getEntero(m33.text.toString())
                    _tt = am33.text.toString()

                    _hhTT = _hh.toString()
                    _mmTT = _mm.toString()

                    if (_hh < 10) _hhTT = "0${_hh}"
                    if (_mm < 10) _mmTT = "0${_mm}"

                    var _finFechaNoche : String = "${f33.text.toString()}"
                    var _finHoraNoche : String = "${_hhTT}:${_mmTT}:00 ${_tt}"

                    var _TLE_HoraInicioNoche = ""
                    if (chk_ini_noche.isChecked && (_iniFechaNoche.trim().length + _iniHoraNoche.trim().length) > 20){
                        _TLE_HoraInicioNoche = FunGeneral.obtenerFechaHora_PorFormato(_iniFechaNoche, _iniHoraNoche, "YYYYMMdd HH:mm:ss")
                        _TLE_Noche = 1
                    }

                    var _TLE_HoraFinNoche = ""
                    if (chk_fin_noche.isChecked && (_finFechaNoche.trim().length + _finHoraNoche.trim().length) > 20){
                        _TLE_HoraFinNoche = FunGeneral.obtenerFechaHora_PorFormato(_finFechaNoche, _finHoraNoche, "YYYYMMdd HH:mm:ss")
                        _TLE_Noche2
                    }

                    tareoLaboresEmpleadoDAO.actualizaHorasTrabajadas(_idTareo, _TLE_Dia, _TLE_Dia2, _TLE_Tarde, _TLE_Tarde2,
                        _TLE_Noche, _TLE_Noche2, "SI", _TLE_HoraInicio, _TLE_HoraFin, _TLE_HoraInicioTarde, _TLE_HoraFinTarde,
                        _TLE_HoraInicioNoche, _TLE_HoraFinNoche, _totalHoras, PER_CodigoNube)
                }

                btnReportar()
            }
        }
        alertDialog.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
    }

    var listaProductividad = ArrayList<TareoLaboresEmpleado_Productividad>()
    lateinit var adapterProductividad : RecyclerView.Adapter<DetalleProductividadVH_sqlite>

    private fun construirAlertDialog_Prod() {
        _revisoProductividad = "NO"

        var rvDetalleProductividad = vCambiarProductividad.findViewById<RecyclerView>(R.id.rvDetalleProductividad)
        var btnAgregarProductividad = vCambiarProductividad.findViewById<Button>(R.id.btnAgregarProductividad)
        var txtCantItems = vCambiarProductividad.findViewById<EditText>(R.id.txtCantItems)
        var txtTituloProd = vCambiarProductividad.findViewById<TextView>(R.id.txtTituloProd)
        var txtTotalProductividad = vCambiarProductividad.findViewById<TextView>(R.id.txtTotalProductividad)

        if (_idDetalle == (-1).toLong()){
            tareoLaboresEmpleado_ProductividadDAO.PA_EliminarDetalleTareoLabores(_idTareo, _idDetalle)
        }

        listaProductividad.clear()
        listaProductividad = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>
        adapterProductividad = DetalleProductividadAD_sqlite(listaProductividad, this)
        txtCantItems.setText("DETALLES: ${listaProductividad.size} ITEMS")

        if (binding.txtNroTrabDetalle.text.toString().contains("JORNAL")){
            txtTituloProd.text = "TIPO: JORNAL"
        }
        else if (binding.txtNroTrabDetalle.text.toString().contains("TAREA")){
            txtTituloProd.text = "TIPO: TAREA"
        }
        else if (binding.txtNroTrabDetalle.text.toString().contains("DESTAJO")){
            txtTituloProd.text = "TIPO: DESTAJO"
        }
        else{
            txtTituloProd.text = "PRODUCTIVIDAD"
        }

        rvDetalleProductividad.layoutManager = LinearLayoutManager(requireContext())
        rvDetalleProductividad.adapter = adapterProductividad

        adapterProductividad.notifyDataSetChanged()

        btnAgregarProductividad.setOnClickListener {
            //Crear detalle

            var codunidad = -1
            var desunidad = "Seleccione"

            if (binding.txtNroTrabDetalle.text.toString().contains("TAREA")){
                desunidad = "TAREA"
            }

            var dd = TareoLaboresEmpleado_Productividad(
                0,
                _idTareo,
                _idDetalle,
                codunidad,
                desunidad,
                0f, 0f, 0f,
                "CAMBIAR",
                0f, 0f, 0f,
                "PENDIENTE"
            )

            dd.idDetalleProd = tareoLaboresEmpleado_ProductividadDAO.guardarTareoLaboresEmpleado_Productividad(dd)
            listaProductividad.add(dd)
            txtCantItems.setText("DETALLES: ${listaProductividad.size} ITEMS")

            adapterProductividad.notifyDataSetChanged()
        }

        if (DatosTareo._EstadoTareoSelect == "SINCRONIZADO"){
            btnAgregarProductividad.isEnabled = false
        }

        var _totProd : Float = 0f
        var _total : Float = 0f
        for(dd in listaProductividad){
            _totProd += dd.TLEP_Cantidad
            if (_tipoTareo == "TAREA"){
                _total += dd.TLEP_TotalDestajo
            }
            else{
                _total += dd.TLEP_Importe
            }
        }
        txtTotalProductividad.setText("TOTAL PRODUCTIVIDAD: ${_totProd}")

        alertDialog_Prod = AlertDialog.Builder(requireContext())
        alertDialog_Prod.setView(vCambiarProductividad)
        alertDialog_Prod.setCancelable(false)
        if (DatosTareo._EstadoTareoSelect != "SINCRONIZADO"){
            alertDialog_Prod.setPositiveButton("Aceptar"){dialoginteface, i ->
                //Actualizar conteo de productividad
                var listaProductividad2 = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>

                var _totProd : Float = 0f
                var _total : Float = 0f
                for(dd in listaProductividad2){
                    _totProd += dd.TLEP_Cantidad
                    if (_tipoTareo == "TAREA"){
                        _total += dd.TLEP_TotalDestajo
                    }
                    else{
                        _total += dd.TLEP_Importe
                    }
                }

                if (PER_CodigoNube == -1){
                    var _detallesProd = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>

                    for (dd in lista) {
                        tareoLaboresEmpleado_ProductividadDAO.PA_EliminarDetalleTareoLabores(_idTareo, dd.idDetalle)

                        for (aa in _detallesProd){
                            var dd = TareoLaboresEmpleado_Productividad(
                                0,
                                _idTareo,
                                dd.idDetalle,
                                aa.UNI_Codigo,
                                aa.TLEP_Unidad,
                                aa.TLEP_Cantidad, aa.TLEP_Precio, aa.TLEP_Importe,
                                aa.TLEP_UnidadDestajo,
                                aa.TLEP_CantidadDestajo, aa.TLEP_PrecioDestajo, aa.TLEP_TotalDestajo,
                                aa.EstadoRevision
                            )
                            dd.idDetalleProd = tareoLaboresEmpleado_ProductividadDAO.guardarTareoLaboresEmpleado_Productividad(dd)
                        }

                        tareoLaboresEmpleadoDAO.actualizaProductividad(_idTareo, dd.PER_CodigoObrero, _totProd, "SI")
                    }
                }
                else{
                    tareoLaboresEmpleadoDAO.actualizaProductividad(_idTareo, PER_CodigoNube, _totProd, "SI")
                }
                btnReportar()
            }
        }
        alertDialog_Prod.setNegativeButton("Cerrar") { dialoginterface, i ->
            dialoginterface.dismiss()

            if (_revisoProductividad == "SI")
            {
                //Actualizar conteo de productividad
                var listaProductividad2 = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>

                var _totProd : Float = 0f
                var _total : Float = 0f
                for(dd in listaProductividad2){
                    _totProd += dd.TLEP_Cantidad
                    if (_tipoTareo == "TAREA"){
                        _total += dd.TLEP_TotalDestajo
                    }
                    else{
                        _total += dd.TLEP_Importe
                    }
                }

                if (PER_CodigoNube == -1){
                    var _detallesProd = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>

                    for (dd in lista) {
                        tareoLaboresEmpleado_ProductividadDAO.PA_EliminarDetalleTareoLabores(_idTareo, dd.idDetalle)

                        for (aa in _detallesProd){
                            var dd = TareoLaboresEmpleado_Productividad(
                                0,
                                _idTareo,
                                dd.idDetalle,
                                aa.UNI_Codigo,
                                aa.TLEP_Unidad,
                                aa.TLEP_Cantidad, aa.TLEP_Precio, aa.TLEP_Importe,
                                aa.TLEP_UnidadDestajo,
                                aa.TLEP_CantidadDestajo, aa.TLEP_PrecioDestajo, aa.TLEP_TotalDestajo,
                                aa.EstadoRevision
                            )
                            dd.idDetalleProd = tareoLaboresEmpleado_ProductividadDAO.guardarTareoLaboresEmpleado_Productividad(dd)
                        }

                        tareoLaboresEmpleadoDAO.actualizaProductividad(_idTareo, dd.PER_CodigoObrero, _totProd, "SI")
                    }
                }
                else{
                    tareoLaboresEmpleadoDAO.actualizaProductividad(_idTareo, PER_CodigoNube, _totProd, "SI")
                }
                btnReportar()
            }
        }
    }

    var _revisoProductividad = "NO"
    override fun onClick2(position: Int, tipo: String) {
        when(tipo){
            "Ok Productividad" -> {
                var listaProductividad2 = tareoLaboresEmpleado_ProductividadDAO.PA_ListarProductividad_PorTareoDetalle(_idTareo, _idDetalle) as ArrayList<TareoLaboresEmpleado_Productividad>

                var _totProd : Float = 0f
                var _total : Float = 0f
                for(dd in listaProductividad2){
                    _totProd += dd.TLEP_Cantidad
                    if (_tipoTareo == "TAREA"){
                        _total += dd.TLEP_TotalDestajo
                    }
                    else{
                        _total += dd.TLEP_Importe
                    }
                }

                var txtTotalProductividad = vCambiarProductividad.findViewById<TextView>(R.id.txtTotalProductividad)
                txtTotalProductividad.setText("TOTAL PRODUCTIVIDAD: ${_totProd}")

                _revisoProductividad = "SI"
            }

            "QuitarDetalle" -> {
                val idDetalleProd = listaProductividad[position].idDetalleProd
                tareoLaboresEmpleado_ProductividadDAO.eliminarDetalle(idDetalleProd)

                listaProductividad.removeAt(position)
                adapterProductividad.notifyDataSetChanged()

                var txtCantItems = vCambiarProductividad.findViewById<EditText>(R.id.txtCantItems)
                txtCantItems.setText("DETALLES: ${listaProductividad.size} ITEMS")
            }
        }
    }
}