package dev.app.peru.guidecsharp.Vista.ui.config

import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.TipoDocumentoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.TipoDocumento
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TipoDocumentos.TipoDocAD
import dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite.TipoDocumentos.TipoDocVH
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentTiposDocumentoBinding
import java.util.ArrayList

class TiposDocumentoFragment : Fragment(), TextWatcher {

    private var _binding: FragmentTiposDocumentoBinding? = null
    private val binding get() = _binding!!

    //Variables
    private lateinit var tipoDocumentoDAO: TipoDocumentoDAO
    private var lista = ArrayList<TipoDocumento>()
    private lateinit var adapter : RecyclerView.Adapter<TipoDocVH>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTiposDocumentoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        tipoDocumentoDAO = ConexionSqlite.getInstancia(requireContext()).tipoDocumentoDAO()
        binding.btnBuscardordoc.setOnClickListener { btnReportar() }
        binding.txtBusquedadoc.addTextChangedListener(this)
        btnReportar()
        listar()

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menudocumentos, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //  return true
        return when (item!!.itemId) {
            R.id.action_migrardoc -> {
                fabSincronizartipoDoc()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun listar() {
        val lista = tipoDocumentoDAO.listarTipoDocumento() as ArrayList<TipoDocumento>

        if (lista.isNotEmpty()) {
            lista.iterator().forEach {
                Log.i("ASISTENCIA LISTs : ", it.toString())
                // DocumentoAdapter.add(it)
            }

        }else{
            Log.i("ASISTENCIA LISTs : ", "VACIO")
        }
    }

    private fun fabSincronizartipoDoc() {
        var i = 0
        val rs = EmpleadoDALC.PA_ListarTipoDocumento()
        while (rs.next()) {
            i++
            val TD_Codigo = rs.getString("TD_Codigo")
            val TD_Descripcion = rs.getString("TD_Descripcion")

            if (tipoDocumentoDAO.PA_ObtenerTipoDocumento_PorCodigo(TD_Codigo) == null)  {
                tipoDocumentoDAO.guardarTipoDocumento(TipoDocumento(TD_Codigo, TD_Descripcion))
            }
            else{
                tipoDocumentoDAO.actualizarTipoDocumento(TD_Codigo, TD_Descripcion)
            }
        }
        Globales.showSuccessMessage("Se sincronizaron $i registros", requireContext())
        btnReportar()
    }

    private fun btnReportar(){
        val bb = "%"+binding.txtBusquedadoc.text.toString()+"%"
        lista = tipoDocumentoDAO.buscadorTipoDocumentos(bb) as ArrayList<TipoDocumento>
        adapter = TipoDocAD(lista)
        binding.rvTipodoc.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTipodoc.adapter = adapter
        binding.txtItemsdoc.text = "Resultados de búsqueda: ${lista.size}"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnReportar()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}