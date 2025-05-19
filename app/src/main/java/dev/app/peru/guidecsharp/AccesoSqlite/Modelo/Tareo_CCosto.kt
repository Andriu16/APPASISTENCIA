package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class Tareo_CCosto(
    @PrimaryKey(autoGenerate = true)
    val TC_Codigo : Long=0,
    var TA_Codigo: Int,
    var CC_Codigo: Int,

    )