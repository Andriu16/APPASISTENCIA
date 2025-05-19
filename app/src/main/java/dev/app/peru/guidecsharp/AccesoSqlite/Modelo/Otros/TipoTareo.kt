package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TipoTareo(
    @PrimaryKey(autoGenerate = true)
    val TT_Codigo: Long=0,
    var TT_Descripcion: String
)