package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.CentroCosto
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Cultivo

@Dao
interface CentroCostoDAO {
    @Insert
    fun guardarCentroCosto(obj: CentroCosto): Long

    @Update
    fun actualizarCentroCosto(obj: CentroCosto): Int

    @Query("DELETE FROM CentroCosto")
    fun eliminarTodos()

    @Query("SELECT * FROM CentroCosto WHERE CC_Codigo=:id LIMIT 1")
    fun PA_ObtenerCentroCosto_PorCodigo(id: Long): CentroCosto

    @Query("SELECT * FROM CentroCosto ORDER BY CC_Descripcion asc")
    fun PA_ListaCentroCosto(): List<CentroCosto>

    @Query("SELECT * FROM CentroCosto where 1=1 and (:CodCultivo=-1 or CU_Codigo=:CodCultivo) and (:tipo='' or CC_Otros=:tipo) ORDER BY CC_Descripcion asc")
    fun PA_ListarCentroCosto_CultivoIncidencia(CodCultivo : Int, tipo: String): List<CentroCosto>
}