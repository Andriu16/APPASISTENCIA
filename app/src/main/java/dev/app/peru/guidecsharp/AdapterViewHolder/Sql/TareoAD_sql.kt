package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Tareo_sql

class TareoAD_sql(val lista: ArrayList<Tareo_sql>, private val listener: MyClickListener): RecyclerView.Adapter<TareoVH_sql>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareoVH_sql {
        val inflater = LayoutInflater.from(parent.context)
        return TareoVH_sql(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: TareoVH_sql, position: Int) {
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