package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TipoDocumento(
    @PrimaryKey //(autoGenerate = true)
    var TD_Codigo: String,
    var TD_Descripcion: String
) {
    override fun toString(): String {
        return TD_Descripcion
    }
}