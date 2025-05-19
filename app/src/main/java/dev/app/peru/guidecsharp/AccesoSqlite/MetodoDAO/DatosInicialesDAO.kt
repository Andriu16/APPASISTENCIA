package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.DatosIniciales
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Recordar
import java.util.Date

@Dao
interface DatosInicialesDAO {
    @Insert
    fun crear(obj: DatosIniciales)

    @Query("SELECT * FROM DatosIniciales LIMIT 1")
    fun PA_ObtenerDatosIniciales(): DatosIniciales

    @Query("DELETE FROM DatosIniciales")
    fun PA_EliminarDatosIniciales(): Int



}