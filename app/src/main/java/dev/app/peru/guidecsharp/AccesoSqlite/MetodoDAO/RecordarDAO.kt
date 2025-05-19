package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Recordar

@Dao
interface RecordarDAO {
    @Insert
    fun crear(obj: Recordar)

    @Query("SELECT * FROM Recordar LIMIT 1")
    fun PA_UltimoRecordado(): Recordar

    @Query("DELETE FROM Recordar")
    fun PA_EliminarRecordado(): Int
}