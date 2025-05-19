package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Persona_sql

class BuscarProveedorAD_sql(val lista: ArrayList<Persona_sql>, private val listener: MyClickListener): RecyclerView.Adapter<BuscarProveedorVH_sql>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuscarProveedorVH_sql {
        val inflater = LayoutInflater.from(parent.context)
        return BuscarProveedorVH_sql(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: BuscarProveedorVH_sql, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    interface  MyClickListener{
        fun onClick(position: Int, tipo:String)
    }
}