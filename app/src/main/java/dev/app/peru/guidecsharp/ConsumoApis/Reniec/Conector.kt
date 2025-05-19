package dev.app.peru.guidecsharp.ConsumoApis.Reniec

interface Conector {
    fun exitoConsultaDni(empleado: EmpleadoApiResponse)
    fun fallaConsultaDni(error: String)


}