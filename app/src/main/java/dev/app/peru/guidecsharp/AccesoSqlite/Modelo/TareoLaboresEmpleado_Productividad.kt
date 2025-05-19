package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TareoLaboresEmpleado_Productividad (
    @PrimaryKey(autoGenerate = true)
    var idDetalleProd: Long,
    var idTareo: Long,
    var idDetalle: Long,
    var UNI_Codigo: Int,
    var TLEP_Unidad: String,
    var TLEP_Cantidad: Float,
    var TLEP_Precio: Float,
    var TLEP_Importe: Float,
    var TLEP_UnidadDestajo: String,
    var TLEP_CantidadDestajo: Float,
    var TLEP_PrecioDestajo: Float,
    var TLEP_TotalDestajo: Float,
    var EstadoRevision: String
){
    override fun toString(): String = "$idTareo|$idDetalle|$UNI_Codigo|$TLEP_Unidad|$TLEP_Cantidad|$TLEP_Precio|$TLEP_Importe|$TLEP_UnidadDestajo|$TLEP_CantidadDestajo|$TLEP_PrecioDestajo|$TLEP_TotalDestajo"
    fun envioBackup():String = "A00@$idDetalleProd|A01@$idTareo|A02@$idDetalle|A03@$UNI_Codigo|A04@$TLEP_Unidad|A05@$TLEP_Cantidad|A06@$TLEP_Precio|A07@$TLEP_Importe|A08@$TLEP_UnidadDestajo|A09@$TLEP_CantidadDestajo|A10@$TLEP_PrecioDestajo|A11@$TLEP_TotalDestajo|A12@$EstadoRevision"
}