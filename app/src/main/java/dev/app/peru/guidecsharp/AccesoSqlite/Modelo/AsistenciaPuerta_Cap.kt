package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AsistenciaPuerta_Cap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var PER_CodigoQR: String,
    var PER_CodigoNube: Int,
    var Trabajador: String,
    var Fecha: String,
    var HoraEntrada: String,
    var HoraSalida: String,
    var SUC_Codigo: Int,
    var Sede: String,
    var xSelect: String,
    var HoraInicio: String
)