package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cultivo(
    @PrimaryKey()
    var CU_Codigo: Int,
    var CU_Descripcion: String,
    var CU_Estado: String,
){
    override fun toString(): String {
        return CU_Descripcion
    }
}