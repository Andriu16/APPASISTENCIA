package dev.app.peru.guidecsharp.Vista.ui.config

import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentConfiguracionBinding
import java.net.Inet4Address
import java.net.NetworkInterface


class ConfiguracionFragment : Fragment() {

    private var _binding: FragmentConfiguracionBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var packingDAO: PackingDAO
    private lateinit var cultivoDAO: CultivoDAO
    private lateinit var centroCostoDAO: CentroCostoDAO
    private lateinit var laboresDAO: LaboresDAO
    private lateinit var actividadDAO: ActividadDAO
    private lateinit var sucursalDAO: SucursalDAO
    private lateinit var unidadesDAO: UnidadesDAO
    private lateinit var unidadDestajoDAO: UnidadDestajoDAO
    private lateinit var tarifaUnidadDestajoDAO: TarifaUnidadDestajoDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        packingDAO = ConexionSqlite.getInstancia(requireContext()).packingDAO()
        cultivoDAO = ConexionSqlite.getInstancia(requireContext()).cultivoDAO()
        centroCostoDAO = ConexionSqlite.getInstancia(requireContext()).centroCostoDAO()
        laboresDAO = ConexionSqlite.getInstancia(requireContext()).laboresDAO()
        actividadDAO = ConexionSqlite.getInstancia(requireContext()).actividadDAO()
        sucursalDAO = ConexionSqlite.getInstancia(requireContext()).sucursalDAO()
        unidadesDAO = ConexionSqlite.getInstancia(requireContext()).unidadesDAO()
        unidadDestajoDAO = ConexionSqlite.getInstancia(requireContext()).unidadDestajoDAO()
        tarifaUnidadDestajoDAO = ConexionSqlite.getInstancia(requireContext()).tarifaUnidadDestajoDAO()

        binding.btnTrabajadores.setOnClickListener { btnTrabajadores_Click() }
        binding.btnTipoDocumentos.setOnClickListener { btnTiposDocumentos_Click() }
        binding.btnPoblarDatos.setOnClickListener { btnPoblarDatos_Click() }
        binding.btnBackup.setOnClickListener { btnCopiaSeguridad_Click() }
        binding.btnRestaurarBackup.setOnClickListener { btnRestaurarBackup_Click() }
        return root
    }

    fun btnTrabajadores_Click() {
        findNavController().navigate(R.id.action_nav_configuration_to_empleadosFragment)
    }

    fun btnTiposDocumentos_Click() {
        findNavController().navigate(R.id.action_nav_configuration_to_tiposDocumentoFragment)
    }

    fun btnPoblarDatos_Click() {

        //Obteniendo Fundo/Packing de la nube
        packingDAO.eliminarTodos()
        var i = 0
        val rsPacking = EmpleadoDALC.PA_ListarPacking()
        while (rsPacking.next()) {
            i++

            val PAC_Codigo = rsPacking.getInt("PAC_Codigo")
            val PAC_Descripcion = rsPacking.getString("PAC_Descripcion")
            val PAC_Estado = rsPacking.getString("PAC_Estado")
            var packing = Packing(PAC_Codigo, PAC_Descripcion, PAC_Estado)
            procesarPacking(packing)
        }

        //Obteniendo Cultivo de la nube
        cultivoDAO.eliminarTodos()
        var j = 0
        val rsCultivo = EmpleadoDALC.PA_ListarCultivo()
        while (rsCultivo.next()) {
            j++

            val CU_Codigo = rsCultivo.getInt("CU_Codigo")
            val CU_Descripcion = rsCultivo.getString("CU_Descripcion")
            val CU_Estado = rsCultivo.getString("CU_Estado")
            var cultivo = Cultivo(CU_Codigo, CU_Descripcion, CU_Estado)
            procesarCultivo(cultivo)
        }

        //Obteniendo Centro de Costo de la nube
        centroCostoDAO.eliminarTodos()
        var k = 0
        val rsCentroCosto = EmpleadoDALC.PA_ListarCentroCosto()
        while (rsCentroCosto.next()) {
            k++

            val CC_Codigo = rsCentroCosto.getLong("CC_Codigo")
            val CC_Descripcion = rsCentroCosto.getString("CC_Descripcion")
            val CC_Estado = rsCentroCosto.getString("CC_Estado")
            val CC_Otros = rsCentroCosto.getString("CC_Otros")
            val CU_Codigo = rsCentroCosto.getInt("CU_Codigo")
            var centroCosto = CentroCosto(CC_Codigo, CC_Descripcion, CC_Estado, CC_Otros, CU_Codigo)
            procesarCentroCosto(centroCosto)
        }

        //Obteniendo Labores de la nube
        laboresDAO.eliminarTodos()
        var l = 0
        val rsLabores = EmpleadoDALC.PA_ListarLabores()
        while (rsLabores.next()) {
            l++

            val Lab_Codigo = rsLabores.getLong("Lab_Codigo")
            val Lab_Descripcion = rsLabores.getString("Lab_Descripcion")
            val Lab_Estado = rsLabores.getString("Lab_Estado")
            val Lab_Otros = rsLabores.getString("Lab_Otros")
            val AC_Codigo = rsLabores.getLong("AC_Codigo")
            var labor = Labores(Lab_Codigo, Lab_Descripcion, Lab_Estado, Lab_Otros, AC_Codigo)
            procesarLabores(labor)
        }

        //Obteniendo Actividad de la nube
        actividadDAO.eliminarTodos()
        var m = 0
        val rsActividades = EmpleadoDALC.PA_ListarActividad()
        while (rsActividades.next()) {
            m++

            val AC_Codigo = rsActividades.getLong("AC_Codigo")
            val AC_Descripcion = rsActividades.getString("AC_Descripcion")
            val AC_Estado = rsActividades.getString("AC_Estado")
            var actividad = Actividad(AC_Codigo, AC_Descripcion, AC_Estado)
            procesarActividad(actividad)
        }

        //Obteniendo Sedes de la nube
        sucursalDAO.eliminarTodos()
        var n = 1
        val SUC_Codigo_ini : Long = -1
        val SUC_Descripcion_ini = "- TODAS LAS SEDES -"
        val SUC_Estado_ini = "Habilitado"
        var sucursal_ini = Sucursal(SUC_Codigo_ini, SUC_Descripcion_ini, SUC_Estado_ini)
        procesarSede(sucursal_ini)

        val rsSucursales = EmpleadoDALC.PA_ListarSede()
        while (rsSucursales.next()) {
            n++

            val SUC_Codigo = rsSucursales.getLong("SUC_Codigo")
            val SUC_Descripcion = rsSucursales.getString("SUC_Descripcion")
            val SUC_Estado = rsSucursales.getString("SUC_Estado")
            var sucursal = Sucursal(SUC_Codigo, SUC_Descripcion, SUC_Estado)
            procesarSede(sucursal)
        }

        //Obteniendo Unidades de la nube
        unidadesDAO.eliminarTodos()
        var o = 0
        val rsUnidades = EmpleadoDALC.PA_ListarUnidades()
        while (rsUnidades.next()) {
            o++

            val UNI_Codigo = rsUnidades.getLong("UNI_Codigo")
            val UNI_Descripcion = rsUnidades.getString("UNI_Descripcion")
            var obj = Unidades(UNI_Codigo, UNI_Descripcion)

            if (unidadesDAO.PA_ObtenerUnidad_PorCodigo(UNI_Codigo) == null) {
                unidadesDAO.guardarUnidad(obj)
            } else {
                unidadesDAO.actualizarUnidad(obj)
            }
        }

        //Obteniendo Unidades Destajo de la nube
        unidadDestajoDAO.eliminarTodos()
        var p = 0
        val rsUnidadesDestajo = EmpleadoDALC.PA_ListarUnidadDestajo_Android()
        while (rsUnidadesDestajo.next()) {
            p++

            val UD_Codigo = rsUnidadesDestajo.getLong("UD_Codigo")
            val UD_Nombre = rsUnidadesDestajo.getLong("UD_Nombre")
            val Lab_Codigo = rsUnidadesDestajo.getInt("Lab_Codigo")
            val UNI_DescripcionTemp = rsUnidadesDestajo.getString("UNI_Descripcion")
            var obj = UnidadesDestajo(UD_Codigo, UD_Nombre, Lab_Codigo, UNI_DescripcionTemp)

            if (unidadDestajoDAO.PA_ObtenerUnidadDestajo_PorCodigo(UD_Codigo) == null) {
                unidadDestajoDAO.guardarUnidadDestajo(obj)
            } else {
                unidadDestajoDAO.actualizarUnidadDestajo(obj)
            }
        }

        //Obteniendo Tarifas de la nube
        tarifaUnidadDestajoDAO.eliminarTodos()
        var q = 0
        val rsTarifas = EmpleadoDALC.PA_listarTarifas_Android()
        while (rsTarifas.next()) {
            q++

            val TUD_Codigo = rsTarifas.getLong("TUD_Codigo")
            val TUD_Nombre = rsTarifas.getString("TUD_Nombre")
            val Lab_Codigo = rsTarifas.getInt("Lab_Codigo")
            val UD_Codigo = rsTarifas.getInt("UD_Codigo")
            val TUD_Costo = rsTarifas.getFloat("TUD_Costo")
            val TUD_Desde = FunGeneral.obtenerFechaHora_PorFormato_Nube_Long(rsTarifas.getString("Desde"))
            val TUD_Hasta = FunGeneral.obtenerFechaHora_PorFormato_Nube_Long(rsTarifas.getString("Hasta"))
            val TUD_TarifaCerrada = rsTarifas.getString("TUD_TarifaCerrada")
            var obj = TarifaUnidadDestajo(TUD_Codigo, TUD_Nombre, Lab_Codigo, UD_Codigo, TUD_Costo,
            TUD_Desde, TUD_Hasta, TUD_TarifaCerrada)

            if (tarifaUnidadDestajoDAO.PA_ObtenerTarifa_PorCodigo(TUD_Codigo) == null) {
                tarifaUnidadDestajoDAO.guardarTarifa(obj)
            } else {
                tarifaUnidadDestajoDAO.actualizarTarifa(obj)
            }
        }

        Globales.showSuccessMessage("Se sincronizaron:\n" +
                "Fundo/Packing: ${i}\n" +
                "Cultivo: ${j}\n" +
                "Centro de Costo: ${k}\n" +
                "Labores: ${l}\n" +
                "Actividades: ${m}\n" +
                "Sedes: ${n}\n"+
                "Unidades: ${o}\n"+
                "Unidades de Labor: ${p}\n"+
                "Tarifas: ${q}", requireContext())
    }

    private fun procesarPacking(packing: Packing) {
        if (packingDAO.PA_ObtenerPacking_PorCodigo(packing.PAC_Codigo) == null) {
            packingDAO.guardarPacking(packing)
        } else {
            packingDAO.actualizarPacking(packing)
        }
    }

    private fun procesarCultivo(cultivo: Cultivo) {
        if (cultivoDAO.PA_ObtenerCultivo_PorCodigo(cultivo.CU_Codigo) == null) {
            cultivoDAO.guardarCultivo(cultivo)
        } else {
            cultivoDAO.actualizarCultivo(cultivo)
        }
    }

    private fun procesarCentroCosto(centroCosto: CentroCosto) {
        if (centroCostoDAO.PA_ObtenerCentroCosto_PorCodigo(centroCosto.CC_Codigo) == null) {
            centroCostoDAO.guardarCentroCosto(centroCosto)
        } else {
            centroCostoDAO.actualizarCentroCosto(centroCosto)
        }
    }

    private fun procesarLabores(labores: Labores) {
        if (laboresDAO.PA_ObtenerLabores_PorCodigo(labores.Lab_Codigo) == null) {
            laboresDAO.guardarLabor(labores)
        } else {
            laboresDAO.actualizarCentroCosto(labores)
        }
    }

    private fun procesarActividad(actividad: Actividad) {
        if (actividadDAO.PA_ObtenerActividad_PorCodigo(actividad.AC_Codigo) == null) {
            actividadDAO.guardarActividad(actividad)
        } else {
            actividadDAO.actualizarActividad(actividad)
        }
    }

    private fun procesarSede(sucursal: Sucursal) {
        if (sucursalDAO.PA_ObtenerSucursal_PorCodigo(sucursal.SUC_Codigo) == null) {
            sucursalDAO.guardarSucursal(sucursal)
        } else {
            sucursalDAO.actualizarSucursal(sucursal)
        }
    }

    private fun btnCopiaSeguridad_Click(){
        var _id = Secure.getString(requireContext().getContentResolver(), Secure.ANDROID_ID)
        var _nombre = obtenerNombreDeDispositivo()!!
        var _ip = getIPHostAddress()

        var c_asis =  0;
        //Empaquetar asistencias
        var _asistencias = ""
        var asistenciaPuertaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
        var lista = asistenciaPuertaDAO.listarAsistencias()

        var xx = 0
        lista.iterator().forEach {

            if (xx == 0) _asistencias = it.envioBackup()
            else _asistencias += "%" + it.envioBackup()
            xx++

            c_asis++
        }

        var c_empl = 0
        //Empaquetar empleados
        var _empleados = ""

        var empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
        var lista_EmpleadosBackup = empleadoDAO.listarEmpleado()

        var mm = 0
        lista_EmpleadosBackup.forEach {
            if (mm == 0) _empleados = it.envioBackup()
            else _empleados += "%" + it.envioBackup()
            mm++

            c_empl++
        }

        var c_tar = 0
        var c_lab = 0
        var c_prod = 0
        //Empaquetar Tareos
        var _tareos = ""
        var _tareoslabores = ""
        var _tareoslaboresproductividad = ""

        var tareoDAO = ConexionSqlite.getInstancia(requireContext()).tareoDAO()
        val tareoLaboresEmpleadoDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleadoDAO()
        val tareolaboresempleadoProductividadDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleado_ProductividadDAO()

        var lista_TareosBackup = tareoDAO.listarTareos()

        var i = 0
        var x = 0
        var y = 0
        for(it in lista_TareosBackup){
            if (i == 0) _tareos = it.envioBackup()
            else _tareos += "%" + it.envioBackup()
            i++

            val _labores = tareoLaboresEmpleadoDAO.PA_ListarDetalleTareo_PorCodigo(it.idTareo, "%%")
            for(ll in _labores){
                if (x == 0) _tareoslabores = ll.envioBackup()
                else _tareoslabores += "%" + ll.envioBackup()
                x++

                val _prod = tareolaboresempleadoProductividadDAO.PA_ListarProductividad_PorTareoDetalle(ll.idTareo, ll.idDetalle)
                for(pp in _prod){
                    if (y == 0) _tareoslaboresproductividad = pp.envioBackup()
                    else _tareoslaboresproductividad += "%" + pp.envioBackup()
                    y++

                    c_prod++
                }

                c_lab++
            }

            c_tar++
        }

        var rpta = ""
        try{
            val rs = EmpleadoDALC.PA_BackupDispositivo(_id, _nombre, _ip, _asistencias, _empleados, _tareos, _tareoslabores, _tareoslaboresproductividad)
            while (rs.next()) {
                rpta = rs.getString("Rpta")
            }

            if (rpta == "OK"){
                Globales.showSuccessMessage("Backup completado.\n"+(c_asis+c_empl+c_tar+c_lab+c_prod)+" Registros guardados de forma masiva.", requireContext())
            }
            else{
                Globales.showErrorMessage("Error: "+rpta, requireContext())
            }
        }
        catch (ex : Exception){
            if (_asistencias.contains("'")){
                Globales.showErrorMessage("Algún dato en Asistencias contiene el caracter: '\nNo se pudo sincronizar", requireContext())
            }
            else if (_empleados.contains("'")){
                Globales.showErrorMessage("Algún dato en Empleados contiene el caracter: '\nNo se pudo sincronizar", requireContext())
            }
            else if (_tareos.contains("'")){
                Globales.showErrorMessage("Algún dato en Tareos contiene el caracter: '\nNo se pudo sincronizar", requireContext())
            }
            else if (_tareoslabores.contains("'")){
                Globales.showErrorMessage("Algún dato en Labores contiene el caracter: '\nNo se pudo sincronizar", requireContext())
            }
            else if (_tareoslaboresproductividad.contains("'")){
                Globales.showErrorMessage("Algún dato en Productividad contiene el caracter: '\nNo se pudo sincronizar", requireContext())
            }
            else{
                Globales.showErrorMessage(ex.message.toString(), requireContext())
            }
        }
    }

    private fun btnRestaurarBackup_Click(){
        //Antes de restaurar asegurese de haber realizado la copia de seguridad

        Globales.showWarningMessageAndCuestions("¿Desea Recuperar El Último Backup?\nEsta acción es irreversible y se eliminarán todos los datos para implantar el Backup", requireContext(),
            {
                var _id = Secure.getString(requireContext().getContentResolver(), Secure.ANDROID_ID)
                var _nombre = obtenerNombreDeDispositivo()!!
                var _ip = getIPHostAddress()

                var rpta = ""
                try{
                    val rsAsistencias = EmpleadoDALC.PA_RestaurarBackupDispositivo_AsistenciaPuerta(_id, _nombre, _ip)
                    val rsEmpleados = EmpleadoDALC.PA_RestaurarBackupDispositivo_Empleado(_id, _nombre, _ip)
                    val rsTareos = EmpleadoDALC.PA_RestaurarBackupDispositivo_Tareo(_id, _nombre, _ip)
                    val rsLabores = EmpleadoDALC.PA_RestaurarBackupDispositivo_TareoLaboresEmpleado(_id, _nombre, _ip)
                    val rsProductividad = EmpleadoDALC.PA_RestaurarBackupDispositivo_TareoLaboresEmpleado_Productividad(_id, _nombre, _ip)

                    //------------------------------------------------------------------------
                    //------------------------------------------------------------------------
                    //------------------------------------------------------------------------

                    var c_asis = 0
                    var c_empl = 0
                    var c_tar = 0
                    var c_lab = 0
                    var c_prod = 0
                    //------------------------------------------------------------------------
                    //--------------------- PROCESANDO LISTA DE EMPLEADOS --------------------
                    //------------------------------------------------------------------------
                    var empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
                    empleadoDAO.eliminarTodos()

                    var listaEmpleados_CodNuevo = ArrayList<Long>()
                    var listaEmpleados_CodAntiguo = ArrayList<Long>()

                    while (rsEmpleados.next()) {
                        var PER_Codigo = rsEmpleados.getLong("PER_Codigo")
                        var dato01 = rsEmpleados.getString("PER_CodigoQR")
                        var dato02 = rsEmpleados.getString("TD_Codigo")
                        var dato03 = rsEmpleados.getString("PER_NroDoc")
                        var dato04 = rsEmpleados.getString("PER_Nombres")
                        var dato05 = rsEmpleados.getString("PER_Paterno")
                        var dato06 = rsEmpleados.getString("PER_Materno")
                        var dato07 = rsEmpleados.getString("PER_Direccion")
                        var dato08 = rsEmpleados.getString("PER_FechaRegistro")
                        var dato09 = rsEmpleados.getInt("PER_CodigoNube")
                        var dato10 = rsEmpleados.getString("PER_Origen")
                        var dato11 = rsEmpleados.getString("PER_EstadoSincronizacion")
                        var dato12 = rsEmpleados.getInt("SUC_Codigo")
                        var dato13 = rsEmpleados.getString("SelectTareo")
                        var dato14 = rsEmpleados.getString("PER_HoraEntrada")

                        var obj = Empleado(0, dato01, dato02, dato03, dato04, dato05, dato06,
                            dato07, dato08, dato09, dato10, dato11, dato12, dato13, dato14)

                        var PER_CodigoNuevo : Long = empleadoDAO.guardarEmpleado(obj)
                        listaEmpleados_CodNuevo.add(PER_CodigoNuevo)
                        listaEmpleados_CodAntiguo.add(PER_Codigo)

                        c_empl++
                    }

                    //------------------------------------------------------------------------
                    //-------------------- PROCESANDO LISTA DE ASISTENCIAS -------------------
                    //------------------------------------------------------------------------
                    var asistenciaDAO = ConexionSqlite.getInstancia(requireContext()).asistenciaPuertaDAO()
                    asistenciaDAO.eliminarTodos()

                    var listaAsistencias_CodNuevo = ArrayList<Long>()
                    var listaAsistencias_CodAntiguo = ArrayList<Long>()

                    while (rsAsistencias.next()) {
                        var AP_Codigo = rsAsistencias.getLong("AP_Codigo")
                        var dato01 = rsAsistencias.getString("AP_Fecha")
                        var dato02 = rsAsistencias.getString("AP_HoraEntrada")
                        var dato03 = rsAsistencias.getString("AP_HoraEntrada_Mostrar")
                        var dato04 = rsAsistencias.getString("AP_HoraSalida")
                        var dato05 = rsAsistencias.getString("AP_HoraSalida_Mostrar")
                        var dato06 = rsAsistencias.getLong("PER_Codigo")

                        //---- Buscar código nuevo insertado en empleados
                        var posAntiguo = listaEmpleados_CodAntiguo.indexOf(dato06)
                        var dato06_Nuevo = listaEmpleados_CodNuevo[posAntiguo].toInt()

                        var dato07 = rsAsistencias.getString("AP_Estado")
                        var dato08 = rsAsistencias.getString("AP_EstadoSincronizacion")

                        var obj = AsistenciaPuerta(0, dato01, dato02, dato03, dato04, dato05,
                            dato06_Nuevo, dato07, dato08)

                        var AP_CodigoNuevo : Long = asistenciaDAO.guardarAsistencia(obj)
                        listaAsistencias_CodNuevo.add(AP_CodigoNuevo)
                        listaAsistencias_CodAntiguo.add(AP_Codigo)

                        c_asis++
                    }

                    //------------------------------------------------------------------------
                    //---------------------- PROCESANDO LISTA DE TAREOS ----------------------
                    //------------------------------------------------------------------------
                    var tareosDAO = ConexionSqlite.getInstancia(requireContext()).tareoDAO()
                    tareosDAO.eliminarTodos()

                    var listaTareos_CodNuevo = ArrayList<Long>()
                    var listaTareos_CodAntiguo = ArrayList<Long>()

                    while (rsTareos.next()) {
                        var idTareo = rsTareos.getLong("idTareo")
                        var dato01 = rsTareos.getInt("TA_Codigo")
                        var dato02 = rsTareos.getString("TA_FechaRegistro")
                        var dato03 = rsTareos.getInt("PER_CodigoSupervisor")
                        var dato04 = rsTareos.getInt("PAC_Codigo")
                        var dato05 = rsTareos.getInt("CU_Codigo")
                        var dato06 = rsTareos.getString("TA_Incidencia")
                        var dato07 = rsTareos.getLong("CC_Codigo")
                        var dato08 = rsTareos.getLong("Lab_Codigo")
                        var dato09 = rsTareos.getLong("AC_Codigo")
                        var dato10 = rsTareos.getString("TA_TipoTareo")
                        var dato11 = rsTareos.getInt("_Trabajadores")

                        var obj = Tareo(0, dato01, dato02, dato03, dato04, dato05,
                            dato06, dato07, dato08, dato09, dato10, dato11)

                        var idTareoNuevo : Long = tareosDAO.guardarTareo(obj)
                        listaTareos_CodNuevo.add(idTareoNuevo)
                        listaTareos_CodAntiguo.add(idTareo)

                        c_tar++
                    }

                    //------------------------------------------------------------------------
                    //--------------------- PROCESANDO LISTA DE LABORES ----------------------
                    //------------------------------------------------------------------------
                    var laboresDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleadoDAO()
                    laboresDAO.eliminarTodos()

                    var listaLabores_CodNuevo = ArrayList<Long>()
                    var listaLabores_CodAntiguo = ArrayList<Long>()

                    while (rsLabores.next()) {
                        var idDetalle = rsLabores.getLong("idDetalle")
                        var dato01 = rsLabores.getLong("idTareo")

                        //---- Buscar código nuevo insertado en tareos
                        var posAntiguo = listaTareos_CodAntiguo.indexOf(dato01)
                        var dato01_Nuevo = listaTareos_CodNuevo[posAntiguo]

                        var dato02 = rsLabores.getInt("PER_CodigoObrero")
                        var dato03 = rsLabores.getString("TLE_HoraInicio")
                        var dato04 = rsLabores.getString("TLE_HoraFin")
                        var dato05 = rsLabores.getFloat("TLE_TotalHoras")
                        var dato06 = rsLabores.getString("TLE_EstadoAprobacion")
                        var dato07 = rsLabores.getFloat("TLE_TotalProductividad")
                        var dato08 = rsLabores.getString("TLE_Turno")
                        var dato09 = rsLabores.getString("TLE_HoraInicioTarde")
                        var dato10 = rsLabores.getString("TLE_HoraFinTarde")
                        var dato11 = rsLabores.getString("TLE_EntradaPuerta")
                        var dato12 = rsLabores.getString("TLE_SalidaPuerta")
                        var dato13 = rsLabores.getString("TLE_HoraInicioNoche")
                        var dato14 = rsLabores.getString("TLE_HoraFinNoche")
                        var dato15 = rsLabores.getInt("TLE_Dia")
                        var dato16 = rsLabores.getInt("TLE_Dia2")
                        var dato17 = rsLabores.getInt("TLE_Tarde")
                        var dato18 = rsLabores.getInt("TLE_Tarde2")
                        var dato19 = rsLabores.getInt("TLE_Noche")
                        var dato20 = rsLabores.getInt("TLE_Noche2")
                        var dato21 = rsLabores.getString("EstadoRevision")

                        var obj = TareoLaboresEmpleado(0, dato01_Nuevo, dato02, dato03, dato04, dato05,
                            dato06, dato07, dato08, dato09, dato10, dato11, dato12, dato13, dato14, dato15,
                            dato16, dato17, dato18, dato19, dato20, dato21)

                        var idDetalleNuevo : Long = laboresDAO.guardarTareoLaboresEmpleado(obj)
                        listaLabores_CodNuevo.add(idDetalleNuevo)
                        listaLabores_CodAntiguo.add(idDetalle)

                        c_lab++
                    }

                    //------------------------------------------------------------------------
                    //----------------- PROCESANDO LISTA DE PRODUCTIVIDAD --------------------
                    //------------------------------------------------------------------------
                    var productividadDAO = ConexionSqlite.getInstancia(requireContext()).tareoLaboresEmpleado_ProductividadDAO()
                    productividadDAO.eliminarTodos()

                    var listaProductividad_CodNuevo = ArrayList<Long>()
                    var listaProductividad_CodAntiguo = ArrayList<Long>()

                    while (rsProductividad.next()) {
                        var idDetalleProd = rsProductividad.getLong("idDetalleProd")

                        var dato01 = rsProductividad.getLong("idTareo")
                        var dato02 = rsProductividad.getLong("idDetalle")

                        //---- Buscar código nuevo insertado en tareos
                        var posAntiguo_1 = listaTareos_CodAntiguo.indexOf(dato01)
                        var dato01_Nuevo = listaTareos_CodNuevo[posAntiguo_1]

                        //---- Buscar código nuevo insertado en tareos
                        var posAntiguo_2 = listaLabores_CodAntiguo.indexOf(dato02)
                        var dato02_Nuevo = listaLabores_CodNuevo[posAntiguo_2]

                        var dato03 = rsProductividad.getInt("UNI_Codigo")
                        var dato04 = rsProductividad.getString("TLEP_Unidad")
                        var dato05 = rsProductividad.getFloat("TLEP_Cantidad")
                        var dato06 = rsProductividad.getFloat("TLEP_Precio")
                        var dato07 = rsProductividad.getFloat("TLEP_Importe")
                        var dato08 = rsProductividad.getString("TLEP_UnidadDestajo")
                        var dato09 = rsProductividad.getFloat("TLEP_CantidadDestajo")
                        var dato10 = rsProductividad.getFloat("TLEP_PrecioDestajo")
                        var dato11 = rsProductividad.getFloat("TLEP_TotalDestajo")
                        var dato12 = rsProductividad.getString("EstadoRevision")

                        var obj = TareoLaboresEmpleado_Productividad(0, dato01_Nuevo,
                            dato02_Nuevo, dato03, dato04, dato05, dato06, dato07, dato08, dato09,
                            dato10, dato11, dato12)

                        var idDetalleProdNuevo : Long = productividadDAO.guardarTareoLaboresEmpleado_Productividad(obj)
                        listaProductividad_CodNuevo.add(idDetalleProdNuevo)
                        listaProductividad_CodAntiguo.add(idDetalleProd)

                        c_prod++
                    }

                    Globales.showSuccessMessage("Backup Restaurado\nSe restauraron "+(c_asis+c_empl+c_tar+c_lab+c_prod)+".", requireContext())
                }
                catch (ex : Exception){
                    Globales.showErrorMessage(ex.message.toString(), requireContext())
                }
            },
            {

            })
    }

    fun obtenerNombreDeDispositivo(): String? {
        val fabricante = Build.MANUFACTURER
        val modelo = Build.MODEL
        return if (modelo.startsWith(fabricante)) {
            primeraLetraMayuscula(modelo)
        } else {
            primeraLetraMayuscula(fabricante) + " " + modelo
        }
    }

    private fun primeraLetraMayuscula(cadena: String?): String? {
        if (cadena == null || cadena.length == 0) {
            return ""
        }
        val primeraLetra = cadena[0]
        return if (Character.isUpperCase(primeraLetra)) {
            cadena
        } else {
            primeraLetra.uppercaseChar().toString() + cadena.substring(1)
        }
    }

    fun getIPHostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}