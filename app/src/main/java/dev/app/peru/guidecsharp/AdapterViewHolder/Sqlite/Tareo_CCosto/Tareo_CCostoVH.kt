package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Tareo


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo_CCosto
import dev.app.peru.guidecsharp.R


class Tareo_CCostoVH(inflater: LayoutInflater, parent: ViewGroup, listener:Tareo_CCostoAD.MyLongClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_tareo_sqlite, parent, false)) {
    private var codigo: TextView = itemView.findViewById(R.id.txtnroventa_cli)
    private var tareo: TextView = itemView.findViewById(R.id.txtfechaventa_cli)
    private var cantidad: TextView = itemView.findViewById(R.id.txtclienteventa_cli)

    private var idventa = 0


    init {

//        cvTareo.setOnLongClickListener {
//            listener.onLongClick(adapterPosition, "Sincronizar")
//            true
//        }

    }

    fun bind(obj: Tareo_CCosto) {
        idventa = obj.TA_Codigo
        codigo.text = "Tipo Doc.: ${obj.CC_Codigo}"
        tareo.text = "Nº Doc.: ${obj.TA_Codigo}"
        cantidad.text = "Nombres: ${obj.CC_Codigo}"

    }
}



