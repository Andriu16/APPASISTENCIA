package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Labores(
    @PrimaryKey()
    var Lab_Codigo: Long,
    var Lab_Descripcion: String,
    var Lab_Estado: String,
    var Lab_Otros: String,
    var AC_Codigo: Long
){
    override fun toString(): String {
        return Lab_Descripcion
    }
}