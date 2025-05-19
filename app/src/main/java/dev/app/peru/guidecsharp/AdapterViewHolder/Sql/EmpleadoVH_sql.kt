package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Empleado_sql
import dev.app.peru.guidecsharp.R
import java.text.SimpleDateFormat
import java.util.*

class EmpleadoVH_sql(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_empleado_modelo, parent, false)) {

    var nroventa: TextView = itemView.findViewById(R.id.txtnroventa_cli)
    var fecha: TextView = itemView.findViewById(R.id.txtfechaventa_cli)
    var cliente: TextView = itemView.findViewById(R.id.txtclienteventa_cli)
    var modalidad: TextView = itemView.findViewById(R.id.txtmodalidadventa_cli)
    var idventa = 0

    init {

    }

    fun bind(obj: Empleado_sql) {
        var sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
        idventa = obj.PER_Codigo
        nroventa.text = "QR: ${obj.PER_NroDoc}"
        fecha.text = "Nombres: ${obj.PER_Nombres}"
        cliente.text = "Apellidos: ${obj.PER_Paterno} ${obj.PER_Materno}"
    }
}