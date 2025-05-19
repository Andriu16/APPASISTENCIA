package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Unidades

@Dao
interface UnidadesDAO {
    @Insert
    fun guardarUnidad(obj: Unidades): Long

    @Update
    fun actualizarUnidad(obj: Unidades): Int

    @Query("DELETE FROM Unidades")
    fun eliminarTodos()

    @Query("SELECT * FROM Unidades WHERE UNI_Codigo=:id LIMIT 1")
    fun PA_ObtenerUnidad_PorCodigo(id: Long): Unidades

    @Query("SELECT * FROM Unidades ORDER BY UNI_Descripcion asc")
    fun PA_ListarUnidad(): List<Unidades>
}