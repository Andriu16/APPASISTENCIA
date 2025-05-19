package dev.app.peru.guidecsharp.AccesoSqlite.Modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import kotlin.coroutines.coroutineContext

@Entity
data class UnidadesDestajo(
    @PrimaryKey
    var UD_Codigo: Long,
    var UD_Nombre: Long,
    var Lab_Codigo: Int,
    var UNI_DescripcionTemp: String
){
    override fun toString(): String {
        return UNI_DescripcionTemp
    }
}