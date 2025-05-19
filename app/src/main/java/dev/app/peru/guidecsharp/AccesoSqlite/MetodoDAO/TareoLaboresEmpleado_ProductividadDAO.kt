package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad

@Dao
interface TareoLaboresEmpleado_ProductividadDAO {
    @Insert
    fun guardarTareoLaboresEmpleado_Productividad(obj: TareoLaboresEmpleado_Productividad): Long

    @Query("DELETE FROM TareoLaboresEmpleado_Productividad WHERE idDetalleProd=:idDetalleProd")
    fun eliminarDetalle(idDetalleProd: Long)

    @Query("UPDATE TareoLaboresEmpleado_Productividad SET " +
            "UNI_Codigo=:UNI_Codigo, " +
            "TLEP_Unidad=:TLEP_Unidad, TLEP_Cantidad=:TLEP_Cantidad, " +
            "TLEP_Precio=:TLEP_Precio, TLEP_Importe=:TLEP_Importe, " +
            "TLEP_UnidadDestajo=:TLEP_UnidadDestajo, TLEP_CantidadDestajo=:TLEP_CantidadDestajo, " +
            "TLEP_PrecioDestajo=:TLEP_PrecioDestajo, TLEP_TotalDestajo=:TLEP_TotalDestajo, " +
            "EstadoRevision=:Revisado " +
            "WHERE idDetalleProd=:idDetalleProd")
    fun actualizaProductividad(idDetalleProd: Long, UNI_Codigo: Long,
                               TLEP_Unidad: String, TLEP_Cantidad: Float,
    TLEP_Precio: Float, TLEP_Importe: Float,
    TLEP_UnidadDestajo: String, TLEP_CantidadDestajo: Float,
    TLEP_PrecioDestajo: Float, TLEP_TotalDestajo: Float, Revisado: String) : Int

    @Query("SELECT * FROM TareoLaboresEmpleado_Productividad tt "+
            "LEFT JOIN Unidades ee on tt.UNI_Codigo=ee.UNI_Codigo "+
            "WHERE tt.idTareo=:idTareo " +
            "AND tt.idDetalle=:idDetalle " +
            "ORDER BY tt.idDetalleProd asc")
    fun PA_ListarProductividad_PorTareoDetalle(idTareo: Long, idDetalle: Long) : List<TareoLaboresEmpleado_Productividad>

    @Query("DELETE FROM TareoLaboresEmpleado_Productividad WHERE idTareo=:idTareo AND idDetalle=:idDetalle")
    fun PA_EliminarDetalleTareoLabores(idTareo: Long, idDetalle: Long)

    @Query("SELECT * FROM TareoLaboresEmpleado WHERE idTareo=:idTareo LIMIT 1")
    fun PA_ObtenerTareoLaboresEmpleado_PorCodigo(idTareo: Int): TareoLaboresEmpleado

    @Query("DELETE FROM TareoLaboresEmpleado_Productividad")
    fun eliminarTodos()
}