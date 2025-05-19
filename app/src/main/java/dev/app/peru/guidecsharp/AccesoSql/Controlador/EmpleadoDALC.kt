package dev.app.peru.guidecsharp.AccesoSql.Controlador

import android.content.Context
import android.util.Log
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.DatosIniciales
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import java.sql.ResultSet

class EmpleadoDALC {
    companion object {
        fun PA_RegistrarEntradaAsistencia_Android(
            perCodigoNube: Int, fecha: String, horaEntrada: String, horaEntradaMostrar: String,
            piCodigo: Int, codUsuarioReg: Int
        ): ResultSet { // O modifica para devolver un tipo más útil si el SP lo hace
            val query = "EXEC RRHH.PA_RegistrarEntradaAsistencia_Android '$perCodigoNube', '$fecha', '$horaEntrada', '$horaEntradaMostrar', '$piCodigo', '$codUsuarioReg'"
            Log.d("EmpleadoDALC", "Ejecutando: $query")
            return FunGeneral.sql.Ejecutar(query, "INSERT_UPDATE") // Usa la acción correcta
        }

        fun PA_RegistrarSalidaAsistencia_Android(
            perCodigoNube: Int, fecha: String, horaSalida: String, horaSalidaMostrar: String,
            piCodigo: Int, codUsuarioReg: Int
        ): ResultSet { // O modifica para devolver un tipo más útil si el SP lo hace
            val query = "EXEC RRHH.PA_RegistrarSalidaAsistencia_Android '$perCodigoNube', '$fecha', '$horaSalida', '$horaSalidaMostrar', '$piCodigo', '$codUsuarioReg'"
            Log.d("EmpleadoDALC", "Ejecutando: $query")
            return FunGeneral.sql.Ejecutar(query, "INSERT_UPDATE") // Usa la acción correcta
        }

        fun PA_ObtenerUsuario_PorNick(_Usuario : String): ResultSet {
            val query = "exec DISPOSITIVO.PA_ObtenerUsuario_PorNick '" + _Usuario + "'"
            return FunGeneral.sql.Ejecutar(query,"SELECT")
        }

        fun obtenerLogo(): ResultSet {
            return FunGeneral.sql.Ejecutar("exec SISTEMA.PA_ObtenerLogoSistema '" + Globales.Tienda + "'","SELECT")
        }

        fun PA_ListarEmpleados_Android(): ResultSet {
            val query = "exec RRHH.PA_ListarEmpleados_Android"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarTipoDocumento(): ResultSet {
            val query = "exec PLANILLA.PA_ListarTipoDocumento ''"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarPermisosUsuario_Android(codPersonal : Int): ResultSet {
            val query = "exec dbo.PA_ListarPermisosUsuario_Android '${codPersonal}'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ProcesarEmpleados_Android(empleados: String): ResultSet {
            val query = "exec RRHH.PA_ProcesarEmpleados_Android '$empleados'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ProcesarAsistenciaEmpleados_Android_ult2(asistencias: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = "exec RRHH.PA_ProcesarAsistenciaEmpleados_Android_ult2 '$asistencias', $codPersonalRegistro"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ObtenerMovimientosJE_Android(MOV_Codigo: Int): ResultSet {
            val query = "exec dbo.PA_ObtenerMovimientosJE_Android '$MOV_Codigo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ObtenerMovimientoJE_AndroidValidar(MOV_Codigo: Int): ResultSet {
            val query = "exec dbo.PA_ObtenerMovimientoJE_AndroidValidar '$MOV_Codigo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ProcesarAsistenciaEmpleados_Android_ult2_test(asistencias: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = "exec RRHH.PA_ProcesarAsistenciaEmpleados_Android_ult2_test '$asistencias', '$codPersonalRegistro'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ProcesarAsistenciaEmpleados_Android_ult3(asistencias: String, PI_Codigo: Int): ResultSet {
            val codPersonalRegistro = Globales.cod

            val query = "exec RRHH.PA_ProcesarAsistenciaEmpleados_Android_ult3 '$asistencias', '$codPersonalRegistro', '$PI_Codigo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_BackupDispositivo(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String, _asistencias: String, _empleados: String, _tareos: String, _labores: String, _productividad: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_BackupDispositivo_2 '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro', '$_asistencias', '$_empleados', '$_tareos', '$_labores', '$_productividad' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_RestaurarBackupDispositivo_AsistenciaPuerta(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_RestaurarBackupDispositivo_AsistenciaPuerta '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_RestaurarBackupDispositivo_Empleado(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_RestaurarBackupDispositivo_Empleado '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_RestaurarBackupDispositivo_Tareo(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_RestaurarBackupDispositivo_Tareo '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_RestaurarBackupDispositivo_TareoLaboresEmpleado(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_RestaurarBackupDispositivo_TareoLaboresEmpleado '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_RestaurarBackupDispositivo_TareoLaboresEmpleado_Productividad(EQU_CodigoUnico: String, EQU_Nombre: String, EQU_UltimoIP: String): ResultSet {
            val codPersonalRegistro = Globales.cod
            val query = " exec DISPOSITIVO.PA_RestaurarBackupDispositivo_TareoLaboresEmpleado_Productividad '$EQU_CodigoUnico', '$EQU_Nombre', '$EQU_UltimoIP', '$codPersonalRegistro' "
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ProcesarTareos_Android(datosTareos: String, datosLabores: String, datosProductividad: String, codSupervisor: Long): ResultSet {
            val query = "exec RRHH.PA_ProcesarTareos_Android2 '$datosTareos', '$datosLabores', '$datosProductividad', '$codSupervisor'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarAsistenciaPuerta_Android(fecha: String): ResultSet {
            val query = "exec dbo.PA_ListarAsistenciaPuerta_Android '$fecha'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarTareo_Android(fecha: String, tipotareo: String, bb: String): ResultSet {
            val query = "exec dbo.PA_ListarTareo_Android '$fecha', '$tipotareo', '$bb'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        //Luego se va a separar en sus respectivas clases DALC
        fun PA_ListarPacking(): ResultSet {
            val query = "exec dbo.PA_ListarPacking"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarCultivo(): ResultSet {
            val query = "exec dbo.PA_ListarCultivo"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarCentroCosto(): ResultSet {
            val query = "exec dbo.PA_ListarCentroCosto"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarLabores(): ResultSet {
            val query = "exec dbo.PA_ListarLabores"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarActividad(): ResultSet {
            val query = "exec dbo.PA_ListarActividad"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarAsistenciaPuerta_Android_Online(fecha: String, buscar: String, solocampo: String): ResultSet {
            val query = "exec dbo.PA_ListarAsistenciaPuerta_Android_Online '$fecha', '$buscar', '$solocampo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida_Android(CodigoQR: String, fecha: String): ResultSet {
            val query = "exec dbo.PA_ObtenerAsistencia_PorEmpleadoFecha_SinSalida_Android '$CodigoQR', '$fecha'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ActualizarHoraSalida_Android(AP_Codigo: Int, AP_HoraSalida: String, PI_Codigo: Int, PER_Codigo: Int): ResultSet {
            val query = "exec dbo.PA_ActualizarHoraSalida_Android '$AP_Codigo', '$AP_HoraSalida', '$PI_Codigo', '$PER_Codigo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarPuertaIngreso_Android(): ResultSet {
            val query = "exec dbo.PA_ListarPuertaIngreso_Android"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarSede(): ResultSet {
            val query = "exec PLANILLA.PA_ListarSede ''"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarAsistenciasPuerta_Android(PI_Codigo: Int): ResultSet {
            val query = "exec dbo.PA_ListarAsistenciasPuerta_Android_2 '$PI_Codigo'"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarUnidades(): ResultSet {
            val query = "exec dbo.PA_ListarUnidades ''"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarUnidadDestajo_Android(): ResultSet {
            val query = "exec dbo.PA_ListarUnidadDestajo_Android"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_listarTarifas_Android(): ResultSet {
            val query = "exec dbo.PA_listarTarifas_Android"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarVacaciones_PorFecha_Android(): ResultSet {
            val query = "exec dbo.PA_ListarVacaciones_PorFecha_Android"
            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }

        fun PA_ListarGrupoTrabajo_Android(): ResultSet {
            val query = "exec dbo.PA_ListarGrupoTrabajo_Android"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }

        fun PA_ListarTurno_Android(): ResultSet {
            val query = "exec dbo.PA_ListarTurno_Android"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }

        fun PA_ListarPersonal_Android(buscar: String): ResultSet {
            val query = "exec dbo.PA_ListarPersonal_Android '${buscar}'"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }

        fun PA_ListarActividad_Android(): ResultSet {
            val query = "exec dbo.PA_ListarActividad_Android"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }

        fun PA_ListarLabor_Android(IDACTIVIDAD: String): ResultSet {
            val query = "exec dbo.PA_ListarLabor_Android '${IDACTIVIDAD}'"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }

        fun PA_ListarConsumidor_Android(): ResultSet {
            val query = "exec dbo.PA_ListarConsumidor_Android"
            return FunGeneral.sql2.Ejecutar(query, "SELECT")
        }
//online add
        fun PA_ObtenerEmpleadoPorQR_Android(qrCode: String): ResultSet {

            // Asegúrate de poner comillas simples alrededor del parámetro varchar
            val query = "exec RRHH.PA_ObtenerEmpleadoPorQR_Android '$qrCode'"
            Log.d("EmpleadoDALC", "Ejecutando: $query") // Es bueno añadir logs para depurar

            return FunGeneral.sql.Ejecutar(query, "SELECT")
        }
    }
}