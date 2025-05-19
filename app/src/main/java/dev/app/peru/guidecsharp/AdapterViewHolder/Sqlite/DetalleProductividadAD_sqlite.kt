package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad

class DetalleProductividadAD_sqlite(val lista: ArrayList<TareoLaboresEmpleado_Productividad>, private val listener: MyClickListener): RecyclerView.Adapter<DetalleProductividadVH_sqlite>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleProductividadVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)
        return DetalleProductividadVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: DetalleProductividadVH_sqlite, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    interface  MyClickListener{
        fun onClick2(position: Int, tipo:String)
    }
}