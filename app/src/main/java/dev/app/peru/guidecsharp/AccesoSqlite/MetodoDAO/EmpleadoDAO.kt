package dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO

import androidx.room.Dao

import androidx.room.Insert
import androidx.room.Query
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado



@Dao
interface EmpleadoDAO {
    @Insert
    fun guardarEmpleado(obj: Empleado) : Long

    @Query("SELECT * FROM Empleado WHERE PER_CodigoQR || PER_NroDoc || PER_Nombres || PER_Paterno || PER_Materno like :buscar")
    fun listarEmpleados(buscar : String): List<Empleado>

    //Para añadir trabajadores sin asistencia al tareo
    @Query("UPDATE Empleado SET SelectTareo=:xSeleccion")
    fun actualizarSeleccionados(xSeleccion: String)

    @Query("SELECT ee.* FROM Empleado ee " +
            "LEFT JOIN AsistenciaPuerta_Cap aa ON ee.PER_CodigoNube=aa.PER_CodigoNube " +
            "WHERE aa.id is null AND ee.SelectTareo like :xSeleccion AND ee.PER_CodigoNube<>-1 AND ee.PER_CodigoQR || ee.PER_NroDoc || ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno like :buscar ORDER BY ee.PER_Paterno ASC")
    fun listarEmpleados_ParaTareoSinAsistencia_Seleccionados(xSeleccion: String, buscar: String): List<Empleado>

    @Query("SELECT ee.* FROM Empleado ee " +
            "LEFT JOIN AsistenciaPuerta_Cap aa ON ee.PER_CodigoNube=aa.PER_CodigoNube " +
            "WHERE aa.id is null AND ee.PER_CodigoNube<>-1 AND ee.PER_CodigoQR || ee.PER_NroDoc || ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno like :buscar ORDER BY ee.PER_Paterno ASC")
    fun listarEmpleados_ParaTareoSinAsistencia(buscar: String): List<Empleado>

    @Query("SELECT ee.* FROM Empleado ee " +
            "LEFT JOIN AsistenciaPuerta_Cap aa ON ee.PER_CodigoNube=aa.PER_CodigoNube " +
            "WHERE aa.id is null AND ee.PER_CodigoNube<>-1 AND ee.PER_CodigoQR || ee.PER_NroDoc || ee.PER_Nombres || ee.PER_Paterno || ee.PER_Materno like :buscar AND ee.SUC_Codigo=:suc_codigo ORDER BY ee.PER_Paterno ASC")
    fun listarEmpleados_ParaTareoSinAsistencia_Sede(buscar: String, suc_codigo: Int): List<Empleado>

    @Query("UPDATE Empleado SET SelectTareo=:valor, PER_HoraEntrada=:PER_HoraEntrada WHERE PER_CodigoNube=:PER_CodigoNube")
    fun actualizarSeleccionEmpleado(valor: String, PER_CodigoNube: Long, PER_HoraEntrada: String)

    @Query("SELECT ee.* FROM Empleado ee " +
            "LEFT JOIN AsistenciaPuerta_Cap aa ON ee.PER_CodigoNube=aa.PER_CodigoNube " +
            "WHERE aa.id is null AND ee.PER_CodigoQR=:campo LIMIT 1")
    fun PA_ObtenerEmpleado_PorQR_ParaTareoSinAsistencia(campo :String): Empleado



    @Query("SELECT * FROM Empleado WHERE PER_EstadoSincronizacion=:estado and PER_CodigoQR || PER_NroDoc || PER_Nombres || PER_Paterno || PER_Materno like :buscar")
    fun listarEmpleados_PorEstado(estado : String, buscar : String): List<Empleado>

    @Query("SELECT * FROM Empleado")
    fun listarEmpleado(): List<Empleado>

    @Query("SELECT * FROM Empleado WHERE PER_CodigoNube=-1")
    fun listarEmpleadoNoSincronizado(): List<Empleado>

    @Query("SELECT * FROM Empleado WHERE PER_CodigoQR=:campo LIMIT 1")
    fun obtenerEmpleadoXQR(campo :String): Empleado

    @Query("SELECT * FROM Empleado WHERE PER_CodigoNube=:campo  LIMIT 1")
    fun obtenerEmpleadoXCNUBE(campo :Int): Empleado

    @Query("SELECT * FROM Empleado WHERE PER_Codigo=:campo  LIMIT 1")
    fun PA_ObtenerEmpleado_PorCodigo(campo :Int): Empleado

    @Query("SELECT * FROM Empleado WHERE PER_CodigoNube=:campo  LIMIT 1")
    fun PA_ObtenerEmpleado_PorCodigoNube(campo :Int): Empleado


    @Query("SELECT * FROM Empleado WHERE PER_NroDoc =:campo AND PER_CodigoNube = '-1' LIMIT 1")
    fun obtenerEmpleadoNoSincronizado(campo :String): Empleado

    @Query("SELECT * FROM Empleado WHERE PER_NroDoc =:campo LIMIT 1")
    fun obtenerEmpleadoXNroDoc(campo :String): Empleado


    @Query("UPDATE Empleado SET PER_CodigoQR =:per_CodigoQR ,TD_Codigo  =:td_Codigo ,PER_NroDoc =:per_NroDoc ," +
            "PER_Nombres=:per_Nombre,PER_Paterno=:per_Paterno,PER_Materno=:per_Materno,PER_Direccion=:per_Direccion," +
            "PER_FechaRegistro=:per_FechaRegistro,PER_CodigoNube=:per_CodigoNube,PER_Origen=:per_Origen,PER_EstadoSincronizacion=:per_EstadoSincronizacion, SUC_Codigo=:suc_Codigo " +
            "WHERE PER_Codigo=:per_Codigo")
    fun EditarEmpleado(per_Codigo: Int, per_CodigoQR: String,td_Codigo: String,
                       per_NroDoc: String, per_Nombre: String,per_Paterno: String,
                       per_Materno: String, per_Direccion: String,per_FechaRegistro: String,
                       per_CodigoNube: Int,per_Origen:String, per_EstadoSincronizacion: String, suc_Codigo: Int)

    @Query("SELECT * FROM Empleado WHERE PER_CodigoNube = -1")
    fun listarEmpleadosNoNube(): List<Empleado>

    @Query("SELECT * FROM Empleado WHERE PER_Codigo =:per_codigo LIMIT 1")
    fun Sincronizacionunitariaempleado(per_codigo:Int): List<Empleado>

    @Query("DELETE FROM Empleado")
    fun eliminarTodos()

}