package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Packing(
    @PrimaryKey()
    var PAC_Codigo: Int,
    var PAC_Descripcion: String,
    var PAC_Estado: String,
){
    override fun toString(): String {
        return PAC_Descripcion
    }
}