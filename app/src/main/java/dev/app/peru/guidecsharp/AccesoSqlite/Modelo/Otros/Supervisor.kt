package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Supervisor(
    @PrimaryKey(autoGenerate = true)
    val SU_Codigo: Long=0,
    var SU_Nombres: String,

)