package dev.app.peru.guidecsharp.Vista.ui.ver_control_puerta // Asegúrate que el paquete sea correcto

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton // Importar ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.RegistroPuertaData
import dev.app.peru.guidecsharp.R
import java.text.SimpleDateFormat // Importar para formateo de fecha
import java.util.Locale // Importar para formateo de fecha

// Interfaz para comunicar acciones al Fragment
interface OnRegistroActionListener {
    fun onEditarClick(registro: RegistroPuertaData, position: Int)
    fun onEliminarClick(registro: RegistroPuertaData, position: Int)
    fun onVerDetalleClick(registro: RegistroPuertaData, position: Int)
}

class AsistenciaAdapter(
    private var listaAsistencias: List<RegistroPuertaData>,
    // Añadir el listener como parámetro del constructor
    private val listener: OnRegistroActionListener
) : RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder>() {

    // Formateadores para mostrar fecha/hora (definir una vez)
    private val formatoEntradaSql = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatoSalidaDisplay = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()) // Formato más corto

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaViewHolder {
        // Asegúrate que R.layout.fila_ver_control_puerta es el nombre correcto de tu layout de item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_ver_control_puerta, parent, false)
        return AsistenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsistenciaViewHolder, position: Int) {
        val asistencia = listaAsistencias[position]
        holder.bind(asistencia, listener, position) // Pasar listener y posición al bind
    }

    override fun getItemCount(): Int = listaAsistencias.size

    fun actualizarLista(nuevaLista: List<RegistroPuertaData>) {
        listaAsistencias = nuevaLista
        notifyDataSetChanged() // O usar DiffUtil para mejor rendimiento
    }

    // --- ViewHolder interno ---
    class AsistenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a las vistas (TextViews)
        private val txtIdRegistro: TextView = itemView.findViewById(R.id.txtnroventa_cli) // Mostrar ID aquí
        private val txtFecha: TextView = itemView.findViewById(R.id.txtfechaventa_cli)
        private val txtNombreCompleto: TextView = itemView.findViewById(R.id.txtnombre_cli)
        private val txtapellido: TextView = itemView.findViewById(R.id.txtapellido_cli) // Mostrar DNI aquí
        private val txtArea: TextView = itemView.findViewById(R.id.txtarea_cli)
        private val txtPlaca: TextView = itemView.findViewById(R.id.txtplaca_cli)
        private val txtMotivo: TextView = itemView.findViewById(R.id.txtmotivo_cli)

        // Referencias a los ImageButtons
        private val btnVerDetalle: ImageButton = itemView.findViewById(R.id.btn_ver_detalle)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        // Formateadores (pueden ser estáticos o pasados)
        private val formatoEntradaSql = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val formatoSalidaDisplay = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault())

        // Función bind ahora recibe el listener y la posición
        fun bind(
            asistencia: RegistroPuertaData,
            listener: OnRegistroActionListener,
            position: Int
        ) {
            // Asignar datos a los TextViews
            txtIdRegistro.text = "ID: ${asistencia.dni}" // Mostrar el ID único
            txtFecha.text = "FECHA REG: ${formatarFechaHoraParaDisplay(asistencia.fechaSistema)}" // Formatear fecha
            txtNombreCompleto.text = "NOMBRE: ${asistencia.nombresApellidos ?: "N/A"}"
            txtapellido.text = "APELLIDOS: ${obtenerApellidos(asistencia.nombresApellidos ?: "N/A")}"
            txtArea.text = "ÁREA: ${asistencia.area ?: "N/A"}"
            txtPlaca.text = "PLACA: ${asistencia.placaVehiculo ?: "---"}"
            txtMotivo.text = "MOTIVO: ${asistencia.motivoIngreso ?: "N/A"}"

            btnVerDetalle.setOnClickListener {
                listener.onVerDetalleClick(asistencia, position)
            }
            // Configurar OnClickListeners para los botones
            btnEditar.setOnClickListener {
                listener.onEditarClick(asistencia, position)
            }

            btnEliminar.setOnClickListener {
                listener.onEliminarClick(asistencia, position)
            }
        }

        // Función Auxiliar para Formatear Fecha/Hora
        private fun formatarFechaHoraParaDisplay(fechaHoraSql: String?): String {
            if (fechaHoraSql.isNullOrBlank()) return "N/A"
            return try {
                val fechaParseada = formatoEntradaSql.parse(fechaHoraSql)
                if (fechaParseada != null) formatoSalidaDisplay.format(fechaParseada) else fechaHoraSql
            } catch (e: Exception) {
                Log.w("AsistenciaVHFormat", "Error formateando fecha: $fechaHoraSql", e)
                fechaHoraSql
            }
        }

        private fun obtenerApellidos(nombreCompleto: String?): String {
            if (nombreCompleto.isNullOrBlank()) return "N/A"
            // Separar por espacios y eliminar partes vacías (si hay doble espacio)
            val partes = nombreCompleto.trim().split(' ').filter { it.isNotBlank() }

            return when {
                partes.size >= 3 -> partes.takeLast(2).joinToString(" ") // Toma las últimas 2
                partes.size == 2 -> partes.last() // Toma la última
                else -> "N/A" // No hay suficientes partes para determinar apellido
            }
        }
    }

} // Fin AsistenciaAdapter