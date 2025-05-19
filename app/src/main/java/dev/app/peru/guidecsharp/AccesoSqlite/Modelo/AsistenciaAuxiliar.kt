package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AsistenciaAuxiliar(
    @PrimaryKey(autoGenerate = true)
    var AA_Codigo: Int,
    var AA_Fecha: String,
    var AA_HorasVacaciones: Float,
    var PER_Codigo: Long
)