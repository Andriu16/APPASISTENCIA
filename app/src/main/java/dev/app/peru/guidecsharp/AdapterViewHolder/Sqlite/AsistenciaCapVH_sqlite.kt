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

class AsistenciaCapVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, listener: AsistenciaCapAD_sqlite.MyClickListener) :
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

    fun bind(obj: AsistenciaPuerta_Cap) {
        if (obj.xSelect == "SI"){
            selectOk.isChecked = true
        }
        else{
            selectOk.isChecked = false
        }

        txtCodigoQr.text = "Código QR: ${obj.PER_CodigoQR}"
        txtEmpleado.text = "${obj.Trabajador}"

        txtHoraEntrada2.text = FunGeneral.obtenerFechaHora_PorFormato_Nube(obj.HoraEntrada, "dd/MM/YYYY hh:mm:ss a")
        txtHoraEntrada2.setTextColor(Color.BLUE)

        if (obj.HoraSalida == ""){
            txtHoraSalida2.text = "SIGUE EN CAMPO"
            txtHoraSalida2.setTextColor(Color.RED)
        }
        else{
            txtHoraSalida2.text = FunGeneral.obtenerFechaHora_PorFormato_Nube(obj.HoraSalida, "dd/MM/YYYY hh:mm:ss a")
            txtHoraSalida2.setTextColor(Color.BLUE)
        }

        if (obj.Sede == ""){
            txtSede.text = "SEDE: -"
        }
        else{
            txtSede.text = "SEDE: ${obj.Sede}"
        }
    }
}