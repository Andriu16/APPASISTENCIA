package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TarifaUnidadDestajo

@Dao
interface TarifaUnidadDestajoDAO {
    @Insert
    fun guardarTarifa(obj: TarifaUnidadDestajo): Long

    @Update
    fun actualizarTarifa(obj: TarifaUnidadDestajo): Int

    @Query("DELETE FROM TarifaUnidadDestajo")
    fun eliminarTodos()

    @Query("SELECT * FROM TarifaUnidadDestajo WHERE TUD_Codigo=:id LIMIT 1")
    fun PA_ObtenerTarifa_PorCodigo(id: Long): TarifaUnidadDestajo

    @Query("SELECT * FROM TarifaUnidadDestajo ORDER BY TUD_Codigo asc")
    fun PA_ListarTarifa(): List<TarifaUnidadDestajo>

    @Query("SELECT * FROM TarifaUnidadDestajo dd " +
            "WHERE dd.Lab_Codigo=:codlabor " +
            "AND dd.UD_Codigo=:codunidad " +
            "AND ( :fecha >= dd.TUD_Desde AND ( (dd.TUD_TarifaCerrada='NO' OR (dd.TUD_TarifaCerrada='SI' and :fecha <= dd.TUD_Hasta)) ) ) LIMIT 1")
    fun PA_ObtenerTarifa_PorLabor_Unidad(codlabor : Long, codunidad : Long, fecha : Long): TarifaUnidadDestajo
}