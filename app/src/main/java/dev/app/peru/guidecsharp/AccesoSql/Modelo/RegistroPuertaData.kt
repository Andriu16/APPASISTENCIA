package dev.app.peru.guidecsharp.AccesoSql.Modelo

data class RegistroPuertaData(
    val riCodigo: Int? = null,
    val fechaRegistro: String,      // Formato 'yyyy-MM-dd HH:mm:ss'
    val fechaSistema: String,       // Formato 'yyyy-MM-dd HH:mm:ss'
    val tipoEvento: String,
    val dni: String,
    val nombresApellidos: String,
    val area: String,
    val motivoIngreso: String,
    val placaVehiculo: String?,     // Nullable
    val personalAutorizo: String?,  // Nullable - Usaremos Globales.Empleado?
    val observacion: String?,       // Nullable
    val otros1: String? = null,     // Nullable, valor por defecto null
    val otros2: String? = null,     // Nullable, valor por defecto null
    val estado: String? = null,      // Nullable, valor por defecto null

    val img1: ByteArray? = null,    // Nullable, para varbinary(max)
    val img2: ByteArray? = null,    // Nullable, para varbinary(max)
    val img3: ByteArray? = null,    // Nullable, para varbinary(max)
    val img4: ByteArray? = null     // Nullable, para varbinary(max)
){
    // --- Sobreescribir equals y hashCode para manejar ByteArray correctamente ---
    // Es buena práctica si planeas comparar instancias o usarlas en colecciones (Set, Map keys)



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegistroPuertaData

        if (fechaRegistro != other.fechaRegistro) return false
        if (fechaSistema != other.fechaSistema) return false
        if (tipoEvento != other.tipoEvento) return false
        if (dni != other.dni) return false
        if (nombresApellidos != other.nombresApellidos) return false
        if (area != other.area) return false
        if (motivoIngreso != other.motivoIngreso) return false
        if (placaVehiculo != other.placaVehiculo) return false
        if (personalAutorizo != other.personalAutorizo) return false
        if (observacion != other.observacion) return false
        if (otros1 != other.otros1) return false
        if (otros2 != other.otros2) return false
        if (estado != other.estado) return false
        if (img1 != null) {
            if (other.img1 == null) return false
            if (!img1.contentEquals(other.img1)) return false
        } else if (other.img1 != null) return false
        if (img2 != null) {
            if (other.img2 == null) return false
            if (!img2.contentEquals(other.img2)) return false
        } else if (other.img2 != null) return false
        if (img3 != null) {
            if (other.img3 == null) return false
            if (!img3.contentEquals(other.img3)) return false
        } else if (other.img3 != null) return false
        if (img4 != null) {
            if (other.img4 == null) return false
            if (!img4.contentEquals(other.img4)) return false
        } else if (other.img4 != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fechaRegistro.hashCode()
        result = 31 * result + fechaSistema.hashCode()
        result = 31 * result + tipoEvento.hashCode()
        result = 31 * result + dni.hashCode()
        result = 31 * result + nombresApellidos.hashCode()
        result = 31 * result + area.hashCode()
        result = 31 * result + motivoIngreso.hashCode()
        result = 31 * result + (placaVehiculo?.hashCode() ?: 0)
        result = 31 * result + (personalAutorizo?.hashCode() ?: 0)
        result = 31 * result + (observacion?.hashCode() ?: 0)
        result = 31 * result + (otros1?.hashCode() ?: 0)
        result = 31 * result + (otros2?.hashCode() ?: 0)
        result = 31 * result + (estado?.hashCode() ?: 0)
        result = 31 * result + (img1?.contentHashCode() ?: 0)
        result = 31 * result + (img2?.contentHashCode() ?: 0)
        result = 31 * result + (img3?.contentHashCode() ?: 0)
        result = 31 * result + (img4?.contentHashCode() ?: 0)
        return result
    }



}
