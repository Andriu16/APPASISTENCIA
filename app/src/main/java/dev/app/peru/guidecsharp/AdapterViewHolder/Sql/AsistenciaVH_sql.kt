package dev.app.peru.guidecsharp.AdapterViewHolder.Sql

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSql.Modelo.AsistenciaPuerta_sql
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Sucursal
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import net.sourceforge.jtds.jdbc.DateTime
import java.text.SimpleDateFormat
import java.util.*

class AsistenciaVH_sql(inflater: LayoutInflater, parent: ViewGroup, listener: AsistenciaAD_sql.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_asistencias_diaria, parent, false)),
    View.OnCreateContextMenuListener {

    var txtCodigoQr: TextView = itemView.findViewById(R.id.txtCodigoQr)
    var txtEmpleado: TextView = itemView.findViewById(R.id.txtEmpleado)
    var txtHoraEntrada2: TextView = itemView.findViewById(R.id.txtHoraEntrada2)
    var txtHoraSalida2: TextView = itemView.findViewById(R.id.txtHoraSalida2)
    var txtSede: TextView = itemView.findViewById(R.id.txtSede)
    var txtPuerta: TextView = itemView.findViewById(R.id.txtPuerta)
    var txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
    var Sincronizar: FloatingActionButton = itemView.findViewById(R.id.fabSincronizarAsistencia)

    var _CodigoGeneral : Int = -1

    init {
        Sincronizar.setOnClickListener {
            listener.onClick(adapterPosition, "Sincronizar")
        }

        //itemView.setOnCreateContextMenuListener(this)
    }

    fun bind(obj: AsistenciaPuerta_sql) {
        var AP_HoraSalida_Mostrar = ""

        if (obj.AP_HoraSalida != ""){
            AP_HoraSalida_Mostrar = FunGeneral.obtenerFechaHora_PorFormato_Nube(obj.AP_HoraSalida, "dd/MM/YYYY hh:mm:ss a")
        }


        if (obj.AP_EstadoSincronizacion == "SINCRONIZADO 1/2") {
            txtEstado.setTextColor(Color.RED)

            if (AP_HoraSalida_Mostrar != ""){
                Sincronizar.isVisible= true
            }else{
                Sincronizar.isVisible= false
            }
        } else {
            txtEstado.setTextColor(Color.BLUE)
            Sincronizar.isVisible= false
        }

        Sincronizar.isVisible= false

        txtCodigoQr.text = "Código QR: ${obj.CodigoQR}"
        txtEmpleado.text = "${obj.Empleado}"

        var AP_HoraEntrada_Mostrar = FunGeneral.obtenerFechaHora_PorFormato_Nube(obj.AP_HoraEntrada, "dd/MM/YYYY hh:mm:ss a")
        txtHoraEntrada2.text = AP_HoraEntrada_Mostrar
        txtHoraEntrada2.setTextColor(Color.BLUE)

        if (obj.AP_HoraSalida == ""){
            txtHoraSalida2.text = "SIGUE EN PLANTA"
            txtHoraSalida2.setTextColor(Color.RED)
        }
        else{
            txtHoraSalida2.text = AP_HoraSalida_Mostrar
            txtHoraSalida2.setTextColor(Color.BLUE)
        }

        txtSede.text = "SEDE: ${obj.Sede}"
        txtPuerta.text = "PUERTA: ${obj.Puerta}"
        txtEstado.text = "ESTADO: ${obj.AP_EstadoSincronizacion}"

        _CodigoGeneral = obj.AP_Codigo
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        DatosTareo.AP_Codigo = _CodigoGeneral
        menu?.add(Menu.NONE, R.id.action_editar_ingreso, 0, "Editar Hora Ingreso")
        menu?.add(Menu.NONE, R.id.action_editar_salida, 0, "Editar Hora Salida")
        menu?.add(Menu.NONE, R.id.action_eliminar_asistencia, 0, "Eliminar Asistencia")
    }
}