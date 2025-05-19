package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO

@Entity
data class AsistenciaPuerta(
    @PrimaryKey (autoGenerate = true)
    var AP_Codigo: Int,
    var AP_Fecha: String,
    var AP_HoraEntrada: String,
    var AP_HoraEntrada_Mostrar: String,
    var AP_HoraSalida: String,
    var AP_HoraSalida_Mostrar: String,
    var PER_Codigo: Int,
    var AP_Estado: String,
    var AP_EstadoSincronizacion: String
) {
    fun envioDatosNube():String = "$AP_Codigo|$AP_Fecha|$AP_HoraEntrada|$AP_HoraSalida"
    fun envioBackup(): String = "A00@$AP_Codigo|A01@$AP_Fecha|A02@$AP_HoraEntrada|A03@$AP_HoraEntrada_Mostrar|A04@$AP_HoraSalida|A05@$AP_HoraSalida_Mostrar|A06@$PER_Codigo|A07@$AP_Estado|A08@$AP_EstadoSincronizacion"
}