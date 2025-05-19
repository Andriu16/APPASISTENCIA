package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TipoDocumento
@Dao
interface TipoDocumentoDAO {
    @Insert
    fun guardarTipoDocumento(obj: TipoDocumento)

    @Query("SELECT * FROM TipoDocumento WHERE TD_Descripcion like :buscar")
    fun buscadorTipoDocumentos(buscar : String): List<TipoDocumento>

    @Query("SELECT * FROM TipoDocumento")
    fun listarTipoDocumento(): List<TipoDocumento>

    @Query("SELECT * FROM TipoDocumento WHERE TD_Codigo=:id LIMIT 1")
    fun obtenerTipoDocumento(id: String): TipoDocumento

    @Query("SELECT * FROM TipoDocumento WHERE  TD_Descripcion= :valor  LIMIT 1")
    fun PA_ObtenerTipoDocumento_PorDescripcion(valor: String ): TipoDocumento

    @Query("SELECT * FROM TipoDocumento WHERE  TD_Codigo= :codigo  LIMIT 1")
    fun PA_ObtenerTipoDocumento_PorCodigo(codigo: String ): TipoDocumento

    @Query("UPDATE TipoDocumento SET TD_Descripcion=:descripcion WHERE TD_Codigo=:codigo")
    fun actualizarTipoDocumento(codigo: String, descripcion: String)
}