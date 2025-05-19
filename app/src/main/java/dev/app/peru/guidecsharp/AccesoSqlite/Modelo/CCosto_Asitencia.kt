package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CCosto_Asitencia(
    @PrimaryKey(autoGenerate = true)
    val AC_Codigo : Long=0,
    var CC_Codigo: Int,
    var AS_Codigo: Int,

    ) {
    override fun toString(): String = "$AC_Codigo $CC_Codigo"
}

//    fun envioDatosNube():String =
//        "$PER_Codigo|$PER_CodigoQR|$TD_Codigo|$PER_NroDoc|$PER_Nombres|$PER_Paterno|$PER_Materno|$PER_Direccion|$PER_FechaRegistro"

