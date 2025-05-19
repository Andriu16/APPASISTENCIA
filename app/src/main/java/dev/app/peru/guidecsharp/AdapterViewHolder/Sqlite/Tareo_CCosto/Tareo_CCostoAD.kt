package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Tareo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.Tareo_CCostoDAO

import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo_CCosto

class Tareo_CCostoAD (private val lista: ArrayList<Tareo_CCosto>,  private val listener: MyLongClickListener): RecyclerView.Adapter<Tareo_CCostoVH>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Tareo_CCostoVH {
        val inflater = LayoutInflater.from(parent.context)
        return Tareo_CCostoVH(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: Tareo_CCostoVH, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
//    interface  MyClickListener{
//        fun onClick(position: Int, tipo:String)
//    }
    interface  MyLongClickListener{
        fun onLongClick(position: Int, tipo:String)
    }
}
