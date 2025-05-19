package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Permisos (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var men_codigo: String,
    var men_nombre: String
)