package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.graphics.Color
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R
import java.text.DecimalFormat

class DetalleTareoVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, listener: DetalleTareoAD_sqlite.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_detalle_tareo_sqlite, parent, false)),
    View.OnCreateContextMenuListener {

    var txtCodigoQR: TextView = itemView.findViewById(R.id.txtCodigoQR)
    var txtEmpleado: TextView = itemView.findViewById(R.id.txtEmpleado)
    var txtTotalHoras: TextView = itemView.findViewById(R.id.txtTotalHoras)
    var txtProductividad: TextView = itemView.findViewById(R.id.txtProductividad)
    var txtEstadoRevision: TextView = itemView.findViewById(R.id.txtEstadoRevision)
    var btnCambiarHoras: Button = itemView.findViewById(R.id.btnCambiarHoras)
    var btnProductividad: Button = itemView.findViewById(R.id.btnProductividad)

    var _CodigoGeneral : Long = -1

    init {
        btnCambiarHoras.setOnClickListener {
            listener.onClick(adapterPosition, "Cambiar Horas")
        }

        btnProductividad.setOnClickListener {
            listener.onClick(adapterPosition, "Productividad")
        }

        itemView.setOnCreateContextMenuListener(this)
    }

    fun bind(obj: TareoLaboresEmpleado) {
        _CodigoGeneral = obj.idDetalle
        DatosTareo.PER_CodigoObrero = obj.PER_CodigoObrero.toLong()

        var empleadoDAO = ConexionSqlite.getInstancia(itemView.context).empleadoDAO()

        var objEmpleado : Empleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(obj.PER_CodigoObrero)

        var _dni = "-"
        var _nombres = "- no encontrado"
        if (objEmpleado != null){
            _dni = objEmpleado.PER_NroDoc
            _nombres = objEmpleado.nombres_apellidos()
        }

        txtCodigoQR.text = "Código QR: ${_dni}"
        txtEmpleado.text = "${_nombres}"
        txtTotalHoras.text = "TOTAL HORAS: [ ${FunGeneral.convertirMinutosEnTexto(obj.TLE_TotalHoras)} ]"

        val formato2 = DecimalFormat("#.##")

        txtProductividad.text = "PRODUCTIVIDAD: [ ${FunGeneral.toStringG29(obj.TLE_TotalProductividad) } ]"

        if (obj.EstadoRevision == "PENDIENTE"){
            txtEstadoRevision.text = "NO REVISADO"
            txtEstadoRevision.setTextColor(Color.RED)
        }
        else{
            txtEstadoRevision.text = "REVISADO"
            txtEstadoRevision.setTextColor(Color.BLUE)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        DatosTareo.idDetalle = _CodigoGeneral
        menu?.add(Menu.NONE, R.id.action_eliminar_trabajador_tareo, 0, "Eliminar Trabajador")
    }
}