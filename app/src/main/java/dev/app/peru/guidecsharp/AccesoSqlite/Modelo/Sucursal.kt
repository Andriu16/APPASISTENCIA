package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sucursal(
    @PrimaryKey()
    var SUC_Codigo: Long,
    var SUC_Descripcion: String,
    var SUC_Estado: String,
){
    override fun toString(): String {
        return SUC_Descripcion
    }
}