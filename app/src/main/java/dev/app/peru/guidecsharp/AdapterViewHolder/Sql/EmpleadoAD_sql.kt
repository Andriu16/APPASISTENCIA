package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Empleado_sql

class EmpleadoAD_sql(val lista: ArrayList<Empleado_sql>): RecyclerView.Adapter<EmpleadoVH_sql>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoVH_sql {
        val inflater = LayoutInflater.from(parent.context)
        return EmpleadoVH_sql(inflater, parent)
    }

    override fun onBindViewHolder(holder: EmpleadoVH_sql, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}