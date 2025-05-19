package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recordar(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var REC_CodigoUsuario: Int,
    var REC_Usuario: String,
    var REC_Contraseña: String,
    var CodUsuario: Int,
    var CodEmpleado: Int,
    var Empleado: String,
    var CodRol: Int,
    var Rol: String
)