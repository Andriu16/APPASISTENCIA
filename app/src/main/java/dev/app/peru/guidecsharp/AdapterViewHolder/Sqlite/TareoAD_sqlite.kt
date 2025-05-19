package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo

class TareoAD_sqlite(val lista: ArrayList<Tareo>, private val listener: MyClickListener): RecyclerView.Adapter<TareoVH_sqlite>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareoVH_sqlite {
        val inflater = LayoutInflater.from(parent.context)
        return TareoVH_sqlite(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: TareoVH_sqlite, position: Int) {
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