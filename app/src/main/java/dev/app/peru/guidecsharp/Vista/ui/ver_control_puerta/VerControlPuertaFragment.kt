package dev.app.peru.guidecsharp.Vista.ui.ver_control_puerta

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSqlImagenes
import dev.app.peru.guidecsharp.AccesoSql.Modelo.RegistroPuertaData
import dev.app.peru.guidecsharp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VerControlPuertaFragment : Fragment(), EditarRegistroDialog.EditarRegistroListener, OnRegistroActionListener {
    // --- Vistas ---
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var etBusqueda: EditText
    private lateinit var etDate: EditText // EditText para la fecha

    // --- Adapter y Datos ---
    private lateinit var asistenciaAdapter: AsistenciaAdapter // Reemplaza con tu adapter real
    //private var listaCompletaRegistros = listOf<RegistroPuertaData>() // Guarda la lista original para filtrar
    private var listaCompletaRegistros = mutableListOf<RegistroPuertaData>() // <-- Cambiado a
    // --- Formateadores de Fecha ---
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val sqlDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // --- Ciclo de Vida del Fragment ---
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.activity_ver_control_puerta, container, false)

        // Inicializar Vistas (Importante hacerlo aquí)
        recyclerView = view.findViewById(R.id.recyclerViewAsistencia) // Confirma ID
        progressBar = view.findViewById(R.id.progressBar)         // Confirma ID
        etBusqueda = view.findViewById(R.id.txt_busqueda)           // Confirma ID
        etDate = view.findViewById(R.id.etDate)                     // Confirma ID

        // Configurar componentes de UI
        setupRecyclerView()
        setupDatePickerListener()
        setupSearchListener()
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menulista_registrar_puerta, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_registro_puertas -> {
                // Aquí manejas la acción para ver registros de puertas
                Toast.makeText(context, "Registro De Puertas", Toast.LENGTH_SHORT).show()
                // Aquí podrías iniciar una nueva actividad o fragment para ver los registros
                navegarRegistrosPuertas()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun navegarRegistrosPuertas() {
        // Ejemplo:
        findNavController().navigate(R.id.nav_control_puerta)

        // O si prefieres usar navegación con fragments:
        //findNavController().navigate(R.id.VerControlPuertaFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Este es un buen lugar para la carga inicial de datos
        val hoy = Calendar.getInstance()
        // Establecer la fecha actual al iniciar y cargar los datos
        etDate.setText(displayDateFormat.format(hoy.time)) // Mostrar fecha actual en formato dd/MM/yyyy
        cargarRegistrosDesdeBD(sqlDateFormat.format(hoy.time)) // Cargar datos con formato yyyy-MM-dd
    }

    // --- Métodos de Configuración ---
    private fun setupRecyclerView() {
        // Reemplaza 'AsistenciaAdapter' con el nombre real de tu adapter
        // Asegúrate que tu adapter puede manejar una lista de RegistroPuertaData
        asistenciaAdapter = AsistenciaAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = asistenciaAdapter
    }

    private fun setupDatePickerListener() {
        // Hacer que el EditText no sea editable por teclado, solo por clic
        etDate.isFocusable = false
        etDate.isClickable = true
        etDate.setOnClickListener {
            mostrarDatePicker()
        }
    }

    private fun setupSearchListener() {
        etBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLista(s.toString()) // Llama a filtrar mientras se escribe
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // --- Métodos de UI ---
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        // Intentar usar la fecha actual del EditText como punto de partida para el picker
        try {
            etDate.text.toString().takeIf { it.isNotEmpty() }?.let {
                displayDateFormat.parse(it)?.also { parsedDate ->
                    calendar.time = parsedDate
                }
            }
        } catch (e: Exception) {
            // Si el parseo falla, simplemente usa la fecha actual (ya está en 'calendar')
            Log.w("DatePicker", "No se pudo parsear fecha de etDate: ${etDate.text}", e)
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(yearSelected, monthOfYear, dayOfMonth) // Establecer la fecha seleccionada
                }
                // Formatear la fecha seleccionada para mostrar y para la consulta SQL
                val fechaSeleccionadaDisplay = displayDateFormat.format(selectedCalendar.time)
                val fechaSeleccionadaSql = sqlDateFormat.format(selectedCalendar.time)

                etDate.setText(fechaSeleccionadaDisplay) // Actualizar el EditText
                etBusqueda.setText("") // Limpiar búsqueda al cambiar fecha
                // Recargar los datos de la BD con la nueva fecha (formato SQL)
                cargarRegistrosDesdeBD(fechaSeleccionadaSql)
            },
            year, month, day // Fecha inicial del picker
        ).show()
    }

    // --- Carga de Datos ---
    // --- Carga de Datos (Modificado para usar MutableList) ---
    private fun cargarRegistrosDesdeBD(fechaSql: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // Limpiar la lista mutable ANTES de la nueva carga
        listaCompletaRegistros.clear() // <-- Limpiar lista mutable existente
        // Notificar al adapter que la lista está vacía temporalmente
        asistenciaAdapter.actualizarLista(listaCompletaRegistros) // Pasar la lista vacía

        Log.i("CargarDatos", "Iniciando carga para fecha SQL: $fechaSql")

        viewLifecycleOwner.lifecycleScope.launch {
            val resultado = obtenerRegistros(fechaSql) // obtenerRegistros sigue devolviendo List?

            progressBar.visibility = View.GONE
            if (resultado != null) {
                // Añadir los nuevos resultados a la lista mutable
                listaCompletaRegistros.addAll(resultado) // <-- Añadir resultados a la lista mutable
                // Actualizar el adapter con la lista ya poblada
                asistenciaAdapter.actualizarLista(listaCompletaRegistros)
                recyclerView.visibility = View.VISIBLE
                Log.i("CargarDatos", "Carga completada: ${listaCompletaRegistros.size} registros para $fechaSql")
                if (listaCompletaRegistros.isEmpty()) {
                    Toast.makeText(context, "No hay registros para la fecha seleccionada.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error al cargar registros para la fecha seleccionada.", Toast.LENGTH_SHORT).show()
                Log.e("CargarDatos", "Error al obtener registros de BD para $fechaSql")
            }
        }
    }

    // --- *** FUNCIÓN MODIFICADA PARA USAR ConexionSqlImagenes y PreparedStatement *** ---
    private suspend fun obtenerRegistros(fechaSql: String): List<RegistroPuertaData>? {
        // fechaSql se espera en formato 'yyyy-MM-dd'
        Log.d("ObtenerRegistros", "Preparando para ejecutar SP con PreparedStatement para fecha: $fechaSql")

        // Usar withContext para asegurar que se ejecuta en hilo de background (Dispatchers.IO para red/BD)
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<RegistroPuertaData>()
            var queryResult: ConexionSqlImagenes.QueryResult? = null // Para manejar los recursos abiertos

            try {
                // 1. --- Usar la conexión parametrizada ---
                val conexionImagenes = ConexionSqlImagenes("true") // O "false" si pruebas en local

                // 2. --- SQL con placeholder ---
                // El SP espera un parámetro @FechaConsulta de tipo 'date'
                val sqlConPlaceholders = "EXEC [dbo].[ObtenerRegistrosActivosPorFechaSistema] @FechaConsulta = ?"

                // 3. --- Lista de parámetros ---
                val parametros = ArrayList<Any?>()

                // 3.1. --- Convertir String 'yyyy-MM-dd' a java.sql.Date ---
                // Esto es más explícito y seguro que pasar el String directamente
                val sqlDate: java.sql.Date? = try {
                    // java.sql.Date.valueOf espera exactamente el formato yyyy-MM-dd
                    java.sql.Date.valueOf(fechaSql)
                } catch (e: IllegalArgumentException) {
                    // Si la fechaSql no tiene el formato correcto, valueOf lanzará una excepción
                    Log.e("ObtenerRegistros", "Formato de fecha inválido ('$fechaSql') para convertir a sql.Date", e)
                    null // Marcar como null si el formato es inválido
                }

                // 3.2. --- Validar si la conversión fue exitosa ---
                if (sqlDate == null) {
                    Log.e("ObtenerRegistros", "No se pudo convertir la fecha '$fechaSql' a sql.Date, abortando la consulta.")
                    return@withContext null // Abortar y devolver null si la fecha no es válida
                }
                // 3.3 --- Añadir el parámetro java.sql.Date a la lista ---
                parametros.add(sqlDate)
                // --- Fin preparación de parámetros ---

                // 4. --- Ejecutar la consulta parametrizada ---
                Log.d("ObtenerRegistros", "Ejecutando consulta parametrizada con sql.Date...")
                queryResult = conexionImagenes.ejecutarConsultaParametrizada(sqlConPlaceholders, parametros)

                // 5. --- Procesar el ResultSet ---
                // El ResultSet está dentro de queryResult.resultSet
                val resultSet = queryResult.resultSet

                // Verificar si el ResultSet es válido
                if (resultSet == null) {
                    // Esto no debería ocurrir si ejecutarConsultaParametrizada está bien hecha (lanzaría SQLException antes)
                    Log.e("ObtenerRegistros", "ejecutarConsultaParametrizada devolvió un objeto QueryResult con ResultSet null (inesperado).")
                    return@withContext null // Indicar error
                }

                // 6. --- Mapeo de Resultados ---
                Log.d("ObtenerRegistros", "Procesando ResultSet...")
                while (resultSet.next()) { // Iterar sobre cada fila del resultado
                    try {
                        // Mapear cada columna de la fila actual a un objeto RegistroPuertaData
                        val registro = RegistroPuertaData(
                            // Usar getString y proporcionar un valor default con ?: "" si la columna podría ser NULL
                            // y el campo en RegistroPuertaData NO es nullable. Si el campo SÍ es nullable,
                            // getString devolverá null directamente si la columna es NULL.
                            riCodigo = resultSet.getInt("RI_Codigo"), // <-- AÑADIDO/VERIFICADO

                            fechaSistema = resultSet.getString("RI_FechaSistema") ?: "",
                            fechaRegistro = resultSet.getString("RI_FechaRegistro") ?: "",
                            tipoEvento = resultSet.getString("RI_TipoEvento") ?: "",
                            dni = resultSet.getString("RI_DNI") ?: "",
                            nombresApellidos = resultSet.getString("RI_NombresApellidos") ?: "",
                            area = resultSet.getString("RI_Area") ?: "",
                            motivoIngreso = resultSet.getString("RI_MotivoIngreso") ?: "",
                            placaVehiculo = resultSet.getString("RI_PlacaVehiculo"), // Asume nullable en DataClass
                            personalAutorizo = resultSet.getString("RI_PersonalAutorizo"), // Asume nullable
                            observacion = resultSet.getString("RI_Observacion"),       // Asume nullable
                            otros1 = resultSet.getString("RI_Otros1"),           // Asume nullable
                            otros2 = resultSet.getString("RI_Otros2"),           // Asume nullable
                            estado = resultSet.getString("RI_Estado") ?: "Desconocido" // Nullable con default

                            // Nota: Las columnas de imagen (RI_Img1, etc.) no se están mapeando aquí.
                            // Si las necesitaras, añade las líneas correspondientes usando resultSet.getBytes("RI_ImgX")
                            // y asegúrate que RegistroPuertaData tenga los campos imgX como ByteArray?
                        )
                        lista.add(registro) // Añadir el registro mapeado a la lista
                    } catch (mappingError: Exception) {
                        // Loggear un error si falla el mapeo de UNA fila específica
                        // (ej. nombre de columna incorrecto, tipo incompatible)
                        Log.e("ObtenerRegistros", "Error mapeando una fila del ResultSet: ${mappingError.message}", mappingError)
                        // Decisión: ¿continuar con las siguientes filas o abortar todo?
                        // Aquí optamos por continuar (se ignora la fila con error)
                        continue
                    }
                } // Fin del bucle while

                Log.i("ObtenerRegistros", "Se mapearon ${lista.size} registros del ResultSet para $fechaSql.")

                // 7. --- Devolver la lista ---
                // La lista puede estar vacía si la consulta no devolvió filas, pero no es un error.
                lista

            } catch (e: SQLException) { // Captura errores específicos de JDBC/SQL
                // Loggear el error SQL detallado
                Log.e("ObtenerRegistros", "Error SQLException durante la ejecución parametrizada para fecha $fechaSql", e)
                // Podrías querer loggear e.getErrorCode() y e.getSQLState() también
                null // Devolver null para indicar que ocurrió un error SQL
            } catch (e: Exception) { // Captura cualquier otra excepción inesperada
                Log.e("ObtenerRegistros", "Error general durante la obtención parametrizada para fecha $fechaSql", e)
                null // Devolver null para indicar un error no específico de SQL
            } finally {
                // 8. --- CERRAR LOS RECURSOS EN finally (¡MUY IMPORTANTE!) ---
                // Esto asegura que ResultSet, PreparedStatement y Connection se cierren
                // incluso si ocurre una excepción durante el procesamiento del ResultSet.
                Log.d("ObtenerRegistros", "Intentando cerrar recursos en finally...")
                queryResult?.closeAll() // Llama al metodo helper en QueryResult para cerrar todo de forma segura
                Log.d("ObtenerRegistros", "Llamada a closeAll() completada.")
            }
        } // Fin de withContext(Dispatchers.IO)
    } // Fin de la función obtenerRegistros

    // --- Filtrado de la Lista ---
    private fun filtrarLista(textoBusqueda: String) {
        val textoLower = textoBusqueda.lowercase(Locale.getDefault()).trim()

        val listaFiltrada = if (textoLower.isEmpty()) {
            // Si no hay texto de búsqueda, mostrar la lista completa
            listaCompletaRegistros
        } else {
            // Filtrar la lista completa basándose en el texto de búsqueda
            listaCompletaRegistros.filter { registro ->
                // Comprobar si el DNI O el nombre/apellidos contienen el texto (ignorando mayúsculas/minúsculas)
                (registro.dni?.contains(textoLower, ignoreCase = true) == true) ||
                        (registro.nombresApellidos?.lowercase(Locale.getDefault())?.contains(textoLower) == true)
                // Puedes añadir más campos al filtro si lo deseas:
                // || (registro.placaVehiculo?.contains(textoLower, ignoreCase = true) == true)
                // || (registro.area?.contains(textoLower, ignoreCase = true) == true)
            }
        }
        // Actualizar el adapter con la lista filtrada (o la completa si no hay búsqueda)
        asistenciaAdapter.actualizarLista(listaFiltrada)
    }

    override fun onEditarClick(registro: RegistroPuertaData, position: Int) {
        Log.d("VerControlPuerta", "Editando registro ID: ${registro.riCodigo} en posición: $position")
        val dialog = EditarRegistroDialog.newInstance(registro, position)
        dialog.show(childFragmentManager, "EditarRegistroDialogTag")
    }

    override fun onEliminarClick(registro: RegistroPuertaData, position: Int) {
        if (registro.riCodigo == null || position < 0 || position >= listaCompletaRegistros.size) {
            Log.e("VerControlPuerta", "Intento de eliminar con datos inválidos. ID: ${registro.riCodigo}, Pos: $position")
            Toast.makeText(context, "Error al intentar eliminar.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("VerControlPuerta", "Eliminar presionado para ID: ${registro.riCodigo} en posición: $position")

        // --- Mostrar Diálogo de Confirmación ---
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Acción")
            .setMessage("¿Estás seguro de que deseas marcar este registro ( DNI: ${registro.dni}) como Eliminado?\n\nEsta acción no se puede deshacer fácilmente desde la app.")
            .setIcon(android.R.drawable.ic_dialog_alert) // Icono de advertencia
            .setPositiveButton("Sí, Eliminar") { dialog, which ->
                // --- Usuario confirmó: Ejecutar la inactivación ---
                inactivarRegistro(registro, position)
            }
            .setNegativeButton("No", null) // No hacer nada si cancela, simplemente cierra el diálogo
            .show()
    }

    override fun onVerDetalleClick(registro: RegistroPuertaData, position: Int) {
        Log.d("VerControlPuerta", "Viendo detalles registro ID: ${registro.riCodigo} en posición: $position")
        // Llama a newInstance pasando true para el modo "solo vista"
        val dialog = EditarRegistroDialog.newInstance(registro, position, isViewOnly = true)
        dialog.show(childFragmentManager, "VerDetalleDialogTag") // Puedes usar un tag diferente si quieres
    }

    // --- Metodo de EditarRegistroDialog.EditarRegistroListener ---
    override fun onRegistroEditado(registroEditado: RegistroPuertaData, position: Int) {
        // ... (lógica para actualizar la lista) ...
        Log.d("VerControlPuerta", "Registro editado callback recibido para ID: ${registroEditado.riCodigo}")
        if (position >= 0 && position < listaCompletaRegistros.size) {
            listaCompletaRegistros[position] = registroEditado
            asistenciaAdapter.notifyItemChanged(position)
            Log.i("VerControlPuerta", "RecyclerView actualizado en posición $position")
            if (etBusqueda.text.isNotEmpty()) {
                filtrarLista(etBusqueda.text.toString())
            }
        } else {
            Log.w("VerControlPuerta", "Posición inválida ($position) recibida después de editar. Recargando.")
            cargarRegistrosDesdeBD(sqlDateFormat.format(displayDateFormat.parse(etDate.text.toString()) ?: Date()))
        }
    }
    private fun inactivarRegistro(registro: RegistroPuertaData, position: Int) {
        // Mostrar un indicador de progreso si se desea (opcional)
        // progressBar.visibility = View.VISIBLE // ¿O usar un ProgressDialog?

        // Lanzar coroutine para la operación de BD
        viewLifecycleOwner.lifecycleScope.launch {
            var success = false
            var errorMsg: String? = null
            val codigoAEliminar = registro.riCodigo ?: return@launch // Salir si el código es null

            withContext(Dispatchers.IO) {
                try {
                    val conexionImagenes = ConexionSqlImagenes("true") // O "false"
                    val sqlConPlaceholders = "EXEC [dbo].[MarcarRegistroInactivo] @RI_Codigo = ?"
                    val parametros = ArrayList<Any?>()
                    parametros.add(codigoAEliminar) // Añadir el ID como parámetro

                    Log.d("InactivarRegistro", "Ejecutando SP para inactivar ID: $codigoAEliminar")
                    val resultadoSP = conexionImagenes.ejecutarActualizacionParametrizada(sqlConPlaceholders, parametros)

                    // Verificar el resultado del SP (0 = éxito según nuestro SP)
                    if (resultadoSP == 0) {
                        success = true
                        Log.i("InactivarRegistro", "Registro ID $codigoAEliminar marcado como inactivo en BD.")
                    } else {
                        // El SP devolvió un código de error o no afectó filas
                        errorMsg = "La base de datos no pudo marcar el registro como inactivo (Código SP: $resultadoSP)."
                        Log.w("InactivarRegistro", errorMsg!!)
                    }

                } catch (e: SQLException) {
                    errorMsg = "Error SQL al inactivar: ${e.message}"
                    Log.e("InactivarRegistro", errorMsg, e)
                } catch (e: Exception) {
                    errorMsg = "Error inesperado al inactivar: ${e.message}"
                    Log.e("InactivarRegistro", errorMsg, e)
                }
            } // Fin withContext

            // --- Actualizar UI en el hilo principal ---
            // progressBar.visibility = View.GONE // Ocultar progreso

            if (success) {
                try {
                    // Eliminar el elemento de la lista local
                    if (position >= 0 && position < listaCompletaRegistros.size) {
                        listaCompletaRegistros.removeAt(position)
                        // Notificar al adapter que un elemento fue eliminado en esa posición
                        asistenciaAdapter.notifyItemRemoved(position)
                        // Notificar que el rango de items DESPUÉS del eliminado cambió
                        // Esto es importante para que las posiciones se recalculen correctamente
                        asistenciaAdapter.notifyItemRangeChanged(position, listaCompletaRegistros.size - position)

                        Toast.makeText(context, "Registro marcado como Inactivo.", Toast.LENGTH_SHORT).show()
                        Log.i("InactivarRegistro", "Item removido del RecyclerView en posición $position.")

                        // Opcional: Si la lista queda vacía, mostrar mensaje
                        if (listaCompletaRegistros.isEmpty()) {
                            Toast.makeText(context, "No quedan registros activos para mostrar.", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Log.e("InactivarRegistro", "La posición $position es inválida después de inactivar. Recargando lista completa.")
                        // Fallback: Recargar toda la lista si la posición es inválida
                        cargarRegistrosDesdeBD(sqlDateFormat.format(displayDateFormat.parse(etDate.text.toString()) ?: Date()))
                    }
                } catch (indexError: IndexOutOfBoundsException) {
                    Log.e("InactivarRegistro", "Error de índice al remover item en posición $position.", indexError)
                    // Fallback: Recargar toda la lista
                    cargarRegistrosDesdeBD(sqlDateFormat.format(displayDateFormat.parse(etDate.text.toString()) ?: Date()))
                }

            } else {
                // Mostrar mensaje de error
                Toast.makeText(context, "Error: ${errorMsg ?: "No se pudo inactivar el registro."}", Toast.LENGTH_LONG).show()
            }
        } // Fin launch
    }
}