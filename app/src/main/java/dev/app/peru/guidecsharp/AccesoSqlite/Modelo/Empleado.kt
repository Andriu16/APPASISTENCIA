package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Empleado(
    @PrimaryKey(autoGenerate = true)
    val PER_Codigo: Int,
    var PER_CodigoQR: String,
    var TD_Codigo: String,
    var PER_NroDoc: String,
    var PER_Nombres: String,
    var PER_Paterno: String,
    var PER_Materno: String,
    var PER_Direccion: String,
    var PER_FechaRegistro: String,
    var PER_CodigoNube: Int,
    var PER_Origen:String,
    var PER_EstadoSincronizacion: String,
    var SUC_Codigo: Int,
    var SelectTareo: String,
    var PER_HoraEntrada: String
){
    override fun toString(): String = "$PER_Codigo $PER_CodigoQR"

    fun envioDatosNube(): String = "$PER_Codigo|$PER_CodigoQR|$TD_Codigo|$PER_NroDoc|$PER_Nombres|$PER_Paterno|$PER_Materno|$PER_Direccion|$PER_FechaRegistro"
    fun envioBackup():String = "COD@$PER_Codigo|QR@$PER_CodigoQR|TD@$TD_Codigo|DOC@$PER_NroDoc|NOM@$PER_Nombres|PAT@$PER_Paterno|MAT@$PER_Materno|DIR@${PER_Direccion.replace("'", "")}|REG@$PER_FechaRegistro|NUBE@$PER_CodigoNube|ORI@$PER_Origen|SIN@$PER_EstadoSincronizacion|SUC@$SUC_Codigo|SEL@$SelectTareo|HE@$PER_HoraEntrada"

    fun nombres_apellidos(): String = "${PER_Nombres} ${PER_Paterno} ${PER_Materno}"
}
