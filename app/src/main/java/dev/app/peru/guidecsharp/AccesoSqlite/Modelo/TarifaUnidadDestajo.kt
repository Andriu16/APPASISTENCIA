package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TarifaUnidadDestajo(
    @PrimaryKey
    var TUD_Codigo: Long,
    var TUD_Nombre: String,
    var Lab_Codigo: Int,
    var UD_Codigo: Int,
    var UD_Costo: Float,
    var TUD_Desde: Long,
    var TUD_Hasta: Long,
    var TUD_TarifaCerrada: String
)