package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado

class DetalleTareoAD_sqlite(val lista: ArrayList<TareoLaboresEmpleado>, private val listener: MyClickListener): RecyclerView.Adapter<DetalleTareoVH_sqlite>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleTareoVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)
        return DetalleTareoVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: DetalleTareoVH_sqlite, position: Int) {
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