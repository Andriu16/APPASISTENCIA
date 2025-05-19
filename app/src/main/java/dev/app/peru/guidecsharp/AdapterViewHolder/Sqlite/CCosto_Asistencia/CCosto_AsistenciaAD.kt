//package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.CCosto_Asistencia
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.CCosto_Asitencia
//import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Asistencias.AsistenciaVH
//
//class CCosto_AsistenciaAD(private val lista: ArrayList<CCosto_Asitencia>, private val listener: CCosto_AsistenciaAD.MyClickListener): RecyclerView.Adapter<CCosto_AsistenciaVH>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CCosto_AsistenciaVH {
//        val inflater = LayoutInflater.from(parent.context)
//        return CCosto_AsistenciaVH(inflater, parent, listener)
//    }
//
//    override fun onBindViewHolder(holder: CCosto_AsistenciaVH, position: Int) {
//        val obj = lista[position]
//        holder.bind(obj)
//    }
//
//    override fun getItemCount(): Int {
//        return lista.size
//    }
//    interface  MyClickListener{
//        fun onClick(position: Int, tipo:String)
//    }
//}