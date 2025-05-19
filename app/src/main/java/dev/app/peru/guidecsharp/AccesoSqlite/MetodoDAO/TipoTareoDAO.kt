package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.TipoTareo
@Dao
interface TipoTareoDAO {
    @Insert
    fun guardarTipoTareo(obj: TipoTareo): Long

    @Query("SELECT * FROM TipoTareo")
    fun listarTipoTareo(): List<TipoTareo>
}