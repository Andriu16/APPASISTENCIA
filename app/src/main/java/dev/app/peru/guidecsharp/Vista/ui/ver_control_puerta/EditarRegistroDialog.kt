package dev.app.peru.guidecsharp.Vista.ui.ver_control_puerta // O donde corresponda

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
// import android.widget.RadioButton // Ya no se usa
// import android.widget.RadioGroup // Ya no se usa
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSqlImagenes
import dev.app.peru.guidecsharp.AccesoSql.Modelo.RegistroPuertaData
import dev.app.peru.guidecsharp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.util.DisplayMetrics
import android.view.Gravity

import android.view.Window
import android.view.WindowManager
import android.widget.TextView

class EditarRegistroDialog : DialogFragment() {

    // --- Vistas del diálogo (Sin Tipo Evento) ---
    private lateinit var btnEditarFecha: Button
    private lateinit var btnEditarHora: Button
    // private lateinit var rgEditarTipoRegistro: RadioGroup // ELIMINADO
    // private lateinit var rbEditarIngreso: RadioButton // ELIMINADO
    // private lateinit var rbEditarSalida: RadioButton // ELIMINADO
    private lateinit var etEditarDni: EditText
    private lateinit var etEditarNombresApellidos: EditText
    private lateinit var etEditarArea: EditText
    private lateinit var etEditarMotivo: EditText
    private lateinit var etEditarPlaca: EditText
    private lateinit var etEditarObservacion: EditText
    private lateinit var btnEditarCancelar: Button
    private lateinit var btnEditarGuardar: Button
    private lateinit var pbEditarProgreso: ProgressBar

    // --- Datos y Formatos ---
    private var registroOriginal: RegistroPuertaData? = null
    private var registroPosition: Int = -1
    private var isViewOnlyMode: Boolean = false
    private val sqlDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val calendarioSeleccionado = Calendar.getInstance()

    // --- Listener para comunicar ---
    interface EditarRegistroListener {
        fun onRegistroEditado(registroEditado: RegistroPuertaData, position: Int)
    }
    private var listener: EditarRegistroListener? = null

    // --- Companion Object (Sin Tipo Evento) ---
    companion object {
        private const val ARG_REGISTRO_CODIGO = "registro_codigo"
        private const val ARG_REGISTRO_FECHA_REG = "registro_fecha_reg"
        // private const val ARG_REGISTRO_TIPO = "registro_tipo" // ELIMINADO
        private const val ARG_REGISTRO_DNI = "registro_dni"
        private const val ARG_REGISTRO_NOMBRES = "registro_nombres"
        private const val ARG_REGISTRO_AREA = "registro_area"
        private const val ARG_REGISTRO_MOTIVO = "registro_motivo"
        private const val ARG_REGISTRO_PLACA = "registro_placa"
        private const val ARG_REGISTRO_OBS = "registro_obs"
        private const val ARG_POSITION = "registro_position"
        private const val ARG_IS_VIEW_ONLY = "is_view_only"
        // El tipo evento se lee del registro original si se necesita

        fun newInstance(
            registro: RegistroPuertaData,
            position: Int,
            isViewOnly: Boolean = false // <-- NUEVO PARÁMETRO con valor por defecto false
        ): EditarRegistroDialog {
            val args = Bundle().apply {
                // ... (put de otros argumentos sin cambios) ...
                registro.riCodigo?.let { putInt(ARG_REGISTRO_CODIGO, it) }
                putString(ARG_REGISTRO_FECHA_REG, registro.fechaRegistro)
                putString(ARG_REGISTRO_DNI, registro.dni)
                putString(ARG_REGISTRO_NOMBRES, registro.nombresApellidos)
                putString(ARG_REGISTRO_AREA, registro.area)
                putString(ARG_REGISTRO_MOTIVO, registro.motivoIngreso)
                putString(ARG_REGISTRO_PLACA, registro.placaVehiculo)
                putString(ARG_REGISTRO_OBS, registro.observacion)
                putInt(ARG_POSITION, position)
                putBoolean(ARG_IS_VIEW_ONLY, isViewOnly) // <-- GUARDAR EL MODO
            }
            val fragment = EditarRegistroDialog()
            fragment.arguments = args
            return fragment
        }



        fun newInstance(registro: RegistroPuertaData, position: Int): EditarRegistroDialog {
            val args = Bundle().apply {
                registro.riCodigo?.let { putInt(ARG_REGISTRO_CODIGO, it) }
                putString(ARG_REGISTRO_FECHA_REG, registro.fechaRegistro)
                // No pasamos el tipo evento como argumento separado
                putString(ARG_REGISTRO_DNI, registro.dni)
                putString(ARG_REGISTRO_NOMBRES, registro.nombresApellidos)
                putString(ARG_REGISTRO_AREA, registro.area)
                putString(ARG_REGISTRO_MOTIVO, registro.motivoIngreso)
                putString(ARG_REGISTRO_PLACA, registro.placaVehiculo)
                putString(ARG_REGISTRO_OBS, registro.observacion)
                putInt(ARG_POSITION, position)
            }
            val fragment = EditarRegistroDialog()
            fragment.arguments = args
            return fragment
        }
    }

    // --- onCreate y Listener ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Reconstruir el registro original simplificado
            registroOriginal = RegistroPuertaData(
                riCodigo = it.getInt(ARG_REGISTRO_CODIGO),
                fechaRegistro = it.getString(ARG_REGISTRO_FECHA_REG, ""),
                tipoEvento = "", // Lo obtenemos del mapeo si es necesario, no se pasa directo
                dni = it.getString(ARG_REGISTRO_DNI, ""),
                nombresApellidos = it.getString(ARG_REGISTRO_NOMBRES, ""),
                area = it.getString(ARG_REGISTRO_AREA, ""),
                motivoIngreso = it.getString(ARG_REGISTRO_MOTIVO, ""),
                placaVehiculo = it.getString(ARG_REGISTRO_PLACA),
                observacion = it.getString(ARG_REGISTRO_OBS),
                fechaSistema = "", estado = "", personalAutorizo = null
            )
            // Recuperar el tipo original si lo necesitáramos del mapeo en VerControl...
            // O pasarlo como otro argumento si fuera absolutamente necesario
            registroPosition = it.getInt(ARG_POSITION, -1)
            isViewOnlyMode = it.getBoolean(ARG_IS_VIEW_ONLY, false) // <-- LEER EL MODO
        }

        // Asignar listener
        try {
            listener = parentFragment as? EditarRegistroListener ?: activity as? EditarRegistroListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar EditarRegistroListener")
        }
    }

    // --- onCreateView ---
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_editar_registro, container, false)
        dialog?.setTitle("Editar Registro")
        return view
    }

    // --- onViewCreated ---
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view) // Vincular vistas
        populateViews() // Poblar con datos originales
        setupDialogListeners() // Configurar botones, etc.
        applyViewOnlyModeIfNeeded()
    }

    private fun applyViewOnlyModeIfNeeded() {
        val titleTextView = dialog?.findViewById<TextView>(androidx.appcompat.R.id.alertTitle) // Intenta obtener el título por defecto

        if (isViewOnlyMode) {
            // Cambiar título (opcional - busca un TextView con ID específico si tienes uno en tu layout)
            dialog?.setTitle("Ver Detalles") // O usa tu TextView de título si tienes uno
            // titleTextView?.text = "Ver Detalles" // Si usas el título por defecto

            // Deshabilitar todos los campos de entrada y botones de fecha/hora
            btnEditarFecha.isEnabled = false
            btnEditarHora.isEnabled = false
            etEditarDni.isEnabled = false
            etEditarNombresApellidos.isEnabled = false
            etEditarArea.isEnabled = false
            etEditarMotivo.isEnabled = false
            etEditarPlaca.isEnabled = false
            etEditarObservacion.isEnabled = false

            // Ocultar el botón de guardar
            btnEditarGuardar.visibility = View.GONE

            // Opcional: Cambiar texto del botón Cancelar a "Cerrar"
            btnEditarCancelar.text = "Cerrar"
        } else {
            // Asegurarse que el título sea el de edición si no es modo vista
            dialog?.setTitle("Editar Registro")
            // titleTextView?.text = "Editar Registro"
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                // --- Metodo 1: Establecer ancho como porcentaje de la pantalla ---
                val displayMetrics = DisplayMetrics()
                // Usar requireActivity() para obtener el WindowManager de forma segura
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels

                // Ajusta el 0.90 (90%) según necesites más o menos ancho
                val desiredWidth = (screenWidth * 0.90).toInt()

                // Aplicar el ancho calculado y mantener la altura ajustada al contenido
                window.setLayout(desiredWidth, WindowManager.LayoutParams.WRAP_CONTENT)
                // Mantener el diálogo centrado
                window.setGravity(Gravity.CENTER)

                // Opcional: Si el fondo de tu layout XML (dialog_editar_registro.xml)
                // ya tiene esquinas redondeadas (ej. CardView como raíz), puedes hacer
                // transparente el fondo de la ventana del diálogo para evitar bordes rectos.
                // window.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
    }


    // --- Vinculación de Vistas (Sin Tipo Evento) ---
    private fun bindViews(view: View) {
        btnEditarFecha = view.findViewById(R.id.btnEditarFecha)
        btnEditarHora = view.findViewById(R.id.btnEditarHora)
        // rgEditarTipoRegistro y RadioButtons ELIMINADOS
        etEditarDni = view.findViewById(R.id.etEditarDni)
        etEditarNombresApellidos = view.findViewById(R.id.etEditarNombresApellidos)
        etEditarArea = view.findViewById(R.id.etEditarArea)
        etEditarMotivo = view.findViewById(R.id.etEditarMotivo)
        etEditarPlaca = view.findViewById(R.id.etEditarPlaca)
        etEditarObservacion = view.findViewById(R.id.etEditarObservacion)
        btnEditarCancelar = view.findViewById(R.id.btnEditarCancelar)
        btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar)
        pbEditarProgreso = view.findViewById(R.id.pbEditarProgreso)
    }

    // --- Poblar Vistas (Sin Tipo Evento) ---
    private fun populateViews() {
        registroOriginal?.let { reg ->
            // Parsear y mostrar fecha/hora
            try {
                sqlDateTimeFormat.parse(reg.fechaRegistro)?.also { calendarioSeleccionado.time = it }
            } catch (e: ParseException) {
                Log.e("EditarDialog", "Error parseando fecha original: ${reg.fechaRegistro}", e)
                calendarioSeleccionado.time = Date() // Fallback a ahora
            }
            actualizarBotonesFechaHora()

            // Poblar campos de texto
            // El tipo evento ya no se puebla aquí
            etEditarDni.setText(reg.dni)
            etEditarNombresApellidos.setText(reg.nombresApellidos)
            etEditarArea.setText(reg.area)
            etEditarMotivo.setText(reg.motivoIngreso)
            etEditarPlaca.setText(reg.placaVehiculo ?: "")
            etEditarObservacion.setText(reg.observacion ?: "")
        }
    }

    // --- Listeners del Diálogo (Sin Tipo Evento) ---
    private fun setupDialogListeners() {
        btnEditarFecha.setOnClickListener { mostrarDatePickerDialog() }
        btnEditarHora.setOnClickListener { mostrarTimePickerDialog() }
        btnEditarCancelar.setOnClickListener { dismiss() }
        btnEditarGuardar.setOnClickListener { intentarGuardarCambios() }
    }

    // --- Lógica de Fecha/Hora (Sin Cambios) ---
    private fun actualizarBotonesFechaHora() {
        btnEditarFecha.text = displayDateFormat.format(calendarioSeleccionado.time)
        btnEditarHora.text = displayTimeFormat.format(calendarioSeleccionado.time)
    }
    private fun mostrarDatePickerDialog() {
        val year = calendarioSeleccionado.get(Calendar.YEAR)
        val month = calendarioSeleccionado.get(Calendar.MONTH)
        val day = calendarioSeleccionado.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendarioSeleccionado.set(Calendar.YEAR, y)
            calendarioSeleccionado.set(Calendar.MONTH, m)
            calendarioSeleccionado.set(Calendar.DAY_OF_MONTH, d)
            actualizarBotonesFechaHora()
        }, year, month, day).show()
    }
    private fun mostrarTimePickerDialog() {
        val hour = calendarioSeleccionado.get(Calendar.HOUR_OF_DAY)
        val minute = calendarioSeleccionado.get(Calendar.MINUTE)
        TimePickerDialog(requireContext(), { _, h, m ->
            calendarioSeleccionado.set(Calendar.HOUR_OF_DAY, h)
            calendarioSeleccionado.set(Calendar.MINUTE, m)
            calendarioSeleccionado.set(Calendar.SECOND, 0) // Resetear segundos
            actualizarBotonesFechaHora()
        }, hour, minute, true).show()
    }

    // --- Lógica de Guardado (Sin Tipo Evento) ---
    private fun intentarGuardarCambios() {

        if (isViewOnlyMode) {
            Log.w("EditarDialog", "Intento de guardar en modo solo vista ignorado.")
            return
        }
        if (!validarCampos()) return

        val registroEditado = recolectarDatosEditados() ?: return

        pbEditarProgreso.visibility = View.VISIBLE
        setDialogButtonsEnabled(false) // Deshabilitar botones

        lifecycleScope.launch {
            val exito = actualizarRegistroEnBD(registroEditado)
            pbEditarProgreso.visibility = View.GONE
            setDialogButtonsEnabled(true) // Habilitar botones

            if (exito) {
                Toast.makeText(context, "Registro actualizado", Toast.LENGTH_SHORT).show()
                listener?.onRegistroEditado(registroEditado, registroPosition)
                dismiss()
            } else {
                Toast.makeText(context, "Error al actualizar el registro", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setDialogButtonsEnabled(enabled: Boolean) {
        if (!isViewOnlyMode) {
            btnEditarGuardar.isEnabled = enabled
            btnEditarFecha.isEnabled = enabled
            btnEditarHora.isEnabled = enabled
            etEditarDni.isEnabled = enabled
            etEditarNombresApellidos.isEnabled = enabled
            etEditarArea.isEnabled = enabled
            etEditarMotivo.isEnabled = enabled
            etEditarPlaca.isEnabled = enabled
            etEditarObservacion.isEnabled = enabled
        }
        // El botón Cancelar siempre debe estar habilitado
        btnEditarCancelar.isEnabled = true
        // ... etc ...
    }

    private fun validarCampos(): Boolean {
        if (etEditarDni.text.isNullOrBlank() || etEditarNombresApellidos.text.isNullOrBlank()) {
            Toast.makeText(context, "DNI y Nombres/Apellidos son obligatorios", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // --- Recolectar Datos (Sin Tipo Evento editable) ---
    private fun recolectarDatosEditados(): RegistroPuertaData? {
        val codigoOriginal = registroOriginal?.riCodigo ?: run {
            Log.e("EditarDialog", "Error: RI_Codigo original es null.")
            Toast.makeText(context, "Error interno: ID de registro no encontrado.", Toast.LENGTH_LONG).show()
            return null
        }

        // Obtener el tipo de evento ORIGINAL (no se edita)
        // Necesitaríamos haberlo recuperado en onCreate o tener acceso al objeto completo
        // Vamos a buscarlo en el registro original (aunque lo inicializamos vacío)
        // ESTO ES UN PUNTO DÉBIL: Deberías asegurar que registroOriginal tenga el tipo correcto.
        // La mejor forma es pasarlo desde el Fragment via newInstance o leerlo de la BD aquí.
        // Por ahora, asumiremos que lo leemos de una fuente fiable (idealmente pasada por argumento).
        val tipoEventoOriginal = obtenerTipoEventoOriginal() // Necesitas implementar esta función

        val fechaHoraEditadaStr = sqlDateTimeFormat.format(calendarioSeleccionado.time)

        return RegistroPuertaData(
            riCodigo = codigoOriginal,
            fechaRegistro = fechaHoraEditadaStr,
            tipoEvento = tipoEventoOriginal, // <<< USA EL VALOR ORIGINAL
            dni = etEditarDni.text.toString().trim(),
            nombresApellidos = etEditarNombresApellidos.text.toString().trim(),
            area = etEditarArea.text.toString().trim(),
            motivoIngreso = etEditarMotivo.text.toString().trim(),
            placaVehiculo = etEditarPlaca.text.toString().trim().ifEmpty { null },
            observacion = etEditarObservacion.text.toString().trim().ifEmpty { null },
            // Rellenar otros campos con datos originales si el listener los necesita completos
            fechaSistema = registroOriginal?.fechaSistema ?: "",
            estado = registroOriginal?.estado ?: "",
            personalAutorizo = registroOriginal?.personalAutorizo
        )
    }

    // --- Función placeholder para obtener el tipo original ---
    // --- ¡¡DEBES IMPLEMENTAR ESTO CORRECTAMENTE!! ---
    private fun obtenerTipoEventoOriginal(): String {
        // Intenta obtenerlo de los argumentos si lo añadiste a newInstance
        // return arguments?.getString("ARG_TIPO_EVENTO_ORIGINAL") ?: "Ingreso" // Ejemplo

        // O si VerControlFragment te pasa el objeto completo:
        // return registroOriginalCompleto?.tipoEvento ?: "Ingreso" // Ejemplo

        // Como fallback muy básico (y probablemente incorrecto):
        Log.w("EditarDialog", "Usando tipo de evento 'Ingreso' por defecto. ¡Implementar obtención correcta!")
        return "Ingreso" // ¡¡ESTO ES SOLO UN FALLBACK!!
    }


    // --- Llamada a BD (SP ahora no incluye @RI_TipoEvento como parámetro editable) ---
    private suspend fun actualizarRegistroEnBD(registro: RegistroPuertaData): Boolean {
        Log.d("EditarDialog", "Actualizando registro ID: ${registro.riCodigo}")
        var success = false
        withContext(Dispatchers.IO) {
            try {
                val conexionImagenes = ConexionSqlImagenes("true")
                // --- Ajustar el SP llamado si cambiaste los parámetros ---
                // Asumiendo que ActualizarRegistroPuerta ahora NO recibe @RI_TipoEvento
                val sqlConPlaceholders = "EXEC [dbo].[ActualizarRegistroPuerta] " +
                        "@RI_Codigo=?, @RI_FechaRegistro=?, @RI_DNI=?, " + // Quitamos TipoEvento
                        "@RI_NombresApellidos=?, @RI_Area=?, @RI_MotivoIngreso=?, " +
                        "@RI_PlacaVehiculo=?, @RI_Observacion=?" // 8 parámetros ahora

                val parametros = ArrayList<Any?>()
                val timestampRegistro = try {
                    Timestamp(sqlDateTimeFormat.parse(registro.fechaRegistro)?.time ?: 0L)
                } catch (e: ParseException) { null }

                if (timestampRegistro == null) {
                    throw IllegalStateException("Fecha de registro inválida para Timestamp.")
                }

                parametros.add(registro.riCodigo)         // 1: @RI_Codigo
                parametros.add(timestampRegistro)       // 2: @RI_FechaRegistro
                // El parámetro 3 ahora es DNI
                parametros.add(registro.dni)             // 3: @RI_DNI
                parametros.add(registro.nombresApellidos) // 4: @RI_NombresApellidos
                parametros.add(registro.area)            // 5: @RI_Area
                parametros.add(registro.motivoIngreso)   // 6: @RI_MotivoIngreso
                parametros.add(registro.placaVehiculo)   // 7: @RI_PlacaVehiculo
                parametros.add(registro.observacion)     // 8: @RI_Observacion

                val filasAfectadas = conexionImagenes.ejecutarActualizacionParametrizada(sqlConPlaceholders, parametros)

                if (filasAfectadas >= 0) { // Éxito si no hay error SQL y filas >= 0
                    success = true
                    Log.i("EditarDialog", "Actualización OK para ID ${registro.riCodigo}. Filas: $filasAfectadas")
                } else {
                    Log.w("EditarDialog", "Actualización no afectó filas o error SP para ID ${registro.riCodigo}. Código: $filasAfectadas")
                }

            } catch (e: SQLException) {
                Log.e("EditarDialog", "Error SQL al actualizar ID ${registro.riCodigo}", e)
            } catch (e: Exception) {
                Log.e("EditarDialog", "Error general al actualizar ID ${registro.riCodigo}", e)
            }
        } // Fin withContext
        return success
    }

} // Fin EditarRegistroDialog