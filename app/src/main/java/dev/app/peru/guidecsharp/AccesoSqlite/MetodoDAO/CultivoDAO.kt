package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Cultivo

@Dao
interface CultivoDAO {
    @Insert
    fun guardarCultivo(obj: Cultivo): Long

    @Update
    fun actualizarCultivo(obj: Cultivo): Int

    @Query("DELETE FROM Cultivo")
    fun eliminarTodos()

    @Query("SELECT * FROM Cultivo WHERE CU_Codigo=:id LIMIT 1")
    fun PA_ObtenerCultivo_PorCodigo(id: Int): Cultivo

    @Query("SELECT * FROM Cultivo ORDER BY CU_Descripcion asc")
    fun PA_ListarCultivo(): List<Cultivo>
}