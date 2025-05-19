package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.AsistenciaPuerta_sql
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta

class AsistenciaAD_sql(val lista: ArrayList<AsistenciaPuerta_sql>, private val listener: MyClickListener): RecyclerView.Adapter<AsistenciaVH_sql>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaVH_sql {
        val inflater = LayoutInflater.from(parent.context)
        return AsistenciaVH_sql(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: AsistenciaVH_sql, position: Int) {
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