package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Unidades(
    @PrimaryKey
    var UNI_Codigo: Long,
    var UNI_Descripcion: String,
) {
    override fun toString(): String {
        return UNI_Descripcion
    }
}