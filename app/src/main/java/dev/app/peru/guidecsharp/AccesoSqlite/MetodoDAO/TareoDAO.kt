package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo

@Dao
interface TareoDAO {
    @Insert
    fun guardarTareo(obj: Tareo): Long

    @Query("DELETE FROM Tareo WHERE TA_Codigo=:idTareo")
    fun PA_EliminarTareo_PorCodigo(idTareo: Long)

    @Query("UPDATE Tareo SET TA_Codigo=:TA_Codigo WHERE idTareo=:idTareo")
    fun actualizarEstado(idTareo: Long, TA_Codigo: Long)

    @Query("UPDATE Tareo SET PAC_Codigo=:pac_codigo, " +
            "CU_Codigo=:cu_codigo, TA_Incidencia=:ta_incidencia, " +
            "CC_Codigo=:cc_codigo, Lab_Codigo=:lab_codigo, " +
            "AC_Codigo=:ac_codigo, TA_TipoTareo=:ta_tipotareo " +
            "WHERE idTareo=:idTareo")
    fun actualizarTareo(pac_codigo: Long, cu_codigo: Long, ta_incidencia: String, cc_codigo: Long,
                        lab_codigo: Long, ac_codigo: Long, ta_tipotareo: String, idTareo: Long)

    @Query("UPDATE Tareo SET _Trabajadores=:cuenta WHERE idTareo=:idTareo")
    fun actualizarCantTrabajadores(idTareo: Long, cuenta: Int)

    @Query("SELECT * FROM Tareo WHERE idTareo=:id LIMIT 1")
    fun PA_ObtenerTareo_PorCodigo(id: Long): Tareo

    @Query("SELECT * FROM Tareo tt " +
            "INNER JOIN Labores ll ON tt.Lab_Codigo=ll.Lab_Codigo " +
            "WHERE substr(TA_FechaRegistro, 1, 8)=:fecha " +
            "AND ll.Lab_Descripcion like :buscar " +
            "AND tt.TA_TipoTareo like :tipotareo " +
            "ORDER BY TA_FechaRegistro asc")
    fun PA_ListarTareo(fecha: String, buscar: String, tipotareo: String): List<Tareo>

    @Query("SELECT * FROM Tareo")
    fun listarTareos(): List<Tareo>

    @Query("DELETE FROM Tareo")
    fun eliminarTodos()
}