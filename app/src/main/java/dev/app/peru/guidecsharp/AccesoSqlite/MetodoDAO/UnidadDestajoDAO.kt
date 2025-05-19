package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.UnidadesDestajo

@Dao
interface UnidadDestajoDAO {
    @Insert
    fun guardarUnidadDestajo(obj: UnidadesDestajo): Long

    @Update
    fun actualizarUnidadDestajo(obj: UnidadesDestajo): Int

    @Query("DELETE FROM UnidadesDestajo")
    fun eliminarTodos()

    @Query("SELECT * FROM UnidadesDestajo WHERE UD_Codigo=:id LIMIT 1")
    fun PA_ObtenerUnidadDestajo_PorCodigo(id: Long): UnidadesDestajo

    @Query("SELECT * FROM UnidadesDestajo ORDER BY UD_Codigo asc")
    fun PA_ListarUnidadDestajo(): List<UnidadesDestajo>

    @Query("SELECT * FROM UnidadesDestajo dd " +
            "INNER JOIN Unidades uu ON dd.UD_Nombre=uu.UNI_Codigo "+
            "WHERE dd.Lab_Codigo=:Lab_Codigo "+
            "ORDER BY uu.UNI_Descripcion asc")
    fun PA_ListarUnidadDestajo_PorLabor(Lab_Codigo: Long): List<UnidadesDestajo>
}