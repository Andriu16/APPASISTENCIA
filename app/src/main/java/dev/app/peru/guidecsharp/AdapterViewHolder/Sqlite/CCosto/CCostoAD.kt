//package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.CCosto
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.CCosto
//
//
//class CCostoAD (private val lista: ArrayList<CCosto>, private val listener: CCostoAD.MyClickListener): RecyclerView.Adapter<CCostoVH>()  {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CCostoVH {
//        val inflater = LayoutInflater.from(parent.context)
//        return CCostoVH(inflater, parent, listener)
//    }
//
//    override fun onBindViewHolder(holder: CCostoVH, position: Int) {
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