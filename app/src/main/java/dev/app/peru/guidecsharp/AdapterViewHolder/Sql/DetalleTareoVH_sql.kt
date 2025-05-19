package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSql.Modelo.DetalleTareo_sql
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Tareo_sql
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R

class DetalleTareoVH_sql(inflater: LayoutInflater, parent: ViewGroup, listener: DetalleTareoAD_sql.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_detalle_tareo, parent, false)) {

    var txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
    var txtActividad: TextView = itemView.findViewById(R.id.txtActividad)
    var txtLabor: TextView = itemView.findViewById(R.id.txtLabor)
    var txtConsumidor: TextView = itemView.findViewById(R.id.txtConsumidor)
    var txtHoras: TextView = itemView.findViewById(R.id.txtHoras)

    var btnEditar: LinearLayout = itemView.findViewById(R.id.btnEditar)
    var btnEliminar: LinearLayout = itemView.findViewById(R.id.btnEliminar)

    var _CodigoGeneral = -1

    init {
        btnEditar.setOnClickListener {
            listener.onClick(adapterPosition, "Editar")
        }
        btnEliminar.setOnClickListener {
            listener.onClick(adapterPosition, "Eliminar")
        }
    }

    fun bind(obj: DetalleTareo_sql) {
        txtDescripcion.text = obj.ApellidosNombres
        txtActividad.text = "ACTIVIDAD: ${obj.Actividad}"
        txtLabor.text = "LABOR: ${obj.Labor}"
        txtConsumidor.text = "CONSUMIDOR: ${obj.Consumidor}"
        txtHoras.text = "HORAS: ${obj.TotalHoras} hrs"
    }
}