package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Supervisor

@Dao
interface SupervisorDAO {
    @Insert
    fun guardarSupervisor(obj: Supervisor): Long

    @Query("SELECT * FROM Supervisor")
    fun listarSupervisor(): List<Supervisor>
}