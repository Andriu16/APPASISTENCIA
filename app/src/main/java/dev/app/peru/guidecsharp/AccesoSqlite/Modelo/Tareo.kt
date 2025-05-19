package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Tareo(
    @PrimaryKey(autoGenerate = true)
    var idTareo: Long,
    var TA_Codigo: Int,
    var TA_FechaRegistro: String,
    var PER_CodigoSupervisor: Int,
    var PAC_Codigo: Int,
    var CU_Codigo: Int,
    var TA_Incidencia: String,
    var CC_Codigo: Long,
    var Lab_Codigo: Long,
    var AC_Codigo: Long,
    var TA_TipoTareo: String,
    var _Trabajadores: Int
){
    override fun toString(): String = "$idTareo|$TA_FechaRegistro|$PAC_Codigo|$CU_Codigo|$TA_Incidencia|$CC_Codigo|$Lab_Codigo|$AC_Codigo|$TA_TipoTareo"
    fun envioBackup():String = "A00@$idTareo|A01@$TA_Codigo|A02@$TA_FechaRegistro|A03@$PER_CodigoSupervisor|A04@$PAC_Codigo|A05@$CU_Codigo|A06@$TA_Incidencia|A07@$CC_Codigo|A08@$Lab_Codigo|A09@$AC_Codigo|A10@$TA_TipoTareo|A11@$_Trabajadores"
}
