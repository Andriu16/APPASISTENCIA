package dev.app.peru.guidecsharp.ConsumoApis.Reniec

data class EmpleadoApiResponse (
    var mensaje: String,
    var dni: String,
    var apellido_paterno: String,
    var apellido_materno: String,
    var direccion: String,
    var nombres: String
){
    override fun toString(): String {
        return "dni='$dni', paterno='$apellido_paterno', materno='$apellido_materno', nombres='$nombres'"
    }
}