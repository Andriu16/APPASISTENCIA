package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta

@Dao
interface AsistenciaPuertaDAO {
    @Insert
    fun guardarAsistencia(obj: AsistenciaPuerta) : Long

    @Query("SELECT * FROM AsistenciaPuerta")
    fun listarAsistencias(): List<AsistenciaPuerta>

    @Query("SELECT * FROM AsistenciaPuerta WHERE (AP_EstadoSincronizacion='NO SINCRONIZADO') OR (AP_EstadoSincronizacion='SINCRONIZADO 1/2' AND AP_HoraSalida!='')")
    fun listarAsistencias_ParaSincronizar(): List<AsistenciaPuerta>

    @Query("DELETE FROM AsistenciaPuerta WHERE AP_Codigo=:codigo")
    fun PA_EliminarAsistencia_PorCodigo(codigo: Int)

    @Query("SELECT * FROM AsistenciaPuerta WHERE AP_Codigo=:AP_Codigo LIMIT 1")
    fun PA_ObtenerAsistencia_PorCodigo(AP_Codigo: Int): AsistenciaPuerta

    @Query("SELECT * FROM AsistenciaPuerta WHERE PER_Codigo=:CodPersonal AND AP_Fecha=:fecha ORDER BY AP_Fecha asc LIMIT 1")
    fun PA_ObtenerAsistencia_PorEmpleadoFecha(CodPersonal: Int, fecha: String): AsistenciaPuerta

    @Query("SELECT * FROM AsistenciaPuerta WHERE PER_Codigo=:CodPersonal AND AP_Fecha=:fecha AND ifnull(AP_HoraSalida, '')='' ORDER BY AP_Fecha asc LIMIT 1")
    fun PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida(CodPersonal: Int, fecha: String): AsistenciaPuerta

    @Query("SELECT * FROM AsistenciaPuerta aa " +
            "LEFT JOIN Empleado ee on aa.PER_Codigo=ee.PER_Codigo "+
            "WHERE AP_Fecha=:campo "+
            "AND ee.PER_CodigoQR || ee.PER_NroDoc || ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno like :buscar "+
            "AND ((:solocampo='NO') OR (:solocampo='SI' AND aa.AP_HoraSalida='') OR (:solocampo='SIx' AND aa.AP_HoraSalida!=''))")
    fun listarAsistenciaPorFecha(campo: String, buscar: String, solocampo: String): List<AsistenciaPuerta>

    @Query("SELECT * FROM AsistenciaPuerta aa "+
            "INNER JOIN Empleado ee on aa.PER_Codigo=ee.PER_Codigo "+
            "WHERE aa.AP_Fecha=:campo and ee.SUC_Codigo=:sede "+
            "AND ee.PER_CodigoQR || ee.PER_NroDoc || ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno like :buscar " +
            "AND ((:solocampo='NO') OR (:solocampo='SI' AND aa.AP_HoraSalida='') OR (:solocampo='SIx' AND aa.AP_HoraSalida!=''))")
    fun listarAsistenciaPorFechaSede(campo: String, sede: Int, buscar: String, solocampo: String): List<AsistenciaPuerta>

    @Query("UPDATE AsistenciaPuerta SET AP_HoraEntrada=:horaentrada, AP_HoraEntrada_Mostrar=:horaEntradaMostrar WHERE AP_Codigo=:AP_Codigo")
    fun actualizarHoraEntrada(horaentrada: String, horaEntradaMostrar: String, AP_Codigo: Int) : Int

    @Query("UPDATE AsistenciaPuerta SET AP_HoraSalida=:horaSalida, AP_HoraSalida_Mostrar=:horaSalidaMostrar WHERE AP_Codigo=:AP_Codigo")
    fun actualizarHoraSalida(horaSalida: String, horaSalidaMostrar: String, AP_Codigo: Int) : Int

    @Query("UPDATE AsistenciaPuerta SET AP_HoraEntrada=:horaEntrada, AP_HoraSalida=:horaSalida, AP_EstadoSincronizacion=:estado WHERE AP_Codigo=:ap_codigo")
    fun actualizarHoraEntradaYSalida(horaEntrada: String, horaSalida: String, estado: String, ap_codigo: Int) : Int

    @Query("DELETE FROM AsistenciaPuerta")
    fun eliminarTodos()

    @Query("DELETE FROM AsistenciaPuerta WHERE AP_EstadoSincronizacion != 'NO SINCRONIZADO'")
    fun eliminarTodosSincronizados()
}