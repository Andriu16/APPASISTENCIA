package dev.app.peru.guidecsharp.Vista.ui.gallery

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.Tareo_sql
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.TareoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.Actividad
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.TareoAD_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sql.TareoVH_sql
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TareoAD_sqlite
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TareoVH_sqlite
import dev.app.peru.guidecsharp.Globales.DatePickerFragment
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentTareosBinding
import dev.app.peru.guidecsharp.databinding.FragmentTareosNubeBinding
import java.util.ArrayList

class TareosNubeFragment : Fragment(), TareoAD_sqlite.MyClickListener, TareoAD_sql.MyClickListener {
    private var _binding: FragmentTareosNubeBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var tareoDAO: TareoDAO
    private var lista = ArrayList<Tareo_sql>()
    private lateinit var adapter : RecyclerView.Adapter<TareoVH_sql>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentTareosNubeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var _ff = FunGeneral.fecha("dd/MM/YYYY")
        binding.etDate.setText(_ff)
        binding.etDate.setOnClickListener { showDatePickerDialog() }

        tareoDAO = ConexionSqlite.getInstancia(requireContext()).tareoDAO()
        adapter = TareoAD_sql(lista, this)
        binding.btnBuscar.setOnClickListener { btnReportar() }
//
//        binding.rbtnJornal.setOnCheckedChangeListener(this)
//        binding.rbtnTarea.setOnCheckedChangeListener(this)
//        binding.rbtnDestajo.setOnCheckedChangeListener(this)
//        binding.rbtnTodos.setOnCheckedChangeListener(this)

//        binding.txtBusqueda.addTextChangedListener(this)
        btnReportar()

        setHasOptionsMenu(true)
        return root
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        var _dia = "$day"
        if (day < 10) _dia = "0$day"

        var _mes = "${month+1}"
        if (month + 1 < 10) _mes = "0${month+1}"

        binding.etDate.setText("$_dia/$_mes/$year")

        btnReportar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        //inflater.inflate(R.menu.menulistadotareos_nube, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun btnReportar(){

        val fecha = FunGeneral.obtenerFecha_PorFormato(binding.etDate.text.toString(), "YYYYMMdd")
        val bb = "%"+binding.txtBusqueda.text.toString()+"%"

        var tipotareo = "%%"
        if (binding.rbtnJornal.isChecked) tipotareo = "%JORNAL%"
        else if (binding.rbtnTarea.isChecked) tipotareo = "%TAREA%"
        else if (binding.rbtnDestajo.isChecked) tipotareo = "%DESTAJO%"

        lista.clear()
        //Obteniendo Tareos de la Nube
        var m = 0
        val rsTareos = EmpleadoDALC.PA_ListarTareo_Android(fecha, tipotareo, bb)
        while (rsTareos.next()) {
            m++

            var obj = Tareo_sql()
            obj.TA_Codigo = rsTareos.getInt("TA_Codigo")
            obj.TA_Fecha = rsTareos.getString("TA_Fecha")
            obj._Packing = rsTareos.getString("Packing")
            obj._Cultivo = rsTareos.getString("Cultivo")
            obj._Incidencia = rsTareos.getString("Incidencia")
            obj._CentroCosto = rsTareos.getString("CentroCosto")
            obj._Labor = rsTareos.getString("Labor")
            obj._TipoTareo = rsTareos.getString("TipoTareo")
            obj._Supervisor = rsTareos.getString("Supervisor")
            obj._Trabajadores = rsTareos.getInt("CantTrabajadores")

            lista.add(obj)
        }

        adapter = TareoAD_sql(lista, this)
        binding.rvTareosSqlite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTareosSqlite.adapter = adapter
        binding.txtItems.text = "Resultados de búsqueda: ${lista.size}"
    }

    override fun onClick(position: Int, tipo: String) {
        when(tipo){
            "Sincronizar"->{
                val obj = lista[position]

            }
        }
    }
}