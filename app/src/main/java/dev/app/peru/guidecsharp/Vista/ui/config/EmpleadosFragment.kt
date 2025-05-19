package dev.app.peru.guidecsharp.Vista.ui.config

import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.EmpleadoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Empleado
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Empleados.EmpleadoAD
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.Empleados.EmpleadoVH
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentEmpleadosBinding
import java.util.ArrayList

class EmpleadosFragment : Fragment() , TextWatcher, EmpleadoAD.MyClickListener,
    CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentEmpleadosBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var empleadoDAO: EmpleadoDAO
    private var lista = ArrayList<Empleado>()
    private lateinit var adapter : RecyclerView.Adapter<EmpleadoVH>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmpleadosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        empleadoDAO = ConexionSqlite.getInstancia(requireContext()).empleadoDAO()
        adapter = EmpleadoAD(lista, this)
        binding.btnBuscar.setOnClickListener { btnReportar() }

        binding.rbtnTodos.setOnCheckedChangeListener(this)
        binding.rbtnSincronizados.setOnCheckedChangeListener(this)
        binding.rbtnNoSincronizados.setOnCheckedChangeListener(this)

        binding.txtBusqueda.addTextChangedListener(this)
        btnReportar()

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menulistadoempleados, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_migrarEmpleados -> {
                SincronizacionNube()
                return true
            }
            R.id.action_sinmasivaempleados->{
                sincronizacionmasiva()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun SincronizacionNube() {
        var msj = ""
        var i = 0
        val rs = EmpleadoDALC.PA_ListarEmpleados_Android()
        while (rs.next()) {
            i++
            var PER_CODIGOQR = rs.getString("PER_CodigoQR")
            val TD_CODIGO = rs.getString("TD_Codigo")
            val PER_MATERNO = rs.getString("PER_Materno")
            val PER_NRODOC = rs.getString("PER_NroDoc")
            val PER_NOMBRE = rs.getString("PER_Nombres")
            val PER_PATERNO = rs.getString("PER_Paterno")
            val PER_DIRECCION = rs.getString("PER_Direccion")
            val PER_FECHAREGISTRO = rs.getString("PER_FechaRegistro")
            val PER_CODIGONUBE = rs.getInt("PER_CodigoNube")
            val PER_ORIGEN = rs.getString("PER_Origen")
            val PER_ESTADOSINCRONIZACION = rs.getString("PER_EstadoSincronizacion")
            val SUC_Codigo = rs.getInt("SUC_Codigo")

            if (PER_CODIGOQR == null) PER_CODIGOQR = PER_NRODOC
            if (BuscarEmpleadoXCNUBE(PER_CODIGONUBE) == null) {
                if (BuscarEmpleadoNoSincronizado(PER_NRODOC) == null) {
                    addEmpleado(
                        PER_CODIGOQR,
                        TD_CODIGO,
                        PER_NRODOC,
                        PER_NOMBRE,
                        PER_PATERNO,
                        PER_MATERNO,
                        PER_DIRECCION,
                        PER_FECHAREGISTRO,
                        PER_CODIGONUBE,
                        PER_ORIGEN,
                        PER_ESTADOSINCRONIZACION,
                        SUC_Codigo
                    )
                    //Log.i("TAG", "EL EMPLEADO REGISTRADO $PER_NOMBRE")
                } else {
                    EditEmpleado(
                        PER_CODIGOQR,
                        TD_CODIGO,
                        PER_NRODOC,
                        PER_NOMBRE,
                        PER_PATERNO,
                        PER_MATERNO,
                        PER_DIRECCION,
                        PER_FECHAREGISTRO,
                        PER_CODIGONUBE,
                        PER_ORIGEN,
                        PER_ESTADOSINCRONIZACION,
                        -2,
                        SUC_Codigo
                    )
                    //Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO $PER_NOMBRE")
                }
            } else {

                EditEmpleado(
                    PER_CODIGOQR,
                    TD_CODIGO,
                    PER_NRODOC,
                    PER_NOMBRE,
                    PER_PATERNO,
                    PER_MATERNO,
                    PER_DIRECCION,
                    PER_FECHAREGISTRO,
                    PER_CODIGONUBE,
                    PER_ORIGEN,
                    PER_ESTADOSINCRONIZACION,
                    -2,
                    SUC_Codigo
                )
                //Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO2 $PER_NOMBRE")
            }
        }
        Globales.showSuccessMessage("Se sincronizaron $i registros", requireContext())
        btnReportar()
    }

    private fun addEmpleado (PER_CodigoQR: String, TD_Codigo: String, PER_NroDoc: String, PER_Nombre: String,
                             PER_Paterno: String, PER_Materno: String="--", PER_Direccion: String="--", PER_FechaRegistro: String, PER_CodigoNube:Int,
                             PER_Origen :String, PER_EstadoSincronizacion: String, SUC_Codigo: Int) {
        val obj = Empleado(0, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
            PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
            PER_Origen,PER_EstadoSincronizacion, SUC_Codigo,"", "")
        empleadoDAO.guardarEmpleado(obj)
    }

    private fun EditEmpleado (PER_CodigoQR: String, TD_Codigo: String, PER_NroDoc: String, PER_Nombre: String,
                              PER_Paterno: String, PER_Materno: String, PER_Direccion: String, PER_FechaRegistro: String, PER_CodigoNube:Int,
                              PER_Origen :String, PER_EstadoSincronizacion: String, PER_Codigo:Int= -2, SUC_Codigo: Int) {

        if (PER_Codigo != -2){
            empleadoDAO.EditarEmpleado(PER_Codigo, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
                PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
                PER_Origen,PER_EstadoSincronizacion, SUC_Codigo)
        }else{
            val empl = BuscarEmpleadoXNroDoc(PER_NroDoc)
            if (empl != null){
                empleadoDAO.EditarEmpleado(empl.PER_Codigo, PER_CodigoQR,TD_Codigo,PER_NroDoc,PER_Nombre,PER_Paterno,
                    PER_Materno, PER_Direccion,PER_FechaRegistro,PER_CodigoNube,
                    PER_Origen,PER_EstadoSincronizacion, SUC_Codigo)
                //Toast.makeText(requireContext() , "Empleado_sql Actualizado Satisfactoriamente", Toast.LENGTH_SHORT).show()

            }else{
                //Toast.makeText(requireContext() , "Empleado_sql No se puede Actualizar por que no encontro su DNI", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun BuscarEmpleadoXCNUBE(campo:Int):Empleado? {
        val empleadotop = empleadoDAO.obtenerEmpleadoXCNUBE(campo)?: return null
        return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
            empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
            empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
    }
    private fun BuscarEmpleadoXNroDoc(campo:String):Empleado? {
        val empleadotop =  empleadoDAO.obtenerEmpleadoXNroDoc(campo)?: return null
        return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
            empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
            empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
    }
    private fun BuscarEmpleadoNoSincronizado(campo: String):Empleado?{
        val empleadotop =  empleadoDAO.obtenerEmpleadoNoSincronizado(campo)?: return null
        return Empleado(empleadotop.PER_Codigo,empleadotop.PER_CodigoQR,empleadotop.TD_Codigo,empleadotop.PER_NroDoc,
            empleadotop.PER_Nombres,empleadotop.PER_Paterno,empleadotop.PER_Materno,empleadotop.PER_Direccion,empleadotop.PER_FechaRegistro,
            empleadotop.PER_CodigoNube,empleadotop.PER_Origen,empleadotop.PER_EstadoSincronizacion, empleadotop.SUC_Codigo, "", "")
    }

    private fun btnReportar(){

        val bb = "%"+binding.txtBusqueda.text.toString()+"%"
        if (binding.rbtnSincronizados.isChecked){
            lista = empleadoDAO.listarEmpleados_PorEstado("SINCRONIZADO", bb) as ArrayList<Empleado>
        }
        else if (binding.rbtnNoSincronizados.isChecked){
            lista = empleadoDAO.listarEmpleados_PorEstado("NO SINCRONIZADO", bb) as ArrayList<Empleado>
        }
        else{
            lista = empleadoDAO.listarEmpleados(bb) as ArrayList<Empleado>
        }

        adapter = EmpleadoAD(lista, this)
        binding.rvEmpleadoSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpleadoSqlite.adapter = adapter
        binding.txtItems.text = "Resultados de búsqueda: ${lista.size}"
    }

    override fun onClick(position: Int,tipo:String){
        when(tipo){
            "Sincronizar"->{
                val obj = lista[position]
                sincronizacionunitaria(obj.PER_Codigo)
                Toast.makeText(requireContext(), "Sincronizar: "+obj.PER_Codigo, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sincronizacionunitaria(perCodigo: Int) {
        val lista = empleadoDAO.Sincronizacionunitariaempleado(perCodigo) as ArrayList<Empleado>
        Sincronizar(lista)
        Globales.showSuccessMessage("Sincronización exitosa.", requireContext())
        ActivityCompat.recreate(requireActivity())
    }

    private fun sincronizacionmasiva() {
        val lista = empleadoDAO.listarEmpleadosNoNube() as ArrayList<Empleado>
        Sincronizar(lista)
        ActivityCompat.recreate(requireActivity())
    }

    private fun Sincronizar(lista:ArrayList<Empleado>){
        var body:String = ""
        if (lista.size == 0){
            Globales.showWarningMessage("No hay empleados pendientes para sincronizar.", requireContext())
            return
        }else{

            var xx = 0
            lista.iterator().forEach {
                //body +=it.envioDatosNube()
                if (xx == 0){
                    body = it.envioDatosNube()
                }else{
                    body = body +"%" + it.envioDatosNube()
                }
                xx++
            }
            //Log.i("body", body)

            var i = 0
            val rs = EmpleadoDALC.PA_ProcesarEmpleados_Android(body)
            while (rs.next()) {
                i++
                val PER_Codigo = rs.getInt("PER_Codigo")//si
                val PER_CODIGOQR = rs.getString("PER_CodigoQR")//si
                val TD_CODIGO = rs.getString("TD_Codigo")//si
                val PER_NRODOC = rs.getString("PER_NroDoc")//si
                val PER_NOMBRE = rs.getString("PER_Nombres")//si
                val PER_PATERNO = rs.getString("PER_Paterno")//si
                val PER_MATERNO = rs.getString("PER_Materno")//si
                val PER_DIRECCION = rs.getString("PER_Direccion")//si
                val PER_FECHAREGISTRO = rs.getString("PER_FechaRegistro")
                val PER_CODIGONUBE = rs.getInt("PER_CodigoNube")//si
                val PER_ORIGEN = "NUBE"
                val PER_ESTADOSINCRONIZACION = "SINCRONIZADO"
                var SUC_Codigo = rs.getInt("SUC_Codigo")

                if (PER_CODIGOQR == null) {
                    if (BuscarEmpleadoXCNUBE(PER_CODIGONUBE) == null) {
                        if (BuscarEmpleadoNoSincronizado(PER_NRODOC) == null) {
                            addEmpleado(
                                PER_NRODOC,
                                TD_CODIGO,
                                PER_NRODOC,
                                PER_NOMBRE,
                                PER_PATERNO,
                                PER_MATERNO,
                                PER_DIRECCION,
                                PER_FECHAREGISTRO,
                                PER_CODIGONUBE,
                                PER_ORIGEN,
                                PER_ESTADOSINCRONIZACION,
                                SUC_Codigo
                            )
                            Log.i("TAG", "EL EMPLEADO REGISTRADO $PER_NOMBRE")

                        } else {
                            EditEmpleado(
                                PER_NRODOC,
                                TD_CODIGO,
                                PER_NRODOC,
                                PER_NOMBRE,
                                PER_PATERNO,
                                PER_MATERNO,
                                PER_DIRECCION,
                                PER_FECHAREGISTRO,
                                PER_CODIGONUBE,
                                PER_ORIGEN,
                                PER_ESTADOSINCRONIZACION,
                                PER_Codigo,
                                SUC_Codigo
                            )
                            Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO $PER_NOMBRE")
                        }
                    } else {

                        EditEmpleado(
                            PER_NRODOC,
                            TD_CODIGO,
                            PER_NRODOC,
                            PER_NOMBRE,
                            PER_PATERNO,
                            PER_MATERNO,
                            PER_DIRECCION,
                            PER_FECHAREGISTRO,
                            PER_CODIGONUBE,
                            PER_ORIGEN,
                            PER_ESTADOSINCRONIZACION,
                            PER_Codigo,
                            SUC_Codigo
                        )
                        Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO2 $PER_NOMBRE")
                    }

                } else {

                    if (BuscarEmpleadoXCNUBE(PER_CODIGONUBE) == null) {
                        if (BuscarEmpleadoNoSincronizado(PER_NRODOC) == null) {
                            addEmpleado(
                                PER_CODIGOQR,
                                TD_CODIGO,
                                PER_NRODOC,
                                PER_NOMBRE,
                                PER_PATERNO,
                                PER_MATERNO,
                                PER_DIRECCION,
                                PER_FECHAREGISTRO,
                                PER_CODIGONUBE,
                                PER_ORIGEN,
                                PER_ESTADOSINCRONIZACION,
                                SUC_Codigo
                            )
                            Log.i("TAG", "EL EMPLEADO REGISTRADO $PER_NOMBRE")

                        } else {
                            EditEmpleado(
                                PER_CODIGOQR,
                                TD_CODIGO,
                                PER_NRODOC,
                                PER_NOMBRE,
                                PER_PATERNO,
                                PER_MATERNO,
                                PER_DIRECCION,
                                PER_FECHAREGISTRO,
                                PER_CODIGONUBE,
                                PER_ORIGEN,
                                PER_ESTADOSINCRONIZACION,
                                PER_Codigo,
                                SUC_Codigo
                            )
                            Log.i("TAG", "EL EMPLEADO YA ESTA EDITADO 3 $PER_NOMBRE")
                        }
                    } else {
                        EditEmpleado(
                            PER_CODIGOQR,
                            TD_CODIGO,
                            PER_NRODOC,
                            PER_NOMBRE,
                            PER_PATERNO,
                            PER_MATERNO,
                            PER_DIRECCION,
                            PER_FECHAREGISTRO,
                            PER_CODIGONUBE,
                            PER_ORIGEN,
                            PER_ESTADOSINCRONIZACION,
                            PER_Codigo,
                            SUC_Codigo
                        )
                    }
                }
            }
            Globales.showSuccessMessage("Se sincronizaron $i registros", requireContext())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //Toast.makeText(this, "antes: "+s.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnReportar()
    }

    override fun afterTextChanged(s: Editable?) {
        //Toast.makeText(this, "despues: "+s.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        btnReportar()
    }
}