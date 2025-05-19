package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta_Cap

@Dao
interface AsistenciaPuerta_CapDAO {
    @Insert
    fun guardarAsistencia_Cap(obj: AsistenciaPuerta_Cap)

    @Query("DELETE FROM AsistenciaPuerta_Cap")
    fun eliminarAsistencia_Cap()

    @Query("UPDATE AsistenciaPuerta_Cap SET xSelect=:valor, HoraInicio=:horainicio WHERE id=:codigo")
    fun actualizarSeleccion(codigo: Int, valor: String, horainicio: String)

    @Query("SELECT * FROM AsistenciaPuerta_Cap aa "+
            "WHERE aa.Fecha=:fecha "+
            "AND aa.PER_CodigoQR=:codigoQR LIMIT 1")
    fun obtenerAsistencia_Cap_PorCodigoQR(fecha: String, codigoQR: String): AsistenciaPuerta_Cap

    @Query("SELECT * FROM AsistenciaPuerta_Cap aa "+
            "WHERE aa.Fecha=:fecha "+
            "AND aa.xSelect like :xseleccion "+
            "AND aa.Trabajador || aa.PER_CodigoQR like :trab")
    fun listarAsistencia_CapPorFecha_Seleccionados(fecha: String, xseleccion: String, trab: String): List<AsistenciaPuerta_Cap>

    @Query("SELECT * FROM AsistenciaPuerta_Cap aa "+
            "WHERE aa.Fecha=:fecha " +
            "AND aa.Trabajador || aa.PER_CodigoQR like :trab")
    fun listarAsistencia_CapPorFecha(fecha: String, trab: String): List<AsistenciaPuerta_Cap>

    @Query("SELECT * FROM AsistenciaPuerta_Cap aa "+
            "WHERE aa.Fecha=:fecha and aa.SUC_Codigo=:sede "+
            "AND aa.Trabajador || aa.PER_CodigoQR like :trab")
    fun listarAsistencia_CapPorFechaSede(fecha: String, sede: Int, trab: String): List<AsistenciaPuerta_Cap>
}