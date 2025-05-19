package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta_Cap
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Sucursal
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R

class EmpleadoTareoVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, listener: EmpleadoTareoAD_sqlite.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_asistencias_diaria2, parent, false)) {

    var txtCodigoQr: TextView = itemView.findViewById(R.id.txtCodigoQr)
    var txtEmpleado: TextView = itemView.findViewById(R.id.txtEmpleado)
    var txtHoraEntrada2: TextView = itemView.findViewById(R.id.txtHoraEntrada2)
    var txtHoraSalida2: TextView = itemView.findViewById(R.id.txtHoraSalida2)
    var txtSede: TextView = itemView.findViewById(R.id.txtSede)
    var selectOk: Switch = itemView.findViewById(R.id.selectOk)

    var _CodigoGeneral = -1

    init {
        selectOk.setOnClickListener {
            listener.onClick(adapterPosition, "Sincronizar")
        }
    }

    fun bind(obj: Empleado) {
        if (obj.SelectTareo == "SI"){
            selectOk.isChecked = true
        }
        else{
            selectOk.isChecked = false
        }

        txtCodigoQr.text = "Código QR: ${obj.PER_CodigoQR}"
        txtEmpleado.text = "${obj.PER_Paterno} ${obj.PER_Materno} ${obj.PER_Nombres}"

        txtHoraEntrada2.text = "NO TIENE"
        txtHoraEntrada2.setTextColor(Color.RED)

        txtHoraSalida2.text = "NO TIENE"
        txtHoraSalida2.setTextColor(Color.RED)

        var sucusalDAO = ConexionSqlite.getInstancia(itemView.context).sucursalDAO()
        var obb = sucusalDAO.PA_ObtenerSucursal_PorCodigo(obj.SUC_Codigo.toLong())
        if (obb != null){
            txtSede.text = "SEDE: ${obb.SUC_Descripcion}"
        }
        else{
            txtSede.text = "SEDE: -"
        }
    }
}