package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo_CCosto

@Dao
interface Tareo_CCostoDAO {
    @Insert
    fun guardarTareo_CCosto(obj: Tareo_CCosto): Long

    @Query("SELECT * FROM Tareo_CCosto")
    fun listarTareo_CCosto(): List<Tareo_CCosto>
}