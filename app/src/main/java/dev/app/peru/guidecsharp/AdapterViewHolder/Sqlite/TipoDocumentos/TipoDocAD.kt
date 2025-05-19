package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TipoDocumentos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TipoDocumento

class TipoDocAD(private val lista: ArrayList<TipoDocumento>): RecyclerView.Adapter<TipoDocVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoDocVH {
        val inflater = LayoutInflater.from(parent.context)
        return TipoDocVH(inflater, parent)
    }

    override fun onBindViewHolder(holder: TipoDocVH, position: Int) {
        val obj = lista[position]
        holder.bind(obj)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}
