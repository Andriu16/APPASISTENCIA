package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaAuxiliar
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Actividad

@Dao
interface AsistenciaAuxiliarDAO {
    @Insert
    fun guardarAsistenciaAuxliar(obj: AsistenciaAuxiliar): Long

    @Query("DELETE FROM AsistenciaAuxiliar")
    fun eliminarTodos()

    @Query("SELECT * FROM AsistenciaAuxiliar WHERE PER_Codigo=:id AND AA_Fecha=:fecha LIMIT 1")
    fun PA_ObtenerAsistenciaAuxiliarDAO(id: Int, fecha: String): AsistenciaAuxiliar
}