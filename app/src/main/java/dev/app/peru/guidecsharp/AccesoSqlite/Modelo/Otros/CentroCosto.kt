package dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CentroCosto(
    @PrimaryKey()
    var CC_Codigo: Long,
    var CC_Descripcion: String,
    var CC_Estado: String,
    var CC_Otros: String,
    var CU_Codigo: Int,
){
    override fun toString(): String {
        return CC_Descripcion
    }
}