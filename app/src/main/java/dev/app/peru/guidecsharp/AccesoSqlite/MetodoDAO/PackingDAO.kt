package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Packing

@Dao
interface PackingDAO {
    @Insert
    fun guardarPacking(obj: Packing): Long

    @Query("DELETE FROM Packing")
    fun eliminarTodos()

    @Update
    fun actualizarPacking(obj: Packing): Int

    @Query("SELECT * FROM Packing WHERE PAC_Codigo=:id LIMIT 1")
    fun PA_ObtenerPacking_PorCodigo(id: Int): Packing

    @Query("SELECT * FROM Packing")
    fun PA_ListarPacking(): List<Packing>
}