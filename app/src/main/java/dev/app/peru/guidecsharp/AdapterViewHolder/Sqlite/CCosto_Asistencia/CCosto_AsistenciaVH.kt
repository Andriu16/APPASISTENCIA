//package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.CCosto_Asistencia
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaSemanal
//import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.CCosto_Asitencia
//import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Asistencias.AsistenciaAD
//import dev.app.peru.guidecsharp.R
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//class CCosto_AsistenciaVH(inflater: LayoutInflater, parent: ViewGroup, listener: CCosto_AsistenciaAD.MyClickListener) :
//    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_actividad_costo, parent, false))
//{
//
//    private var codigoqr: TextView = itemView.findViewById(R.id.txtnroventa_cli)
//    private var empleado: TextView = itemView.findViewById(R.id.txtclienteventa_cli)
//    private var origen: TextView = itemView.findViewById(R.id.txtmodalidadventa_cli)
//    private var tiponrodoc: TextView = itemView.findViewById(R.id.txtfechaventa_cli)
//    private var codigointerno: TextView = itemView.findViewById(R.id.txtnrooperacionventa_cli)
//    private var estado: TextView = itemView.findViewById(R.id.txtdocumentoventa_cli)
//    private var Sincronizar: FloatingActionButton = itemView.findViewById(R.id.fabSincronizar)
//    private var idventa = 0
//
//    init {
//
//        Sincronizar.setOnClickListener {
//            listener.onClick(adapterPosition, "Sincronizar")
//        }
//    }
//    fun formatDate(date: String): String {
//        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        val date = inputFormat.parse(date)
//        return outputFormat.format(date)
//    }
//
//
//
////    fun bind(obj: CCosto_Asitencia) {
////
////        idventa = obj.PER_Codigo
////        codigoqr.text = "Nº Doc.: $obj.ASIST_DIA_Cod"
////        tiponrodoc.text = "Fecha: ${formatDate(obj.ASIST_DIA_Fecha)}"
////        empleado.text = "EMPLEADO:  "
////        origen.text = "HORA INGRESO: ${obj.ASIST_HoraEntrada}"
////        codigointerno.text = "HORA SALIDA: ${obj.ASIST_HoraSalida}"
////        estado.text ="ESTADO: ${obj.ASIST_Estado}" //verde sincronizado  rojo no sincronizado
////
////    }
//
//
//}