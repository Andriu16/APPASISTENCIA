package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta_Cap

class AsistenciaCapAD_sqlite(val lista: ArrayList<AsistenciaPuerta_Cap>, private val listener: MyClickListener): RecyclerView.Adapter<AsistenciaCapVH_sqlite>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaCapVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)
        return AsistenciaCapVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: AsistenciaCapVH_sqlite, position: Int) {
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