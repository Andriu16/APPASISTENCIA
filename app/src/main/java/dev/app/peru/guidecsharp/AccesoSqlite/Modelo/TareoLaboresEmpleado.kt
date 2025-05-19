package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import android.media.tv.TvView.TimeShiftPositionCallback
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.sql.Timestamp

@Entity
class TareoLaboresEmpleado(
    @PrimaryKey(autoGenerate = true)
    var idDetalle: Long,
    var idTareo: Long,
    var PER_CodigoObrero: Int,
    var TLE_HoraInicio: String,
    var TLE_HoraFin: String,
    var TLE_TotalHoras: Float,
    var TLE_EstadoAprobacion: String,
    var TLE_TotalProductividad: Float,
    var TLE_Turno: String,
    var TLE_HoraInicioTarde: String,
    var TLE_HoraFinTarde: String,
    var TLE_EntradaPuerta: String,
    var TLE_SalidaPuerta: String,
    var TLE_HoraInicioNoche: String,
    var TLE_HoraFinNoche: String,
    var TLE_Dia: Int,
    var TLE_Dia2: Int,
    var TLE_Tarde: Int,
    var TLE_Tarde2: Int,
    var TLE_Noche: Int,
    var TLE_Noche2: Int,
    var EstadoRevision: String
) {
    override fun toString(): String = "$idTareo|$idDetalle|$PER_CodigoObrero|$TLE_HoraInicio|$TLE_HoraFin|$TLE_TotalHoras|$TLE_TotalProductividad|$TLE_Turno|$TLE_HoraInicioTarde|$TLE_HoraFinTarde|$TLE_HoraInicioNoche|$TLE_HoraFinNoche|$TLE_Dia|$TLE_Tarde|$TLE_Noche|${TLE_Dia2}|${TLE_Tarde2}|${TLE_Noche2}"
    fun envioBackup():String = "A00@$idDetalle|A01@$idTareo|A02@$PER_CodigoObrero|A03@$TLE_HoraInicio|A04@$TLE_HoraFin|A05@$TLE_TotalHoras|A06@$TLE_EstadoAprobacion|A07@$TLE_TotalProductividad|A08@$TLE_Turno|A09@$TLE_HoraInicioTarde|A10@$TLE_HoraFinTarde|A11@$TLE_EntradaPuerta|A12@$TLE_SalidaPuerta|A13@$TLE_HoraInicioNoche|A14@$TLE_HoraFinNoche|A15@$TLE_Dia|A16@$TLE_Dia2|A17@$TLE_Tarde|A18@$TLE_Tarde2|A19@$TLE_Noche|A20@$TLE_Noche2|A21@$EstadoRevision"
}