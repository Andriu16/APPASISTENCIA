package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Sucursal

@Dao
interface SucursalDAO {
    @Insert
    fun guardarSucursal(obj: Sucursal): Long

    @Update
    fun actualizarSucursal(obj: Sucursal): Int

    @Query("DELETE FROM Sucursal")
    fun eliminarTodos()

    @Query("SELECT * FROM Sucursal WHERE SUC_Codigo=:id LIMIT 1")
    fun PA_ObtenerSucursal_PorCodigo(id: Long): Sucursal

    @Query("SELECT * FROM Sucursal ORDER BY SUC_Descripcion asc")
    fun PA_ListarSucursal(): List<Sucursal>
}