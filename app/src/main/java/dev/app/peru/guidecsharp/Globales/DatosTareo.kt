package dev.app.peru.guidecsharp.Globales

import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.AsistenciaPuerta

object DatosTareo {
    var PAC_Codigo: Int = -1
    var CU_Codigo: Int = -1
    var TA_Incidencia: String = ""
    var CC_Codigo: Long = -1
    var Lab_Codigo: Long = -1
    var AC_Codigo: Long = -1
    var TA_TipoTareo: String = ""

    fun reiniciarDatosTareo(){
        PAC_Codigo = -1
        CU_Codigo = -1
        TA_Incidencia = ""
        CC_Codigo = -1
        Lab_Codigo = -1
        AC_Codigo = -1
        TA_TipoTareo = ""
    }

    var _CodTareoSelect : Long = -1
    var _EstadoTareoSelect : String = ""

    var AP_Codigo : Int = -1
    var TA_Codigo : Long = -1
    var idDetalle : Long = -1
    var PER_CodigoObrero : Long = -1
    var idTareoDetalle_AddTrabajador : Long = -1
}