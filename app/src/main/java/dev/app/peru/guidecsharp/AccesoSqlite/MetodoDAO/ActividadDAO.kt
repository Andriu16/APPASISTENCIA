package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Actividad
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.CentroCosto

@Dao
interface ActividadDAO {
    @Insert
    fun guardarActividad(obj: Actividad): Long

    @Update
    fun actualizarActividad(obj: Actividad): Int

    @Query("DELETE FROM Actividad")
    fun eliminarTodos()

    @Query("SELECT * FROM Actividad WHERE AC_Codigo=:id LIMIT 1")
    fun PA_ObtenerActividad_PorCodigo(id: Long): Actividad

    @Query("SELECT * FROM Actividad ORDER BY AC_Descripcion asc")
    fun PA_ListarActividad(): List<Actividad>
}