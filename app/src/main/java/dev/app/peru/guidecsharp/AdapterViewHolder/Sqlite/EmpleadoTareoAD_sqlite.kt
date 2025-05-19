package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta_Cap
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado

class EmpleadoTareoAD_sqlite(val lista: ArrayList<Empleado>, private val listener: MyClickListener): RecyclerView.Adapter<EmpleadoTareoVH_sqlite>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoTareoVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)
        return EmpleadoTareoVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: EmpleadoTareoVH_sqlite, position: Int) {
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