package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.graphics.Color
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSql.Modelo.AsistenciaOnlineDTO.AsistenciaOnlineDTO
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta

import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R
import java.util.*

class AsistenciaVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, private val listener: AsistenciaAD_sqlite.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_asistencias_diaria, parent, false)),
    View.OnCreateContextMenuListener {


    private var txtCodigoQr: TextView = itemView.findViewById(R.id.txtCodigoQr)
    private var txtEmpleado: TextView = itemView.findViewById(R.id.txtEmpleado)
    private var txtHoraEntrada2: TextView = itemView.findViewById(R.id.txtHoraEntrada2)
    private var txtHoraSalida2: TextView = itemView.findViewById(R.id.txtHoraSalida2)
    private var txtSede: TextView = itemView.findViewById(R.id.txtSede)
    private var txtPuerta: TextView = itemView.findViewById(R.id.txtPuerta)
    private var txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
    private var fabSincronizarAsistencia: FloatingActionButton = itemView.findViewById(R.id.fabSincronizarAsistencia)


    private var currentData: Any? = null
    private var isLocalData: Boolean = false

    init {

        fabSincronizarAsistencia.setOnClickListener {
            if (isLocalData ) {

                listener.onClick(adapterPosition, "Sincronizar")
            }
        }


        itemView.setOnCreateContextMenuListener(this)
    }


    fun bind(data: Any) {
        currentData = data
        isLocalData = data is AsistenciaPuerta


        fabSincronizarAsistencia.isVisible = false
        txtEstado.setTextColor(Color.DKGRAY)
        txtHoraEntrada2.setTextColor(Color.DKGRAY)
        txtHoraSalida2.setTextColor(Color.DKGRAY)


        when (data) {
            is AsistenciaPuerta -> {

                bindLocalData(data)
            }
            is AsistenciaOnlineDTO -> {

                bindOnlineData(data)
            }
            else -> {

                Log.w("AsistenciaVH", "Tipo de dato inesperado: ${data::class.java.name}")

                clearViews()
            }
        }
    }

    private fun bindLocalData(localData: AsistenciaPuerta) {
        val empleadoDAO = ConexionSqlite.getInstancia(itemView.context).empleadoDAO()
        val sucursalDAO = ConexionSqlite.getInstancia(itemView.context).sucursalDAO()

        val empleado = empleadoDAO.PA_ObtenerEmpleado_PorCodigo(localData.PER_Codigo)
        val sede = empleado?.let { sucursalDAO.PA_ObtenerSucursal_PorCodigo(it.SUC_Codigo.toLong()) }

        txtEmpleado.text = empleado?.nombres_apellidos() ?: "Empleado ID: ${localData.PER_Codigo}"
        txtCodigoQr.text = "QR : ${empleado?.PER_CodigoQR ?: "--"}"
        txtSede.text = "SEDE : ${sede?.SUC_Descripcion ?: "--"}"
        txtPuerta.text = ""

        txtHoraEntrada2.text = localData.AP_HoraEntrada_Mostrar.takeIf { it.isNotEmpty() } ?: "--"
        if (localData.AP_HoraSalida.isEmpty()) {
            txtHoraSalida2.text = "SIGUE EN CAMPO"
            txtHoraSalida2.setTextColor(Color.RED)
        } else {
            txtHoraSalida2.text = localData.AP_HoraSalida_Mostrar
            txtHoraSalida2.setTextColor(Color.BLUE)
        }
        txtHoraEntrada2.setTextColor(Color.BLUE)


        val estadoSync = localData.AP_EstadoSincronizacion
        txtEstado.text = "ESTADO: $estadoSync"
        if (estadoSync == "NO SINCRONIZADO" || estadoSync == "SINCRONIZADO 1/2") {
            txtEstado.setTextColor(Color.RED)

            fabSincronizarAsistencia.isVisible = true
        } else {
            txtEstado.setTextColor(Color.BLUE)
            fabSincronizarAsistencia.isVisible = false
        }
    }

    private fun bindOnlineData(onlineData: AsistenciaOnlineDTO) {
        txtEmpleado.text = onlineData.Empleado ?: "Servidor ID: ${onlineData.PER_Codigo}"
        txtCodigoQr.text = "QR: ${onlineData.CodigoQR ?: "--"}"
        txtSede.text = "SEDE: ${onlineData.Sede ?: "--"}"
        txtPuerta.text = "PUERTA: ${onlineData.Puerta ?: "--"}"

        val entradaMostrar = onlineData.AP_HoraEntrada?.let { FunGeneral.obtenerFechaHora_PorFormato_Nube(it, "dd/MM/YYYY hh:mm:ss a") } ?: "--"
        txtHoraEntrada2.text = entradaMostrar
        txtHoraEntrada2.setTextColor(Color.BLUE)

        if (onlineData.AP_HoraSalida.isNullOrEmpty()) {
            txtHoraSalida2.text = "SIGUE EN PLANTA"
            txtHoraSalida2.setTextColor(Color.RED)
        } else {
            val salidaMostrar = FunGeneral.obtenerFechaHora_PorFormato_Nube(onlineData.AP_HoraSalida!!, "dd/MM/YYYY hh:mm:ss a")
            txtHoraSalida2.text = salidaMostrar
            txtHoraSalida2.setTextColor(Color.BLUE)
        }



        txtEstado.text = "ESTADO: ${onlineData.AP_EstadoSincronizacion ?: "--"}"
        txtEstado.setTextColor(Color.BLUE)


        fabSincronizarAsistencia.isVisible = false
    }

    private fun clearViews() {
        txtCodigoQr.text = ""
        txtEmpleado.text = "Error de datos"
        txtHoraEntrada2.text = ""
        txtHoraSalida2.text = ""
        txtSede.text = ""
        txtPuerta.text = ""
        txtEstado.text = ""
        fabSincronizarAsistencia.isVisible = false
    }



    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (isLocalData && currentData is AsistenciaPuerta) {

            val localAsistencia = currentData as AsistenciaPuerta
            DatosTareo.AP_Codigo = localAsistencia.AP_Codigo



            menu?.setHeaderTitle("Opciones ")
            menu?.add(Menu.NONE, R.id.action_editar_ingreso, Menu.NONE, "Editar Ingreso ")
            menu?.add(Menu.NONE, R.id.action_editar_salida, Menu.NONE, "Editar Salida ")
            menu?.add(Menu.NONE, R.id.action_eliminar_asistencia, Menu.NONE, "Eliminar ")

        }

    }
}