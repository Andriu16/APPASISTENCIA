package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.UnidadesDestajo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R
import java.text.DecimalFormat

class DetalleProductividadVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, listener: DetalleProductividadAD_sqlite.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_detalle_productividad, parent, false)),
    TextWatcher {

    var cboUnidades: Spinner = itemView.findViewById(R.id.cboUnidades)
    var txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
    var txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
    var txtTotal: TextView = itemView.findViewById(R.id.txtTotal)

    var txtUnidadDestajo : EditText = itemView.findViewById(R.id.txtUnidadDestajo)
    var txtCantidadDestajo: TextView = itemView.findViewById(R.id.txtCantidadDestajo)
    var txtPrecioDestajo: TextView = itemView.findViewById(R.id.txtPrecioDestajo)
    var txtTotalDestajo: TextView = itemView.findViewById(R.id.txtTotalDestajo)

    var btnQuitarDetalle: Button = itemView.findViewById(R.id.btnQuitarDetalle)
    var btnOkProductividad: Button = itemView.findViewById(R.id.btnOkProductividad)


    init {
        btnOkProductividad.setOnClickListener {

            var codunidad : Long = -1
            var desunidad : String = "2"

            if (listaUnidades.size > 0){
                codunidad = listaUnidades[cboUnidades.selectedItemPosition].UD_Nombre
                desunidad = listaUnidades[cboUnidades.selectedItemPosition].UNI_DescripcionTemp
            }

            var SF = DecimalFormat("0.00")

            var _cant = FunGeneral.getFloat(txtCantidad.text.toString())
            var _precio = FunGeneral.getFloat(txtPrecio.text.toString())
            var _imp = SF.format(_cant * _precio).toFloat()

            var _unidadDestajo = txtUnidadDestajo.text.toString()
            var _cant2 = FunGeneral.getFloat(txtCantidadDestajo.text.toString())
            var _precio2 = FunGeneral.getFloat(txtPrecioDestajo.text.toString())
            var _imp2 = SF.format(_cant2 * _precio2).toFloat()

            var tareolaboresempleadoProductividaDAO = ConexionSqlite.getInstancia(itemView.context).tareoLaboresEmpleado_ProductividadDAO()
            tareolaboresempleadoProductividaDAO.actualizaProductividad(idDetalleProd, codunidad, desunidad, _cant, _precio, _imp, _unidadDestajo, _cant2, _precio2, _imp2, "REVISADO")

            Toast.makeText(itemView.context, "Datos Guardados", Toast.LENGTH_SHORT).show()
            listener.onClick2(adapterPosition, "Ok Productividad")
        }
        btnQuitarDetalle.setOnClickListener {
            listener.onClick2(adapterPosition, "QuitarDetalle")
        }

        txtCantidad.addTextChangedListener(this)
        txtPrecio.addTextChangedListener(this)
        txtCantidadDestajo.addTextChangedListener(this)
        txtPrecioDestajo.addTextChangedListener(this)
    }

    var unidadDestajoDAO = ConexionSqlite.getInstancia(itemView.context).unidadDestajoDAO()
    var listaUnidades = ArrayList<UnidadesDestajo>()

    var idDetalleProd : Long = 0
    var _tipoTareo = ""
    fun bind(obj: TareoLaboresEmpleado_Productividad) {
        idDetalleProd = obj.idDetalleProd

        var tareoDAO = ConexionSqlite.getInstancia(itemView.context).tareoDAO()
        var objTareo = tareoDAO.PA_ObtenerTareo_PorCodigo(obj.idTareo)

        _tipoTareo = objTareo.TA_TipoTareo
        if (objTareo.TA_TipoTareo == "TAREA"){
            txtPrecio.isEnabled = false
            txtPrecio.setText("0.00")
        }
        else if (objTareo.TA_TipoTareo == "DESTAJO"){
            txtUnidadDestajo.isEnabled = false
            txtCantidadDestajo.isEnabled = false
            txtPrecioDestajo.isEnabled = false
        }

        //Llenar el combo
        listaUnidades = unidadDestajoDAO.PA_ListarUnidadDestajo_PorLabor(objTareo.Lab_Codigo) as ArrayList<UnidadesDestajo>
        if (objTareo.TA_TipoTareo == "TAREA"){
            listaUnidades.clear()
            listaUnidades.add(UnidadesDestajo(-1, -1, -1, "TAREA"))
        }

        var adapterUnidades : ArrayAdapter<UnidadesDestajo> = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, listaUnidades)
        cboUnidades.adapter = adapterUnidades

        var _ok = true
        cboUnidades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long ) {
                //Obtener el costo por cada consulta
                if (obj.EstadoRevision == "PENDIENTE"){

                }
                else {
                    if (_ok){
                        _ok = false
                        return
                    }
                }

                var codunidad : Long = -1
                var codlabor : Long = objTareo.Lab_Codigo
                var fecha_long = FunGeneral.obtenerFechaHora_PorFormato_Nube_Long(objTareo.TA_FechaRegistro)

                if (listaUnidades.size > 0){
                    codunidad = listaUnidades[cboUnidades.selectedItemPosition].UD_Nombre
                }

                var tarifaUnidadDestajoDAO = ConexionSqlite.getInstancia(itemView.context).tarifaUnidadDestajoDAO()
                var objPrecio = tarifaUnidadDestajoDAO.PA_ObtenerTarifa_PorLabor_Unidad(codlabor, codunidad, fecha_long)
                if (objPrecio != null){
                    //Toast.makeText(itemView.context, "PRECIO: ${objPrecio.UD_Costo}", Toast.LENGTH_SHORT).show()
                    txtPrecio.text = "${objPrecio.UD_Costo}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        var _pos = -1
        var _encontrado = false
        listaUnidades.forEach{
            if (_encontrado) return@forEach

            _pos += 1
            if (it.UD_Nombre == obj.UNI_Codigo.toLong()){
                _encontrado = true
            }
        }

        if (_encontrado){
            cboUnidades.setSelection(_pos)
        }

        var SF = DecimalFormat("0.00")

        txtCantidad.text = "${obj.TLEP_Cantidad}"
        txtPrecio.text = "${obj.TLEP_Precio}"
        txtTotal.text = "Total: s/. ${SF.format(obj.TLEP_Importe)}"

        txtUnidadDestajo.setText("${obj.TLEP_UnidadDestajo}")
        if (obj.TLEP_CantidadDestajo == 0f){
            txtCantidadDestajo.text = ""
        }
        else {
            txtCantidadDestajo.text = "${obj.TLEP_CantidadDestajo}"
        }
        if (obj.TLEP_PrecioDestajo == 0f){
            txtPrecioDestajo.text = ""
        }
        else{
            txtPrecioDestajo.text = "${obj.TLEP_PrecioDestajo}"
        }
        txtTotalDestajo.text = "Total: s/. ${SF.format(obj.TLEP_TotalDestajo)}"

        if (obj.EstadoRevision == "PENDIENTE"){
            txtCantidad.setText("")
            txtCantidad.requestFocus()
        }

        if (objTareo.TA_Codigo > 0){
            cboUnidades.isEnabled = false
            txtCantidad.isEnabled = false
            txtPrecio.isEnabled = false
            txtTotal.isEnabled = false

            txtUnidadDestajo.isEnabled = false
            txtCantidadDestajo.isEnabled = false
            txtPrecioDestajo.isEnabled = false
            txtTotalDestajo.isEnabled = false

            btnQuitarDetalle.isEnabled = false
            btnOkProductividad.isEnabled = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var SF = DecimalFormat("0.00")

        var _cant = FunGeneral.getFloat(txtCantidad.text.toString())
        var _precio = FunGeneral.getFloat(txtPrecio.text.toString())
        var _imp = _cant * _precio
        txtTotal.setText("Total: s/. ${SF.format(_imp)}")

        var _cant2 = FunGeneral.getFloat(txtCantidadDestajo.text.toString())
        var _precio2 = FunGeneral.getFloat(txtPrecioDestajo.text.toString())
        var _imp2 = _cant2 * _precio2
        txtTotalDestajo.setText("Total: s/. ${SF.format(_imp2)}")
    }

    override fun afterTextChanged(s: Editable?) {

    }
}