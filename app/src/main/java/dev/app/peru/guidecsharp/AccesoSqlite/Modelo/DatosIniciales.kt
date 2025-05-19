package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class DatosIniciales(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var PI_Codigo: Int,
    var PI_Descripcion: String


)