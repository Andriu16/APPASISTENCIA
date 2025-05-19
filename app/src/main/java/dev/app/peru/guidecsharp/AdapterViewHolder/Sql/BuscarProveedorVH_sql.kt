package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Persona_sql
import dev.app.peru.guidecsharp.R

class BuscarProveedorVH_sql(inflater: LayoutInflater, parent: ViewGroup, listener: BuscarProveedorAD_sql.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_proveedor_guia, parent, false)) {

    var lblNombreCliente: TextView = itemView.findViewById(R.id.lblNombreCliente)

    init {
        itemView.setOnClickListener {
            listener.onClick(adapterPosition, "Item_click")
        }
    }

    fun bind(obj: Persona_sql) {
        lblNombreCliente.text = "${obj.NroDoc} - ${obj.ApellidosNombres}"
    }
}