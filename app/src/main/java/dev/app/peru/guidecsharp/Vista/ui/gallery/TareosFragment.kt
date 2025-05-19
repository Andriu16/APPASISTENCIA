package dev.app.peru.guidecsharp.Vista.ui.gallery

import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.DetalleProductividadAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TareoAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TareoVH_sqlite
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentTareosBinding
import java.util.ArrayList

class TareosFragment : Fragment(), TareoAD_sqlite.MyClickListener,
    CompoundButton.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private var _binding: FragmentTareosBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var tareoDAO: TareoDAO
    private var lista = ArrayList<Tareo>()
    private lateinit var adapter : RecyclerView.Adapter<TareoVH_sqlite>

    lateinit var vCambiarDatos: View
    lateinit var alertDialog_Datos: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentTareosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        vCambiarDatos = layoutInflater.inflate(R.layout.flotante_cambiar_datos_tareo, null)

        var _ff = FunGeneral.fecha("dd/MM/YYYY")
        binding.etDate.setText(_ff)
        binding.etDate.setOnClickListener { showDatePickerDialog() }

        tareoDAO = ConexionSqlite.getInstancia(requireContext()).tareoDAO()
        adapter = TareoAD_sqlite(lista, this)
        binding.btnBuscar.setOnClickListener { btnReportar() }

        binding.rbtnJornal.setOnCheckedChangeListener(this)
        binding.rbtnTarea.setOnCheckedChangeListener(this)
        binding.rbtnDestajo.setOnCheckedChangeListener(this)
        binding.rbtnTodos.setOnCheckedChangeListener(this)

        binding.txtBusqueda.addTextChangedListener(this)
        btnReportar()

        setHasOptionsMenu(true)
        return root
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_editar_datos -> {
                //Mostrar hora de salida para edicion
                if (vCambiarDatos.parent != null) {
                    (vCambiarDatos.parent as ViewGroup).removeView(vCambiarDatos)
                    alertDialog_Datos.setView(vCambiarDatos)
                }
                construirAlertDialog_Datos()
                return true
            }
            R.id.action_eliminar_tareo -> {
                var obj = tareoDAO.PA_ObtenerTareo_PorCodigo(DatosTareo.TA_Codigo.toLong())
                if (obj == null){
                    Globales.showWarningMessage("El tareo ya no existe", requireContext())
                    return true
                }

                if (obj.TA_Codigo > 0){
                    Globales.showWarningMessage("El tareo ya ha sido sincronizado", requireContext())
                    return true
                }

                Globales.showWarningMessageAndCuestions("¿Desea Eliminar El Tareo?", requireContext(),
                    {
                        tareoDAO.PA_EliminarTareo_PorCodigo(DatosTareo.AP_Codigo.toLong())
                        btnReportar()
                    },
                    {

                    })
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    //Variables
    private lateinit var packingDAO: PackingDAO
    private var arrayPacking = ArrayList<Packing>()
    private lateinit var adapterPacking: ArrayAdapter<Packing>

    private lateinit var cultivoDAO: CultivoDAO
    private var arrayCultivo = ArrayList<Cultivo>()
    private lateinit var adapterCultivo: ArrayAdapter<Cultivo>

    private lateinit var centroCostoDAO: CentroCostoDAO
    private var arrayCentroCosto = ArrayList<CentroCosto>()
    private lateinit var adapterCentroCosto: ArrayAdapter<CentroCosto>

    private lateinit var laboresDAO: LaboresDAO
    private var arrayLabores = ArrayList<Labores>()
    private lateinit var adapterLabores: ArrayAdapter<Labores>

    private lateinit var actividadDAO: ActividadDAO
    private var arrayActividad = ArrayList<Actividad>()
    private lateinit var adapterActividad: ArrayAdapter<Actividad>

    private lateinit var cboCultivo : Spinner
    private lateinit var cboIncidencia : Spinner
    private lateinit var cboActividad : Spinner
    private lateinit var cboPacking : Spinner
    private lateinit var cboCentroCosto : Spinner
    private lateinit var cboLabor : Spinner
    private lateinit var cboTipoTareo : Spinner

    private lateinit var txtParronLote : TextView

    private fun construirAlertDialog_Datos() {
        var txtFundoPacking = vCambiarDatos.findViewById<TextView>(R.id.txtFundoPacking)
        var txtCultivo = vCambiarDatos.findViewById<TextView>(R.id.txtCultivo)
        var txtIncidencia = vCambiarDatos.findViewById<TextView>(R.id.txtIncidencia)
        txtParronLote = vCambiarDatos.findViewById<TextView>(R.id.txtParronLote)
        var txtLabor = vCambiarDatos.findViewById<TextView>(R.id.txtLabor)
        var txtActividad = vCambiarDatos.findViewById<TextView>(R.id.txtActividad)
        var txtTipoTareo = vCambiarDatos.findViewById<TextView>(R.id.txtTTipoTareo)
        var btnSiguiente = vCambiarDatos.findViewById<Button>(R.id.btnSiguiente)

        cboCultivo = vCambiarDatos.findViewById<Spinner>(R.id.cboCultivo)
        cboIncidencia = vCambiarDatos.findViewById<Spinner>(R.id.cboIncidencia)
        cboActividad = vCambiarDatos.findViewById<Spinner>(R.id.cboActividad)
        cboPacking = vCambiarDatos.findViewById<Spinner>(R.id.cboPacking)
        cboCentroCosto = vCambiarDatos.findViewById<Spinner>(R.id.cboCentroCosto)
        cboLabor = vCambiarDatos.findViewById<Spinner>(R.id.cboLabor)
        cboTipoTareo = vCambiarDatos.findViewById<Spinner>(R.id.cboTipoTareo)

        txtFundoPacking.setOnClickListener { recargar("FundoPacking") }
        txtCultivo.setOnClickListener { recargar("Cultivo") }
        txtIncidencia.setOnClickListener { recargar("Incidencia") }
        txtParronLote.setOnClickListener { recargar("CentroCosto") }
        txtLabor.setOnClickListener { recargar("Labor") }
        txtActividad.setOnClickListener { recargar("Actividad") }
        txtTipoTareo.setOnClickListener { recargar("TipoTareo") }

        cboCultivo.onItemSelectedListener = this
        cboIncidencia.onItemSelectedListener = this
        cboActividad.onItemSelectedListener = this

        packingDAO = ConexionSqlite.getInstancia(requireContext()).packingDAO()
        cultivoDAO = ConexionSqlite.getInstancia(requireContext()).cultivoDAO()
        centroCostoDAO = ConexionSqlite.getInstancia(requireContext()).centroCostoDAO()
        laboresDAO = ConexionSqlite.getInstancia(requireContext()).laboresDAO()
        actividadDAO = ConexionSqlite.getInstancia(requireContext()).actividadDAO()

        adapterPacking = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayPacking)
        adapterCultivo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayCultivo)
        adapterCentroCosto = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayCentroCosto)
        adapterLabores = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayLabores)
        adapterActividad = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayActividad)

        cboPacking.adapter = adapterPacking
        cboCultivo.adapter = adapterCultivo
        cboCentroCosto.adapter = adapterCentroCosto
        cboLabor.adapter = adapterLabores
        cboActividad.adapter = adapterActividad

        listarPacking()
        listarCultivo()
        listarActividad()
        listarTipoTareo()

        var objTareo = tareoDAO.PA_ObtenerTareo_PorCodigo(DatosTareo.TA_Codigo)
        if (objTareo != null){

            var x = -1
            var i = 0
            arrayPacking.forEach {
                if (it.PAC_Codigo == objTareo.PAC_Codigo && x == -1){
                    x = i
                }
                i++
            }

            if (x != -1){
                cboPacking.setSelection(x)
            }

            x = -1
            i = 0;
            arrayCultivo.forEach {
                if (it.CU_Codigo == objTareo.CU_Codigo && x == -1){
                    x = i
                }
                i++
            }

            if (x != -1){
                cboCultivo.setSelection(x)
            }

            if (objTareo.TA_Incidencia == "DIRECTA") cboIncidencia.setSelection(0)
            else cboIncidencia.setSelection(1)

            x = -1
            i = 0;
            arrayCentroCosto.forEach {
                if (it.CC_Codigo == objTareo.CC_Codigo && x == -1){
                    x = i
                }
                i++
            }

            if (x != -1){
                cboCentroCosto.setSelection(x)
            }

            x = -1
            i = 0;
            arrayActividad.forEach {
                if (it.AC_Codigo == objTareo.AC_Codigo && x == -1){
                    x = i
                }
                i++
            }

            if (x != -1){
                cboActividad.setSelection(x)
            }

            Toast.makeText(requireContext(), "tam: "+arrayLabores.size+" tam ac: ${arrayActividad.size}", Toast.LENGTH_SHORT).show()
            listarLabores()
            Toast.makeText(requireContext(), "tam2: "+arrayLabores.size, Toast.LENGTH_SHORT).show()

            x = -1
            i = 0;
            arrayLabores.forEach {
                if (it.Lab_Codigo == objTareo.Lab_Codigo && x == -1){
                    x = i
                }
                i++
            }

            if (x != -1){
                cboLabor.setSelection(x)
            }

            if (objTareo.TA_TipoTareo == "JORNAL") cboTipoTareo.setSelection(0)
            else if (objTareo.TA_TipoTareo == "TAREA") cboTipoTareo.setSelection(1)
            else cboTipoTareo.setSelection(2)
        }

        alertDialog_Datos = AlertDialog.Builder(requireContext())
        alertDialog_Datos.setView(vCambiarDatos)
        var ok = alertDialog_Datos.show()

        btnSiguiente.setOnClickListener {
            if (cboPacking.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Fundo/Packing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboCultivo.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Cultivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboIncidencia.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Incidencia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboCentroCosto.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Parrón/Lote/Centro Costo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboLabor.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Labor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboActividad.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cboTipoTareo.selectedItemPosition == -1){
                Toast.makeText(requireContext(), "Seleccione Tipo Tareo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Globales.showSuccessesMessageAndCuestions("¿Guardar Cambios?", requireContext(),
                {
                    var PAC_Codigo = arrayPacking[cboPacking.selectedItemPosition].PAC_Codigo
                    var CU_Codigo = arrayCultivo[cboCultivo.selectedItemPosition].CU_Codigo
                    var TA_Incidencia = cboIncidencia.selectedItem.toString()
                    var CC_Codigo = arrayCentroCosto[cboCentroCosto.selectedItemPosition].CC_Codigo
                    var Lab_Codigo = arrayLabores[cboLabor.selectedItemPosition].Lab_Codigo
                    var AC_Codigo = arrayActividad[cboActividad.selectedItemPosition].AC_Codigo
                    var TA_TipoTareo = cboTipoTareo.selectedItem.toString()

                    tareoDAO.actualizarTareo(PAC_Codigo.toLong(), CU_Codigo.toLong(), TA_Incidencia, CC_Codigo,
                    Lab_Codigo, AC_Codigo, TA_TipoTareo, DatosTareo.TA_Codigo)
                    ok.dismiss()

                    btnReportar()

                },
                {

                })
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (cboIncidencia.selectedItem.toString() == "DIRECTA") {
            txtParronLote.text = "Parrón/Lote:"
        } else {
            txtParronLote.text = "Centro de Costo:"
        }

        listarCentroCosto()
        listarLabores()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun listarPacking() {
        adapterPacking.clear()

        arrayPacking = packingDAO.PA_ListarPacking() as ArrayList<Packing>
        if (arrayPacking.isNotEmpty()) {
            arrayPacking.iterator().forEach {
                adapterPacking.add(it)
            }
        }

        adapterPacking.notifyDataSetChanged()
        if (cboPacking.count > 0){
            cboPacking.setSelection(0)
        }
    }

    private fun listarCultivo() {
        adapterCultivo.clear()

        arrayCultivo = cultivoDAO.PA_ListarCultivo() as ArrayList<Cultivo>
        if (arrayCultivo.isNotEmpty()) {
            arrayCultivo.iterator().forEach {
                adapterCultivo.add(it)
            }
        }

        adapterCultivo.notifyDataSetChanged()

        if (cboCultivo.count > 0){
            //binding.cboCultivo.setSelection(0)
            listarCentroCosto()
        }
    }

    private fun listarIncidencia() {
        if (cboIncidencia.count > 0){
            cboIncidencia.setSelection(0)
        }
    }

    private fun listarCentroCosto() {

        if (cboIncidencia.selectedItem.toString() == "DIRECTA" && arrayCultivo.size == 0) {
            Toast.makeText(requireContext(), "Seleccione Cultivo", Toast.LENGTH_SHORT).show()
            return
        }

        var _CodCultivo = -1
        if (cboIncidencia.selectedItem.toString() == "DIRECTA") {
            if (cboCultivo.selectedItemPosition == -1) return
            _CodCultivo = arrayCultivo[cboCultivo.selectedItemPosition].CU_Codigo
        }

        var _tipo = cboIncidencia.selectedItem.toString()

        adapterCentroCosto.clear()

        arrayCentroCosto = centroCostoDAO.PA_ListarCentroCosto_CultivoIncidencia(_CodCultivo, _tipo) as ArrayList<CentroCosto>
        if (arrayCentroCosto.isNotEmpty()) {
            arrayCentroCosto.iterator().forEach {
                adapterCentroCosto.add(it)
            }
        }

        adapterCentroCosto.notifyDataSetChanged()
    }

    private fun listarLabores() {

        adapterLabores.clear()
        adapterLabores.notifyDataSetChanged()

        if (cboActividad.size > 0){
            var ac_codigo = arrayActividad[cboActividad.selectedItemPosition].AC_Codigo
            var incidencia = cboIncidencia.selectedItem.toString()

            arrayLabores = laboresDAO.PA_ListarLabores_PorIncidencia_Etapa(incidencia, ac_codigo) as ArrayList<Labores>
            if (arrayLabores.isNotEmpty()) {
                arrayLabores.iterator().forEach {
                    adapterLabores.add(it)
                }
            }

            adapterLabores.notifyDataSetChanged()
        }
    }

    private fun listarActividad() {
        adapterActividad.clear()

        arrayActividad = actividadDAO.PA_ListarActividad() as ArrayList<Actividad>
        if (arrayActividad.isNotEmpty()) {
            arrayActividad.iterator().forEach {
                adapterActividad.add(it)
            }
        }

        adapterActividad.notifyDataSetChanged()

        listarLabores()
    }

    private fun listarTipoTareo(){

    }

    private fun recargar(combo: String) {
        if (combo == "FundoPacking") {
            listarPacking()
            if (arrayPacking.size == 0){
                Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (combo == "Cultivo") {
            listarCultivo()
            if (arrayCultivo.size == 0){
                Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (combo == "Incidencia") {
            listarIncidencia()
        }
        else if (combo == "CentroCosto") {
            listarCentroCosto()
            if (arrayCentroCosto.size == 0){
                Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (combo == "Actividad") {
            listarActividad()
            if (arrayActividad.size == 0){
                Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (combo == "Labor") {
            listarLabores()
            if (arrayLabores.size == 0){
                Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (combo == "TipoTareo") {
            listarTipoTareo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menulistadotareos, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_subir_no_sincronizados_Tareos -> {
                sincronizar_masivo(-1)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun sincronizar_masivo(pos: Int){
        var _msj = ""
        var packingDAO = ConexionSqlite.getInstancia(requireContext()).packingDAO()
        var cultivoDAO = ConexionSqlite.getInstancia(requireContext()).cultivoDAO()
        var laboresDAO = ConexionSqlite.getInstancia(requireContext()).laboresDAO()
        var empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()

        var _datosTareo : String = ""
        var _datosLabores : String = ""
        var _datosProductividad : String = ""
        var _CodigoSupervisor : Int = -1

        //Empaquetar datos de envío
        val tareoLaboresEmpleadoDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleadoDAO()
        val tareolaboresempleadoProductividadDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleado_ProductividadDAO()

        var i = 0
        if (pos == -1){
            var x = 0
            var y = 0
            for(it in lista){
                if (it.TA_Codigo > 0) continue //No incluir los que ya fueron sincronizados

                if (i == 0) _datosTareo = it.toString()
                else _datosTareo += "%" + it.toString()
                i++

                val _labores = tareoLaboresEmpleadoDAO.PA_ListarDetalleTareo_PorCodigo(it.idTareo, "%%")
                for(ll in _labores){
                    if (it.TA_TipoTareo == "JORNAL"){
                        if (ll.TLE_TotalHoras == 0f){
                            if (_msj == "") {
                                var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                                var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                                var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                                var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                                _msj = "${objEmpleado.nombres_apellidos()} NO TIENE HORAS DE TRABAJO\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                            }
                        }
                    }
                    else{
                        if (ll.TLE_TotalHoras == 0f){
                            if (_msj == "") {
                                var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                                var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                                var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                                var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                                _msj = "${objEmpleado.nombres_apellidos()} NO TIENE HORAS DE TRABAJO\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                            }
                        }

                        if (ll.TLE_TotalProductividad == 0f){
                            if (_msj == "") {
                                var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                                var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                                var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                                var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                                _msj = "${objEmpleado.nombres_apellidos()} NO TIENE PRODUCTIVIDAD\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                            }
                        }
                    }

                    if (x == 0) _datosLabores = ll.toString()
                    else _datosLabores += "%" + ll.toString()
                    x++

                    val _prod = tareolaboresempleadoProductividadDAO.PA_ListarProductividad_PorTareoDetalle(ll.idTareo, ll.idDetalle)
                    for(pp in _prod){
                        if (y == 0) _datosProductividad = pp.toString()
                        else _datosProductividad += "%" + pp.toString()
                        y++
                    }
                }
            }
        }
        else{
            val it = lista[pos]
            _datosTareo = it.toString()

            val _labores = tareoLaboresEmpleadoDAO.PA_ListarDetalleTareo_PorCodigo(it.idTareo, "%%")
            var x = 0
            var y = 0
            for(ll in _labores){
                if (x == 0) _datosLabores = ll.toString()
                else _datosLabores += "%" + ll.toString()
                x++

                if (it.TA_TipoTareo == "JORNAL"){
                    if (ll.TLE_TotalHoras == 0f){
                        if (_msj == "") {
                            var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                            var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                            var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                            var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                            _msj = "${objEmpleado.nombres_apellidos()} NO TIENE HORAS DE TRABAJO\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                        }
                    }
                }
                else{
                    if (ll.TLE_TotalHoras == 0f){
                        if (_msj == "") {
                            var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                            var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                            var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                            var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                            _msj = "${objEmpleado.nombres_apellidos()} NO TIENE HORAS DE TRABAJO\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                        }
                    }

                    if (ll.TLE_TotalProductividad == 0f){
                        if (_msj == "") {
                            var objPacking = packingDAO.PA_ObtenerPacking_PorCodigo(it.PAC_Codigo)
                            var objCultivo = cultivoDAO.PA_ObtenerCultivo_PorCodigo(it.CU_Codigo)
                            var objLabor = laboresDAO.PA_ObtenerLabores_PorCodigo(it.Lab_Codigo)
                            var objEmpleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(ll.PER_CodigoObrero)

                            _msj = "${objEmpleado.nombres_apellidos()} NO TIENE PRODUCTIVIDAD\n\nMás información:\n${objPacking.PAC_Descripcion}\nCultivo: ${objCultivo.CU_Descripcion}\nLabor: ${objLabor.Lab_Descripcion}\nTipo Tareo: ${it.TA_TipoTareo}"
                        }
                    }
                }

                val _prod = tareolaboresempleadoProductividadDAO.PA_ListarProductividad_PorTareoDetalle(ll.idTareo, ll.idDetalle)
                for(pp in _prod){
                    if (y == 0) _datosProductividad = pp.toString()
                    else _datosProductividad += "%" + pp.toString()
                    y++
                }
            }

            i++
        }

        if (_msj != ""){
            Globales.showErrorMessage(_msj, requireContext())
            return
        }

        //ENVIAR LOS DATOS A LA NUBE
        var j = 0
        var k = 0
        val rsRespuesta = EmpleadoDALC.PA_ProcesarTareos_Android(_datosTareo, _datosLabores, _datosProductividad, Globales.cod.toLong())
        if (rsRespuesta == null){
            Globales.showErrorMessage("Error al comunicarse con la Nube", requireContext())
            return
        }

        while(rsRespuesta.next()){
            var idTareo = rsRespuesta.getLong("idTareo")
            var TA_Codigo = rsRespuesta.getLong("TA_Codigo")
            var estado = rsRespuesta.getString("Estado")

            if (estado == "OK"){
                j++
                tareoDAO.actualizarEstado(idTareo, TA_Codigo)
            }
            else{
                k++
                tareoDAO.actualizarEstado(idTareo, -2)
            }
        }

        if (k > 0){
            Globales.showWarningMessage("Correctos: ${j} tareos\nErróneos: ${k} tareos\nCausa: conflicto en registros", requireContext())
        }
        else{
            Globales.showSuccessMessage("Se han sincronizado ${i} Tareos", requireContext())
        }

        btnReportar()
    }

    override fun onClick(position: Int,tipo:String){
        when(tipo){
            "Sincronizar"->{
                sincronizar_masivo(position)
            }
            "Item_click" ->{
                val obj = lista[position]
                DatosTareo._CodTareoSelect = obj.idTareo
                if (obj.TA_Codigo > 0){
                    DatosTareo._EstadoTareoSelect = "SINCRONIZADO"
                }
                else{
                    DatosTareo._EstadoTareoSelect = ""
                }

                findNavController().navigate(R.id.action_tareosFragment_to_detalleTareoFragment)
            }
        }
    }

    fun btnReportar(){
        DatosTareo._CodTareoSelect = -1

        val fecha = FunGeneral.obtenerFecha_PorFormato(binding.etDate.text.toString(), "YYYYMMdd")
        val bb = "%"+binding.txtBusqueda.text.toString()+"%"

        var tipotareo = "%%"
        if (binding.rbtnJornal.isChecked) tipotareo = "%JORNAL%"
        else if (binding.rbtnTarea.isChecked) tipotareo = "%TAREA%"
        else if (binding.rbtnDestajo.isChecked) tipotareo = "%DESTAJO%"

        lista = tareoDAO.PA_ListarTareo(fecha, bb, tipotareo) as ArrayList<Tareo>

        adapter = TareoAD_sqlite(lista, this)
        binding.rvTareosSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTareosSqlite.adapter = adapter
        binding.txtItems.text = "Resultados de búsqueda: ${lista.size}"
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        btnReportar()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnReportar()
    }

    override fun afterTextChanged(s: Editable?) {

    }


}