package dev.app.peru.guidecsharp.AccesoSql.Modelo

class AsistenciaOnlineDTO {
    data class AsistenciaOnlineDTO(
    var AP_Codigo: Int,
    var AP_Fecha: String?,
    var AP_HoraEntrada: String?,
    var AP_HoraSalida: String?,
    var PER_Codigo: Int,
    var AP_EstadoSincronizacion: String?,
    var CodigoQR: String?,
    var Empleado: String?,
    var Puerta: String?,
    var Sede: String?
    )
}