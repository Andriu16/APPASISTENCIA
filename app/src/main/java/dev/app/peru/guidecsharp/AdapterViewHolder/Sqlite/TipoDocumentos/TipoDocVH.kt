package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TipoDocumentos


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TipoDocumento
import dev.app.peru.guidecsharp.R
import java.text.SimpleDateFormat
import java.util.*

class TipoDocVH(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_tipodoc_sqlite, parent, false)) {

    var codigo: TextView = itemView.findViewById(R.id.txtcodigodocc)
    private var descripcion: TextView = itemView.findViewById(R.id.txtdescripciondocc)

    fun bind(obj: TipoDocumento) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

        codigo.text = "Codigo.: ${obj.TD_Codigo}"
        descripcion.text = "Descripcion: ${obj.TD_Descripcion}"
    }
}


