package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Tareo_sql
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R

class TareoVH_sql(inflater: LayoutInflater, parent: ViewGroup, listener: TareoAD_sql.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_tareo_sqlite, parent, false)) {

    var txtTituloTareo: TextView = itemView.findViewById(R.id.txtTituloTareo)
    var txtCultivoFila: TextView = itemView.findViewById(R.id.txtCultivoFila)
    var txtIncidenciaFila: TextView = itemView.findViewById(R.id.txtIncidenciaFila)
    var txtParronLoteCC: TextView = itemView.findViewById(R.id.txtParronLoteCC)
    var txtLabor: TextView = itemView.findViewById(R.id.txtLabor)
    var txtTipoTareo: TextView = itemView.findViewById(R.id.txtTipoTareo)
    var txtTipoTrab2: TextView = itemView.findViewById(R.id.txtTipoTrab2)
    var txtEstadoTareo: TextView = itemView.findViewById(R.id.txtEstadoTareo)
    var fabSincronizarTareo: FloatingActionButton = itemView.findViewById(R.id.fabSincronizarTareo)

    var _CodigoGeneral = -1

    init {
        fabSincronizarTareo.setOnClickListener {
            listener.onClick(adapterPosition, "Sincronizar")
        }
    }

    fun bind(obj: Tareo_sql) {

        var _solofecha = FunGeneral.obtenerFecha_PorFormato_Nube(obj.TA_Fecha, "dd/MM/YYYY")

        txtTituloTareo.text = ".:: ${obj._Packing}"
        txtCultivoFila.text = "CULTIVO: ${obj._Cultivo}"
        txtIncidenciaFila.text = "INCIDENCIA: ${obj._Incidencia}"

        if (obj._Incidencia == "DIRECTA"){
            txtParronLoteCC.text = "PARRÓN/LOTE: ${obj._CentroCosto}"
        }
        else{
            txtParronLoteCC.text = "CENTRO COSTO: ${obj._CentroCosto}"
        }

        txtLabor.text = "LABOR: ${obj._Labor}"
        txtTipoTareo.text = "${obj._TipoTareo}"

        if (obj._TipoTareo == "JORNAL"){
            txtTipoTareo.setTextColor(Color.BLUE)
        }
        else if (obj._TipoTareo == "TAREA"){
            txtTipoTareo.setTextColor(Color.GREEN)
        }
        else {
            txtTipoTareo.setTextColor(Color.MAGENTA)
        }

        txtTipoTrab2.text = "${obj._Trabajadores}"

        txtEstadoTareo.text = "ESTADO: OK"
        txtEstadoTareo.setTextColor(Color.GREEN)

        fabSincronizarTareo.isVisible = false
    }
}