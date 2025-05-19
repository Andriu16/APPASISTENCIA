package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Labores
@Dao
interface LaboresDAO {
    @Insert
    fun guardarLabor(obj: Labores): Long

    @Update
    fun actualizarCentroCosto(obj: Labores): Int

    @Query("DELETE FROM Labores")
    fun eliminarTodos()

    @Query("SELECT * FROM Labores WHERE Lab_Codigo=:id LIMIT 1")
    fun PA_ObtenerLabores_PorCodigo(id: Long): Labores

    @Query("SELECT * FROM Labores ORDER BY Lab_Descripcion ASC")
    fun PA_ListarLabores(): List<Labores>

    @Query("SELECT * FROM Labores WHERE Lab_Otros=:incidencia ORDER BY Lab_Descripcion asc")
    fun PA_ListarLabores_PorIncidencia(incidencia: String): List<Labores>

    @Query("SELECT * FROM Labores WHERE Lab_Otros=:incidencia AND AC_Codigo=:ac_codigo ORDER BY Lab_Descripcion asc")
    fun PA_ListarLabores_PorIncidencia_Etapa(incidencia: String, ac_codigo: Long): List<Labores>
}