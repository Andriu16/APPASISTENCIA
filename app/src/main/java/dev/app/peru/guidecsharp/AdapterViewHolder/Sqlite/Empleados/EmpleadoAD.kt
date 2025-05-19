package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Empleados

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado

class EmpleadoAD(private val lista: ArrayList<Empleado>, private val listener: MyClickListener): RecyclerView.Adapter<EmpleadoVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoVH {
        val inflater = LayoutInflater.from(parent.context)
        return EmpleadoVH(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: EmpleadoVH, position: Int) {
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