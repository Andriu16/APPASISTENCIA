package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Actividad(
    @PrimaryKey()
    var AC_Codigo: Long,
    var AC_Descripcion: String,
    var AC_Estado: String,
){
    override fun toString(): String {
        return AC_Descripcion
    }
}