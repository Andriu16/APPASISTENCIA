package dev.app.peru.guidecsharp.Vista.ui.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.*
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.BuscarProveedorAD_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.BuscarProveedorVH_sql
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.ActivityBusquedaProveedorGuiaBinding
import dev.app.peru.guidecsharp.databinding.FlotanteDatosDetalleTareoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.ResultSet

class BusquedaProveedorGuiaActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    BuscarProveedorAD_sql.MyClickListener {

    private lateinit var binding: ActivityBusquedaProveedorGuiaBinding

    //Variables
    private var lista = ArrayList<Persona_sql>()
    private lateinit var adapter : RecyclerView.Adapter<BuscarProveedorVH_sql>

    lateinit var vDetalleTareo: View
    lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaProveedorGuiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vDetalleTareo = layoutInflater.inflate(R.layout.flotante_datos_detalle_tareo, null)

        adapter = BuscarProveedorAD_sql(lista, this)
        binding.rvBusquedaClientes.layoutManager = LinearLayoutManager(this)
        binding.rvBusquedaClientes.adapter = adapter

        binding.btnCerrarBusquedaProductos.setOnClickListener { btnCerrarBusqueda() }
        binding.txtBuscarProductos.setOnQueryTextListener(this)

        var buscar = intent.getStringExtra("buscar")
        binding.txtBuscarProductos.setQuery(buscar, false)

        binding.txtBuscarProductos.requestFocus()
    }

    fun btnCerrarBusqueda(){
        finish()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query!!.trim().isEmpty() == false){
            ocultarTeclado()
        }
        btnReportar()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText!!.trim().isEmpty()){

        }
        else{

            if (newText!!.trim().length > 2){
                btnReportar()
            }
        }
        return true
    }

    fun btnReportar(){
        var bb = binding.txtBuscarProductos.query.toString()
        if (bb.length > 2){
            bb = ("%" + bb + "%").replace(" ", "%")

            var _listaRegistros = ArrayList<Persona_sql>()
            var rs: ResultSet? = null
            var _msj = ""

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rs = EmpleadoDALC.PA_ListarPersonal_Android(bb)
                } catch (e: Exception) {
                    _msj = e.message.toString()
                }

                runOnUiThread {

                    if (_msj != "") {
                        return@runOnUiThread
                    }

                    while (rs!!.next()) {
                        var obj = Persona_sql()
                        obj.IDCODIGOGENERAL = rs!!.getString("IDCODIGOGENERAL")
                        obj.NroDoc = rs!!.getString("IDCODIGOGENERAL")
                        obj.ApellidosNombres = rs!!.getString("PERSONAL")
                        _listaRegistros.add(obj)
                    }

                    lista.clear()
                    lista.addAll(_listaRegistros)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(position: Int, tipo: String) {
        when(tipo){
            "Item_click" -> {
                if (vDetalleTareo.parent != null) {
                    (vDetalleTareo.parent as ViewGroup).removeView(vDetalleTareo)
                    alertDialog.setView(vDetalleTareo)
                }
                ContruirDialogo(position)
            }
        }
    }

    var _CodigoActividad = ""
    fun btnActividad(txt: TextInputLayout){
        val buscar = "%"

        val frm = FunGeneral.mostrarCargando(this)
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

            runOnUiThread {
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

                MaterialAlertDialogBuilder(this@BusquedaProveedorGuiaActivity)
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

        val frm = FunGeneral.mostrarCargando(this)
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

            runOnUiThread {
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

                MaterialAlertDialogBuilder(this@BusquedaProveedorGuiaActivity)
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

        val frm = FunGeneral.mostrarCargando(this)
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

            runOnUiThread {
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

                MaterialAlertDialogBuilder(this@BusquedaProveedorGuiaActivity)
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
        val obj = lista[position]

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

        txtTrabajador.text = obj.NroDoc + " - " + obj.ApellidosNombres

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

        alertDialog = AlertDialog.Builder(this)
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

            Globales._fragmentTareoNuevo!!.agregarDetalleTareo(
                obj.NroDoc + " - " + obj.ApellidosNombres,
                txtActividad.editText!!.text.toString(),
                txtLabor.editText!!.text.toString(),
                txtConsumidor.editText!!.text.toString(),
                txtHoras.editText!!.text.toString(),
                obj.IDCODIGOGENERAL,
                _CodigoActividad,
                _CodigoLabor,
                _CodigoConsumidor)

            ocultarTeclado()
            finish()
        }
    }

    fun ocultarTeclado(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}