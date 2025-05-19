package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.DetalleTareo_sql
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Tareo_sql

class DetalleTareoAD_sql(val lista: ArrayList<DetalleTareo_sql>, private val listener: MyClickListener): RecyclerView.Adapter<DetalleTareoVH_sql>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleTareoVH_sql {
        val inflater = LayoutInflater.from(parent.context)
        return DetalleTareoVH_sql(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: DetalleTareoVH_sql, position: Int) {
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