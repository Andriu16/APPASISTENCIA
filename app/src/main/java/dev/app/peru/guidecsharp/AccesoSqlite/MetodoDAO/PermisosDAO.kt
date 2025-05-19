package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Permisos

@Dao
interface PermisosDAO {
    @Insert
    fun crear(obj: Permisos)

    @Query("SELECT * FROM Permisos")
    fun PA_ListarPermisos(): List<Permisos>

    @Query("SELECT * FROM Permisos WHERE men_codigo=:codigo LIMIT 1")
    fun PA_ObtenerPermiso_PorCodigo(codigo: String): Permisos

    @Query("DELETE FROM Permisos")
    fun PA_EliminarPermisos(): Int
}