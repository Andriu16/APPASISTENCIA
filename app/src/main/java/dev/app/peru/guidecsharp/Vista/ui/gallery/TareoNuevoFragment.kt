package dev.app.peru.guidecsharp.Vista.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import androidx.fragment.app.Fragment
import dev.app.peru.guidecsharp.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.*
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.DetalleTareoAD_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.DetalleTareoVH_sql
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.databinding.FragmentTareoNuevoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.ResultSet

class TareoNuevoFragment : Fragment(), DetalleTareoAD_sql.MyClickListener {

    private var _binding: FragmentTareoNuevoBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var adapter : RecyclerView.Adapter<DetalleTareoVH_sql>

    lateinit var vDetalleTareo: View
    lateinit var alertDialog: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View {
        _binding = FragmentTareoNuevoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Globales._fragmentTareoNuevo = this
        vDetalleTareo = layoutInflater.inflate(R.layout.flotante_datos_detalle_tareo, null)

        adapter = DetalleTareoAD_sql(Globales._ArrayDetalleTareo, this)
        binding.rvProductoSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductoSqlite.adapter = adapter

        binding.txtGrupoTrabajo.setEndIconOnClickListener { btnGrupoTrabajo() }
        binding.etgrupotrabajo.setOnClickListener { btnGrupoTrabajo() }

        binding.txtTurno.setEndIconOnClickListener { btnTurno() }
        binding.etturno.setOnClickListener { btnTurno() }

        binding.btnBuscarTrabajador.setOnClickListener { btnBuscarTrabajador() }
        binding.btnSiguiente.setOnClickListener { siguiente() }

        IDGRUPOTRABAJO = Globales._IDGRUPOTRABAJO
        binding.txtGrupoTrabajo.editText!!.setText(Globales._DESCRIPCIONGRUPOTRABAJO)

        IDTURNO = Globales._IDTURNO
        binding.txtTurno.editText!!.setText(Globales._DESCRIPCIONTURNO)

        btnReportar()

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_tareos, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_catalogo_tareos -> {
                //findNavController().navigate(R.id.action_nav_gallery_to_tareosNubeFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    var IDGRUPOTRABAJO = ""
    fun btnGrupoTrabajo() {
        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var _listaRegistros = ArrayList<GrupoTrabajo_sql>()
        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ListarGrupoTrabajo_Android()
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = GrupoTrabajo_sql()
                    obj.IDGRUPOTRABAJO = rs!!.getString("IDGRUPOTRABAJO")
                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
                    _listaRegistros.add(obj)
                }

                val items = arrayOfNulls<String>(_listaRegistros.size)
                for(i in _listaRegistros.indices){
                    items[i] = _listaRegistros[i].DESCRIPCION
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.lblCategorias)
                    .setIcon(R.drawable.ic_menu)
                    .setItems(items) { dialog, which ->
                        IDGRUPOTRABAJO = _listaRegistros[which].IDGRUPOTRABAJO
                        binding.txtGrupoTrabajo.editText!!.setText(items[which])

                        Globales._IDGRUPOTRABAJO = IDGRUPOTRABAJO
                        Globales._DESCRIPCIONGRUPOTRABAJO = items[which].toString()
                    }
                    .show()
            }
        }
    }

    var IDTURNO = ""
    fun btnTurno() {
        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var _listaRegistros = ArrayList<Turno_sql>()
        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ListarTurno_Android()
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = Turno_sql()
                    obj.IDTURNO = rs!!.getString("IDTURNOTRABAJO")
                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
                    _listaRegistros.add(obj)
                }

                val items = arrayOfNulls<String>(_listaRegistros.size)
                for(i in _listaRegistros.indices){
                    items[i] = _listaRegistros[i].DESCRIPCION
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.lblCategorias)
                    .setIcon(R.drawable.ic_menu)
                    .setItems(items) { dialog, which ->
                        IDTURNO = _listaRegistros[which].IDTURNO
                        binding.txtTurno.editText!!.setText(items[which])

                        Globales._IDGRUPOTRABAJO = IDTURNO
                        Globales._DESCRIPCIONTURNO = items[which].toString()
                    }
                    .show()
            }
        }
    }

    fun btnBuscarTrabajador() {

        val buscar: String = "" //binding.txtBuscarTransportista.editText!!.text.toString()
        val intent = Intent(activity, BusquedaProveedorGuiaActivity::class.java)
        intent.putExtra("buscar", buscar)
        startActivity(intent)
    }

    fun btnReportar(){

        val _listaRegistros = ArrayList<DetalleTareo_sql>()
        _listaRegistros.addAll(Globales._ArrayDetalleTareo)

        Globales._ArrayDetalleTareo.clear()
        Globales._ArrayDetalleTareo.addAll(_listaRegistros)
        adapter.notifyDataSetChanged()

        binding.txtItems.text = "Registros: ${Globales._ArrayDetalleTareo.size}"
    }

    fun resetearErorres(){
        binding.txtGrupoTrabajo.isErrorEnabled = false
        binding.txtGrupoTrabajo.error = null
        binding.txtTurno.isErrorEnabled = false
        binding.txtTurno.error = null
    }

    private fun siguiente(){
        resetearErorres()

        if (IDGRUPOTRABAJO == ""){
            binding.txtGrupoTrabajo.isErrorEnabled = true
            binding.txtGrupoTrabajo.error = "Indique Grupo de Trabajo"
            binding.txtGrupoTrabajo.requestFocus()
            return
        }

        if (IDTURNO == ""){
            binding.txtTurno.isErrorEnabled = true
            binding.txtTurno.error = "Indique Turno"
            binding.txtTurno.requestFocus()
            return
        }

        if (Globales._ArrayDetalleTareo.size == 0){
            Globales.showErrorMessage("Agrege trabajadores al registro", requireContext())
            return
        }

        var FECHA : String = "20240904 10:20:00"
        var PERIODO : String = "202409"
        var PERIODO_PLANILLA : String = "202409"
        var IDRESPONSABLE : String = ""
        var IDPLANILLA : String = ""
        var IDEMISOR : String = ""
        var IDOPERACION : String = ""
        var IDSUCURSAL : String = ""
        var RENDIMIENTO : Float = 0f
        var T_HORAS : Float = 0f

        var _CadenaCabTareo = "${FECHA}|${PERIODO}|${PERIODO_PLANILLA}|${IDTURNO}|${IDRESPONSABLE}|${IDPLANILLA}|${IDEMISOR}|${IDOPERACION}|${IDSUCURSAL}|${RENDIMIENTO}|${T_HORAS}|${IDGRUPOTRABAJO}"
        var _CadenaDetTareo = ""

        Globales._ArrayDetalleTareo.forEachIndexed { index, dd ->
            dd.TIPO_ASISTENCIA = "N"

            if (index == 0){
                _CadenaDetTareo = "${index}|${dd.IDCONSUMIDOR}|${dd.TIPO_ASISTENCIA}|${dd.TotalHoras}|${dd.IDACTIVIDAD}|${dd.IDLABOR}|${dd.IDCODIGOGENERAL}"
            }else{
                _CadenaDetTareo += "%" + "${index}|${dd.IDCONSUMIDOR}|${dd.TIPO_ASISTENCIA}|${dd.TotalHoras}|${dd.IDACTIVIDAD}|${dd.IDLABOR}|${dd.IDCODIGOGENERAL}"
            }
        }

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                rs = EmpleadoDALC.PA_ListarActividad_Android(_CadenaCabTareo, _CadenaDetTareo, Globales.cod)
//            } catch (e: Exception) {
//                _msj = e.message.toString()
//            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

//                while (rs!!.next()) {
//                    var obj = Actividad_sql()
//                    obj.IDACTIVIDAD = rs!!.getString("IDACTIVIDAD")
//                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
//                    _listaRegistros.add(obj)
//                }
//
//                val items = arrayOfNulls<String>(_listaRegistros.size)
//                for(i in _listaRegistros.indices){
//                    items[i] = _listaRegistros[i].DESCRIPCION
//                }
//
//                MaterialAlertDialogBuilder(requireContext())
//                    .setTitle(R.string.lblCategorias)
//                    .setIcon(R.drawable.ic_menu)
//                    .setItems(items) { dialog, which ->
//                        _CodigoActividad = _listaRegistros[which].IDACTIVIDAD
//                        txt.editText!!.setText(items[which])
//                    }
//                    .show()
            }
        }

    }

    override fun onClick(position: Int, tipo: String) {
        if (tipo == "Editar"){
            if (vDetalleTareo.parent != null) {
                (vDetalleTareo.parent as ViewGroup).removeView(vDetalleTareo)
                alertDialog.setView(vDetalleTareo)
            }
            ContruirDialogo(position)
        }else if (tipo == "Eliminar"){
            Globales._ArrayDetalleTareo.removeAt(position)
            adapter.notifyDataSetChanged()

            binding.txtItems.text = "Registros: ${Globales._ArrayDetalleTareo.size}"
        }
    }

    var _CodigoActividad = ""
    fun btnActividad(txt: TextInputLayout){
        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var _listaRegistros = ArrayList<Actividad_sql>()
        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ListarActividad_Android()
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = Actividad_sql()
                    obj.IDACTIVIDAD = rs!!.getString("IDACTIVIDAD")
                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
                    _listaRegistros.add(obj)
                }

                val items = arrayOfNulls<String>(_listaRegistros.size)
                for(i in _listaRegistros.indices){
                    items[i] = _listaRegistros[i].DESCRIPCION
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.lblCategorias)
                    .setIcon(R.drawable.ic_menu)
                    .setItems(items) { dialog, which ->
                        _CodigoActividad = _listaRegistros[which].IDACTIVIDAD
                        txt.editText!!.setText(items[which])
                    }
                    .show()
            }
        }
    }

    var _CodigoLabor = ""
    fun btnLabor(txt: TextInputLayout, txtActividad: TextInputLayout){
        txtActividad.isErrorEnabled = false
        txtActividad.error = null

        if (_CodigoActividad == ""){
            txtActividad.isErrorEnabled = true
            txtActividad.error = "Primero indique Actividad"
            txtActividad.requestFocus()
            return
        }

        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var _listaRegistros = ArrayList<Labor_sql>()
        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ListarLabor_Android(_CodigoActividad)
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = Labor_sql()
                    obj.IDLABOR = rs!!.getString("IDLABOR")
                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
                    _listaRegistros.add(obj)
                }

                val items = arrayOfNulls<String>(_listaRegistros.size)
                for(i in _listaRegistros.indices){
                    items[i] = _listaRegistros[i].DESCRIPCION
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.lblCategorias)
                    .setIcon(R.drawable.ic_menu)
                    .setItems(items) { dialog, which ->
                        _CodigoLabor = _listaRegistros[which].IDLABOR
                        txt.editText!!.setText(items[which])
                    }
                    .show()
            }
        }
    }

    var _CodigoConsumidor = ""
    fun btnConsumidor(txt: TextInputLayout){
        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var _listaRegistros = ArrayList<Consumidor_sql>()
        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ListarConsumidor_Android()
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = Consumidor_sql()
                    obj.IDCONSUMIDOR = rs!!.getString("IDCONSUMIDOR")
                    obj.DESCRIPCION = rs!!.getString("DESCRIPCION")
                    _listaRegistros.add(obj)
                }

                val items = arrayOfNulls<String>(_listaRegistros.size)
                for(i in _listaRegistros.indices){
                    items[i] = _listaRegistros[i].DESCRIPCION
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.lblCategorias)
                    .setIcon(R.drawable.ic_menu)
                    .setItems(items) { dialog, which ->
                        _CodigoConsumidor = _listaRegistros[which].IDCONSUMIDOR
                        txt.editText!!.setText(items[which])
                    }
                    .show()
            }
        }
    }

    fun ContruirDialogo(position: Int){

        val obj = Globales._ArrayDetalleTareo[position]

        //Valores iniciales
        val txtTrabajador = vDetalleTareo.findViewById<TextView>(R.id.txtTrabajador)
        val txtActividad = vDetalleTareo.findViewById<TextInputLayout>(R.id.txtActividad)
        val txtLabor = vDetalleTareo.findViewById<TextInputLayout>(R.id.txtLabor)
        val txtConsumidor = vDetalleTareo.findViewById<TextInputLayout>(R.id.txtConsumidor)

        val etActividad = vDetalleTareo.findViewById<TextInputEditText>(R.id.etactividad)
        val etLabor = vDetalleTareo.findViewById<TextInputEditText>(R.id.etlabor)
        val etConsumidor = vDetalleTareo.findViewById<TextInputEditText>(R.id.etconsumidor)

        val txtHoras = vDetalleTareo.findViewById<TextInputLayout>(R.id.txtHoras)

        val btnCerrar = vDetalleTareo.findViewById<Button>(R.id.btnCerrar)
        val btnGuardar = vDetalleTareo.findViewById<Button>(R.id.btnGuardar)

        txtTrabajador.text = obj.ApellidosNombres

        //Cargar datos actuales
        txtActividad.editText!!.setText(obj.Actividad)
        txtLabor.editText!!.setText(obj.Labor)
        txtConsumidor.editText!!.setText(obj.Consumidor)
        _CodigoActividad = obj.IDACTIVIDAD
        _CodigoLabor = obj.IDLABOR
        _CodigoConsumidor = obj.IDCONSUMIDOR

        txtHoras.editText!!.setText(obj.TotalHoras.toString())

        txtActividad.setEndIconOnClickListener { btnActividad(txtActividad) }
        etActividad.setOnClickListener { btnActividad(txtActividad) }

        txtLabor.setEndIconOnClickListener { btnLabor(txtLabor, txtActividad) }
        etLabor.setOnClickListener { btnLabor(txtLabor, txtActividad) }

        txtConsumidor.setEndIconOnClickListener { btnConsumidor(txtConsumidor) }
        etConsumidor.setOnClickListener { btnConsumidor(txtConsumidor) }

        //Limpiar errores
        txtActividad.isErrorEnabled = false
        txtActividad.error = null
        txtLabor.isErrorEnabled = false
        txtLabor.error = null
        txtConsumidor.isErrorEnabled = false
        txtConsumidor.error = null

        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(vDetalleTareo)
        alertDialog.setCancelable(true)
        val vista = alertDialog.show()

        btnCerrar.setOnClickListener {
            vista.dismiss()
        }

        btnGuardar.setOnClickListener {
            txtActividad.isErrorEnabled = false
            txtActividad.error = null
            txtLabor.isErrorEnabled = false
            txtLabor.error = null
            txtConsumidor.isErrorEnabled = false
            txtConsumidor.error = null

            if (_CodigoActividad == ""){
                txtActividad.isErrorEnabled = true
                txtActividad.error = "Indique Actividad"
                txtActividad.requestFocus()
                return@setOnClickListener
            }

            if (_CodigoLabor == ""){
                txtLabor.isErrorEnabled = true
                txtLabor.error = "Indique Labor"
                txtLabor.requestFocus()
                return@setOnClickListener
            }

            if (_CodigoConsumidor == ""){
                txtConsumidor.isErrorEnabled = true
                txtConsumidor.error = "Indique Consumidor"
                txtConsumidor.requestFocus()
                return@setOnClickListener
            }

            var horas = FunGeneral.getFloat(txtHoras.editText!!.text.toString())
            if (horas <= 0){
                txtHoras.isErrorEnabled = true
                txtHoras.error = "Ingrese valor de horas > 0"
                txtHoras.requestFocus()
                return@setOnClickListener
            }

            obj.Actividad = txtActividad.editText!!.text.toString()
            obj.Labor = txtLabor.editText!!.text.toString()
            obj.Consumidor = txtConsumidor.editText!!.text.toString()
            obj.TotalHoras = FunGeneral.getFloat(txtHoras.editText!!.text.toString())
            obj.IDACTIVIDAD = _CodigoActividad
            obj.IDLABOR = _CodigoLabor
            obj.IDCONSUMIDOR = _CodigoConsumidor

            vista.dismiss()

            adapter.notifyDataSetChanged()

            binding.txtItems.text = "Registros: ${Globales._ArrayDetalleTareo.size}"
        }
    }

    fun agregarDetalleTareo(dato1: String, dato2: String, dato3: String, dato4: String, dato5: String,
                            IDCODIGOGENERAL: String, IDACTIVIDAD: String, IDLABOR: String, IDCONSUMIDOR: String){
        var dd = DetalleTareo_sql()
        dd.ApellidosNombres = dato1 //"JOSUE CHACON IBARROLA"
        dd.Actividad = dato2 //"LIMPIEZA"
        dd.Labor = dato3 //"LABOR DE PRUEBA"
        dd.Consumidor = dato4 //"CONSUMIDOR"
        dd.TotalHoras = dato5.toFloat() //8f

        dd.IDCODIGOGENERAL = IDCODIGOGENERAL
        dd.IDACTIVIDAD = IDACTIVIDAD
        dd.IDLABOR = IDLABOR
        dd.IDCONSUMIDOR = IDCONSUMIDOR

        Globales._ArrayDetalleTareo.add(dd)

        binding.rvProductoSqlite.getLayoutManager()?.scrollToPosition(Globales._ArrayDetalleTareo.size -1);

        adapter.notifyDataSetChanged()

        binding.txtItems.text = "Registros: ${Globales._ArrayDetalleTareo.size}"
    }
}