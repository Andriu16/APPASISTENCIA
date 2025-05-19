package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Empleados

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Empleados.EmpleadoAD
import dev.app.peru.guidecsharp.R
import java.text.SimpleDateFormat
import java.util.*

class EmpleadoVH(inflater: LayoutInflater, parent: ViewGroup, listener: EmpleadoAD.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_empleado_sqlite, parent, false)) {

    private var codigoqr: TextView = itemView.findViewById(R.id.txtnroventa_cli)
    private var tiponrodoc: TextView = itemView.findViewById(R.id.txtfechaventa_cli)
    private var empleado: TextView = itemView.findViewById(R.id.txtclienteventa_cli)
    private var origen: TextView = itemView.findViewById(R.id.txtmodalidadventa_cli)
    private var codigointerno: TextView = itemView.findViewById(R.id.txtnrooperacionventa_cli)
    private var estado: TextView = itemView.findViewById(R.id.txtdocumentoventa_cli)
    private var Sincronizar: FloatingActionButton = itemView.findViewById(R.id.fabSincronizar)
    private var idventa = 0

    init {

        Sincronizar.setOnClickListener {
            listener.onClick(adapterPosition, "Sincronizar")
        }
    }

    fun bind(obj: Empleado) {
        var sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

        if (obj.PER_EstadoSincronizacion == "NO SINCRONIZADO") {
            estado.setTextColor(Color.RED)
            Sincronizar.isVisible= true
            //documento4.setTextColor(Color.RED)
            //total2.setTextColor(Color.BLACK)
        } else {
            estado.setTextColor(Color.BLUE)
            Sincronizar.isVisible= false

        }

        idventa = obj.PER_Codigo
        codigoqr.text = "Código QR: ${obj.PER_CodigoQR}"
        tiponrodoc.text = "TIPO y Nº DOC.: ${obj.TD_Codigo} - ${obj.PER_NroDoc}"
        empleado.text = "EMPLEADO: ${obj.PER_Nombres} ${obj.PER_Paterno} ${obj.PER_Materno} "
        origen.text = "ORIGEN: ${obj.PER_Origen}"
        codigointerno.text = "CODIGO DE NUBE: ${obj.PER_CodigoNube}"
        estado.text = "ESTADO: ${obj.PER_EstadoSincronizacion}" //verde sincronizado  rojo no sincronizado

    }

}