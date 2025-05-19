package dev.app.peru.guidecsharp.AccesoSqlite.Controlador

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.*
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.TarifaUnidadDestajoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*

@Database(
    entities = [
        Recordar::class, TipoDocumento::class, Empleado::class, AsistenciaPuerta::class,
        CCosto_Asitencia::class, CentroCosto::class, Tareo::class, Cultivo::class,
        Packing::class, Labores::class, Supervisor::class, TipoTareo::class,Tareo_CCosto::class,
        Actividad::class, Sucursal::class, AsistenciaPuerta_Cap::class, TareoLaboresEmpleado::class,
        Unidades::class, UnidadesDestajo::class, TarifaUnidadDestajo::class,
        TareoLaboresEmpleado_Productividad::class, Permisos::class, AsistenciaAuxiliar::class,
        DatosIniciales::class
    ],
    version = 3
)
abstract class ConexionSqlite : RoomDatabase() {
    abstract fun recordarDAO(): RecordarDAO
    abstract fun tipoDocumentoDAO(): TipoDocumentoDAO
    abstract fun empleadoDAO(): EmpleadoDAO
    abstract fun asistenciaPuertaDAO(): AsistenciaPuertaDAO
    abstract fun centroCostoDAO(): CentroCostoDAO
    abstract fun cCostoAsistenciaDAO(): CCosto_AsistenciaDAO
    abstract fun tareoDAO(): TareoDAO
    abstract fun cultivoDAO(): CultivoDAO
    abstract fun packingDAO(): PackingDAO
    abstract fun laboresDAO(): LaboresDAO
    abstract fun supervisorDAO(): SupervisorDAO
    abstract fun tipoTareoDAO(): TipoTareoDAO
    abstract fun tareo_CCostoDAO(): Tareo_CCostoDAO
    abstract fun actividadDAO(): ActividadDAO
    abstract fun sucursalDAO(): SucursalDAO
    abstract fun asistenciaPuerta_CapDAO() : AsistenciaPuerta_CapDAO
    abstract fun tareoLaboresEmpleadoDAO() : TareoLaboresEmpleadoDAO
    abstract fun unidadesDAO() : UnidadesDAO
    abstract fun unidadDestajoDAO() : UnidadDestajoDAO
    abstract fun tarifaUnidadDestajoDAO() : TarifaUnidadDestajoDAO
    abstract fun tareoLaboresEmpleado_ProductividadDAO() : TareoLaboresEmpleado_ProductividadDAO
    abstract fun permisosDAO() : PermisosDAO
    abstract fun asistenciaauxiliarDAO() : AsistenciaAuxiliarDAO
    abstract fun datosInicialesDAO() : DatosInicialesDAO

    companion object {
        private var INSTANCIA: ConexionSqlite? = null
        fun getInstancia(context: Context): ConexionSqlite {
            if (INSTANCIA == null) {
                INSTANCIA = Room.databaseBuilder(
                    context.applicationContext,
                    ConexionSqlite::class.java, "BDConsultasNisira"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
            return INSTANCIA!!
        }
    }
}