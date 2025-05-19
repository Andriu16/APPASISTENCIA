package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class AsistenciaAD_sqlite(val lista: ArrayList<Any>, private val listener: MyClickListener)
    : RecyclerView.Adapter<AsistenciaVH_sqlite>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)

        return AsistenciaVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: AsistenciaVH_sqlite, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    interface MyClickListener {
        fun onClick(position: Int, tipo: String)

    }
}