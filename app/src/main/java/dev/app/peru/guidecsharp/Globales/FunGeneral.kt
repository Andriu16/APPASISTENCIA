package dev.app.peru.guidecsharp.Globales

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.provider.Settings.Secure
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSql
import dev.app.peru.guidecsharp.AccesoSql.Controlador.ConexionSql_2
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*
import dev.app.peru.guidecsharp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class FunGeneral : Application() {
    companion object{
        val sql = ConexionSql("true")
        val sql2 = ConexionSql_2("true")
        lateinit var retrofitDoc : Retrofit

        fun idUnico(context: Context) : String {
            return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        }

        fun toStringG29(number : Float) : String{
            val formato2 = DecimalFormat("#.##")
            return formato2.format(number).toString()
        }

        fun getEntero(valor: String) : Int {
            var dec = 0;
            try
            {
                if (valor == null) dec = 0;
                if (valor != "")
                    dec = valor.toInt()
            }
            catch(ex: java.lang.Exception) { }
            return dec;
        }

        fun getFloat(valor: String) : Float {
            var dec = 0f;
            try
            {
                if (valor == null) dec = 0f;
                if (valor != "")
                    dec = valor.toFloat()
            }
            catch(ex: java.lang.Exception) { }
            return dec;
        }

        fun convertirMinutosEnTexto(min: Float) : String {
            var _entera = (min / 60).toInt()
            var _residuo =  String.format("%.2f", (min - (_entera  * 60))).toFloat()
            return "${_entera}:${FunGeneral.toStringG29(_residuo)}"
        }

        fun obtenerFecha_PorFormato(fecha: String, formato: String) : String{
            //separa dia, mes y año de fecha con formato: 27/06/2023

            var _año = 0
            var _mes = 0
            var _dia = 0
            if (fecha.length >= 8){
                _año = fecha.substring(6, 10).toInt()
                _mes = fecha.substring(3, 5).toInt()
                _dia = fecha.substring(0, 2).toInt()
            }

            var _hora = 0
            var _minuto = 0
            var _segundo = 0

            return FunGeneral.convertirFecha(_año, _mes, _dia, _hora, _minuto, _segundo, formato)
        }

        fun obtenerHora_PorFormato(hora: String, formato: String) : String{
            //separa hora, min y seg de hora con formato: 10:15:20 a. m.

            var _año = 0
            var _mes = 0
            var _dia = 0

            var _hora = 0
            var _minuto = 0
            var _segundo = 0
            if (hora.length > 5){
                _hora = hora.substring(0, 2).toInt()
                _minuto = hora.substring(3, 5).toInt()
                _segundo = hora.substring(6, 8).toInt()
            }

            var _hora24 = 0
            if (hora.contains(("a."))){
                if (_hora == 12) _hora24 = 0
                if (_hora < 12) _hora24 = _hora
            }
            else{
                if (_hora == 12) _hora24 = 12
                if (_hora < 12) _hora24 = _hora + 12
            }

            return FunGeneral.convertirFecha(_año, _mes, _dia, _hora24,_minuto,_segundo, formato)
        }

        fun obtenerFechaHora_PorFormato(fecha: String, hora: String, formato: String) : String{
            //separa datos de fecha con formato: 27/06/2023
            //separa datos de hora con formato: 10:15:20 a. m.

            var _año = 0
            var _mes = 0
            var _dia = 0
            if (fecha.length >= 8){
                _año = fecha.substring(6, 10).toInt()
                _mes = fecha.substring(3, 5).toInt()
                _dia = fecha.substring(0, 2).toInt()
            }

            var _hora = 0
            var _minuto = 0
            var _segundo = 0
            if (hora.length > 5){
                _hora = hora.substring(0, 2).toInt()
                _minuto = hora.substring(3, 5).toInt()
                _segundo = hora.substring(6, 8).toInt()
            }

            var _hora24 = 0
            if (hora.contains(("a."))){
                if (_hora == 12) _hora24 = 0
                if (_hora < 12) _hora24 = _hora
            }
            else{
                if (_hora == 12) _hora24 = 12
                if (_hora < 12) _hora24 = _hora + 12
            }

            return FunGeneral.convertirFecha(_año, _mes, _dia, _hora24,_minuto,_segundo, formato)
        }

        fun obtenerFechaHora_PorFormato_Date(fecha: String) : Date {
            //separa datos de fecha con formato: 27/06/2023 10:15:20 a. m.

            var _año = 0
            var _mes = 0
            var _dia = 0
            if (fecha.length >= 8){
                _año = fecha.substring(6, 10).toInt()
                _mes = fecha.substring(3, 5).toInt()
                _dia = fecha.substring(0, 2).toInt()
            }

            var _hora = 0
            var _minuto = 0
            var _segundo = 0
            if (fecha.length >= 8){
                _hora = fecha.substring(11, 13).toInt()
                _minuto = fecha.substring(14, 16).toInt()
                _segundo = fecha.substring(17, 19).toInt()
            }

            var _hora24 = 0
            if (fecha.contains(("a."))){
                if (_hora == 12) _hora24 = 0
                if (_hora < 12) _hora24 = _hora
            }
            else{
                if (_hora == 12) _hora24 = 12
                if (_hora < 12) _hora24 = _hora + 12
            }

            return getFecha(_año, _mes, _dia, _hora24,_minuto,_segundo)
        }

        fun obtenerFechaHora_PorFormato_Nube(fechayhora: String, formato: String) : String{
            if (fechayhora == "") return ""
            //separa datos de fecha y hora con formato: 20230627 18:15:20

            var _año = 0
            var _mes = 0
            var _dia = 0

            var _hora = 0
            var _minuto = 0
            var _segundo = 0

            if (fechayhora.length >= 8){
                _año = fechayhora.substring(0, 4).toInt()
                _mes = fechayhora.substring(4, 6).toInt()
                _dia = fechayhora.substring(6, 8).toInt()

                _hora = fechayhora.substring(9, 11).toInt()
                _minuto = fechayhora.substring(12, 14).toInt()
                _segundo = fechayhora.substring(15, 17).toInt()
            }

            return FunGeneral.convertirFecha(_año, _mes, _dia, _hora,_minuto,_segundo, formato)
        }

        fun obtenerFecha_DeLong(fecha: Long): String {
            val dateTime = java.util.Date(fecha)
            val format = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.US)
            return format.format(dateTime)
        }

        fun obtenerFechaHora_PorFormato_Nube_Long(fechayhora: String) : Long{
            //separa datos de fecha y hora con formato: 20230627 18:15:20

            var _año = 0
            var _mes = 0
            var _dia = 0

            var _hora = 0
            var _minuto = 0
            var _segundo = 0

            if (fechayhora.length >= 8){
                _año = fechayhora.substring(0, 4).toInt()
                _mes = fechayhora.substring(4, 6).toInt()
                _dia = fechayhora.substring(6, 8).toInt()

                _hora = fechayhora.substring(9, 11).toInt()
                _minuto = fechayhora.substring(12, 14).toInt()
                _segundo = fechayhora.substring(15, 17).toInt()
            }

            return getFecha(_año, _mes, _dia, _hora,_minuto,_segundo).time.toLong()
        }

        fun obtenerFechaHora_PorFormato_Nube_Date(fechayhora: String) : Date{
            //separa datos de fecha y hora con formato: 20230627 18:15:20

            var _año = 0
            var _mes = 0
            var _dia = 0

            var _hora = 0
            var _minuto = 0
            var _segundo = 0

            if (fechayhora.length >= 8){
                _año = fechayhora.substring(0, 4).toInt()
                _mes = fechayhora.substring(4, 6).toInt()
                _dia = fechayhora.substring(6, 8).toInt()

                _hora = fechayhora.substring(9, 11).toInt()
                _minuto = fechayhora.substring(12, 14).toInt()
                _segundo = fechayhora.substring(15, 17).toInt()
            }

            return getFecha(_año, _mes, _dia, _hora,_minuto,_segundo)
        }

        fun obtenerFecha_PorFormato_Nube(fechayhora: String, formato: String) : String{
            //separa datos de fecha con formato: 20230627

            var _año = 0
            var _mes = 0
            var _dia = 0

            var _hora = 0
            var _minuto = 0
            var _segundo = 0

            if (fechayhora.length >= 8){
                _año = fechayhora.substring(0, 4).toInt()
                _mes = fechayhora.substring(4, 6).toInt()
                _dia = fechayhora.substring(6, 8).toInt()
            }

            return FunGeneral.convertirFecha(_año, _mes, _dia, _hora,_minuto,_segundo, formato)
        }

        private fun convertirFecha(año: Int, mes: Int, dia: Int, hora: Int, minuto: Int, segundo: Int, format: String): String {
            val formato = SimpleDateFormat(format, Locale.getDefault())
            var calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))

            var año2 = año - 1900
            var mes2 = mes - 1

            var _fecha = Date(año2, mes2, dia, hora, minuto, segundo)
            val horaActual = formato.format(_fecha)
            return horaActual
        }

        private fun getFecha(año: Int, mes: Int, dia: Int, hora: Int, minuto: Int, segundo: Int): Date {
            var año2 = año - 1900
            var mes2 = mes - 1

            return Date(año2, mes2, dia, hora, minuto, segundo)
        }

        fun fecha(format: String): String {
            val calendar = Calendar.getInstance();
            val dateFormat = SimpleDateFormat(format)
            return dateFormat.format(calendar.time)
        }

        fun fecha_nextDay(format: String): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dateFormat = SimpleDateFormat(format)
            return dateFormat.format(calendar.time)
        }

        fun hora(format: String): String {
            val calendar = Calendar.getInstance();
            val dateFormat = SimpleDateFormat(format)
            return dateFormat.format(calendar.time)
        }

        fun getFechaActual(format: String): String {
            val formato = SimpleDateFormat(format, Locale.getDefault())
            return formato.format(Date())
        }

        //Para fecha de tomar asistencia
        var _seguirFechaTomarAsistencia = true
        fun FechaMostrar_TomarAsistencia(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun run() {
                    if (_seguirFechaTomarAsistencia){
                        val formato = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun FechaMostrar_TomarAsistencia_Stop(valor: Boolean){
            _seguirFechaTomarAsistencia = valor
        }

        //Para hora de tomar asistencia
        var _seguirHoraTomarAsistencia = true
        fun HoraMostrar_TomarAsistencia(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (_seguirHoraTomarAsistencia){
                        val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun HoraMostrar_TomarAsistencia_Stop(valor: Boolean){
            _seguirHoraTomarAsistencia = valor
        }

        //Para hora de tareo parte 2
        var _seguirTareoParte2 = true
        fun HoraMostrar_TareoParte2(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (_seguirTareoParte2){
                        val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun  HoraMostrar_TareoParte2_Stop(valor: Boolean){
            _seguirTareoParte2 = valor
        }

        //Para hora de tareo , trabajadores con asistencia
        var _seguirTareoTrabConAsistencia = true
        fun HoraMostrar_Tareo_TrabConAsistencia(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (_seguirTareoTrabConAsistencia){
                        val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun  HoraMostrar_Tareo_TrabConAsistencia_Stop(valor: Boolean){
            _seguirTareoTrabConAsistencia = valor
        }

        //Para hora de tareo , trabajadores con asistencia
        var _seguirTareoTrabSinAsistencia = true
        fun HoraMostrar_Tareo_TrabSinAsistencia(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (_seguirTareoTrabSinAsistencia){
                        val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun  HoraMostrar_Tareo_TrabSinAsistencia_Stop(valor: Boolean){
            _seguirTareoTrabSinAsistencia = valor
        }

        //Para hora de asistencia diaria
        var _seguirHoraAsistenciaDiaria = true
        fun HoraMostrar_AsistenciaDiaria(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (_seguirHoraAsistenciaDiaria){
                        val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun  HoraMostrar_AsistenciaDiaria_Stop(valor: Boolean){
            _seguirHoraAsistenciaDiaria = valor
        }

        //Para fecha de asistencia diaria
        var _seguirFechaAsistenciaDiaria = true
        fun FechaMostrar_AsistenciaDiaria(textView: TextView) {
            val handler = Handler()
            handler.post(object : Runnable {
                @SuppressLint("NewApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun run() {
                    if (_seguirFechaAsistenciaDiaria){
                        val formato = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
                        val horaActual = formato.format(Date())
                        textView.text = horaActual
                    }
                    handler.postDelayed(this, 1000)
                }
            })
        }
        fun  FechaMostrar_AsistenciaDiaria_Stop(valor: Boolean){
            _seguirFechaAsistenciaDiaria = valor
        }

        fun descargarData_RegistroTareo(context: Context) {
            var a = obtenerTiposDocumento(context)
            var b = obtenerTrabajadores(context)
            var i = obtenerPacking(context)
            var j = obtenerCultivo(context)
            var k = obtenerCentroCosto(context)
            var l = obtenerLabores(context)
            var m = obtenerActividad(context)
            var n = obtenerSucursal(context)
            var o = obtenerUnidades(context)
            var p = obtenerUnidadesDestajo(context)
            var q = obtenerTarifas(context)
            var r = obtenerVacaciones(context)

            Globales.showSuccessMessage("Se sincronizaron:\n" +
                    "Fundo/Packing: ${i}\n" +
                    "Cultivo: ${j}\n" +
                    "Centro de Costo: ${k}\n" +
                    "Labores: ${l}\n" +
                    "Etapas: ${m}\n" +
                    "Unidades: ${o}\n"+
                    "Unidades de Labor: ${p}\n"+
                    "Tarifas: ${q}\n" +
                    "Tipos de Doc.: ${a}\n"+
                    "Trabajadores: ${b}\n"+
                    "Sedes: ${n}", context)
        }

        fun obtenerPacking(context: Context) : Int {
            val packingDAO = ConexionSqlite.getInstancia(context).packingDAO()

            //Obteniendo Fundo/Packing de la nube
            packingDAO.eliminarTodos()
            var i = 0
            val rsPacking = EmpleadoDALC.PA_ListarPacking()
            while (rsPacking.next()) {
                i++

                val PAC_Codigo = rsPacking.getInt("PAC_Codigo")
                val PAC_Descripcion = rsPacking.getString("PAC_Descripcion")
                val PAC_Estado = rsPacking.getString("PAC_Estado")
                var packing = Packing(PAC_Codigo, PAC_Descripcion, PAC_Estado)
                if (packingDAO.PA_ObtenerPacking_PorCodigo(packing.PAC_Codigo) == null) {
                    packingDAO.guardarPacking(packing)
                } else {
                    packingDAO.actualizarPacking(packing)
                }
            }
            return i
        }

        fun obtenerCultivo(context: Context) : Int {
            val cultivoDAO = ConexionSqlite.getInstancia(context).cultivoDAO()

            //Obteniendo Cultivo de la nube
            cultivoDAO.eliminarTodos()
            var j = 0
            val rsCultivo = EmpleadoDALC.PA_ListarCultivo()
            while (rsCultivo.next()) {
                j++

                val CU_Codigo = rsCultivo.getInt("CU_Codigo")
                val CU_Descripcion = rsCultivo.getString("CU_Descripcion")
                val CU_Estado = rsCultivo.getString("CU_Estado")
                var cultivo = Cultivo(CU_Codigo, CU_Descripcion, CU_Estado)
                if (cultivoDAO.PA_ObtenerCultivo_PorCodigo(cultivo.CU_Codigo) == null) {
                    cultivoDAO.guardarCultivo(cultivo)
                } else {
                    cultivoDAO.actualizarCultivo(cultivo)
                }
            }
            return j
        }

        fun obtenerCentroCosto(context: Context) : Int {
            val centroCostoDAO = ConexionSqlite.getInstancia(context).centroCostoDAO()

            //Obteniendo Centro de Costo de la nube
            centroCostoDAO.eliminarTodos()
            var k = 0
            val rsCentroCosto = EmpleadoDALC.PA_ListarCentroCosto()
            while (rsCentroCosto.next()) {
                k++

                val CC_Codigo = rsCentroCosto.getLong("CC_Codigo")
                val CC_Descripcion = rsCentroCosto.getString("CC_Descripcion")
                val CC_Estado = rsCentroCosto.getString("CC_Estado")
                val CC_Otros = rsCentroCosto.getString("CC_Otros")
                val CU_Codigo = rsCentroCosto.getInt("CU_Codigo")
                var centroCosto =
                    CentroCosto(CC_Codigo, CC_Descripcion, CC_Estado, CC_Otros, CU_Codigo)
                if (centroCostoDAO.PA_ObtenerCentroCosto_PorCodigo(centroCosto.CC_Codigo) == null) {
                    centroCostoDAO.guardarCentroCosto(centroCosto)
                } else {
                    centroCostoDAO.actualizarCentroCosto(centroCosto)
                }
            }

            return k
        }

        fun obtenerLabores(context: Context) : Int {
            val laboresDAO = ConexionSqlite.getInstancia(context).laboresDAO()

            //Obteniendo Labores de la nube
            laboresDAO.eliminarTodos()
            var l = 0
            val rsLabores = EmpleadoDALC.PA_ListarLabores()
            while (rsLabores.next()) {
                l++

                val Lab_Codigo = rsLabores.getLong("Lab_Codigo")
                val Lab_Descripcion = rsLabores.getString("Lab_Descripcion")
                val Lab_Estado = rsLabores.getString("Lab_Estado")
                val Lab_Otros = rsLabores.getString("Lab_Otros")
                val AC_Codigo = rsLabores.getLong("AC_Codigo")
                var labores = Labores(Lab_Codigo, Lab_Descripcion, Lab_Estado, Lab_Otros, AC_Codigo)
                if (laboresDAO.PA_ObtenerLabores_PorCodigo(labores.Lab_Codigo) == null) {
                    laboresDAO.guardarLabor(labores)
                } else {
                    laboresDAO.actualizarCentroCosto(labores)
                }
            }

            return l
        }

        fun obtenerActividad(context: Context) : Int {
            val actividadDAO = ConexionSqlite.getInstancia(context).actividadDAO()

            //Obteniendo Actividad de la nube
            actividadDAO.eliminarTodos()
            var m = 0
            val rsActividades = EmpleadoDALC.PA_ListarActividad()
            while (rsActividades.next()) {
                m++

                val AC_Codigo = rsActividades.getLong("AC_Codigo")
                val AC_Descripcion = rsActividades.getString("AC_Descripcion")
                val AC_Estado = rsActividades.getString("AC_Estado")
                var actividad = Actividad(AC_Codigo, AC_Descripcion, AC_Estado)
                if (actividadDAO.PA_ObtenerActividad_PorCodigo(actividad.AC_Codigo) == null) {
                    actividadDAO.guardarActividad(actividad)
                } else {
                    actividadDAO.actualizarActividad(actividad)
                }
            }

            return m
        }

        fun obtenerSucursal(context: Context) : Int {
            val sucursalDAO = ConexionSqlite.getInstancia(context).sucursalDAO()

            //Obteniendo Sedes de la nube
            sucursalDAO.eliminarTodos()
            var n = 1
            val SUC_Codigo_ini: Long = -1
            val SUC_Descripcion_ini = "- TODAS LAS SEDES -"
            val SUC_Estado_ini = "Habilitado"
            var sucursal_ini = Sucursal(SUC_Codigo_ini, SUC_Descripcion_ini, SUC_Estado_ini)
            if (sucursalDAO.PA_ObtenerSucursal_PorCodigo(sucursal_ini.SUC_Codigo) == null) {
                sucursalDAO.guardarSucursal(sucursal_ini)
            } else {
                sucursalDAO.actualizarSucursal(sucursal_ini)
            }

            val rsSucursales = EmpleadoDALC.PA_ListarSede()
            while (rsSucursales.next()) {
                n++

                val SUC_Codigo = rsSucursales.getLong("SUC_Codigo")
                val SUC_Descripcion = rsSucursales.getString("SUC_Descripcion")
                val SUC_Estado = rsSucursales.getString("SUC_Estado")
                var sucursal = Sucursal(SUC_Codigo, SUC_Descripcion, SUC_Estado)
                if (sucursalDAO.PA_ObtenerSucursal_PorCodigo(sucursal.SUC_Codigo) == null) {
                    sucursalDAO.guardarSucursal(sucursal)
                } else {
                    sucursalDAO.actualizarSucursal(sucursal)
                }
            }

            return n
        }

        fun obtenerAsistenciasPuerta(context: Context, PI_Codigo: Int) : Int {
            val asistenciaPuertaDAO = ConexionSqlite.getInstancia(context).asistenciaPuertaDAO()
            val empleadoDAO = ConexionSqlite.getInstancia(context).empleadoDAO()

            //Obteniendo Asistencias Puerta de la nube
            asistenciaPuertaDAO.eliminarTodosSincronizados()
            var n = 0
            val rsAsistencias = EmpleadoDALC.PA_ListarAsistenciasPuerta_Android(PI_Codigo)
            while (rsAsistencias.next()) {
                val AP_Fecha = rsAsistencias.getString("AP_Fecha")
                val AP_HoraEntrada = rsAsistencias.getString("AP_HoraEntrada")
                val AP_HoraSalida = rsAsistencias.getString("AP_HoraSalida")

                var AP_HoraEntrada_Mostrar = ""
                if (AP_HoraEntrada != ""){
                    AP_HoraEntrada_Mostrar = FunGeneral.obtenerFechaHora_PorFormato_Nube(AP_HoraEntrada, "dd/MM/YYYY hh:mm:ss a")
                }

                var AP_HoraSalida_Mostrar = ""
                if (AP_HoraSalida != ""){
                    AP_HoraSalida_Mostrar = FunGeneral.obtenerFechaHora_PorFormato_Nube(AP_HoraSalida, "dd/MM/YYYY hh:mm:ss a")
                }

                var PER_CodigoNube : Int = rsAsistencias.getInt("PER_Codigo")
                var personal = empleadoDAO.PA_ObtenerEmpleado_PorCodigoNube(PER_CodigoNube)
                if (personal != null){
                    var PER_Codigo : Int = personal.PER_Codigo

                    val AP_Estado = rsAsistencias.getString("AP_Estado")
                    //val AP_EstadoSincronizacion = rsAsistencias.getString("AP_EstadoSincronizacion")

                    var _cuenta = 0;
                    if (AP_HoraEntrada != "") _cuenta++;
                    if (AP_HoraSalida != "") _cuenta++;

                    var AP_EstadoSincronizacion = "";
                    if (_cuenta == 2) AP_EstadoSincronizacion = "SINCRONIZADO 2/2";
                    else if (_cuenta == 1) AP_EstadoSincronizacion = "SINCRONIZADO 1/2";
                    else AP_EstadoSincronizacion = "NO SINCRONIZADO";

                    var asistencia = AsistenciaPuerta(0, AP_Fecha, AP_HoraEntrada, AP_HoraEntrada_Mostrar, AP_HoraSalida, AP_HoraSalida_Mostrar, PER_Codigo, AP_Estado, AP_EstadoSincronizacion)
                    asistenciaPuertaDAO.guardarAsistencia(asistencia)

                    n++
                }
            }

            return n
        }

        fun obtenerUnidades(context: Context) : Int {
            val unidadesDAO = ConexionSqlite.getInstancia(context).unidadesDAO()

            //Obteniendo Unidades de la nube
            unidadesDAO.eliminarTodos()
            var o = 0
            val rsUnidades = EmpleadoDALC.PA_ListarUnidades()
            while (rsUnidades.next()) {
                o++

                val UNI_Codigo = rsUnidades.getLong("UNI_Codigo")
                val UNI_Descripcion = rsUnidades.getString("UNI_Descripcion")
                var obj = Unidades(UNI_Codigo, UNI_Descripcion)

                if (unidadesDAO.PA_ObtenerUnidad_PorCodigo(UNI_Codigo) == null) {
                    unidadesDAO.guardarUnidad(obj)
                } else {
                    unidadesDAO.actualizarUnidad(obj)
                }
            }

            return o
        }

        fun obtenerUnidadesDestajo(context: Context) : Int {
            val unidadDestajoDAO = ConexionSqlite.getInstancia(context).unidadDestajoDAO()

            //Obteniendo Unidades Destajo de la nube
            unidadDestajoDAO.eliminarTodos()
            var p = 0
            val rsUnidadesDestajo = EmpleadoDALC.PA_ListarUnidadDestajo_Android()
            while (rsUnidadesDestajo.next()) {
                p++

                val UD_Codigo = rsUnidadesDestajo.getLong("UD_Codigo")
                val UD_Nombre = rsUnidadesDestajo.getLong("UD_Nombre")
                val Lab_Codigo = rsUnidadesDestajo.getInt("Lab_Codigo")
                val UNI_DescripcionTemp = rsUnidadesDestajo.getString("UNI_Descripcion")
                var obj = UnidadesDestajo(UD_Codigo, UD_Nombre, Lab_Codigo, UNI_DescripcionTemp)

                if (unidadDestajoDAO.PA_ObtenerUnidadDestajo_PorCodigo(UD_Codigo) == null) {
                    unidadDestajoDAO.guardarUnidadDestajo(obj)
                } else {
                    unidadDestajoDAO.actualizarUnidadDestajo(obj)
                }
            }

            return p
        }

        fun obtenerTarifas(context: Context) : Int {
            val tarifaUnidadDestajoDAO =
                ConexionSqlite.getInstancia(context).tarifaUnidadDestajoDAO()

            //Obteniendo Tarifas de la nube
            tarifaUnidadDestajoDAO.eliminarTodos()
            var q = 0
            val rsTarifas = EmpleadoDALC.PA_listarTarifas_Android()
            while (rsTarifas.next()) {
                q++

                val TUD_Codigo = rsTarifas.getLong("TUD_Codigo")
                val TUD_Nombre = rsTarifas.getString("TUD_Nombre")
                val Lab_Codigo = rsTarifas.getInt("Lab_Codigo")
                val UD_Codigo = rsTarifas.getInt("UD_Codigo")
                val TUD_Costo = rsTarifas.getFloat("TUD_Costo")
                val TUD_Desde =
                    FunGeneral.obtenerFechaHora_PorFormato_Nube_Long(rsTarifas.getString("Desde"))
                val TUD_Hasta =
                    FunGeneral.obtenerFechaHora_PorFormato_Nube_Long(rsTarifas.getString("Hasta"))
                val TUD_TarifaCerrada = rsTarifas.getString("TUD_TarifaCerrada")
                var obj = TarifaUnidadDestajo(
                    TUD_Codigo, TUD_Nombre, Lab_Codigo, UD_Codigo, TUD_Costo,
                    TUD_Desde, TUD_Hasta, TUD_TarifaCerrada
                )

                if (tarifaUnidadDestajoDAO.PA_ObtenerTarifa_PorCodigo(TUD_Codigo) == null) {
                    tarifaUnidadDestajoDAO.guardarTarifa(obj)
                } else {
                    tarifaUnidadDestajoDAO.actualizarTarifa(obj)
                }
            }

            return q
        }

        fun obtenerVacaciones(context: Context) : Int {
            val asistenciaPuertaDAO = ConexionSqlite.getInstancia(context).asistenciaauxiliarDAO()

            //Obteniendo Tarifas de la nube
            asistenciaPuertaDAO.eliminarTodos()
            var q = 0
            val rsDatos = EmpleadoDALC.PA_ListarVacaciones_PorFecha_Android()
            while (rsDatos.next()) {
                q++

                val AA_Fecha = rsDatos.getString("AA_Fecha")
                val AA_HorasVacaciones = rsDatos.getFloat("AA_HorasVacaciones")
                val PER_Codigo = rsDatos.getLong("PER_Codigo")

                var obj = AsistenciaAuxiliar(
                    0, AA_Fecha, AA_HorasVacaciones, PER_Codigo
                )

                asistenciaPuertaDAO.guardarAsistenciaAuxliar(obj)
            }

            return q
        }

        fun mostrarCargando(context: Context, title: String = "Cargando...", cancelable: Boolean = false) : ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setMessage(title);
            progressDialog.setCancelable(cancelable)
            return progressDialog
        }

        fun descargarData_TomarAsistencia(context: Context, PI_Codigo: Int) {

            val frm = FunGeneral.mostrarCargando(context, "Descargando...")
            frm.show()

            var a = 0
            var b = 0
            var c = 0
            var d = 0

            var msj = ""
            var error = false

            try{
                a = obtenerTiposDocumento(context)
                b = obtenerTrabajadores(context)
                c = obtenerSucursal(context)
                d = obtenerAsistenciasPuerta(context, PI_Codigo)
                //var r = obtenerVacaciones(context)

                msj = "Se sincronizaron:"+
                        "\n- Tipos de Doc.: ${a}"+
                        "\n- Trabajadores: ${b}"+
                        "\n- Sedes: ${c}"+
                        "\n- Asistencias: ${d}"
            }
            catch (e: Exception){
                msj = e.message.toString()
                error = true
            }


            frm.dismiss()

            if (error){
                Globales.showWarningMessage(msj, context)
            }else{
                Globales.showSuccessMessage(msj, context)
            }

        }

        fun obtenerTrabajadores(context: Context) : Int {
            var empleadoDAO = ConexionSqlite.getInstancia(context).empleadoDAO()

            var msj = ""
            var a = 0
            val rsEmpleados = EmpleadoDALC.PA_ListarEmpleados_Android()
            while (rsEmpleados.next()) {
                a++
                var PER_CODIGOQR = rsEmpleados.getString("PER_CodigoQR")
                val TD_CODIGO = rsEmpleados.getString("TD_Codigo")
                val PER_MATERNO = rsEmpleados.getString("PER_Materno")
                val PER_NRODOC = rsEmpleados.getString("PER_NroDoc")
                val PER_NOMBRE = rsEmpleados.getString("PER_Nombres")
                val PER_PATERNO = rsEmpleados.getString("PER_Paterno")
                val PER_DIRECCION = rsEmpleados.getString("PER_Direccion")
                val PER_FECHAREGISTRO = rsEmpleados.getString("PER_FechaRegistro")
                val PER_CODIGONUBE = rsEmpleados.getInt("PER_CodigoNube")
                val PER_ORIGEN = rsEmpleados.getString("PER_Origen")
                val PER_ESTADOSINCRONIZACION = rsEmpleados.getString("PER_EstadoSincronizacion")
                val SUC_Codigo = rsEmpleados.getInt("SUC_Codigo")

                if (PER_CODIGOQR == null) PER_CODIGOQR = PER_NRODOC
                if (BuscarEmpleadoXCNUBE(PER_CODIGONUBE, empleadoDAO) == null) {
                    if (BuscarEmpleadoNoSincronizado(PER_NRODOC, empleadoDAO) == null) {
                        addEmpleado(
                            PER_CODIGOQR,
                            TD_CODIGO,
                            PER_NRODOC,
                            PER_NOMBRE,
                            PER_PATERNO,
                            PER_MATERNO,
                            PER_DIRECCION,
                            PER_FECHAREGISTRO,
                            PER_CODIGONUBE,
                            PER_ORIGEN,
                            PER_ESTADOSINCRONIZACION,
                            SUC_Codigo,
                            empleadoDAO
                        )
                        //Log.i("TAG", "EL EMPLEADO REGISTRADO $PER_NOMBRE")
                    } else {
                        EditEmpleado(
                            PER_CODIGOQR,
                            TD_CODIGO,
                            PER_NRODOC,
                            PER_NOMBRE,
                            PER_PATERNO,
                            PER_MATERNO,
                            PER_DIRECCION,
                            PER_FECHAREGISTRO,
                            PER_CODIGONUBE,
                            PER_ORIGEN,
                            PER_ESTADOSINCRONIZACION,
                            -2,
                            SUC_Codigo,
                            empleadoDAO
                        )
                        //Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO $PER_NOMBRE")
                    }
                } else {

                    EditEmpleado(
                        PER_CODIGOQR,
                        TD_CODIGO,
                        PER_NRODOC,
                        PER_NOMBRE,
                        PER_PATERNO,
                        PER_MATERNO,
                        PER_DIRECCION,
                        PER_FECHAREGISTRO,
                        PER_CODIGONUBE,
                        PER_ORIGEN,
                        PER_ESTADOSINCRONIZACION,
                        -2,
                        SUC_Codigo,
                        empleadoDAO
                    )
                    //Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO2 $PER_NOMBRE")
                }
            }

            //Para obtener permisos
            obtenerPemisos(context)

            return a
        }

        fun obtenerPemisos(context: Context) : Int {
            return 0

            var permisosDAO = ConexionSqlite.getInstancia(context).permisosDAO()

            //Obteniendo Permisos
            var i = 0
            val rs = EmpleadoDALC.PA_ListarPermisosUsuario_Android(Globales.cod)

            permisosDAO.PA_EliminarPermisos()

            while (rs.next()) {
                i++
                val men_codigo = rs.getString("men_codigo")
                val men_nombre = rs.getString("men_nombre")

                permisosDAO.crear(Permisos(0, men_codigo, men_nombre))
            }

            visibleItemsMenu(context)

            return i
        }

        fun visibleItemsMenu(context: Context){
            if (Globales.cargarView == "NO"){
                return
            }

            var permisoDAO = ConexionSqlite.getInstancia(context).permisosDAO()

            var _no = false

            Globales.navView.menu.findItem(R.id.nav_home).isVisible = true
            Globales.navView.menu.findItem(R.id.nav_gallery).isVisible = false
            Globales.navView.menu.findItem(R.id.nav_slideshow).isVisible = false
            Globales.navView.menu.findItem(R.id.nav_configuration).isVisible = true

            Globales.inic.cargarInicio(R.id.nav_home)
        }

         fun obtenerTiposDocumento(context: Context) : Int {
            var tipoDocumentoDAO = ConexionSqlite.getInstancia(context).tipoDocumentoDAO()

            //Obteniendo Tipos de Documento de la nube
            var i = 0
            val rs = EmpleadoDALC.PA_ListarTipoDocumento()
            while (rs.next()) {
                i++
                val TD_Codigo = rs.getString("TD_Codigo")
                val TD_Descripcion = rs.getString("TD_Descripcion")

                if (tipoDocumentoDAO.PA_ObtenerTipoDocumento_PorCodigo(TD_Codigo) == null)  {
                    tipoDocumentoDAO.guardarTipoDocumento(TipoDocumento(TD_Codigo, TD_Descripcion))
                }
                else{
                    tipoDocumentoDAO.actualizarTipoDocumento(TD_Codigo, TD_Descripcion)
                }
            }

            return i
        }

        private fun BuscarEmpleadoXCNUBE(campo:Int, empleadoDAO: EmpleadoDAO): Empleado? {
            val empleadotop = empleadoDAO.obtenerEmpleadoXCNUBE(campo)?: return null
            return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
                empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
                empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
        }
        private fun BuscarEmpleadoXNroDoc(campo:String, empleadoDAO: EmpleadoDAO): Empleado? {
            val empleadotop =  empleadoDAO.obtenerEmpleadoXNroDoc(campo)?: return null
            return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
                empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
                empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
        }
        private fun BuscarEmpleadoNoSincronizado(campo: String, empleadoDAO: EmpleadoDAO): Empleado?{
            val empleadotop =  empleadoDAO.obtenerEmpleadoNoSincronizado(campo)?: return null
            return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
                empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
                empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
        }

        private fun addEmpleado (PER_CodigoQR: String, TD_Codigo: String, PER_NroDoc: String, PER_Nombre: String,
                                 PER_Paterno: String, PER_Materno: String="--", PER_Direccion: String="--", PER_FechaRegistro: String, PER_CodigoNube:Int,
                                 PER_Origen :String, PER_EstadoSincronizacion: String, SUC_Codigo: Int,
                                    empleadoDAO: EmpleadoDAO) {
            val obj = Empleado(0, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
                PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
                PER_Origen,PER_EstadoSincronizacion, SUC_Codigo, "", "")
            empleadoDAO.guardarEmpleado(obj)
        }

        private fun EditEmpleado (PER_CodigoQR: String, TD_Codigo: String, PER_NroDoc: String, PER_Nombre: String,
                                  PER_Paterno: String, PER_Materno: String, PER_Direccion: String, PER_FechaRegistro: String, PER_CodigoNube:Int,
                                  PER_Origen :String, PER_EstadoSincronizacion: String, PER_Codigo:Int= -2, SUC_Codigo: Int,
                                    empleadoDAO: EmpleadoDAO) {

            if (PER_Codigo != -2){
                empleadoDAO.EditarEmpleado(PER_Codigo, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
                    PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
                    PER_Origen,PER_EstadoSincronizacion, SUC_Codigo)
            }else{
                val empl = BuscarEmpleadoXNroDoc(PER_NroDoc, empleadoDAO)
                if (empl != null){
                    empleadoDAO.EditarEmpleado(empl.PER_Codigo, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
                        PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
                        PER_Origen,PER_EstadoSincronizacion, SUC_Codigo)
                    //Toast.makeText(requireContext() , "Empleado_sql Actualizado Satisfactoriamente", Toast.LENGTH_SHORT).show()

                }else{
                    //Toast.makeText(requireContext() , "Empleado_sql No se puede Actualizar por que no encontro su DNI", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun tieneConexionInternet(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }




    }

    override fun onCreate() {
        super.onCreate()
        var urlBase : String = "https://apiconsult.parmesac.com/"
        retrofitDoc = Retrofit.Builder().baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }


}