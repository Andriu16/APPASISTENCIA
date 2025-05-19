package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Packing
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TareoLaboresEmpleado_Productividad

@Dao
interface TareoLaboresEmpleadoDAO {
    @Insert
    fun guardarTareoLaboresEmpleado(obj: TareoLaboresEmpleado): Long

    @Query("UPDATE TareoLaboresEmpleado SET " +
            "TLE_Dia=:dia, TLE_Dia2=:dia2, TLE_Tarde=:tarde, TLE_Tarde2=:tarde2, " +
            "TLE_Noche=:noche, TLE_Noche2=:noche2, EstadoRevision=:revision, " +
            "TLE_HoraInicio=:iniDia, TLE_HoraFin=:finDia, " +
            "TLE_HoraInicioTarde=:iniTarde, TLE_HoraFinTarde=:finTarde, " +
            "TLE_HoraInicioNoche=:iniNoche, TLE_HoraFinNoche=:finNoche, " +
            "TLE_TotalHoras=:horas " +
            "WHERE idTareo=:idTareo " +
            "AND PER_CodigoObrero=:codigoObrero")
    fun actualizaHorasTrabajadas(idTareo: Long, dia: Int, dia2: Int, tarde: Int, tarde2: Int, noche: Int, noche2: Int, revision: String,
    iniDia: String, finDia: String, iniTarde: String, finTarde: String, iniNoche: String, finNoche: String, horas: Float, codigoObrero: Int) : Int

    @Query("DELETE FROM TareoLaboresEmpleado WHERE idDetalle=:idDetalle ")
    fun PA_EliminarTrabajador_PorCodigo(idDetalle: Long) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_TotalHoras=:valor, EstadoRevision='SI' WHERE idDetalle=:idDetalle ")
    fun actualizaHorasTrabajadas_JC(idDetalle: Long, valor: Float) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Dia=:valor, TLE_HoraInicio=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraDia(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Dia2=:valor, TLE_HoraFin=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraDia2(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Tarde=:valor, TLE_HoraInicioTarde=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraTarde(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Tarde2=:valor, TLE_HoraFinTarde=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraTarde2(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Noche=:valor, TLE_HoraInicioNoche=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraNoche(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET TLE_Noche2=:valor, TLE_HoraFinNoche=:fechahora WHERE idDetalle=:idDetalle ")
    fun actualizaHoraNoche2(idDetalle: Long, valor: Int, fechahora: String) : Int

    @Query("UPDATE TareoLaboresEmpleado SET " +
            "TLE_TotalProductividad=:totProductividad, " +
            "EstadoRevision=:revision " +
            "WHERE idTareo=:idTareo " +
            "AND PER_CodigoObrero=:codigoObrero")
    fun actualizaProductividad(idTareo: Long, codigoObrero: Int, totProductividad: Float, revision: String) : Int


    @Query("SELECT * FROM TareoLaboresEmpleado tt "+
            "LEFT JOIN Empleado ee on tt.PER_CodigoObrero=ee.PER_CodigoNube "+
            "WHERE idTareo=:idTareo " +
            "AND ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno || ee.PER_CodigoQR like :buscar "+
            "ORDER BY idDetalle asc")
    fun PA_ListarDetalleTareo_PorCodigo(idTareo: Long, buscar: String) : List<TareoLaboresEmpleado>

    @Query("SELECT * FROM TareoLaboresEmpleado WHERE idTareo=:idTareo AND PER_CodigoObrero=:idPersonal LIMIT 1")
    fun PA_ObtenerTareoLaboresEmpleado_PorCodigo(idTareo: Long, idPersonal: Int): TareoLaboresEmpleado

    @Query("SELECT * FROM TareoLaboresEmpleado WHERE idDetalle=:idDetalle LIMIT 1")
    fun PA_ObtenerTareoLaboresEmpleado_PorCodigoInterno(idDetalle: Long): TareoLaboresEmpleado

    @Query("SELECT * FROM TareoLaboresEmpleado tt " +
        "LEFT JOIN Empleado ee on tt.PER_CodigoObrero=ee.PER_CodigoNube "+
        " WHERE idTareo=:idTareo AND ee.PER_CodigoQR=:codigoQR LIMIT 1")
    fun PA_ObtenerTareoLaboresEmpleado_PorCodigoQR(idTareo: Long, codigoQR: String): TareoLaboresEmpleado

    @Query("DELETE FROM TareoLaboresEmpleado")
    fun eliminarTodos()
}